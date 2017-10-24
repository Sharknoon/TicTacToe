package core.gui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import core.networking.ServerManager;
import general.InstanceManager;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Josua Frank
 */
public class GUI extends Application {

    private ExecutorService threadPool;
    private Stage primaryStage;
    private InstanceManager iManager;

    public GUI() {

    }

    @Override
    public void start(Stage pPrimaryStage) throws Exception {
        //Generelle Parameter beladen
        primaryStage = pPrimaryStage;
        iManager = new InstanceManager();
        iManager.addInstance(this);
        this.threadPool = Executors.newCachedThreadPool();
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream("/GUI.fxml"));
        iManager.addInstance((GUIController) loader.getController());
        Scene scene = new Scene(root);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();
        this.requestEmail();
    }

    /**
     * Fügt eine Nachricht zu der Oberfläche hinzu
     *
     * @param pMessage Die anzuzeugende Nachricht
     * @param pColor Die Farbe der anzuzeigenden Nachricht
     */
    public void addToTextFlow(String pMessage, Color pColor) {
        Platform.runLater(() -> {
            iManager.getGUIController().addToTextFlow(pMessage + "\n", pColor);
        });
    }

    /**
     * Setzt die Liste, die links zu finden ist, mit den Spielernamen und einer
     * Checkbox
     */
    public void setListView() {
        Platform.runLater(() -> {
            iManager.getGUIController().setListView(false);
        });
    }

    public void requestEmail() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("E-Maileinstellungen für die E-Mailkonfirmation");
            alert.setHeaderText("Bitte gib die Emailadresse und das Passwort für die Email ein");
            alert.setContentText(null);

            GridPane gp = new GridPane();
            Label emailLabel = new Label("E-Mailadresse:");
            gp.add(emailLabel, 0, 0);
            TextField emailField = new TextField();
            gp.add(emailField, 1, 0);
            Label passwordLabel = new Label("Passwort:");
            gp.add(passwordLabel, 0, 1);
            PasswordField passwordField = new PasswordField();
            gp.add(passwordField, 1, 2);

            gp.setHgap(20);
            BorderPane bp = new BorderPane(gp);
            alert.getDialogPane().setContent(bp);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                String[] loginData = {emailField.getText(), passwordField.getText()};//DBPassword, Emailadress, EmailPW
                threadPool.submit(new ServerManager(iManager, loginData));
                iManager.getGUIController().setInstanceManager(iManager);
                iManager.getGUIController().setListView(true);
                primaryStage.setOnCloseRequest((WindowEvent we) -> {
                    iManager.getServerManager().disconnect();
                });
            } else {
                System.exit(0);
            }
        });
    }

    /**
     * Die Main-Methode des Servers
     *
     * @param args
     */
    public static void main(String[] args) {
        GUI.launch((String[]) null);
    }
}
