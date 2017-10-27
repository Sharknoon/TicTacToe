/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiLauncher;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import networking.ClientManager;
import general.Language;
import general.Oberflaeche;
import java.awt.Color;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javax.swing.JOptionPane;

/**
 *
 * @author Josua Frank
 */
public class LauncherOberflaeche extends Application implements Oberflaeche {

    /*
    
     *Defaultcloseoperation in start
    
     */
    //Der Client und die Sprachklasse
    private ClientManager client;
    private Language languages;
    private FXMLLauncherController controller;
    private Stage stage;
    private Boolean rightJavaVersion = false;
    private Scene scene;

    public LauncherOberflaeche() {
        Platform.setImplicitExit(false);
        String javaVersion = System.getProperty("java.version");
        if (javaVersion.startsWith("1.")) {
            if (Integer.valueOf(javaVersion.charAt(2)) >= 8
                    && Integer.valueOf(javaVersion.charAt(javaVersion.length() - 2)) >= 4) {//Java 1.8u40 wegen Alerts benötigt
                System.out.println("Richtige Java-Version");
                rightJavaVersion = true;
            } else {
                System.out.println("Falsche Java-Version");
            }
        } else {//java 9 +
            rightJavaVersion = true;
        }
    }

    @Override
    public void start(Stage pStage) throws Exception {
        //Generelle Parameter beladen
        this.stage = pStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLLauncher.fxml"));
        Parent root = (Parent) loader.load();
        controller = (FXMLLauncherController) loader.getController();
        scene = new Scene(root);
        client = new ClientManager(this, stage);
        this.languages = client.getLanguages();
        stage.setTitle(languages.getString(45));
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setOnCloseRequest((WindowEvent we) -> {
            client.verbindungTrennen();
        });
        scene.widthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) -> {
            controller.windowResized(newSceneWidth.intValue(), 0);
        });
        scene.heightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) -> {
            controller.windowResized(0, newSceneHeight.intValue());
        });
        controller.setOberflaeche(this);
        controller.setClient(client);
        controller.setLanguages(languages);
        controller.setComboBoxLanuage(languages.getLanguage(), Integer.valueOf(languages.getString(0)));
        aendereSprache(languages.getMainLanguage());
        controller.startFocus();
        if (rightJavaVersion) {
            stage.show();
        } else {
            JOptionPane.showMessageDialog(null, "Wrong Java-Version installed: " + System.getProperty("java.version") + "\nJava 1.8 Update 40 requiered!");
        }
    }

    /**
     * Schreibt auf das Login-Fenster eine Nachricht
     *
     * @param text Die zu schreibende Nachricht
     */
    public void setStatusFeld(String text) {
        Platform.runLater(() -> {
            controller.setLabelStatus(text);
        });
    }

    /**
     * Zeigt mehrere Fehler an
     *
     * @param pFehler die anzuzeigenden Fehler als ArrayList
     */
    public void zeigeFehlerAn(ArrayList<String> pFehler) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(languages.getString(43));
            alert.setHeaderText(languages.getString(44));
            ObservableList<String> fehler = FXCollections.observableArrayList(pFehler);
            ListView<String> errorList = new ListView<>(fehler);
            errorList.setFocusTraversable(false);
            errorList.setMaxHeight(fehler.size() * 5);
            alert.getDialogPane().setContent(errorList);
            alert.setHeight(50);
//            DialogPane dialogPane = alert.getDialogPane();
//            dialogPane.getStylesheets().add(getClass().getResource("../general/CSSDialog.css").toExternalForm());
//            dialogPane.getStyleClass().add("standardDialog");
            alert.showAndWait();
        });
    }

    /**
     * Zeigt einen Fehler an
     *
     * @param pFehler den anzuzeigenden Fehler als String
     */
    public void zeigeFehlerAn(String pFehler) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.getDialogPane().setBackground(new Background(new BackgroundImage(new Image("header.png"), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
            alert.setTitle(languages.getString(43));
            alert.setHeaderText(languages.getString(44));
            alert.setContentText(pFehler);
//            DialogPane dialogPane = alert.getDialogPane();
//            dialogPane.getStylesheets().add(getClass().getResource("../general/CSSDialog.css").toExternalForm());
//            dialogPane.getStyleClass().add("standardDialog");
            alert.showAndWait();
        });
    }

    /**
     * Ändert die Sprache für das System
     *
     * @param language Die in die zu ändernde Sprache (z.B. "en")
     */
    public void aendereSprache(String language) {
        languages.setMainLanguage(language);
        controller.setLabelLoginUsername(languages.getString(15));
        controller.setLabelLoginPassword(languages.getString(16));
        controller.setLabelRegiserUsername(languages.getString(15));
        controller.setLabelRegisterFirstName(languages.getString(17));
        controller.setLabelRegisterLastName(languages.getString(18));
        controller.setLabelRegisterPassword1(languages.getString(16));
        controller.setLabelRegisterPassword2(languages.getString(16));
        controller.setLabelRegisterEmail(languages.getString(19));
        controller.setButtonTextLogin(languages.getString(5));
        controller.setButtonTextRegister(languages.getString(8));
        controller.setLabelPasswordForgotten(languages.getString(79));
    }

    /**
     * Zeigt das Emailcode-Bestätigungsfenster
     */
    public void showCodeVerification() {
        Platform.runLater(() -> {
            controller.showCodeVerification();
        });
    }

    /**
     * Zeigt das Passwort-Bestätigungsfenster an
     */
    public void showPasswordVerification() {
        Platform.runLater(() -> {
            controller.showNewPasswordDialog();
        });
    }

    /**
     * Zeigt das Email-Eingabefenster für den Passwort-Vergessen Dialog an
     */
    public void showEmailInput() {
        Platform.runLater(() -> {
            controller.fxLabelPasswordForgottenPRESSED(null);
        });
    }

    @Override
    public int[] getSize() {
        int[] size = new int[2];
        size[0] = (int) scene.getWidth();
        size[1] = (int) scene.getHeight();
        return size;
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    public static void main(String[] args) {
        LauncherOberflaeche.launch();
    }

    @Override
    public void setSize(int[] size) {
        controller.anchorPane.setPrefSize(size[0], size[1]);
    }

}
