/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiLobby;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import general.Language;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import networking.ClientManager;

/**
 * FXML Controller class
 *
 * @author Josua Frank
 */
public class FXMLLobbyController implements Initializable {

    @FXML
    public AnchorPane anchorPane;
    @FXML
    private TextFlow fxTextFlowChat;
    @FXML
    private TextField fxTextFieldChat;
    @FXML
    private VBox fxVBoxPlayers;
    @FXML
    private VBox fxVBoxScores;
    @FXML
    private ScrollPane scrollpane;
    private ClientManager client;
    private Language languages;
    private LobbyOberflaeche oberflaeche;
    private final HashMap<String, Button> buttons = new HashMap<>();
    private final String font = "Segoe UI";

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    /**
     * Wird aufgerufen, wenn man im Chattextfeld eine Taste drückt
     *
     * @param event Das Event, das die gedrückte Taste beinhaltet
     */
    public void fxTextFieldChatKeyPressed(Event event) {
        KeyEvent evt = (KeyEvent) event;
        if (evt.getCode().toString().equals("ENTER") && fxTextFieldChat.getText().length() > 0) {
            if (fxTextFieldChat.getText().length() > 100) {
                oberflaeche.zeigeFehlerAn(languages.getString(91).replace("[NUMBER]", "100"));
                fxTextFieldChat.setText("");
            } else {
                String[] befehl = {"lobbychat", fxTextFieldChat.getText()};
                client.sendObject(befehl);
                fxTextFieldChat.setText("");
            }
        }
    }

    /**
     * Speichert die Referenz zur Lobbyoberfläche
     *
     * @param pOberflaeche Die Referenz zur Lobyoberfläche
     */
    public void setOberflaeche(LobbyOberflaeche pOberflaeche) {
        this.oberflaeche = pOberflaeche;

    }

    /**
     * Baut die Vertikale Box auf der linken Seite des Spieles, in der die
     * Spieler mit ihren Status und einem Knopf eingezeigt werden.
     *
     * @param onlineUsers Die HashMap(String username, int status) mit den
     * jeweiligen Status (0 = in Lobby, 1 = Spielt gerade 2 = schaut gerade
     * einem Spiel zu)
     */
    public void buildPlayersVBox(HashMap<String, Integer> onlineUsers) {
        fxVBoxPlayers.getChildren().clear();
        Text title = new Text(languages.getString(48));
        title.setFont(Font.font(font, 18));
        title.setFill(Color.valueOf("0078cc"));
        fxVBoxPlayers.getChildren().add(title);
        GridPane fxGridPanePlayers = new GridPane();
        fxGridPanePlayers.setPadding(new Insets(5));
        int counter = 0;
        for (Map.Entry<String, Integer> entry : onlineUsers.entrySet()) {
            String username = entry.getKey();
            Integer pStatus = entry.getValue();
            String aktions = "";
            String status = "";
            Color color = Color.BLACK;
            if (pStatus != null) {
                switch (pStatus) {//0 = in Lobby, 1 = Spielt gerade 2 = schaut gerade einem Spiel zu
                    case 0:
                        aktions = languages.getString(28);
                        status = languages.getString(30);
                        color = Color.GREEN;
                        break;
                    case 1:
                        aktions = languages.getString(29);
                        status = languages.getString(31);
                        color = Color.BLUE;
                        break;
                    case 2:
                        aktions = languages.getString(39);
                        status = languages.getString(38);
                        color = Color.valueOf("0078cc");
                        break;
                    default:
                        break;
                }
            }
            Text fxTextUsername = new Text(username);
            fxTextUsername.setFont(Font.font(font, 16));
            fxGridPanePlayers.add(fxTextUsername, 0, counter);
            Text fxTextStatus = new Text("  " + status + "  ");
            fxTextStatus.setFont(Font.font(font, 16));
            fxTextStatus.setFill(color);
            fxGridPanePlayers.add(fxTextStatus, 1, counter);
            if (!username.equals(client.getUsername())) {//Wenn es ich NICHT selbst bin, soll noch ein Button hin
                Button fxButtonActions = new Button(aktions);
                fxButtonActions.setOnAction((ActionEvent e) -> {
                    oberflaeche.playOrWatchButtonClicked(username, pStatus);//Falls es so nciht klappt, mach es mit einer Hashmaop username, status
                });
                buttons.put(username, fxButtonActions);
                fxGridPanePlayers.add(fxButtonActions, 2, counter);
            }
            counter++;
            RowConstraints row = new RowConstraints();
            row.setMinHeight(40);
            fxGridPanePlayers.getRowConstraints().add(row);
        }
        fxVBoxPlayers.getChildren().add(fxGridPanePlayers);
    }

