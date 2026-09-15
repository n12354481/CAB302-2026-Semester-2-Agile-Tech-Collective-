package SocialPostings;

/**
 * represents a single row from the "post" table
 * matches all the columns defined in the databaseschema
 */

public record SocialPostings(
    int postId,
    int userId,
    String title,
    String description,
    String content,
    String image,
    String eventDate,
    String startTime,
    String endTime,
    String eventLocation
    ) {
}
