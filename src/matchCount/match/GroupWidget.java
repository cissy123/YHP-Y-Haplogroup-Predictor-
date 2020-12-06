package matchCount.match;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.*;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class GroupWidget extends Application{
    public GroupWidget(List<Item> itemsList,String category){
        this.itemsList = itemsList;
        this.category = category;
    }

    //    variable
    private List<Item> itemsList;
    private String category; //to judge if selecte groups or popus
    private ObservableList<Item> items = FXCollections.observableArrayList();
    private List<Integer> selectedItems = new ArrayList<>();

    //    @Override
    public void start(Stage primaryStage) throws Exception {
        TableView<Item> table = initTable(primaryStage);
// button set
        Button pb_OK = new Button("OK");
        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setBottom(pb_OK);

        primaryStage.setTitle("ItemSelection");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        // if not itemslist exist
        if (itemsList.size() == 0)
        {
            Label ll_not_ava = new Label("Not Avaliable");
            root.setCenter(ll_not_ava);
        }


        pb_OK.setOnAction((e) -> {
//            if clicked a table cell, not used but taking notes
//            ObservableList<Item> observableList = table.getSelectionModel().getSelectedItems();
//            for (Item kk :observableList){
//                System.out.println(kk.getName());
//            }

////            finally! fxxk
//            ObservableList<TableColumn<Item, ?>> columnsValue = table.getColumns();
//            TableColumn<Item, ?> box= columnsValue.get(0);
////            TableColumn<Item, ?> name= columnsValue.get(1);
//            for (int jj = 0; jj < itemsList.size(); jj++) {
//                if (box.getCellObservableValue(jj).getValue().equals(true)) {
////                System.out.println(name.getCellObservableValue(jj).getValue());
////                System.out.println(itemsList.get(jj).getName());
//                    this.selectedItems.add(jj);
//                }
//            }

//          clean code, traversal all items and judge if selected
            ObservableList<TableColumn<Item, ?>> columnsValue = table.getColumns();
            TableColumn<Item, ?> box= columnsValue.get(0);
            for (int jj = 0; jj < itemsList.size(); jj++) {
                if (box.getCellObservableValue(jj).getValue().equals(true)) {
                    this.selectedItems.add(jj);
                }
            }
            primaryStage.close();
            if(this.category.equals("group")) {
                Middle.SelectedGroupIndexs = this.selectedItems;
            }else {
                Middle.SelectedPopuIndexs = this.selectedItems;
            }
        });
    }

// table
    public TableView<Item> initTable(Stage stage) {
        //Create table and columns
        TableView<Item> table = new TableView<>();
        TableColumn<Item, Boolean> column_select = new TableColumn<>();
        TableColumn<Item, String> column_name = new TableColumn<>("Categories");
        //Create checkbox
        CheckBox selectAll = new CheckBox();
        //Add items to ObservableList
        this.items.addAll(itemsList);

        //Make table editable
        table.setEditable(true);

        //Make one column use checkboxes instead of text
        column_select.setCellFactory(CheckBoxTableCell.forTableColumn(column_select));

        //Change ValueFactory for each column
        column_select.setCellValueFactory(new PropertyValueFactory<>("selected"));
        column_name.setCellValueFactory(new PropertyValueFactory<>("name"));

        //Use box as column header
        column_select.setGraphic(selectAll);
        //Select all checkboxes when checkbox in header is pressed
        selectAll.setOnAction(e -> selectAllBoxes(e));
        //Add columns to the table
        table.getColumns().addAll(column_select, column_name);
        table.setItems(items);
//        stage.setScene(scene);
        return table;

    }
    public void selectAllBoxes(ActionEvent e) {
        //Iterate through all items in ObservableList
        for (Item item : this.items) {
            //And change "selected" boolean
            item.setSelected(((CheckBox) e.getSource()).isSelected());
        }
    }
}
