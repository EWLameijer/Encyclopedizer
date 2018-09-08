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

    // If there is an original entry of the encyclopedia that is being edited, originalArticle refers to it. If a new
    // article is being created, originalArticle is empty.
    private Optional<Article> originalArticle;

    // a reference to the primary stage of the application
    private Stage primaryStage;

    // for opening a new (encyclopedia) file
    @FXML
    private MenuItem openFileMenuItem;

    // for closing and exiting the application
    @FXML
    private MenuItem CloseMenuItem;

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

    /**
     * Initialize the window/application at startup time
     */
    @FXML
    public void initialize() {
        initializeData("");

        // set listeners
        setTopicBrowseFieldListeners();
        setDescriptionAreaListeners();

        // set accelerators (Keyboard shortcuts like Ctrl+Q)
        CloseMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
        openFileMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
    }

    /**
     * Initialize the data (so the encyclopedia)
     */
    private void initializeData(String encyName) {
        ency = new Encyclopedia(encyName);
        showFirstApplicableEntry("");
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
            Article cleanedCurrentArticle = getCurrentArticle().cleanUp();
            if (originalArticle.isPresent() && articleHasBeenChanged()) {
                ency.replaceEntry(originalArticle.get(), cleanedCurrentArticle);
            } else {
                ency.addEntry(cleanedCurrentArticle);
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
                if (!originalArticle.isPresent()) {
                    topicLabel.setText(topicBrowseField.getText());
                }
                descriptionArea.requestFocus();
            }
            else if (originalArticle.isPresent()) {
                if (event.getCode() == KeyCode.DOWN) {
                    Article articleToView = ency.getNextEntry(originalArticle.get());
                    topicBrowseField.setText(articleToView.getTopic());
                    refreshWindow(articleToView);
                } else if(event.getCode() == KeyCode.UP ) {
                    Article articleToView = ency.getPreviousEntry(originalArticle.get());
                    topicBrowseField.setText(articleToView.getTopic());
                    refreshWindow(articleToView);
                }
            }
        }
        );
    }

   /**
     * Gets the current article, which is the topic as shown in the topic label with the description as given in the
     * description area.
     *
     * @return the article in the main window, in its current state.
     */
    private Article getCurrentArticle() {
        return new Article(topicLabel.getText() + ": " + descriptionArea.getText());
    }

   private void refreshWindow(Article articleToView) {
        topicLabel.setText(articleToView.getTopic());
        descriptionArea.setText(articleToView.getDescription());
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

    @FXML
    void descriptionAreaKeyHandler(KeyEvent event) {
        if (topicLabel.getText().equals("")) {
            topicLabel.setText(topicBrowseField.getText());
        }
    }
    /**
     * Shows the first entry in the dictionary that starts with the given text
     *
     * @param keyText the text that the shown entry should start with
     *                TODO: Make this case-insensitive!
     */
    private void showFirstApplicableEntry(String keyText) {
        originalArticle = ency.getEntryStartingWith(keyText);
        Article articleToView = originalArticle.orElse(Article.defaultArticle);
        refreshWindow(articleToView);
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

    @FXML
    void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            ency = new Encyclopedia(file.getAbsolutePath());
            showFirstApplicableEntry("");
        }
    }

    void setStage(Stage stage) {
        primaryStage = stage;
    }
}
