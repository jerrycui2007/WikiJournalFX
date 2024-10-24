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
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

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
        // Open the article
        currentOpenArticle = new Article(path);

        // Clear existing content
        centerPanel.getChildren().clear();

        // Create main VBox to hold content and gallery
        VBox mainVBox = new VBox(10);

        // Create HBox to separate content and right panel
        HBox mainContainer = new HBox(10);

        // Left side: main content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox contentBox = new VBox(10);
        scrollPane.setContent(contentBox);

        // Title TextField
        TextField titleTextField = new TextField(currentOpenArticle.getTitle());
        titleTextField.setStyle("-fx-font-size: 48px; -fx-font-family: Arial;");
        contentBox.getChildren().add(titleTextField);

        // Paragraphs
        Map<String, HTMLEditor> paragraphEditors = new HashMap<>();
        for (Entry<String, String> paragraph : currentOpenArticle.getParagraphs().entrySet()) {
            TextField paragraphHeaderField = new TextField(paragraph.getKey());
            paragraphHeaderField.setStyle("-fx-font-size: 24px; -fx-font-family: Arial;");
            contentBox.getChildren().add(paragraphHeaderField);

            HTMLEditor paragraphHTMLEditor = new HTMLEditor();
            paragraphHTMLEditor.setHtmlText(paragraph.getValue());
            paragraphHTMLEditor.setPrefHeight(300);
            contentBox.getChildren().add(paragraphHTMLEditor);

            paragraphEditors.put(paragraph.getKey(), paragraphHTMLEditor);
        }

        // Right side: right panel
        VBox rightPanel = new VBox(10);
        rightPanel.setMinWidth(300);
        rightPanel.setMaxWidth(300);

        Label rightPanelLabel = new Label("Article Information");
        rightPanelLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        rightPanel.getChildren().add(rightPanelLabel);

        // Main Image
        ImageView mainImageView = new ImageView(new Image(currentOpenArticle.getMainImage(), 300, 200, true, true));
        rightPanel.getChildren().add(mainImageView);

        // Caption
        TextField captionField = new TextField(currentOpenArticle.getCaption());
        captionField.setStyle("-fx-font-size: 12px; -fx-font-family: Arial;");
        rightPanel.getChildren().add(captionField);

        // Infobox
        Map<String, TextField> infoboxFields = new HashMap<>();
        for (Map.Entry<String, String> row : currentOpenArticle.getInfobox().entrySet()) {
            HBox rowBox = new HBox(5);

            TextField keyField = new TextField(row.getKey());
            keyField.setPrefWidth(100);
            keyField.setStyle("-fx-font-size: 12px; -fx-font-family: Arial;");

            TextField valueField = new TextField(row.getValue());
            valueField.setPrefWidth(150);
            valueField.setStyle("-fx-font-size: 12px; -fx-font-family: Arial;");

            rowBox.getChildren().addAll(keyField, valueField);
            rightPanel.getChildren().add(rowBox);

            infoboxFields.put(row.getKey(), valueField);
        }

        // Export to HTML button
        Button exportButton = new Button("Export to HTML");
        exportButton.setStyle("-fx-font-size: 12px; -fx-font-family: Arial;");
        exportButton.setOnAction(event -> {
            currentOpenArticle.setTitle(titleTextField.getText());
            currentOpenArticle.setCaption(captionField.getText());

            Map<String, String> updatedParagraphs = new HashMap<>();
            for (Map.Entry<String, HTMLEditor> entry : paragraphEditors.entrySet()) {
                updatedParagraphs.put(entry.getKey(), entry.getValue().getHtmlText());
            }
            currentOpenArticle.setParagraphs(updatedParagraphs);

            Map<String, String> updatedInfobox = new HashMap<>();
            for (Map.Entry<String, TextField> entry : infoboxFields.entrySet()) {
                updatedInfobox.put(entry.getKey(), entry.getValue().getText());
            }
            currentOpenArticle.setInfobox(updatedInfobox);

            currentOpenArticle.convertToHTML();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Successful");
            alert.setHeaderText(null);
            alert.setContentText("Export successful");
            alert.showAndWait();
        });
        rightPanel.getChildren().add(exportButton);

        // Add content and right panel to main container
        mainContainer.getChildren().addAll(scrollPane, rightPanel);

        // Set the width of the scrollPane to fill the remaining space
        scrollPane.prefWidthProperty().bind(
                mainContainer.widthProperty().subtract(rightPanel.widthProperty()).subtract(10)
        );

        // Add the main container to the mainVBox
        mainVBox.getChildren().add(mainContainer);

        // Gallery
        ScrollPane galleryScrollPane = new ScrollPane();
        HBox galleryBox = new HBox(10);
        galleryScrollPane.setContent(galleryBox);
        galleryScrollPane.setFitToWidth(true);
        galleryScrollPane.setPrefHeight(150);

        // Add images to the gallery
        for (Map.Entry<String, String> entry : currentOpenArticle.getGallery().entrySet()) {
            VBox imageContainer = new VBox(5);
            ImageView imageView = new ImageView(new Image(entry.getKey(), 100, 100, true, true));
            TextField captionField2 = new TextField(entry.getValue());
            captionField2.setMaxWidth(100);
            imageContainer.getChildren().addAll(imageView, captionField2);
            galleryBox.getChildren().add(imageContainer);
        }

        // Add the gallery to the mainVBox
        mainVBox.getChildren().add(galleryScrollPane);

        // Add the mainVBox to the centerPanel
        centerPanel.getChildren().add(mainVBox);
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
