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
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.Optional;

import javafx.stage.FileChooser;

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

        // Gallery section
        Label galleryLabel = new Label("Gallery");
        galleryLabel.setStyle("-fx-font-size: 24px; -fx-font-family: Arial;");
        contentBox.getChildren().add(galleryLabel);

        // Initialize gallery box
        HBox galleryBox = new HBox(10);
        galleryBox.setStyle("-fx-padding: 10;");

        // Add images to the gallery
        Map<String, String> updatedGallery = new LinkedHashMap<>(currentOpenArticle.getGallery());

        for (Map.Entry<String, String> entry : currentOpenArticle.getGallery().entrySet()) {
            VBox imageContainer = new VBox(5);
            try {
                ImageView imageView = new ImageView(new Image("file:" + entry.getKey(), 100, 100, true, true));
                TextField imageCaptionField = new TextField(entry.getValue());
                imageCaptionField.setMaxWidth(100);
                imageContainer.getChildren().addAll(imageView, imageCaptionField);
                galleryBox.getChildren().add(imageContainer);
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to load image: " + entry.getKey());
                e.printStackTrace();
            }
        }

        // Add Image Button
        Button addImageButton = new Button("Add Image");
        addImageButton.setStyle("-fx-font-size: 12px; -fx-font-family: Arial;");
        addImageButton.setOnAction(event -> {
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            
            // Show file chooser dialog
            File selectedFile = fileChooser.showOpenDialog(addImageButton.getScene().getWindow());
            
            if (selectedFile != null) {
                // Create a dialog for caption input
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Add Image Caption");
                dialog.setHeaderText("Enter caption for the image");

                // Set the button types
                ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

                // Create the caption field
                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);
                grid.setPadding(new Insets(20, 150, 10, 10));

                TextField mediaCptionField = new TextField();
                grid.add(new Label("Caption:"), 0, 0);
                grid.add(mediaCptionField, 1, 0);

                dialog.getDialogPane().setContent(grid);

                // Convert the result to caption when the add button is clicked
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == addButtonType) {
                        return mediaCptionField.getText();
                    }
                    return null;
                });

                // Show the dialog and process the result
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(caption -> {
                    String imagePath = selectedFile.getAbsolutePath();
                    
                    // Add new image to the gallery
                    updatedGallery.put(imagePath, caption);
                    
                    // Create and add new image container
                    VBox imageContainer = new VBox(5);
                    ImageView imageView = new ImageView(new Image(imagePath, 100, 100, true, true));
                    TextField imageCaptionField = new TextField(caption);
                    imageCaptionField.setMaxWidth(100);
                    imageContainer.getChildren().addAll(imageView, imageCaptionField);
                    galleryBox.getChildren().add(imageContainer);

                    // Update the article's gallery
                    currentOpenArticle.setGallery(updatedGallery);
                });
            }
        });

        // Add the button to the gallery section
        VBox galleryControls = new VBox(10);
        galleryControls.getChildren().addAll(addImageButton, galleryBox);
        contentBox.getChildren().add(galleryControls);

        scrollPane.setContent(contentBox);

        // Right side: right panel
        VBox rightPanel = new VBox(10);
        rightPanel.setMinWidth(300);
        rightPanel.setMaxWidth(300);

        Label rightPanelLabel = new Label("Article Information");
        rightPanelLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        rightPanel.getChildren().add(rightPanelLabel);

        // Main Image
        ImageView mainImageView = new ImageView(new Image("file:" + currentOpenArticle.getMainImage(), 300, 200, true, true));
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

        // Save button
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-font-size: 12px; -fx-font-family: Arial;");

        // Export button
        Button exportButton = new Button("Export to HTML");
        exportButton.setStyle("-fx-font-size: 12px; -fx-font-family: Arial;");
        exportButton.setOnAction(event -> {
            currentOpenArticle.convertToHTML();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Export Successful");
            alert.setHeaderText(null);
            alert.setContentText("Export successful");
            alert.showAndWait();
        });

        // Save button action
        saveButton.setOnAction(event -> {
            // Update article with current values
            currentOpenArticle.setTitle(titleTextField.getText());
            currentOpenArticle.setCaption(captionField.getText());

            // Update paragraphs
            Map<String, String> updatedParagraphs = new HashMap<>();
            for (Map.Entry<String, HTMLEditor> entry : paragraphEditors.entrySet()) {
                updatedParagraphs.put(entry.getKey(), entry.getValue().getHtmlText());
            }
            currentOpenArticle.setParagraphs(updatedParagraphs);

            // Update infobox
            Map<String, String> updatedInfobox = new HashMap<>();
            for (Map.Entry<String, TextField> entry : infoboxFields.entrySet()) {
                updatedInfobox.put(entry.getKey(), entry.getValue().getText());
            }
            currentOpenArticle.setInfobox(updatedInfobox);

            // Update gallery captions
            Map<String, String> updatedGalleryWithCaptions = new LinkedHashMap<>();
            for (int i = 0; i < galleryBox.getChildren().size(); i++) {
                VBox imageContainer = (VBox) galleryBox.getChildren().get(i);
                TextField galleryCaptionField = (TextField) imageContainer.getChildren().get(1);
                String imagePath = updatedGallery.keySet().toArray(new String[0])[i];
                updatedGalleryWithCaptions.put(imagePath, galleryCaptionField.getText());
            }
            currentOpenArticle.setGallery(updatedGalleryWithCaptions);

            // Save to JSON file
            currentOpenArticle.saveToJSON(path);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Save Successful");
            alert.setHeaderText(null);
            alert.setContentText("Article saved successfully");
            alert.showAndWait();
        });

        // Add both buttons in an HBox
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(saveButton, exportButton);
        rightPanel.getChildren().add(buttonBox);

        // Add content and right panel to main container
        mainContainer.getChildren().addAll(scrollPane, rightPanel);

        // Set the width of the scrollPane to fill the remaining space
        scrollPane.prefWidthProperty().bind(
                mainContainer.widthProperty().subtract(rightPanel.widthProperty()).subtract(10)
        );

        // Add the main container to the mainVBox
        mainVBox.getChildren().add(mainContainer);

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
