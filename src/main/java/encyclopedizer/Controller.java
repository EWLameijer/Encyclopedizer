package encyclopedizer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {
    @FXML
    private TextArea conceptDataArea;
    @FXML
    private TextField conceptNameField;
    @FXML
    private Label conceptNameLabel;
    @FXML
    private Label firstApplicableConceptLabel;
    @FXML
    private Label lastApplicableConceptLabel;
    @FXML
    private ListView<?> referringConceptsArea;
}
