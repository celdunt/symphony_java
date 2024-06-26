package loc.ex.symphony.db;

import loc.ex.symphony.indexdata.IndexStruct;

import java.sql.*;

public class DbHandler {

    private Connection connection;
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
            statement.execute(getIndexStructDbTableString());
            statement.execute(getSynonymDbTableString());
            statement.execute(getSynonymHelpDbTableString());
            statement.execute(getHashDbTableString());
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


    public void put(IndexStruct value) {
        //check hash:
            //if not exist: add index_struct, get index_struct id, start_cycle: add synonym, get synonym id, add synonym_help, end_cycle, add hash, get hash_id, add hash_help
            //id exist: get hash_id, add index_struct, get index_struct id, start_cycle: add synonym, get synonym id, add synonym_help, end_cycle, add hash_help
        try (Statement statement = connection.createStatement()) {
            ResultSet hash = statement.executeQuery(
                    "select * from Hash where key = '" + value.getWord() + "'"
            );
            if (!hash.next()) {
                //продолжить
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void get(String key) {
        //get id by key from hash, get all hash_help where hashId=hash_id; cycle_start: get indexStruct where id=hash_help[i].indexId,
        // get indexStruct_id, get all synonymHelp where indexId=indexStruct_id, get synonym_word where id=synonymHelp_id
    }
}