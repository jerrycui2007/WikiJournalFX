package com.wikijournalfx.wikijournalfx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class Controller {

    public VBox rightPanel;
    public VBox leftPanel;
    @FXML
    private VBox dateArticlesContainer;

    @FXML
    private VBox peopleArticlesContainer;

    @FXML
    private VBox otherArticlesContainer;

    @FXML
    private VBox centerPanel;

    private Article currentOpenArticle;

    // Initialize method to dynamically load articles into the containers
    @FXML
    public void initialize() {
        // Load date articles
        ArrayList<File> dateArticles = getDateFiles(getJSONFiles("Articles/Dates"));
        for (File article : dateArticles) {
            Label articleLabel = new Label(insertSlashes(article.getName()));
            articleLabel.setStyle("-fx-font-size: 12px;");
            articleLabel.setOnMouseClicked(event -> {
                try {
                    editorScreen(article.getPath());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            dateArticlesContainer.getChildren().add(articleLabel);
        }

        // Load people articles
        ArrayList<File> peopleArticles = getJSONFiles("Articles/People");
        for (File article : peopleArticles) {
            Label articleLabel = new Label(article.getName().replace(".json", ""));
            articleLabel.setStyle("-fx-font-size: 12px;");
            peopleArticlesContainer.getChildren().add(articleLabel);
        }

        // Load other articles
        ArrayList<File> otherArticles = getJSONFiles("Articles/Other");
        for (File article : otherArticles) {
            Label articleLabel = new Label(article.getName().replace(".json", ""));
            articleLabel.setStyle("-fx-font-size: 12px;");
            otherArticlesContainer.getChildren().add(articleLabel);
        }
    }

    // Handle button actions, currently placeholder methods
    @FXML
    private void handleNewDateArticle() {
        System.out.println("Create new date article");
    }

    @FXML
    private void handleNewPersonArticle() {
        System.out.println("Create new person article");
    }

    @FXML
    private void handleNewOtherArticle() {
        System.out.println("Create new other article");
    }

    /**
     * Given an <code>ArrayList</code> of .json files, filter out the files whose names do not match the format for a
     * date article.
     * Proper format: monthdayyear, no spaces or slashes, two digits for month and day, four digits for year
     * Example: 02132007 for February 13th 2007
     *
     * @param jsonFiles <code>ArrayList</code> containing the .json files
     * @return          <code>ArrayList</code> of only the proper date files
     */
    public static ArrayList<File> getDateFiles(ArrayList<File> jsonFiles) {
        ArrayList<File> dateJsonFiles = new ArrayList<File>();

        for (File file : jsonFiles) {
            if (file.getName().matches("^\\d{8}\\.json$")) {  // regular expression matches 8 digits + .json
                dateJsonFiles.add(file);
            }
        }

        return dateJsonFiles;
    }

    /**
     * Returns an <code>ArrayList</code> of <code>File</code> objects, each of which is a .JSON file in the given
     * directory.
     *
     * @param directoryPath path of the directory to search through
     * @return              <code>ArrayList</code> of the <code>File</code> objects of the .JSON files
     */
    public static ArrayList<File> getJSONFiles(String directoryPath) {
        ArrayList<File> jsonFiles = new ArrayList<File>();

        File directory = new File(directoryPath);

        // Check if the given path exists and is a directory
        if (directory.exists() && directory.isDirectory()) {
            // filter for only .json files
            File[] files = directory.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".json");
                }
            });

            // Add the files to the ArrayList
            if (files != null) {
                jsonFiles.addAll(Arrays.asList(files));
            }
        }

        return jsonFiles;
    }

    private void editorScreen(String path) throws IOException {
        // Open the editor screen
        currentOpenArticle = new Article(path);

        // Create a header label with the article title
        Label titleLabel = new Label(currentOpenArticle.getTitle());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Clear existing content
        centerPanel.getChildren().clear();

        // Create a ScrollPane to wrap the content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox contentBox = new VBox(10);
        scrollPane.setContent(contentBox);

        // Title TextField
        TextField titleTextField = new TextField(currentOpenArticle.getTitle());
        titleTextField.setStyle("-fx-font-size: 48px; -fx-font-family: Arial;");
        contentBox.getChildren().add(titleTextField);

        // Paragraphs
        for (Entry<String, String> paragraph : currentOpenArticle.getParagraphs().entrySet()) {
            TextField paragraphHeaderField = new TextField(paragraph.getKey());
            paragraphHeaderField.setStyle("-fx-font-size: 24px; -fx-font-family: Arial;");
            contentBox.getChildren().add(paragraphHeaderField);

            HTMLEditor paragraphHTMLEditor = new HTMLEditor();
            paragraphHTMLEditor.setHtmlText(paragraph.getValue());
            paragraphHTMLEditor.setPrefHeight(300);  // Adjust the value as needed
            contentBox.getChildren().add(paragraphHTMLEditor);  // Add this line
        }

        // Gallery
        Label galleryHeaderLabel = new Label("Gallery");
        galleryHeaderLabel.setStyle("-fx-font-size: 24px; -fx-font-family: Arial;");
        contentBox.getChildren().add(galleryHeaderLabel);

        for (Map.Entry<String, String> media : currentOpenArticle.getGallery().entrySet()) {
            try {
                String imagePath = getClass().getResource(media.getKey()).toExternalForm();
                Image image = new Image(imagePath, 300, 200, true, true);
                ImageView imageView = new ImageView(image);
                contentBox.getChildren().add(imageView);

                TextField captionField = new TextField(media.getValue());
                captionField.setStyle("-fx-font-size: 12px; -fx-font-family: Arial;");
                contentBox.getChildren().add(captionField);
            } catch (Exception e) {
                System.err.println("Error loading image: " + media.getKey());
                e.printStackTrace();
            }
        }

        // Add Image Button
        Button addImageButton = new Button("Add image");
        addImageButton.setStyle("-fx-font-size: 12px; -fx-font-family: Arial;");
        addImageButton.setMaxWidth(Double.MAX_VALUE);
        contentBox.getChildren().add(addImageButton);

        // Add the ScrollPane to the centerPanel
        centerPanel.getChildren().add(scrollPane);
    }

    /**
     * Insert slashes into a numerical date
     * Example: 02132007 --> 02/13/2007
     * @param date date
     * @return     date with slashes
     */
    public static String insertSlashes(String date) {
        return date.substring(0, 2) + "/" + date.substring(2, 4) + "/" + date.substring(4, 8);
    }
}
