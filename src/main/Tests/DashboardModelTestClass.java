//import com.example.cab302project.Dashboard.DashboardModel;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//
//public class DashboardModelTestClass {
//    @BeforeEach
//    public void setUp() {
//
//    }
//    //1) Generate community wellbeing statistic: Correct community statistic is calculated
//    @Test
//    public  void WeeklyActivities() {
//        //Given user completed 3 activities
//        //When
//        int result = DashboardModel.getWeeklyActivitiesCompleted();
//        //Then
//        assertEquals(3, result);
//        //User: John Smith
//        //Activities: Monday, tuesday, wednes
//        //Result: 3
//    }
//    //1) Generate community wellbeing statistic: Correct community statistic is calculated
//    @Test
//    public  void WellbeingStatistics() {
//
//    }
//
//    //2) Community statistic excludes opted-out users: Users who disabled contribution aren't included
//    @Test
//    public  void CommunityStatsExcludeOptedOutUsers() {
//
//    }
///*
//Dashboard
////Have minimal tests ig
//    1) Calculate weekly activities completed: Returns correct no. of activities completed
//    2) Calculate weekly activity target/rpocess: Returns correct progress based on completed vs target activities
//    3) Calculate overall wellbeing progress: Correct percentage is calculated from the user's relevant data
//    4) Calculate reward count: Returns correct number of rewards earned
//    5) Dashboard statistics with no activity: Returns 0
//    6) Dashboard statistics update after activity completion: Statistics increaqse properly
//
//
//    7) Generate community wellbeing statistic: Correct community statistic is calculated
//    8) Community statistic excludes opted-out users: Users who disabled contribution aren't included
//    9) Generate community message from statistic: Appropriate message is returned based on community data
//
//
//    10) Generate personalised AI recommendations: Recommendations are based on user's data
//    11) Recommendation limit:  No more than 4 recommendations are returned.
//    12) Recommendations with insufficient data: Generic recommendations are returned
//    13) Different user data produces different recommendations: Recommendations respond to changes in user statistics
//*/
//}