package loc.ex.symphony.db;

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



}
