package encyclopedizer;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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


    // for opening a new (encyclopedia) file
    @FXML
    private MenuItem openFileMenuItem;

    // for closing and exiting the application
    @FXML
    private MenuItem CloseMenuItem;

    // for creating a new category
    @FXML
    private MenuItem createCategoryItem;

    // for deleting a category
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

    // To display other topics that contain the sought keyword/topic
    @FXML
    private ListView<?> referringConceptsArea;

    // to show the categories that the current topic belongs to
    @FXML
    private TextField categoriesField;

    // for choosing the category to display (default: all)
    @FXML
    private ChoiceBox<String> categoryChoice;


    /**
     * Initialize the window/application at startup time
     */
    @FXML
    public void initialize() {
        initializeData("");

        // set listeners
        setTopicBrowseFieldListeners();
        setDescriptionAreaListeners();
        categoryChoice.getSelectionModel().selectedItemProperty().addListener(this::categoryChanged);

        // set accelerators
        CloseMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        openFileMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        createCategoryItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        deleteCategoryMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
    }

    String getCurrentCategory() {
        return categoryChoice.getValue();
    }

    /**
     * Initialize the data (so the encyclopedia)
     */
    private void initializeData(String encyName) {
        ency = new Encyclopedia(encyName);
        showFirstApplicableEntry("");
        updateCategoryChoices();
    }

    boolean articleHasBeenChanged() {
        //todo
        return true;
    }

    /**
     * Set the listeners to the description area, basically what to do when ENTER is pressed (which is to
     * save the entry, but it needs to be decided whether an entry needs to be added or replaced.)
     */
    private void setDescriptionAreaListeners() {
        descriptionArea.setOnKeyPressed((event) -> { if(event.getCode() == KeyCode.ENTER) {
            if (originalEntry.isPresent() && articleHasBeenChanged()) {
                ency.replaceEntry(originalEntry.get(), getCurrentArticle());
            } else {
                ency.addEntry(getCurrentArticle());
            }
            topicBrowseField.requestFocus(); } });
    }

    /**
     * Set the listener(s) for the topic browsing field, which must update the description when the topic(name) is
     * changed for whatever reason.
     */
    private void setTopicBrowseFieldListeners() {
        topicBrowseField.setOnKeyPressed((event) -> {
            if(event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                if (!originalEntry.isPresent()) {
                    topicLabel.setText(topicBrowseField.getText());
                }
                descriptionArea.requestFocus();
            }
            else if(event.getCode() == KeyCode.DOWN) {
                currentArticle = ency.getNextEntry(currentArticle, getCurrentCategory());
                topicBrowseField.setText(currentArticle.getTopic());
                refreshWindow();
            }
            else if(event.getCode() == KeyCode.UP) {
                currentArticle = ency.getPreviousEntry(currentArticle, getCurrentCategory());
                topicBrowseField.setText(currentArticle.getTopic());
                refreshWindow();
            }
        }
        );
    }

    /** if the category is changed in the choice box, only continue showing the item if it falls under the new category.
     * Otherwise show the next entry that falls under the category.
      */
    private void categoryChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (oldValue == null || newValue == null) return;

        if (oldValue.equals(newValue) || newValue.equals("all") || getCurrentArticle().hasCategory(newValue)) {
            // do nothing: you can continue dislaying the current entry
        } else {
            currentArticle = ency.getNextArticle(topicLabel.getText(), newValue);
            refreshWindow();
        }
    }

    /**
     * Gets the current article, which is the topic as shown in the topic label with the description as given in the
     * description area.
     *
     * @return the article in the main window, in its current state.
     */
    private Article getCurrentArticle() {
        return new Article(topicLabel.getText()+ categoriesField.getText() + ": " + descriptionArea.getText());
    }

   private void refreshWindow() {
        topicLabel.setText(currentArticle.getTopic());
        categoriesField.setText(currentArticle.getCategoriesAsString());
        descriptionArea.setText(currentArticle.getDescription());
    }

    /**
     * Action to perform when updating the text in the topic browse field
     *
     * @param event the KeyEvent that indicates that an action has taken place in the topic browse field
     */
    @FXML
    void updateTopic(KeyEvent event) {
        String keyText = topicBrowseField.getText();
        showFirstApplicableEntry(keyText);
    }

    /**
     * Shows the first entry in the dictionary that starts with the given text
     *
     * @param keyText the text that the shown entry should start with
     *                TODO: Make this case-insensitive!
     */
    private void showFirstApplicableEntry(String keyText) {
        originalEntry = ency.getEntryStartingWith(keyText);
        currentArticle = originalEntry.orElse(Article.defaultArticle);
        refreshWindow();
    }

    /**
     * Closes the application when the File->Quit is pressed (or Ctrl+Q)
     *
     * @param event the event indicating that the user wants to close the application
     */
    @FXML
    void closeApplication(ActionEvent event) {
        Platform.exit();
    }

    /**
     *
     */
    private void updateCategoryChoices() {
        ObservableList optionList = FXCollections.observableArrayList();

        ency.getCategoryList(optionList);
        categoryChoice.setItems(optionList);
        categoryChoice.setValue("all");
    }

    @FXML
    void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            ency = new Encyclopedia(file.getAbsolutePath());
            showFirstApplicableEntry("");
            updateCategoryChoices();
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
