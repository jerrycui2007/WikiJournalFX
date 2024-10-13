module com.wikijournalfx.wikijournalfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.wikijournalfx.wikijournalfx to javafx.fxml;
    exports com.wikijournalfx.wikijournalfx;
}