    /**
     * Baut die Vertikale Box auf der rechten Seite des Spieles, in der die
     * Spieler mit ihren Scores angezeigt werden.
     *
     * @param scores Die HashMap(String username, int[] scores) mit den
     * jeweiligen Scores (int[0] = wins, int[1] = defeats, int[2] draws)
     */
    public void buildPlayersScoresList(HashMap<String, int[]> scores) {
        fxVBoxScores.getChildren().clear();
        Text title = new Text(languages.getString(73));
        title.setFont(Font.font(font, 18));
        title.setFill(Color.valueOf("0078cc"));
        fxVBoxScores.getChildren().add(title);

        GridPane fxGridPaneScoresList = new GridPane();
        Text fxTextGamesPlayedHeader = new Text(languages.getString(74));
        fxTextGamesPlayedHeader.setFont(Font.font(font, 16));
        fxGridPaneScoresList.add(fxTextGamesPlayedHeader, 1, 0);
        Text fxTextKDHeader = new Text(languages.getString(75));
        fxTextKDHeader.setFont(Font.font(font, 16));
        fxGridPaneScoresList.add(fxTextKDHeader, 2, 0);
        ColumnConstraints cc = new ColumnConstraints(90, 90, 130);
        fxGridPaneScoresList.getColumnConstraints().addAll(cc, cc, cc);

        int counter = 0;
        for (Map.Entry<String, int[]> entrySet : scores.entrySet()) {
            String username = entrySet.getKey();
            int[] values = entrySet.getValue();
            int gamesPlayed = values[0] + values[1] + values[2];
            int kd = calculateWinRatioOnAllGames(values[0], values[1], values[2]);

            Text fxTextUsername = new Text(username);
            fxTextUsername.setFont(Font.font(font, 16));
            fxTextUsername.setFill(Color.valueOf("0078cc"));
            fxGridPaneScoresList.add(fxTextUsername, 0, (counter + 1));
            Text fxTextGamesPlayed = new Text(String.valueOf(gamesPlayed));
            fxTextGamesPlayed.setFont(Font.font(font, 16));
            if (gamesPlayed > 100) {
                fxTextGamesPlayed.setFill(Color.GOLD);
            } else if (gamesPlayed > 50) {
                fxTextGamesPlayed.setFill(Color.SILVER);
            } else if (gamesPlayed > 25) {
                fxTextGamesPlayed.setFill(Color.BROWN);
            } else {
                fxTextGamesPlayed.setFill(Color.BLACK);
            }
            GridPane.setHalignment(fxTextGamesPlayed, HPos.CENTER);
            fxGridPaneScoresList.add(fxTextGamesPlayed, 1, (counter + 1));
            Text fxTextKD = new Text(String.valueOf(kd));
            fxTextKD.setFont(Font.font(font, 16));
            if (kd < 40) {
                fxTextKD.setFill(Color.RED);
            } else if (kd > 60) {
                fxTextKD.setFill(Color.GREEN);
            } else {
                fxTextKD.setFill(Color.ORANGE);
            }
            GridPane.setHalignment(fxTextKD, HPos.CENTER);
            fxGridPaneScoresList.add(fxTextKD, 2, (counter + 1));
            counter++;
        }
        fxVBoxScores.getChildren().add(fxGridPaneScoresList);
    }

    /**
     * Berechnet das Kills/Deaths bzw das Wins/Draws Verhältnis
     *
     * @param wins Die Anzahl der Gewinne
     * @param defeats Die Anzahl der Unterlegenheiten
     * @param draws Die Anzahl von Unentschieden
     * @return Das Verhältnis als Kommazahl auf 2 Nachkommastellen gerundet
     *
     * TODO: Refresh benötigt!
     */
    public int calculateWinRatioOnAllGames(int wins, int defeats, int draws) {//6 wins 5 defeats = 1.2
        int allGames = wins + defeats;
        if (allGames > 0) {
            double winRatio = wins / (double) allGames;
            winRatio *= 100;
            System.out.println(winRatio + "; " + wins + "; " + defeats + "; " + draws);
            return (int) winRatio;
        } else {
            return 0;
        }
    }

    /**
     * Speichert die Referenz des Clienten und speichert bei dieser Gelegenheit
     * auch gleich die Languages-Referenz
     *
     * @param pClient Die Referenz zum Clienten
     */
    public void setClient(ClientManager pClient) {
        this.client = pClient;
        this.languages = client.getLanguages();
    }

    /**
     * Setzt den Satartfocus auf den Chat, sodass man direkt loslegen kann, zu
     * schreiben
     */
    public void startFocus() {
        fxTextFieldChat.requestFocus();
    }

    /**
     * Fügt eine Nachricht zum Chatfeld hinzu, VORICHT: '\n' für eine neue Zeile
     * nicht vergessen
     *
     * @param pMessage Die anzuzeigende Nachricht
     * @param pColor Die Farbe der anzuzeigenden Nachricht, bei mehreren Farben,
     * diese Methode mehrmals aufrufen, aber das '\n' weglassen
     */
    public void addToTextFlow(String pMessage, Color pColor) {
        this.windowResized();
        Text text = new Text(pMessage);
        text.setFont(Font.font("Arial", 16));
        text.setFill(pColor);
        fxTextFlowChat.getChildren().addAll(text);
        scrollpane.setVvalue(fxTextFlowChat.getChildren().size());
        while (fxTextFlowChat.getChildren().size() > 250) {
            fxTextFlowChat.getChildren().remove(0, fxTextFlowChat.getChildren().size() - 250);
        }
    }

    /**
     * Setzt alle Knöpfe in der Spielerliste für denjenigen auf unanklickbar,
     * bzw stellt sie wieder klickbar her
     *
     * @param flag true = wieder anklickbar, false = nicht mehr anklickbar
     */
    public void setAllButtonsEnabled(boolean flag) {
        for (Map.Entry<String, Button> entry : buttons.entrySet()) {
            String key = entry.getKey();
            Button value = entry.getValue();
            value.setDisable(!flag);
        }
    }

    /**
     * Wird aufgerufen, wenn die Fenstergröße geaendert wird.
     */
    public void windowResized() {
        fxTextFlowChat.setMaxWidth(scrollpane.getWidth() - 10);
        scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

}
