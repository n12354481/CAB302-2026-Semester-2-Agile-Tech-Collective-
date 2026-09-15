package Database;

import MoodForm.CheckIn;
import MoodForm.ICheckInDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class DatabaseCheckInDAO implements ICheckInDAO {

    private final Connection connection;

    public DatabaseCheckInDAO() {
        connection = DatabaseConnection.getInstance();
    }

    @Override
    public boolean saveCheckIn(CheckIn checkIn) {

        String query =
                "INSERT INTO checkin "
                        + "(userID, checkin_date, emotion_today, sleep, water, study_stress) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            connection.setAutoCommit(false);

            int checkinID;

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 query,
                                 Statement.RETURN_GENERATED_KEYS
                         )) {

                statement.setInt(
                        1,
                        checkIn.getUserID()
                );

                statement.setString(
                        2,
                        checkIn.getCheckinDate().toString()
                );

                statement.setInt(
                        3,
                        checkIn.getEmotionToday()
                );

                statement.setInt(
                        4,
                        checkIn.getSleep()
                );

                statement.setInt(
                        5,
                        checkIn.getWater()
                );

                statement.setInt(
                        6,
                        checkIn.getStudyStress()
                );

                statement.executeUpdate();

                try (ResultSet generatedKeys =
                             statement.getGeneratedKeys()) {

                    if (!generatedKeys.next()) {
                        connection.rollback();
                        return false;
                    }

                    checkinID =
                            generatedKeys.getInt(1);
                }
            }

            checkIn.setCheckinID(checkinID);

            for (String moodName : checkIn.getMoods()) {

                int moodID =
                        getOrCreateMood(moodName);

                linkMoodToCheckIn(
                        checkinID,
                        moodID
                );
            }

            connection.commit();

            return true;

        } catch (SQLException e) {

            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            System.err.println(
                    "Unable to save check-in: "
                            + e.getMessage()
            );

            return false;

        } finally {

            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int getOrCreateMood(
            String moodName
    ) throws SQLException {

        String findQuery =
                "SELECT moodID "
                        + "FROM mood "
                        + "WHERE mood_name = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(findQuery)) {

            statement.setString(
                    1,
                    moodName
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                if (result.next()) {
                    return result.getInt(
                            "moodID"
                    );
                }
            }
        }

        String insertQuery =
                "INSERT INTO mood (mood_name) "
                        + "VALUES (?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             insertQuery,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(
                    1,
                    moodName
            );

            statement.executeUpdate();

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException(
                "Unable to create mood: "
                        + moodName
        );
    }

    private void linkMoodToCheckIn(
            int checkinID,
            int moodID
    ) throws SQLException {

        String query =
                "INSERT INTO checkin_mood "
                        + "(checkinID, moodID) "
                        + "VALUES (?, ?)";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setInt(
                    1,
                    checkinID
            );

            statement.setInt(
                    2,
                    moodID
            );

            statement.executeUpdate();
        }
    }

    @Override
    public List<CheckIn> getCheckInsForUser(
            int userID
    ) {

        List<CheckIn> checkIns =
                new ArrayList<>();

        String query =
                "SELECT * "
                        + "FROM checkin "
                        + "WHERE userID = ? "
                        + "ORDER BY checkin_date ASC, checkinID ASC";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setInt(
                    1,
                    userID
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    checkIns.add(
                            createCheckInFromResult(
                                    result
                            )
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Unable to retrieve check-ins: "
                            + e.getMessage()
            );
        }

        return checkIns;
    }

    @Override
    public List<CheckIn> getCheckInsForDate(
            int userID,
            LocalDate date
    ) {

        List<CheckIn> checkIns =
                new ArrayList<>();

        String query =
                "SELECT * "
                        + "FROM checkin "
                        + "WHERE userID = ? "
                        + "AND checkin_date = ? "
                        + "ORDER BY checkinID ASC";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setInt(
                    1,
                    userID
            );

            statement.setString(
                    2,
                    date.toString()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    checkIns.add(
                            createCheckInFromResult(
                                    result
                            )
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Unable to retrieve check-ins for date: "
                            + e.getMessage()
            );
        }

        return checkIns;
    }

    @Override
    public List<CheckIn> getCheckInsBetweenDates(
            int userID,
            LocalDate startDate,
            LocalDate endDate
    ) {

        List<CheckIn> checkIns =
                new ArrayList<>();

        String query =
                "SELECT * "
                        + "FROM checkin "
                        + "WHERE userID = ? "
                        + "AND checkin_date BETWEEN ? AND ? "
                        + "ORDER BY checkin_date ASC, checkinID ASC";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setInt(
                    1,
                    userID
            );

            statement.setString(
                    2,
                    startDate.toString()
            );

            statement.setString(
                    3,
                    endDate.toString()
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    checkIns.add(
                            createCheckInFromResult(
                                    result
                            )
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Unable to retrieve check-ins between dates: "
                            + e.getMessage()
            );
        }

        return checkIns;
    }

    private CheckIn createCheckInFromResult(
            ResultSet result
    ) throws SQLException {

        int checkinID =
                result.getInt("checkinID");

        List<String> moods =
                getMoodsForCheckIn(checkinID);

        return new CheckIn(
                checkinID,
                result.getInt("userID"),
                LocalDate.parse(
                        result.getString(
                                "checkin_date"
                        )
                ),
                result.getInt(
                        "emotion_today"
                ),
                result.getInt(
                        "sleep"
                ),
                result.getInt(
                        "water"
                ),
                result.getInt(
                        "study_stress"
                ),
                moods
        );
    }

    private List<String> getMoodsForCheckIn(
            int checkinID
    ) {

        List<String> moods =
                new ArrayList<>();

        String query =
                "SELECT mood.mood_name "
                        + "FROM mood "
                        + "JOIN checkin_mood "
                        + "ON mood.moodID = checkin_mood.moodID "
                        + "WHERE checkin_mood.checkinID = ?";

        try (PreparedStatement statement =
                     connection.prepareStatement(query)) {

            statement.setInt(
                    1,
                    checkinID
            );

            try (ResultSet result =
                         statement.executeQuery()) {

                while (result.next()) {

                    moods.add(
                            result.getString(
                                    "mood_name"
                            )
                    );
                }
            }

        } catch (SQLException e) {

            System.err.println(
                    "Unable to retrieve moods: "
                            + e.getMessage()
            );
        }

        return moods;
    }
}