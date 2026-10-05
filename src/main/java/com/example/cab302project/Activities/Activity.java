package com.example.cab302project.Activities;

/**
 * represents activity that can be viewed and selected by user
 */
public class Activity {
   private int id;
   private final String name;
   private final String category;
   private final String description;
   private final int goal;

   /**
    * creates an activity that already has a database id
    *
    * @param id activity id from database
    * @param name name of activity
    * @param category category the activity belongs to
    * @param description description shown in activity details
    * @param goal suggested activity duration
    */
   public Activity(int id, String name, String category, String description, int goal) {
      this(name, category, description, goal);
      this.id = id;
   }

   // returns stored information for activity
   public Activity(String name, String category, String description, int goal) {
      this.name = name;
      this.category = category;
      this.description = description;
      this.goal = goal;
   }

   public int getId() { return id; }
   public String getName() { return name; }
   public String getCategory() { return category; }
   public String getDescription() { return description; }
   public int getGoal() { return goal; }
}
