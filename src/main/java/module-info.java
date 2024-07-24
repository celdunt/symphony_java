module loc.ex.symphony {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires annotations;
    requires java.logging;
    requires org.fxmisc.richtext;
    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires org.fxmisc.flowless;


    opens loc.ex.symphony to javafx.fxml;
    exports loc.ex.symphony;
    exports loc.ex.symphony.ui;
    exports loc.ex.symphony.listview;
    exports loc.ex.symphony.indexdata;
    exports loc.ex.symphony.search;
    opens loc.ex.symphony.ui to javafx.fxml;
}