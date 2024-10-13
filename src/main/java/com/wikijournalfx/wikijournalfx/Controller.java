package com.wikijournalfx.wikijournalfx;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    @FXML
    private VBox dateArticlesContainer;

    @FXML
    private VBox peopleArticlesContainer;

    @FXML
    private VBox otherArticlesContainer;

    // Initialize method to dynamically load articles into the containers
    @FXML
    public void initialize() {
        // Load date articles
        ArrayList<File> dateArticles = getDateFiles(getJSONFiles("Articles/Dates"));
        for (File article : dateArticles) {
            Label articleLabel = new Label(insertSlashes(article.getName()));
            articleLabel.setStyle("-fx-font-size: 12px;");
            articleLabel.setOnMouseClicked(event -> editorScreen(article.getPath()));
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

    private void editorScreen(String path) {
        // Open the editor screen
        System.out.println("Opening article: " + path);
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
