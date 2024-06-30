package loc.ex.symphony.db;

import loc.ex.symphony.indexdata.IndexStruct;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbHandler {

    private Connection connection;
    private final String LAST_ID_QUERY = "select last_insert_rowid();";
    public DbHandler() throws SQLException, ClassNotFoundException {
        connectDb();
    }

    private void connectDb() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        this.connection = DriverManager.getConnection("jdbc:sqlite:symphony.db");
        createDbTables();
    }

    private void createDbTables() {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("drop table if exists IndexStruct");
            statement.execute(getIndexStructDbTableString());
            statement.executeUpdate("drop table if exists Synonym");
            statement.execute(getSynonymDbTableString());
            statement.executeUpdate("drop table if exists SynonymHelp");
            statement.execute(getSynonymHelpDbTableString());
            statement.executeUpdate("drop table if exists Hash");
            statement.execute(getHashDbTableString());
            statement.executeUpdate("drop table if exists HashHelp");
            statement.execute(getHashHelpDbTableString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getIndexStructDbTableString() {
        return """
                CREATE TABLE "IndexStruct" (
                	"id"	INTEGER NOT NULL UNIQUE,
                	"bookId"	INTEGER NOT NULL,
                	"chapterId"	INTEGER NOT NULL,
                	"fragmentId"	INTEGER NOT NULL,
                	"position"	INTEGER NOT NULL,
                	"wordLength"	INTEGER NOT NULL,
                	"word"	TEXT NOT NULL,
                	PRIMARY KEY("id" AUTOINCREMENT)
                )
                """;
    }

    private String getSynonymDbTableString() {
        return """
                CREATE TABLE "Synonym" (
                	"id"	INTEGER NOT NULL UNIQUE,
                	"word"	TEXT NOT NULL,
                	PRIMARY KEY("id" AUTOINCREMENT)
                )
                """;
    }

    private String getSynonymHelpDbTableString() {
        return """
                CREATE TABLE "SynonymHelp" (
                	"id"	INTEGER NOT NULL UNIQUE,
                	"indexId"	INTEGER NOT NULL,
                	"synonymId"	INTEGER NOT NULL,
                	PRIMARY KEY("id" AUTOINCREMENT)
                )
                """;
    }

    private String getHashDbTableString() {
        return """
                CREATE TABLE "Hash" (
                	"id"	INTEGER NOT NULL UNIQUE,
                	"key"	TEXT NOT NULL,
                	PRIMARY KEY("id" AUTOINCREMENT)
                )
                """;
    }

    private String getHashHelpDbTableString() {
        return """
                CREATE TABLE "HashHelp" (
                	"id"	INTEGER NOT NULL UNIQUE,
                	"indexId"	INTEGER NOT NULL,
                	"hashId"	INTEGER NOT NULL,
                	PRIMARY KEY("id" AUTOINCREMENT)
                )
                """;
    }


    private int insertIndexStruct(IndexStruct obj) {
        try(Statement statement = connection.createStatement()) {
            String query = String.format("""
                    insert into IndexStruct(bookId, chapterId, fragmentId, position, wordLength, word)
                    values(%d, %d, %d, %d, %d, '%s')
                    """, obj.getBookID(), obj.getChapterID(), obj.getFragmentID(), obj.getPosition(), 2, "obj.getWord()");
            statement.executeUpdate(query);
            ResultSet rs = statement.executeQuery(LAST_ID_QUERY);
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void insertSynonyms(int indexId, IndexStruct value, Statement statement) throws SQLException {
        /*for (String synonym : value.getSynonyms()) {
            String query = String.format("""
                            insert into Synonym(word) values("%s")""", synonym);
            statement.executeUpdate(query);
            ResultSet rs = statement.executeQuery(LAST_ID_QUERY);
            query = String.format("""
                            insert into SynonymHelp(indexId, synonymId) values(%d, "%s")""",
                    indexId, rs.getInt(1));
            statement.executeUpdate(query);
        }*/
    }

    public void addSynonym(int indexId, String word) {
        try(Statement statement = connection.createStatement()) {

            String query = String.format("""
                            insert into Synonym(word) values("%s")""", word);
            statement.executeUpdate(query);
            ResultSet rs = statement.executeQuery(LAST_ID_QUERY);
            query = String.format("""
                            insert into SynonymHelp(indexId, synonymId) values(%d, "%s")""",
                    indexId, rs.getInt(1));
            statement.executeUpdate(query);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void put(IndexStruct value) {
        //check hash:
            //if not exist: add index_struct, get index_struct id, start_cycle: add synonym, get synonym id, add synonym_help, end_cycle, add hash, get hash_id, add hash_help
            //id exist: get hash_id, add index_struct, get index_struct id, start_cycle: add synonym, get synonym id, add synonym_help, end_cycle, add hash_help
        try (Statement statement = connection.createStatement()) {
            ResultSet hash = statement.executeQuery(
                    String.format("""
                            select * from Hash where key = "%s"
                            """, "value.getWord()")
            );
            if (!hash.next()) {
                int indexId = insertIndexStruct(value);
                insertSynonyms(indexId, value, statement);
                String query = String.format("""
                        insert into Hash(key) values("%s")""",
                        "value.getWord()");
                statement.executeUpdate(query);
                ResultSet rs = statement.executeQuery(LAST_ID_QUERY);
                query = String.format("""
                        insert into HashHelp(indexId, hashId) values(%d, %d)""",
                        indexId, rs.getInt(1));
                statement.executeUpdate(query);
            } else {
                int hashId = hash.getInt("id");
                int indexId = insertIndexStruct(value);
                insertSynonyms(indexId, value, statement);
                String query = String.format("""
                        insert into HashHelp(indexId, hashId) values(%d, %d)""",
                        indexId, hashId);
                statement.executeUpdate(query);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<IndexStruct> get(String key) {
        //get id by key from hash, get all hash_help where hashId=hash_id; cycle_start: get indexStruct where id=hash_help[i].indexId,
        // get indexStruct_id, get all synonymHelp where indexId=indexStruct_id, get synonym_word where id=synonymHelp_id
        List<IndexStruct> resultList = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            ResultSet hash = statement.executeQuery(
                    String.format("""
                            select * from Hash where key = "%s"
                            """, key)
            );
            int hashId = hash.getInt("id");
            if (hash.next()) {
                ResultSet hashHelp = statement.executeQuery(
                        String.format("""
                                select * from HashHelp where hashId = %d
                                """, hashId)
                );
                while (hashHelp.next()) {
                    int indexId = hashHelp.getInt("indexId");
                    ResultSet indexSet = statement.executeQuery(
                            String.format("""
                                    select * from IndexStruct where id = %d""", indexId)
                    );
                    while (indexSet.next()) {
                        IndexStruct indexStruct = new IndexStruct();
                        //indexStruct.setId(indexSet.getInt("id"));
                        indexStruct.setBookID(indexSet.getInt("bookId"));
                        indexStruct.setChapterID(indexSet.getInt("chapterId"));
                        indexStruct.setFragmentID(indexSet.getInt("fragmentId"));
                        indexStruct.setPosition(indexSet.getInt("position"));
                        //indexStruct.setWordLength(indexSet.getInt("wordLength"));
                        //indexStruct.setWord(indexSet.getString("word"));

                        ResultSet synonymHelpSet = statement.executeQuery(
                                String.format("""
                                        select * from SynonymHelp where indexId=%d""", indexId)
                        );
                        while (synonymHelpSet.next()) {
                            int synonymId = synonymHelpSet.getInt("synonymId");
                            ResultSet synonymSet = statement.executeQuery(
                                    String.format(
                                            """
                                            select * from Synonym where id = %d""", synonymId
                            ));
                            //indexStruct.getSynonyms().add(synonymSet.getString("word"));
                        }
                        resultList.add(indexStruct);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return resultList;
    }
}