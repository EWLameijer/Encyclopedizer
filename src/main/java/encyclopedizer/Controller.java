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

/**
 * Controller is simply the controller for the Encyclopedizer app, handling what all buttons and menu items do.
 */
public class Controller {

    // Reference to the encyclopedia that is viewed/edited
    private Encyclopedia ency;

    // If there is an original entry that is being edited, store it in originalEntry
    private Optional<Entry> originalEntry;

    // What the entry currently looks like (which can be different from the original entry, if text is being edited
    // or such
    private Entry currentEntry;

    // a reference to the primary stage of the application
    private Stage primaryStage;


    // open new (encyclopedia) file
    @FXML
    private MenuItem openFileMenuItem;

    // create new category
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
    private TextField categoriesField;

    @FXML
    private MenuItem CloseMenuItem;

    @FXML
    public void initialize() {
        ency = new Encyclopedia();
        showEntry("");

        conceptBrowseField.setOnKeyPressed((event) -> {
            if(event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                if (!originalEntry.isPresent()) {
                    conceptNameLabel.setText(conceptBrowseField.getText());
                }
                conceptDataArea.requestFocus();
            }
            else if(event.getCode() == KeyCode.DOWN) {
                currentEntry = ency.getNextEntry(currentEntry);
                conceptBrowseField.setText(currentEntry.getTopic());
                refreshWindow();
            }
            else if(event.getCode() == KeyCode.UP) {
                currentEntry = ency.getPreviousEntry(currentEntry);
                conceptBrowseField.setText(currentEntry.getTopic());
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
        createCategoryItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        deleteCategoryMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
    }


    Entry getCurrentEntry() {
        return new Entry(conceptNameLabel.getText()+ ": " + conceptDataArea.getText());
    }

    void refreshWindow() {

        conceptNameLabel.setText(currentEntry.getTopic());
        categoriesField.setText(currentEntry.getCategoriesAsString());
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
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            ency = new Encyclopedia(file.getAbsolutePath());
            showEntry("");
        }

    }

    @FXML
    void createCategory(ActionEvent event) {
        TextInputDialog categoryNameInput = new TextInputDialog();
        categoryNameInput.setTitle("Create new category");
        categoryNameInput.setHeaderText("");
        categoryNameInput.setGraphic(null);
        categoryNameInput.setContentText("category name:");
        Optional<String> result = categoryNameInput.showAndWait();
        if (result.isPresent()) {
            String newCategory = result.get();
            if (ency.containsCategory(newCategory)) {
                /// @@@ DO SOMETHING
            } else {
                ency.addCategory(newCategory);
                // @@ also add
            }
        }
    }

    @FXML
    void deleteCategory(ActionEvent event) {

    }

    @FXML
    void editCategories(KeyEvent event) {
        System.out.println(event.getCharacter());
        Optional<String> firstCorrectCategory = ency.getFirstCategory(event.getCharacter());
        if (firstCorrectCategory.isPresent()) {
            categoriesField.setText("[" + firstCorrectCategory.get() + "]");
            ency.updateCategories(currentEntry, categoriesField.getText());

        }
        event.consume();
    }

    void setStage(Stage stage) {
        primaryStage = stage;
    }
}
