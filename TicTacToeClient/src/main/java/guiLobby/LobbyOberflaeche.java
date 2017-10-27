/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiLobby;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import general.Language;
import general.Oberflaeche;
import java.util.Map;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonBar;
import networking.ClientManager;

/**
 *
 * @author Josua Frank
 */
public class LobbyOberflaeche implements Oberflaeche {

    private final ClientManager client;
    private final Language languages;
    private Stage stage;
    private FXMLLobbyController controller = null;
    private Scene scene;
    private HashMap<String, String> scheduledTasks = new HashMap<>();//Mehtode, Parameter
    private Alert request;
    private boolean closingRequest;

    public LobbyOberflaeche(ClientManager pClient, Stage pStage) {
        this.stage = pStage;
        this.client = pClient;
        this.languages = client.getLanguages();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLLobby.fxml"));
        Parent root;
        try {
            root = (Parent) loader.load();
            controller = (FXMLLobbyController) loader.getController();
            scene = new Scene(root);
        } catch (IOException ex) {
            System.err.print("LobbyOberfläche konnte nicht gestartet werden: ");
            ex.printStackTrace(System.out);
            return;
        }
        controller.setOberflaeche(this);
        controller.setClient(client);
        controller.startFocus();
        scene.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            controller.windowResized();
        });
        Platform.runLater(() -> {
            stage.setMinWidth(800);
            stage.setMinHeight(600);
        });
    }

    /**
     * Fügt eine Nachricht zum Lobbychat hinzu
     *
     * @param pMessage die zu hinzufügende Nachricht im Format
     * [NAME]§§§§[Nachricht]
     */
    public void addToLobbyChat(String pUsername, String pMessage) {
        Platform.runLater(() -> {
            controller.addToTextFlow(pUsername, Color.valueOf("0078cc"));
            controller.addToTextFlow(":  " + pMessage + "\n", Color.BLACK);
        });
    }

    /**
     * Baut die Vertikale Box auf der linken Seite des Spieles, in der die
     * Spieler mit ihren Status und einem Knopf eingezeigt werden.
     *
     * @param onlineUsers Die HashMap(String username, int status) mit den
     * jeweiligen Status (0 = in Lobby, 1 = Spielt gerade 2 = schaut gerade
     * einem Spiel zu)
     */
    public void buildList(HashMap<String, Integer> onlineUsers) {
        Platform.runLater(() -> {
            controller.buildPlayersVBox(onlineUsers);
        });
    }

    /**
     * Baut die Vertikale Box auf der rechten Seite des Spieles, in der die
     * Spieler mit ihren Scores angezeigt werden.
     *
     * @param scores Die HashMap(String username, int[] scores) mit den
     * jeweiligen Scores (int[0] = wins, int[1] = defeats, int[2] draws)
     */
    public void buildScore(HashMap<String, int[]> scores) {
        Platform.runLater(() -> {
            controller.buildPlayersScoresList(scores);
        });
    }

    /**
     * Schreibt im Lobbychat, dass jemand nicht länger im Spiel ist
     *
     * @param pUsername Derjenige, der nicht länger im Spiel ist
     */
    public void addToLobbyChatLeftTheGame(String pUsername) {
        Platform.runLater(() -> {
            if (!pUsername.equals("null")) {
                controller.addToTextFlow(pUsername, Color.RED);
                controller.addToTextFlow(languages.getString(26) + "\n", Color.RED);
            }
        });
    }

    /**
     * Schreibt im Lobbychat, dass jemands Spiel gecrashed ist
     *
     * @param pUsername Derjenige, dem sein Spiel gecrashed ist
     */
    public void addToLobbyChatCrashedIt(String pUsername) {
        Platform.runLater(() -> {
            if (!pUsername.equals("null")) {
                controller.addToTextFlow(pUsername, Color.RED);
                controller.addToTextFlow(languages.getString(41) + "\n", Color.RED);
            }
        });
    }

    /**
     * Schreibt im Lobbychat, dass jemand dem Spiel beigetreten ist
     *
     * @param pUsername Derjenige, der beigetreten ist
     */
    public void addToLobbyChatJoinedTheGame(String pUsername) {
        Platform.runLater(() -> {
            controller.addToTextFlow(pUsername, Color.GREEN);
            controller.addToTextFlow(languages.getString(27) + "\n", Color.GREEN);
        });
    }

    /**
     * Zeigt das Spielanfragenfenster an
     *
     * @param pFromUsername Der Anfrager, der dich zum Spiel herausfordern
     * möchte
     */
    public void zeigeRequestAn(String pFromUsername) {
        Platform.runLater(() -> {
            if (stage.getScene().equals(scene)) {//Wenn man schon in der Lobby ist
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                this.request = alert;
                alert.setTitle(languages.getString(47));
                alert.setHeaderText(pFromUsername + languages.getString(32));
                alert.setContentText(languages.getString(42));

                ButtonType accept = new ButtonType(languages.getString(76));
                ButtonType decline = new ButtonType(languages.getString(77));
                ButtonType cancel = new ButtonType(languages.getString(78), ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(accept, decline, cancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == accept) {
                    String[] befehl = {"accept", pFromUsername};
                    client.sendObject(befehl);
                    setAllListButtonsEnabled(true);
                } else if (result.get() == decline) {
                    String[] befehl = {"declined", pFromUsername};
                    client.sendObject(befehl);
                    setAllListButtonsEnabled(true);
                } else {
                    System.out.println("Nicht reagiert");
                    String[] befehl = {"requestunanswered", pFromUsername};
                    client.sendObject(befehl);
                    setAllListButtonsEnabled(true);
                }
            } else {
                this.scheduledTasks.put("zeigeRequestAn", pFromUsername);
            }
        });
    }

    /**
     * Entfernt das Spielanfragenfenster, falls derjenige nicht reagiert hat
     * (z.B. AFK)
     */
    public void entferneRequest() {
        Platform.runLater(() -> {
            this.request.close();
        });
    }

    /**
     * Setzt alle Knöpfe in der Spielerliste für denjenigen auf unanklickbar,
     * bzw stellt sie wieder klickbar her
     *
     * @param flag true = wieder anklickbar, false = nicht mehr anklickbar
     */
    public void setAllListButtonsEnabled(boolean flag) {
        controller.setAllButtonsEnabled(flag);
    }

    /**
     * Wird ausgelöst, wenn der Spieler einen Knopf neben einem anderen Spieler
     * anklickt
     *
     * @param pUsername Derjenige, den der Spieler
     * heruasfordern/Zusehen/Ebenfalls zusehen möchte
     * @param pStatus 0 = Status Online -> Ich möchte ihn herausfordern, 1 =
     * Status Ingame -> Ich möchte zusehen, 2 = Status Zuschauend -> Ich möchte
     * ebenfalls demjenigen zusehen
     */
    public void playOrWatchButtonClicked(String pUsername, int pStatus) {
        Platform.runLater(() -> {
            this.setAllListButtonsEnabled(false);
            switch (pStatus) {
                case 0://Status Online -> Ich möchte ihn herausfordern
                    String[] befehl = {"play", pUsername};
                    client.sendObject(befehl);
                    break;
                case 1://Status Ingame -> Ich möchte zusehen
                    String[] befehl2 = {"watch", pUsername};
                    client.sendObject(befehl2);
                    break;
                case 2://Status Zuschauend -> Ich möchte ebenfalls demjenigen zusehen
                    String[] befehl3 = {"watchalso", pUsername};
                    client.sendObject(befehl3);
                    break;
            }
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

    /**
     * Führt Aufgaben aus, die in der Zeit zwischen dem Spielende und dem
     * Lobbyeintritt (max 2 Sekunden) angefallen sind
     */
    public void executescheduledTasks() {
        Platform.runLater(() -> {
            for (Map.Entry<String, String> entry : scheduledTasks.entrySet()) {
                String method = entry.getKey();
                String parameter = entry.getValue();
                switch (method) {
                    case "zeigeRequestAn":
                        this.zeigeRequestAn(parameter);
                        scheduledTasks.remove(method);
                        break;
                }
            }
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
            alert.setTitle(languages.getString(43));
            alert.setHeaderText(languages.getString(44));
            alert.setContentText(pFehler);
//            DialogPane dialogPane = alert.getDialogPane();
//            dialogPane.getStylesheets().add(getClass().getResource("../general/CSSDialog.css").toExternalForm());
//            dialogPane.getStyleClass().add("standardDialog");
            alert.showAndWait();
        });
    }

    @Override
    public void setSize(int[] size) {
        controller.anchorPane.setPrefSize(size[0], size[1]);
    }

}
