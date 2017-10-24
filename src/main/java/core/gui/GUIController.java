package core.gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import core.networking.ClientManager;
import general.InstanceManager;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 *
 * @author Josua Frank
 */
public class GUIController implements Initializable {

    @FXML
    private TextField input;
    @FXML
    private TextFlow output;
    @FXML
    private VBox list = new VBox();
    @FXML
    private ScrollPane scrollPane;
    private InstanceManager iManager;
    private HashMap<ClientManager, CheckBox> checkboxMap = new HashMap<>();
    private Boolean everybodySelected = false;
    private HashMap<Integer, String> commandHistory = new HashMap<>();
    private int selectedHistory = 0;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setInstanceManager(InstanceManager pIManager){
        this.iManager = pIManager;
    }
    
    /**
     * Wird aufgerufen, wenn eine Taste im Textfeld gedrückt wird, bei ENTER
     * wird der command abgeschickt, bei UP wird der vorherige command ausgewählt
     * und bei DOWN derüberprungene vorherige command (ist sozusagen eine Liste)
     *
     * @param evt Das Event, das die Gedrückte Taste beinhaltet
     */
    public void tasteGedrueckt(Event evt) {
        KeyEvent event = (KeyEvent) evt;
        if (event.getCode().equals(KeyCode.ENTER)) {
            commandHistory.put(commandHistory.size() + 1, input.getText());
            selectedHistory = commandHistory.size();
            boolean atLeastOneSelected = false;
            if (everybodySelected) {
                String eingabe2 = input.getText();
                String[] command = eingabe2.split(" ");
                iManager.getServerManager().sendToEveryone(command);
                atLeastOneSelected = true;
            } else {
                for (Map.Entry<ClientManager, CheckBox> entry : checkboxMap.entrySet()) {
                    ClientManager client = entry.getKey();
                    CheckBox checkbox = entry.getValue();
                    if ((!checkbox.isDisabled()) && checkbox.isSelected()) {
                        String eingabe2 = input.getText();
                        String[] command = eingabe2.split(" ");
                        client.sendObject(command);
                        atLeastOneSelected = true;
                    }
                }
            }
            input.positionCaret(0);
            input.clear();
            if (atLeastOneSelected == false) {
                this.showMessage("Kein Benutzer ausgewählt!");
                return;
            }
        } else if (event.getCode().equals(KeyCode.UP)) {
            if (selectedHistory > 0) {
                selectedHistory--;
                input.setText(commandHistory.get(selectedHistory + 1));
            }
            input.positionCaret(input.getText().length());
        } else if (event.getCode().equals(KeyCode.DOWN)) {
            if (selectedHistory < commandHistory.size()) {
                selectedHistory++;
                input.setText(commandHistory.get(selectedHistory + 1));
                if (input.getText() == null) {
                    input.positionCaret(0);
                } else {
                    input.positionCaret(input.getText().length());
                }
            } else if (selectedHistory == commandHistory.size()) {
                input.positionCaret(0);
                input.clear();
            }
        }
    }

    /**
     * Zeigt eine einfach Nachricht als Fenster an
     *
     * @param string Die anzuzeigende Nachricht
     */
    private void showMessage(String string) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(string);
            alert.showAndWait();
        });
    }

    /**
     * Fügt eine neue Zeile zur Textausgabe hnizu
     *
     * @param pMessage Die Nachricht
     * @param pColor Die Farbe, in der die Nachricht sein soll
     */
    public void addToTextFlow(String pMessage, Color pColor) {
        Text text = new Text(pMessage);
        text.setFill(pColor);
        text.setFont(Font.font("Lucida Console"));
        output.getChildren().addAll(text);
        scrollPane.setVvalue(output.getChildren().size());
        while (output.getChildren().size() > 250) {
            output.getChildren().remove(0, output.getChildren().size() - 250);
        }
    }

    /**
     * Setzt die Liste, die links zu finden ist, mit den Spielernamen und einer
     * Checkbox
     *
     * @param firstStart Gibt an, ob diese Liste zum ersten Mal aufgerufen wird,
     * falls ja, wird die Checkbox Jeder aktiviert
     */
    public void setListView(boolean firstStart) {
        list.getChildren().clear();
        list.setPadding(new Insets(10, 10, 10, 10));
        Text title = new Text("Online Benutzer:");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        list.getChildren().add(title);
        Label labelUsername = new Label("Jeder");
        labelUsername.setMouseTransparent(true);
        labelUsername.setBorder(null);
        labelUsername.setMaxSize(150, 50);
        labelUsername.setStyle("-fx-font-alignment: center");
        BorderPane pane = new BorderPane();
        pane.setLeft(labelUsername);
        pane.setPadding(new Insets(10, 10, 10, 10));
        CheckBox checkBoxEveryone = new CheckBox();
        checkBoxEveryone.setOnAction((ActionEvent e) -> {
            for (Map.Entry<ClientManager, CheckBox> entry : checkboxMap.entrySet()) {
                CheckBox value = entry.getValue();
                value.setDisable(checkBoxEveryone.isSelected());
            }
            everybodySelected = checkBoxEveryone.isSelected();
        });
        if (firstStart) {
            checkBoxEveryone.setSelected(true);
            everybodySelected = true;
        }
        pane.setRight(checkBoxEveryone);
        list.getChildren().add(pane);
        if (iManager.getServerManager().getClientManagerMap() != null) {
            HashMap<String, ClientManager> map = iManager.getServerManager().getClientManagerMap();
            for (Map.Entry<String, ClientManager> entry : map.entrySet()) {
                ClientManager clientManager = entry.getValue();
                String username;
                if (clientManager.getUsername() == null) {
                    username = clientManager.getIP().toString() + " (Launcher)";
                } else {
                    username = clientManager.getUsername();
                }
                Label labelUsername2 = new Label(username);
                labelUsername2.setMouseTransparent(true);
                labelUsername2.setBorder(null);
                labelUsername2.setMaxSize(150, 50);
                labelUsername2.setStyle("-fx-font-alignment: center");
                BorderPane pane2 = new BorderPane();
                pane2.setLeft(labelUsername2);
                pane2.setPadding(new Insets(10, 10, 10, 10));
                CheckBox checkBoxUser = new CheckBox();
                checkboxMap.put(clientManager, checkBoxUser);
                pane2.setRight(checkBoxUser);
                list.getChildren().add(pane2);
            }
        }
    }

}
