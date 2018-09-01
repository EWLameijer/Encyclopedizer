package encyclopedizer;

import javafx.scene.control.Alert;

public class ReportError {
    static void report(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Encyclopedizer found an error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
