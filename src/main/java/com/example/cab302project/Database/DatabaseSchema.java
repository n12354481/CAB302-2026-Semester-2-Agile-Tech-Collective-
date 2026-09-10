package com.example.cab302project.Database;

import com.example.cab302project.Settings.ISettingsDAO;
import com.example.cab302project.Settings.SettingsModel;

import java.sql.*;

/**
 * The shared tables, from the team's schema document.
 *
 * <p>SQLite has no date type, so dates are ISO-8601 text (YYYY-MM-DD) and times are HH:MM.
 * Foreign keys are off by default and are per-connection, so they are enabled here.
 */
public final class DatabaseSchema {

    private DatabaseSchema() {
    }

    private static final String[] TABLES = {

            "CREATE TABLE IF NOT EXISTS users ("
                    + "userID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "email TEXT NOT NULL UNIQUE, "
                    + "username TEXT NOT NULL UNIQUE, "
                    + "password TEXT NOT NULL)",

            // Column names match DatabaseSettingsDAO.
            "CREATE TABLE IF NOT EXISTS settings ("
                    + "user_id INTEGER PRIMARY KEY, "
                    + "community_participation INTEGER NOT NULL DEFAULT 0, "
                    + "activity_data_participation INTEGER NOT NULL DEFAULT 0, "
                    + "checkin_data_participation INTEGER NOT NULL DEFAULT 0, "
                    + "ai_personalisation INTEGER NOT NULL DEFAULT 0, "
                    + "ai_activity_personalisation INTEGER NOT NULL DEFAULT 0, "
                    + "ai_checkin_personalisation INTEGER NOT NULL DEFAULT 0, "
                    + "FOREIGN KEY (user_id) REFERENCES users (userID) ON DELETE CASCADE)",

            // Catalogue of activities, not a record of time spent -- see activity_log.
            "CREATE TABLE IF NOT EXISTS activity ("
                    + "activityID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "activity_name TEXT NOT NULL, "
                    + "category TEXT NOT NULL, "
                    + "activity_description TEXT, "
                    + "goal INTEGER, "
                    + "points INTEGER NOT NULL DEFAULT 0)",

            // One row per logged instance. Not in the schema doc -- see KNOWN-ISSUES.
            "CREATE TABLE IF NOT EXISTS activity_log ("
                    + "logID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "userID INTEGER NOT NULL, "
                    + "activityID INTEGER NOT NULL, "
                    + "log_date TEXT NOT NULL, "
                    + "minutes INTEGER NOT NULL, "
                    + "FOREIGN KEY (userID) REFERENCES users (userID) ON DELETE CASCADE, "
                    + "FOREIGN KEY (activityID) REFERENCES activity (activityID))",

            "CREATE TABLE IF NOT EXISTS checkin ("
                    + "checkinID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "userID INTEGER NOT NULL, "
                    + "checkin_date TEXT NOT NULL, "
                    + "emotion_today INTEGER, "
                    + "sleep INTEGER, "
                    + "water INTEGER, "
                    + "study_stress INTEGER, "
                    + "UNIQUE (userID, checkin_date), "
                    + "FOREIGN KEY (userID) REFERENCES users (userID) ON DELETE CASCADE)",

            "CREATE TABLE IF NOT EXISTS mood ("
                    + "moodID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "mood_name TEXT NOT NULL UNIQUE)",

            "CREATE TABLE IF NOT EXISTS checkin_mood ("
                    + "checkinID INTEGER NOT NULL, "
                    + "moodID INTEGER NOT NULL, "
                    + "PRIMARY KEY (checkinID, moodID), "
                    + "FOREIGN KEY (checkinID) REFERENCES checkin (checkinID) ON DELETE CASCADE, "
                    + "FOREIGN KEY (moodID) REFERENCES mood (moodID))",

            "CREATE TABLE IF NOT EXISTS post ("
                    + "postID INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "userID INTEGER NOT NULL, "
                    + "title TEXT NOT NULL, "
                    + "description TEXT, "
                    + "content TEXT, "
                    + "image TEXT, "
                    + "event_date TEXT, "
                    + "start_time TEXT, "
                    + "end_time TEXT, "
                    + "event_location TEXT, "
                    + "FOREIGN KEY (userID) REFERENCES users (userID) ON DELETE CASCADE)"
    };

    /** Creates the shared tables if they are not already there. Safe to call on startup. */
    public static void createAll(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            for (String ddl : TABLES) {
                statement.executeUpdate(ddl);
            }
        }
    }
}
