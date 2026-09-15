import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Checks that the SQLite JDBC driver is on the classpath.
 * <p>
 * DatabaseConnection builds a "jdbc:sqlite:" connection, but the project had no
 * driver dependency, so that call failed at runtime with "No suitable driver".
 * This test fails if the org.xerial:sqlite-jdbc dependency is ever dropped again.
 * <p>
 * Uses an in-memory database so no .db file is written during the build.
 */
class SqliteDriverTest {

    @Test
    void sqliteDriverIsAvailable() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            assertNotNull(connection, "Expected a connection from the SQLite driver");
            assertFalse(connection.isClosed(), "Expected the connection to be open");
        }
    }
}
