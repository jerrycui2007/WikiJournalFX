package com.wikijournalfx.wikijournalfx;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.LinkedHashMap;
/**
 * Class for each article
 * Handles reading the article from .json, writing to html, etc.
 *
 * @author Jerry Cui
 * @version %I%, %G%
 * @since 1.0
 */
public class Article {
    private String type;
    private String title;
    private Map<String, String> paragraphs; // header -> content
    private Map<String, String> gallery = new LinkedHashMap<>();    // image path -> caption
    private String mainImage;
    private String caption;
    private Map<String, String> infobox;    // row title -> caption

    /**
     * Constructor that takes in the file path and reads the data
     *
     * @param jsonFilePath path of .json file for article
     */
    public Article(String jsonFilePath) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(jsonFilePath)) {
            // Parse JSON file into JournalEntry object
            Article article = gson.fromJson(reader, Article.class);
            this.type = article.type;
            this.title = article.title;
            this.paragraphs = article.paragraphs;
            this.gallery = article.gallery;
            this.mainImage = article.mainImage;
            this.caption = article.caption;
            this.infobox = article.infobox;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates/overwrites an HTML file in the directory
     */
    public void convertToHTML() {
        try {
            // Create file to write to
            PrintWriter printWriter = new PrintWriter(title + ".html");

            printWriter.println("<!DOCTYPE html>");
            printWriter.println("<html>");
            printWriter.println("<head><title>" + title + "</title></head>");

            printWriter.println("<body>");
            printWriter.println("<h1>" + title + "</h1>");
            printWriter.println("<img src=\"" + this.mainImage + "\" alt=\"Main Image\" width=\"400\" height=\"auto\">");
            printWriter.println("<figcaption>" + caption + "</figcaption>");

            printWriter.println("<h2>Info</h2>");
            printWriter.println("<table>");
            for (Map.Entry<String, String> row : infobox.entrySet()) {
                printWriter.println("<tr>");
                printWriter.println("<th>" + row.getKey() + "</th>");
                printWriter.println("<td><span>" + row.getValue() + "</span><br></td>");
                printWriter.println("</tr>");
            }
            printWriter.println("</table>");

            for (Map.Entry<String, String> paragraph : paragraphs.entrySet()) {
                printWriter.println("<h2>" + paragraph.getKey() + "</h2>");
                printWriter.println("<p>" + paragraph.getValue() + "</p>");
            }

            printWriter.println("<h2>Gallery</h2>");
            for (Map.Entry<String, String> image : gallery.entrySet()) {
                printWriter.println("<div class=\"image\">");
                printWriter.println("<img src=\"" + image.getKey().replace("file:", "") + "\">");
                printWriter.println("<figcaption>" + image.getValue() + "</figcaption>");
                printWriter.println("</div>");
            }

            printWriter.println("</body>");
            printWriter.println("</html>");

            printWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current state to JSON
     *
     * @param filePath path to save the JSON file
     */
    public void saveToJSON(String filePath) {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter and setter methods

    /**
     * Gets the type of the article.
     *
     * @return the type of the article
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the article.
     *
     * @param type the type of the article
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the title of the article.
     *
     * @return the title of the article
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the article.
     *
     * @param title the title of the article
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the paragraphs of the article.
     *
     * @return a map containing the paragraphs (header -> content)
     */
    public Map<String, String> getParagraphs() {
        return paragraphs;
    }

    /**
     * Sets the paragraphs of the article.
     *
     * @param paragraphs a map containing the paragraphs (header -> content)
     */
    public void setParagraphs(Map<String, String> paragraphs) {
        this.paragraphs = paragraphs;
    }

    /**
     * Gets the gallery of the article.
     *
     * @return a map containing the gallery (image path -> caption)
     */
    public Map<String, String> getGallery() {
        return gallery;
    }

    /**
     * Sets the gallery of the article.
     *
     * @param gallery a map containing the gallery (image path -> caption)
     */
    public void setGallery(Map<String, String> gallery) {
        this.gallery = gallery;
    }

    /**
     * Gets the main image of the article.
     *
     * @return the main image path of the article
     */
    public String getMainImage() {
        return mainImage;
    }

    /**
     * Sets the main image of the article.
     *
     * @param mainImage the main image path of the article
     */
    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    /**
     * Gets the caption associated with the article.
     *
     * @return the caption of the article
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption associated with the article.
     *
     * @param caption the caption of the article
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Gets the infobox of the article.
     *
     * @return a map containing the infobox (row title -> caption)
     */
    public Map<String, String> getInfobox() {
        return infobox;
    }

    /**
     * Sets the infobox of the article.
     *
     * @param infobox a map containing the infobox (row title -> caption)
     */
    public void setInfobox(Map<String, String> infobox) {
        this.infobox = infobox;
    }
}
