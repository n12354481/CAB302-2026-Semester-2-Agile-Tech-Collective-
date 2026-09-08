package com.example.cab302project.Rewards;

import com.example.cab302project.Database.DatabaseSchema;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Test: the DDL runs, running it twice ensures its safe.
 * Uses an in memory database so no .db file is written during the build.
 */
class GardenSchemaTest {

    @Test
    void schemaCreates() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            DatabaseSchema.createAll(connection);
            GardenSchema.create(connection);
            GardenSchema.create(connection);
        }
    }
}
