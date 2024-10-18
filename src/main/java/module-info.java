module com.wikijournalfx.wikijournalfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.wikijournalfx.wikijournalfx to javafx.fxml, com.google.gson;
    exports com.wikijournalfx.wikijournalfx;
}
