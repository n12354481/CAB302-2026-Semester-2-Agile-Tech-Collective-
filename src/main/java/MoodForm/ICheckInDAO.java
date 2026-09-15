package MoodForm;

import java.time.LocalDate;
import java.util.List;

public interface ICheckInDAO {

    boolean saveCheckIn(CheckIn checkIn);

    List<CheckIn> getCheckInsForUser(int userID);

    List<CheckIn> getCheckInsForDate(
            int userID,
            LocalDate date
    );

    List<CheckIn> getCheckInsBetweenDates(
            int userID,
            LocalDate startDate,
            LocalDate endDate
    );
}