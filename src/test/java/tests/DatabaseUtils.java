package tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {

    // H2 in-memory database URL.
    // "mem:testdb" means database memory mein create hoga.
    // DB_CLOSE_DELAY=-1 means connection close hone ke baad bhi
    // database JVM ke end tak available rahega.
    private static final String URL =
            "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

    // H2 ka default username
    private static final String USER = "sa";

    // H2 ka default password yahan blank hai
    private static final String PASSWORD = "";


    // ---------------------------------------------------------
    // Method 1: Database connection create karna
    // ---------------------------------------------------------

    public static Connection getConnection() throws SQLException {

        // DriverManager database ke saath JDBC connection establish karta hai
        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }


    // ---------------------------------------------------------
    // Method 2: Test database aur test data create karna
    // ---------------------------------------------------------

    public static void setupDatabase() throws SQLException {

        // try-with-resources:
        // Connection aur Statement automatically close ho jayenge.
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Orders table create kar rahe hain
            statement.execute("""
                    CREATE TABLE orders (
                        id VARCHAR(50),
                        customer_id INT,
                        amount DECIMAL(10,2),
                        status VARCHAR(20)
                    )
                    """);

            // Test purpose ke liye ek order database mein insert kar rahe hain
            statement.execute("""
                    INSERT INTO orders
                    VALUES ('1001', 101, 1499.00, 'CREATED')
                    """);
        }
    }
}