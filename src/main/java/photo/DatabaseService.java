package photo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.tools.Server;

public class DatabaseService {
//        private static final String DB_CONNECTION = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_CONNECTION = "jdbc:h2:mem:test";
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    private static Server server;

    public DatabaseService() {
        try {
            server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-tcpDaemon").start();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("URL: jdbc:h2:" + server.getURL() + "/mem:test");
    }

    public static void insertWithStatement(Integer id, String userName) throws SQLException {
        Connection connection = getDBConnection();
        Statement stmt = null;
        try {
            connection.setAutoCommit(false);
            stmt = connection.createStatement();
            stmt.execute("CREATE TABLE PERSON(id int primary key, username varchar(255))");
//            stmt.execute("INSERT INTO PERSON(id, username) VALUES(1, 'Anju')");
            stmt.execute("INSERT INTO PERSON(id, username) VALUES(" + id + ", '" + userName + "')");

            ResultSet rs = stmt.executeQuery("select * from PERSON");
            System.out.println("H2 In-Memory Database inserted through Statement");
            while (rs.next()) {
                System.out.println("Id " + rs.getInt("id") + " Name " + rs.getString("username"));
            }

//            stmt.execute("DROP TABLE PERSON");
            stmt.close();
            connection.commit();
        } catch (SQLException e) {

            System.out.println("Exception Message " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
            connection.close();
        }
    }

    public static Connection getDBConnection() {

        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);

            return dbConnection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbConnection;
    }
}
