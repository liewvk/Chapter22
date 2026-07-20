import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class StudentDatabaseSetup {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:students.db";
        return DriverManager.getConnection(url);
    }

    public static void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS students (
                    student_id TEXT PRIMARY KEY,
                    name TEXT NOT NULL,
                    course TEXT NOT NULL,
                    mark REAL NOT NULL
                );
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(sql);
            System.out.println("Students table is ready.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTable();
    }
}