package encyclopedizer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

/**
 * Controller is simply the controller for the Encyclopedizer app, handling what all buttons and menu items do.
 */
public class Controller {

    // Reference to the encyclopedia that is viewed/edited
    private Encyclopedia ency;

    // If there is an original entry that is being edited, store it in originalEntry
    private Optional<Article> originalEntry;

    // What the entry currently looks like (which can be different from the original entry, if text is being edited
    // or such
    private Article currentArticle;

    // a reference to the primary stage of the application
    private Stage primaryStage;


    // open new (encyclopedia) file
    @FXML
    private MenuItem openFileMenuItem;

    // create a new category
    @FXML
    private MenuItem createCategoryItem;

    // delete a category
    @FXML
    private MenuItem deleteCategoryMenuItem;

    // the field in which the user can type the first letters of the topic to be sought
    @FXML
    private TextField topicBrowseField;

    // the area in which the description is displayed (the things that are known about the topic)
    @FXML
    private TextArea descriptionArea;

    // The label that gives the full name of the topic (the browsefield may just contain 'j', but if the first
    // article starting with 'j' is 'java', the topicLabel will contain the full term 'java'
    @FXML
    private Label topicLabel;


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

        topicBrowseField.setOnKeyPressed((event) -> {
            if(event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                if (!originalEntry.isPresent()) {
                    topicLabel.setText(topicBrowseField.getText());
                }
                descriptionArea.requestFocus();
            }
            else if(event.getCode() == KeyCode.DOWN) {
                currentArticle = ency.getNextEntry(currentArticle);
                topicBrowseField.setText(currentArticle.getTopic());
                refreshWindow();
            }
            else if(event.getCode() == KeyCode.UP) {
                currentArticle = ency.getPreviousEntry(currentArticle);
                topicBrowseField.setText(currentArticle.getTopic());
                refreshWindow();
            }
        }
        );

        descriptionArea.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) {
            if (originalEntry.isPresent()) {
                ency.replaceEntry(originalEntry.get(), getCurrentArticle());
            } else {
                ency.addEntry(getCurrentArticle());
            }
            topicBrowseField.requestFocus(); } });

        CloseMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        openFileMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        createCategoryItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        deleteCategoryMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
    }


    Article getCurrentArticle() {
        return new Article(topicLabel.getText()+ ": " + descriptionArea.getText());
    }

    void refreshWindow() {
        topicLabel.setText(currentArticle.getTopic());
        categoriesField.setText(currentArticle.getCategoriesAsString());
        descriptionArea.setText(currentArticle.getDescription());
    }


    @FXML
    void updateConcept(KeyEvent event) {
        String keyText = topicBrowseField.getText();
        showEntry(keyText);
       //
    }

    void showEntry(String keyText) {
        originalEntry = ency.getEntryStartingWith(keyText);
        currentArticle = originalEntry.orElse(Article.defaultArticle);
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
            ency.updateCategories(currentArticle, categoriesField.getText());

        }
        event.consume();
    }

    void setStage(Stage stage) {
        primaryStage = stage;
    }
}
