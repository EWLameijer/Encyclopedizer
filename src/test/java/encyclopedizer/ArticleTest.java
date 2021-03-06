package encyclopedizer;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by Eric-Wubbo on 08-07-17.
 */
public class ArticleTest {

    @Test
    public void testEntryConstructor() {
        String randomTerm = "blueberry";
        String randomData = "tastes good. Also is blue.";
        String randomEntry = randomTerm + ":" + randomData;

        Article testArticle = new Article(randomEntry);
        assertEquals(testArticle.getTopic(), randomTerm);
        assertEquals(testArticle.getDescription(), randomData);
    }
}
/*
<GridPane alignment="center" hgap="10" vgap="10" xmlns:fx="http://javafx.com/fxml/1" xmlns="http://javafx.com/javafx/8.0.111" fx:controller="encyclopedizer.Controller">
<columnConstraints>
<ColumnConstraints />
</columnConstraints>
<rowConstraints>
<RowConstraints />
</rowConstraints>
<children>
<GridPane prefHeight="375.0" prefWidth="456.0">
<columnConstraints>
<ColumnConstraints hgrow="SOMETIMES" maxWidth="223.0" minWidth="10.0" prefWidth="74.0" />
<ColumnConstraints hgrow="SOMETIMES" maxWidth="330.0" minWidth="10.0" prefWidth="94.0" />
<ColumnConstraints hgrow="SOMETIMES" maxWidth="330.0" minWidth="10.0" prefWidth="95.0" />
<ColumnConstraints hgrow="SOMETIMES" maxWidth="330.0" minWidth="10.0" prefWidth="109.0" />
<ColumnConstraints hgrow="SOMETIMES" maxWidth="330.0" minWidth="10.0" prefWidth="139.0" />
</columnConstraints>
<rowConstraints>
<RowConstraints maxHeight="118.0" minHeight="10.0" prefHeight="25.0" vgrow="SOMETIMES" />
<RowConstraints maxHeight="118.0" minHeight="10.0" prefHeight="25.0" vgrow="SOMETIMES" />
<RowConstraints maxHeight="118.0" minHeight="10.0" prefHeight="27.0" vgrow="SOMETIMES" />
<RowConstraints maxHeight="276.0" minHeight="10.0" prefHeight="18.0" vgrow="SOMETIMES" />
<RowConstraints maxHeight="276.0" minHeight="10.0" prefHeight="185.0" vgrow="SOMETIMES" />
<RowConstraints maxHeight="168.0" minHeight="10.0" prefHeight="24.0" vgrow="SOMETIMES" />
<RowConstraints maxHeight="168.0" minHeight="10.0" prefHeight="96.0" vgrow="SOMETIMES" />
</rowConstraints>
<children>
<TextField fx:id="conceptNameField" />
<Label fx:id="firstApplicableConceptLabel" text="Label" GridPane.columnIndex="2" />
<Label fx:id="lastApplicableConceptLabel" text="Label" GridPane.columnIndex="4" />
<Label fx:id="conceptNameLabel" text="Label" GridPane.rowIndex="2" />
<TextArea fx:id="conceptDataArea" prefHeight="182.0" prefWidth="192.0" GridPane.columnSpan="5" GridPane.rowIndex="4" />
<ListView fx:id="referringConceptsArea" prefHeight="200.0" prefWidth="200.0" GridPane.columnSpan="5" GridPane.rowIndex="6" />
</children>
</GridPane>
</children>
</GridPane>*/