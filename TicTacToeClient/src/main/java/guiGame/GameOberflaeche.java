/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiGame;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import general.Language;
import general.Oberflaeche;
import guiLobby.LobbyOberflaeche;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import networking.ClientManager;

/**
 *
 * @author Josua Frank
 */
public class GameOberflaeche implements Oberflaeche {

    private final ClientManager client;
    private final Language languages;
    private Stage stage;
    private FXMLGameController controller = null;
    private Scene scene;
    private LobbyOberflaeche lobby;
    private Alert gameFinishAlert;

    public GameOberflaeche(ClientManager pClient, Stage pStage, LobbyOberflaeche pLobby) {
        this.stage = pStage;
        this.client = pClient;
        this.languages = client.getLanguages();
        this.lobby = pLobby;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLGame.fxml"));
        Parent root;
        try {
            root = (Parent) loader.load();
            controller = (FXMLGameController) loader.getController();
            scene = new Scene(root);
        } catch (IOException ex) {
            System.err.print("GameOberfläche konnte nicht gestartet werden: ");
            ex.printStackTrace(System.out);
            return;
        }

        controller.setClient(client);
        controller.startFocus();
    }

    /**
     * Fügt eine Nachricht zum Spielchat hinzu
     *
     * @param pUsername
     * @param pMessage die zu hinzufügende Nachricht
     * 
     */
    public void addToGameChat(String pUsername, String pMessage) {
        Platform.runLater(() -> {
            controller.addToTextPane(pUsername, Color.valueOf("0078cc"));
            controller.addToTextPane(": " + pMessage + "\n", Color.BLACK);
        });
    }

    /**
     * Schreibt im Spielchat, dass jemand nicht länger zusieht
     *
     * @param pUsername Derjenige, der nicht länger zusieht
     */
    public void addToGameChatNoLongerWatchingTheGame(String pUsername) {
        Platform.runLater(() -> {
            controller.removePlayer(pUsername);
            controller.addToTextPane(pUsername, Color.RED);
            controller.addToTextPane(languages.getString(37) + "\n", Color.RED);
        });
    }

    /**
     * Schreibt im Spielchat, dass jemand neues zusieht
     *
     * @param pUsername Der neue Zuschauer
     */
    public void addToGameChatWatchingTheGame(String pUsername) {
        Platform.runLater(() -> {
            controller.addToTextPane(pUsername, Color.GREEN);
            controller.addToTextPane(languages.getString(36) + "\n", Color.GREEN);
        });
    }

    /**
     * Baut die Spielerliste links von dem Spielfeld
     *
     * @param onlineUsers Die anzuzeigenden Spieler mit Ihrem Status (0 = in
     * Lobby, 1 = Spielt gerade 2 = schaut gerade einem Spiel zu)
     */
    public void buildList(HashMap<String, Integer> onlineUsers) {
        Platform.runLater(() -> {
            controller.buildVBoxPlayers(onlineUsers);
        });
    }

    /**
     * Sehr interressante Methode ;) Grundsätzlich überschreibt diese Klasse den
     * Status eines Spielers oder Zuschauers, wenn sich dieser in diesem Spiel
     * befindet. So steht z.B. statt 'Schaut einem Spielzu' 'Schaut diesem Spiel
     * zu'
     *
     * @param gameMembers Die Spielmitglieder (Spieler und Zuschauer) als
     * ArrayList
     */
    public void overrideStatusOfPlayers(ArrayList<String> gameMembers) {
        Platform.runLater(() -> {
            controller.overrideStatusOfPlayers(gameMembers);
        });
    }

    /**
     * Setzt den Versus Titel im Spiel, z.B. Tom vs Michael
     *
     * @param pFirstPlayer Der Name des ersten Spielers
     * @param pLastPlayer Der Name des zweiten Spielers
     */
    public void setHeadline(String pFirstPlayer, String pLastPlayer) {
        Platform.runLater(() -> {
            controller.setHeadLine(pFirstPlayer, pLastPlayer);
        });
    }

    /**
     * Baut das Spielfeld aufgrund der HashMap(Spielfeldnummer von 0-8,
     * Username) auf
     *
     * @param gameField Integer = Spielfeldnummer von 0-8, String = Username
     */
    public void buildGameField(HashMap<Integer, String> gameField) {
        Platform.runLater(() -> {
            controller.buildGameField(gameField);
        });
    }

    /**
     * Schreibt unter dem Spielfeld die Anweisungen, z.B. wer an der Reihe ist
     *
     * @param pPlayer Der Spieler, der an der Reihe ist
     */
    public void setInstructions(String pPlayer) {
        Platform.runLater(() -> {
            controller.setInstructions(pPlayer);
            controller.startTimer();
        });
    }

    /**
     * Erzeugt den Text, für die SpielendNachricht und tuft diese Nachricht dann
     * auf
     *
     * @param flag 0 = gewonnen, 1 = unentschieden
     * @param username Falls es einen Gewinner gibt, steht er hier
     */
    public void showMessage(int flag, String username) {//0 = gewonnen, 1 = unentschieden
        Platform.runLater(() -> {
            switch (flag) {
                case 0:
                    if (client.getUsername().equals(username)) {//Wenn ich der Gewinner bin
                        this.showGameFinishMessage(languages.getString(64).replace("[PLAYER]", username));
                    } else {
                        this.showGameFinishMessage(languages.getString(65).replace("[PLAYER]", username));
                    }
                    break;
                case 1:
                    this.showGameFinishMessage(languages.getString(66));
                    break;
                default:
                    System.err.println("Ungültiger Nachrichtenparameter: " + flag);
                    break;
            }
        });
    }

    /**
     * Zeigt die Spielendnachricht an
     *
     * @param string Die Spielendnachricht
     */
    private void showGameFinishMessage(String string) {
        Platform.runLater(() -> {
            gameFinishAlert = new Alert(Alert.AlertType.INFORMATION);
            gameFinishAlert.setTitle(languages.getString(46));
            gameFinishAlert.setHeaderText(null);
            gameFinishAlert.setContentText(string);
//          DialogPane dialogPane = alert.getDialogPane();
//          dialogPane.getStylesheets().add(getClass().getResource("../general/CSSDialog.css").toExternalForm());
//          dialogPane.getStyleClass().add("standardDialog");
            Optional<ButtonType> result = gameFinishAlert.showAndWait();
            if (result.isPresent()) {
                client.setScene(1);
            } else {
                client.setScene(1);
            }
        });
    }

    /**
     * Entfernt die Spielendnachricht, falls der Spieler nicht unternommen hat
     * (z.B. AFK) und der Server eine automatische Schließmeldung geschickt hat
     */
    public void removeGameFinishMessage() {
        Platform.runLater(() -> {
            gameFinishAlert.close();
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

    @Override
    public void setSize(int[] size) {
        controller.anchorPane.setPrefSize(size[0], size[1]);
    }

}
