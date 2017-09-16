package encyclopedizer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.*;

public class Main extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
            this.primaryStage = primaryStage;

            Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

            primaryStage.setScene(new Scene(root, 1000, 800));
            primaryStage.show();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root2 = (Parent)loader.load();
        Controller myController = loader.getController();
        myController.setStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    static Stage getStage() {
        return primaryStage;
    }


}

/*

        public static final ObservableList names =
                FXCollections.observableArrayList();
        public static final ObservableList data =
                FXCollections.observableArrayList();

        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("List View Sample");

            final ListView listView = new ListView(data);
            listView.setPrefSize(200, 250);
            listView.setEditable(true);

            names.addAll(
                    "Adam", "Alex", "Alfred", "Albert",
                    "Brenda", "Connie", "Derek", "Donny",
                    "Lynne", "Myrtle", "Rose", "Rudolph",
                    "Tony", "Trudy", "Williams", "Zach"
            );

            for (int i = 0; i < 18; i++) {
                data.add("anonym");
            }

            listView.setItems(data);
            listView.setCellFactory(ComboBoxListCell.forListView(names));

            StackPane root = new StackPane();
            root.getChildren().add(listView);
            primaryStage.setScene(new Scene(root, 200, 250));
            primaryStage.show();
        }
    }*/

