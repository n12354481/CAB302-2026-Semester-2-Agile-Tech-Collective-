package com.example.cab302project.Rewards;

/**
 * One thing logged per day (for now), as the selected day card lists the info.
 *
 * @param name    the activity name, or the rest entry's label
 * @param kind    the activity category (MOVEMENT, STUDY, SOCIAL) or the rest kind (SLEEP, DELIBERATE REST)
 * @param minutes how long it lasted
 */
public record DayEntry(String name, String kind, int minutes) {
}
