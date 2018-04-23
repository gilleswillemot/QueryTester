package gui;

import com.sun.prism.paint.Color;
import domain.Database;
import domain.DomainController;
import domain.Query;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.text.Text;
import javax.swing.event.ChangeEvent;

public class MainGuiController extends AnchorPane implements Initializable {

    private DomainController domainController;

    @FXML
    private Button connectButton;
    @FXML
    private Button execButton;
    @FXML
    private Button execMultipleButton;
    @FXML
    private ListView<Query> queryListView;
    @FXML
    private TextField passwordTextfield;
    @FXML
    private TextField serverAdressTextfield;
    @FXML
    private TextField servnameTextfield;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Text info;
    @FXML
    private CheckBox windowsAuthCheckbox;
    @FXML
    private ComboBox<String> databaseChoice;
    @FXML
    Label serverLabel;

    private Database database = Database.MySQL;

    public MainGuiController(DomainController controller) {
        this.domainController = controller;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainGui.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        execButton.setVisible(false);
        execMultipleButton.setVisible(false);

        queryListView.setItems(domainController.getQueries());
        queryListView.getSelectionModel().selectFirst();

        usernameTextField.setText("root");
        passwordTextfield.setText("root");
        servnameTextfield.setText("dbperf");
        serverAdressTextfield.setText("localhost");

        this.connectButton.setOnAction(e -> {
            //TODO surround with while, for letting the user keep trying to connect with other parameters.
            try {
                domainController.connectToDatabase(database, windowsAuthCheckbox.isSelected(), servnameTextfield.getText(), serverAdressTextfield.getText(),
                        usernameTextField.getText(), passwordTextfield.getText());
                execButton.setVisible(true);
                execMultipleButton.setVisible(true);
                this.connectButton.setStyle("-fx-background-color: #00cc00");
            } catch (Exception ex) {
                this.connectButton.setStyle("-fx-background-color: #ff0000");
            }
        });

        this.execButton.setOnAction(e -> {
            switch (database) {
                case MySQL:
                    domainController.executeMySQLQuery(queryListView.getSelectionModel().getSelectedItem(), 1);
                    break;
                case MSSQL:
                    domainController.executeMSSQLQuery(queryListView.getSelectionModel().getSelectedItem(), 1);
                    break;
            }
        });

        this.execMultipleButton.setOnAction(e -> {
            switch (database) {
                case MySQL:
                    domainController.executeMySQLQuery(queryListView.getSelectionModel().getSelectedItem(), 50);
                    break;
                case MSSQL:
                    domainController.executeMSSQLQuery(queryListView.getSelectionModel().getSelectedItem(), 50);
                    break;
            }
        });
        windowsAuthCheckbox.setVisible(false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<String> databasesStrings = Stream.of(Database.values())
                .map(Database::name).collect(Collectors.toList());

        ObservableList<String> databaseValues = FXCollections.observableArrayList(databasesStrings);
        System.out.println(databaseValues);
        databaseChoice.setItems(databaseValues);
        databaseChoice.getSelectionModel().select("MySQL");
//        databaseChoice.getItems().addListener((ListChangeListener.Change change) -> {
//            // String newSelectedItem = ... ; // figure item that should be selected instead
//            // cb.setValue(newSelectedItem);
//            System.out.println(change);
//        });
        databaseChoice.setOnAction(value -> {
            database = Database.valueOf(databaseChoice.getSelectionModel().getSelectedItem());
            System.out.println(database.name());
            execButton.setVisible(false);
            execMultipleButton.setVisible(false);
            this.connectButton.setStyle("-fx-background-color: "+new Button().getBackground());
//            this.connectButton.setBackground();
            if (Database.MSSQL.equals(database)) {
                info.setText("username & password fields may be left empty if database does not use them.");
                windowsAuthCheckbox.setVisible(true);
                serverAdressTextfield.setText("\\SQLEXPRESS");
                usernameTextField.setText("");
                passwordTextfield.setText("");
                serverLabel.setText("server name");

            } else {
                info.setText("");
                windowsAuthCheckbox.setVisible(false);
                serverAdressTextfield.setText("localhost");
                serverLabel.setText("network address");
                usernameTextField.setText("root");
                passwordTextfield.setText("root");
            }
        });
    }

}
