module ahu.bsc.sbnjplag {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens ahu.bsc.sbnjplag to javafx.fxml;
    exports ahu.bsc.sbnjplag;
}