package com.example.cab302project;

import com.example.cab302project.Activities.Activity;
import com.example.cab302project.Database.DatabaseActivityDAO;
import com.example.cab302project.Database.DatabaseConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

public class ActivityDaoTest {
    boolean autoCommit  = true;

    DatabaseActivityDAO activityDAO = new DatabaseActivityDAO();
    Connection connection = DatabaseConnection.getInstance();

    Activity testRec = new Activity("Test Activity 99", "Fitness", "Record for testing", 10, "");

    @BeforeEach
    public void setUp() throws SQLException {
        try {
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        try{
            connection.rollback();
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInsert() {
        int id = activityDAO.insert(testRec);
        assert id > 0;
    }

    @Test void testFindByName() {
        int id = activityDAO.insert(testRec);
        assert id > 0;
        Activity found = activityDAO.findByName(testRec.getName());
        assert  checkEqual(testRec, found);
    }

    public boolean checkEqual(Activity ac1, Activity ac2) {
        return (ac1.getName().equals(ac2.getName())
                && ac1.getCategory().equals(ac2.getCategory())
                && ac1.getDescription().equals(ac2.getDescription())
                && ac1.getGoal() == ac2.getGoal()
                && ac1.getImageFile().equals(ac2.getImageFile())
        );
    }
}
