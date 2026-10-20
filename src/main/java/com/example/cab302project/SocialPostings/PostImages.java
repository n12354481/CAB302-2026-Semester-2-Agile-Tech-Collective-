package com.example.cab302project.SocialPostings;

import javafx.scene.layout.Region;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
public class PostImages {

    private static final Path IMAGE_FOLDER = Path.of("post-images");

    private PostImages() {}

    public static String saveImage(File source) throws IOException {
        Files.createDirectories(IMAGE_FOLDER);

        String name = source.getName();
        int dot = name.lastIndexOf('.');
        String extension = dot >= 0 ? name.substring(dot) : "";

        Path target = IMAGE_FOLDER.resolve(UUID.randomUUID() + extension);
        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }

    public static void showImage(Region region, String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return;
        }

        String url;
        if (imagePath.startsWith("/")) {
            java.net.URL resource = PostImages.class.getResource(imagePath);
            if (resource == null) {
                return;
            }
            url = resource.toExternalForm();
        } else {
            File file = new File(imagePath);
            if (!file.exists()) {
                return;
            }
            url = file.toURI().toString();
        }

        url = url.replace("'", "%27");
        region.setStyle("-fx-background-image: url('" + url + "'); "
                + "-fx-background-size: cover; "
                + "-fx-background-position: center; "
                + "-fx-background-repeat: no-repeat;");
    }
}
