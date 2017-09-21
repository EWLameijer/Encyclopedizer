package encyclopedizer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller {

    Encyclopedia ency;
    Optional<Entry> originalEntry;
    Entry currentEntry;


    private Stage stage;


    @FXML
    private MenuItem openFileMenuItem;
    @FXML
    private MenuItem createCategoryItem;
    @FXML
    private MenuItem deleteCategoryMenuItem;
    @FXML
    private TextArea conceptDataArea;
    @FXML
    private TextField conceptBrowseField;
    @FXML
    private Label conceptNameLabel;
    @FXML
    private Label firstApplicableConceptLabel;
    @FXML
    private Label lastApplicableConceptLabel;
    @FXML
    private ListView<?> referringConceptsArea;

    @FXML
    private MenuItem CloseMenuItem;

    @FXML
    public void initialize() {
        ency = new Encyclopedia();

        conceptBrowseField.setOnKeyPressed((event) -> {
            if(event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                if (!originalEntry.isPresent()) {
                    conceptNameLabel.setText(conceptBrowseField.getText());
                }
                conceptDataArea.requestFocus();
            }
            else if(event.getCode() == KeyCode.DOWN) {
                currentEntry = ency.getNextEntry(currentEntry);
                conceptBrowseField.setText(currentEntry.getName());
                refreshWindow();
            }
            else if(event.getCode() == KeyCode.UP) {
                currentEntry = ency.getPreviousEntry(currentEntry);
                conceptBrowseField.setText(currentEntry.getName());
                refreshWindow();
            }
        }
        );

        conceptDataArea.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) {
            if (originalEntry.isPresent()) {
                ency.replaceEntry(originalEntry.get(), getCurrentEntry());
            } else {
                ency.addEntry(getCurrentEntry());
            }
            conceptBrowseField.requestFocus(); } });

        CloseMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        openFileMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
    }


    Entry getCurrentEntry() {
        return new Entry(conceptNameLabel.getText()+ ": " + conceptDataArea.getText());
    }

    void refreshWindow() {

        conceptNameLabel.setText(currentEntry.getName());
        conceptDataArea.setText(currentEntry.getDescription());
    }


    @FXML
    void updateConcept(KeyEvent event) {
        String keyText = conceptBrowseField.getText();
        showEntry(keyText);
       //
    }

    void showEntry(String keyText) {
        originalEntry = ency.getEntryStartingWith(keyText);
        currentEntry = originalEntry.orElse(Entry.DEFAULT_ENTRY);
        refreshWindow();
    }

    @FXML
    void closeApplication(ActionEvent event) {
        System.out.println("Closing");
        Platform.exit();
    }

    @FXML
    void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            ency = new Encyclopedia(file.getAbsolutePath());
            showEntry("");
        }

    }

    @FXML
    void createCategory(ActionEvent event) {

    }

    @FXML
    void deleteCategory(ActionEvent event) {

    }

    void setStage(Stage stage) {
        this.stage = stage;
    }
}
