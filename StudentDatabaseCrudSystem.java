import java.sql.*;
import java.util.Scanner;

public class StudentDatabaseCrudSystem {

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

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void addStudent(Scanner input) {
        String sql = """
                INSERT INTO students (student_id, name, course, mark)
                VALUES (?, ?, ?, ?);
                """;

        System.out.print("Enter student ID: ");
        String studentId = input.nextLine().trim();

        System.out.print("Enter name: ");
        String name = input.nextLine().trim();

        System.out.print("Enter course: ");
        String course = input.nextLine().trim();

        System.out.print("Enter mark: ");
        String markText = input.nextLine();

        try {
            double mark = Double.parseDouble(markText);

            if (mark < 0 || mark > 100) {
                System.out.println("Mark must be between 0 and 100.");
                return;
            }

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setString(1, studentId);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, course);
                preparedStatement.setDouble(4, mark);

                preparedStatement.executeUpdate();

                System.out.println("Student added successfully.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid mark. Please enter a number.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void viewStudents() {
        String sql = """
                SELECT student_id, name, course, mark
                FROM students
                ORDER BY student_id;
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println();
            System.out.println("Student Records");
            System.out.println("---------------");

            boolean found = false;

            while (resultSet.next()) {
                found = true;

                System.out.println("Student ID: " + resultSet.getString("student_id"));
                System.out.println("Name      : " + resultSet.getString("name"));
                System.out.println("Course    : " + resultSet.getString("course"));
                System.out.printf("Mark      : %.2f%n", resultSet.getDouble("mark"));
                System.out.println("Grade     : " + getGrade(resultSet.getDouble("mark")));
                System.out.println();
            }

            if (!found) {
                System.out.println("No student records found.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void searchStudent(Scanner input) {
        String sql = """
                SELECT student_id, name, course, mark
                FROM students
                WHERE student_id = ?;
                """;

        System.out.print("Enter student ID to search: ");
        String studentId = input.nextLine().trim();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, studentId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println();
                System.out.println("Student Found");
                System.out.println("-------------");
                System.out.println("Student ID: " + resultSet.getString("student_id"));
                System.out.println("Name      : " + resultSet.getString("name"));
                System.out.println("Course    : " + resultSet.getString("course"));
                System.out.printf("Mark      : %.2f%n", resultSet.getDouble("mark"));
                System.out.println("Grade     : " + getGrade(resultSet.getDouble("mark")));
            } else {
                System.out.println("Student not found.");
            }

            resultSet.close();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void updateStudentMark(Scanner input) {
        String sql = """
                UPDATE students
                SET mark = ?
                WHERE student_id = ?;
                """;

        System.out.print("Enter student ID: ");
        String studentId = input.nextLine().trim();

        System.out.print("Enter new mark: ");
        String markText = input.nextLine();

        try {
            double mark = Double.parseDouble(markText);

            if (mark < 0 || mark > 100) {
                System.out.println("Mark must be between 0 and 100.");
                return;
            }

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setDouble(1, mark);
                preparedStatement.setString(2, studentId);

                int rowsUpdated = preparedStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Student mark updated successfully.");
                } else {
                    System.out.println("Student not found.");
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid mark. Please enter a number.");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void deleteStudent(Scanner input) {
        String sql = """
                DELETE FROM students
                WHERE student_id = ?;
                """;

        System.out.print("Enter student ID to delete: ");
        String studentId = input.nextLine().trim();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, studentId);

            int rowsDeleted = preparedStatement.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Student not found.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static String getGrade(double mark) {
        if (mark >= 80) {
            return "A";
        } else if (mark >= 70) {
            return "B";
        } else if (mark >= 60) {
            return "C";
        } else if (mark >= 50) {
            return "D";
        } else {
            return "F";
        }
    }

    public static int readChoice(Scanner input) {
        try {
            System.out.print("Enter your choice: ");
            return Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        createTable();

        int choice;

        do {
            System.out.println();
            System.out.println("Student Database System");
            System.out.println("-----------------------");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Search Student");
            System.out.println("4. Update Student Mark");
            System.out.println("5. Delete Student");
            System.out.println("6. Exit");

            choice = readChoice(input);

            switch (choice) {
                case 1:
                    addStudent(input);
                    break;
                case 2:
                    viewStudents();
                    break;
                case 3:
                    searchStudent(input);
                    break;
                case 4:
                    updateStudentMark(input);
                    break;
                case 5:
                    deleteStudent(input);
                    break;
                case 6:
                    System.out.println("Program ended.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }

        } while (choice != 6);

        input.close();
    }
}
