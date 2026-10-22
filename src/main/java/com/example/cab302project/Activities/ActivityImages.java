package com.example.cab302project.Activities;

import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

public class ActivityImages {
    private static final String RESOURCE_FOLDER = "/com/example/cab302project/activity-images/";

    private static final Path UPLOAD_FOLDER = Paths.get(System.getProperty("user.home"), "CAB302-activity-images");

    private static final Map<String, String> DEFAULT_IMAGES = Map.ofEntries(
            Map.entry("Swimming", "swimming.jpg"),
            Map.entry("Jogging", "jogging.jpg"),
            Map.entry("Tennis", "tennis.jpg"),
            Map.entry("Study with a friend", "study-with-friend.jpg"),
            Map.entry("Attend a workshop", "workshop.jpg"),
            Map.entry("Join a STEM society event", "stem-event.jpg"),
            Map.entry("Meditation", "meditation.jpg"),
            Map.entry("Review lecture notes", "review-notes.jpg"),
            Map.entry("Coding Practice", "coding-practice.jpg"),
            Map.entry("Reading", "reading.jpg"),
            Map.entry("Gym workout", "gym-workout.jpg"),
            Map.entry("Go for a walk", "walking.jpg"),
            Map.entry("University club event", "uni-club.jpg"),
            Map.entry("Lunch with a friend", "lunch-with-friend.jpg")
    );

    private ActivityImages() {
    }

    public static Image load(Activity activity) {
        if (activity == null){
            return null;
        }

        String imageFile = activity.getImageFile();
        System.out.println("Activity: " + activity.getName());
        System.out.println("Database image: " + imageFile);

        if (imageFile == null || imageFile.isBlank()) {
            imageFile = DEFAULT_IMAGES.get(activity.getName());
        }

        System.out.println("Loading image: " + imageFile);
        return load(imageFile);
    }

    public static Image load(String imageFile) {
        if (imageFile == null || imageFile.isBlank()) {
            return null;
        }

        try {
            Path uploadedFile =
                    UPLOAD_FOLDER.resolve(imageFile).normalize();

            if (uploadedFile.startsWith(UPLOAD_FOLDER) && Files.isRegularFile(uploadedFile)) {
                try (InputStream input =  Files.newInputStream(uploadedFile)) {
                    Image image = new Image(input);
                    return image.isError() ? null : image;
                }
            }

            String filename = Paths.get(imageFile).getFileName().toString();
            try (InputStream input = ActivityImages.class.getResourceAsStream(RESOURCE_FOLDER + filename)) {
                if (input == null) {
                    System.out.println(
                            "IMAGE NOT FOUND: " + RESOURCE_FOLDER + filename);
                    return null;
                }
                Image image = new Image(input);
                if (image.isError()) {
                    System.out.println("IMAGE ERROR: " + filename);
                    return null;
                }

                System.out.println("IMAGE LOADED: " + filename);
                return image;
            }
        } catch (IOException | RuntimeException e) {
            System.err.println("Could not load activity image: " + imageFile);
            return null;
        }
    }

    public static String saveUpload(File selectedFile)
        throws IOException {
        if (selectedFile == null) {
            return null;
        }

        String originalName = selectedFile.getName().toLowerCase();
        String extension;

        if (originalName.endsWith(".png")) {
            extension = "png";
        } else if (originalName.endsWith(".jpg")) {
            extension = "jpg";
        } else if (originalName.endsWith(".jpeg")) {
            extension = "jpeg";
        } else {
            throw new IOException("Please select a PNG or JPG image.");
        }

        Files.createDirectories(UPLOAD_FOLDER);
        String savedName = UUID.randomUUID() + extension;

        Files.copy(
                selectedFile.toPath(),
                UPLOAD_FOLDER.resolve(savedName),
                StandardCopyOption.COPY_ATTRIBUTES);
        return savedName;
    }
}
