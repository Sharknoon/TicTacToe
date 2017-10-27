/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiGame;

import general.Language;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import networking.ClientManager;

/**
 * FXML Controller class
 *
 * @author Josua Frank
 */
public class FXMLGameController implements Initializable {

    @FXML
    public AnchorPane anchorPane;
    @FXML
    private TextFlow fxTextFlowChat;
    @FXML
    private TextField fxTextFieldChat;
    @FXML
    private TextFlow fxTextFlowUsernameAndStatus = new TextFlow();
    @FXML
    private Button fxButtonBack;
    @FXML
    private TextFlow fxTextFlowHeadline;
    @FXML
    private GridPane fxGridPaneGamefield;
    @FXML
    private ScrollPane scrollpane;
    @FXML
    private TextFlow fxTextFlowInstructions;
    private ClientManager client;
    private Language languages;
    private HashMap<String, Text> fxTextStaten;
    private String player1, player2;
    private Boolean clickAllowed = false;
    private final Color colorPlayer1 = Color.BLUE;
    private final Color colorPlayer2 = Color.RED;
    private final String font = "Segoe UI";
    private int counter = 10;
    private String player;
    Timeline timer;

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
     * Wird aufgerufen, wennn eine Taste im Chattextfeld gedrückt wurde, bei
     * ENTER sendet sie den Text und leert das Feld
     *
     * @param event Das Event beinhaltet die gedrückte Taste, zum überprüfen, ob
     * sie ENTER ist
     */
    public void fxTextFieldChatKeyPressed(Event event) {
        KeyEvent evt = (KeyEvent) event;
        if (evt.getCode().toString().equals("ENTER") && fxTextFieldChat.getText().length() > 0) {
            if (fxTextFieldChat.getText().length() > 100) {
                this.zeigeFehlerAn(languages.getString(91).replace("[NUMBER]", "100"));
                fxTextFieldChat.setText("");
            } else {
                String[] befehl = {"gamechat", fxTextFieldChat.getText()};
                client.sendObject(befehl);
                fxTextFieldChat.setText("");
            }
        }
    }

    /**
     * Wird aufgerufen, wenn der Knopf oben links (Zurück zur Lobby) geklickt
     * wurde. Überprüft, ob man selbst ein Spieler ist, falls ja, wird das Spiel
     * abgebrochen, ansonsten sieht man nur nicht länger zu
     *
     * @param event Unwichtig
     */
    public void fxButtonBackPressed(ActionEvent event) {
        if (!(player1.equals(client.getUsername()) || player2.equals(client.getUsername()))) {
            client.setScene(1);
            String[] befehl = {"nolongerwatching"};
            client.sendObject(befehl);
        } else {
            String[] befehl = {"quitgame"};
            client.sendObject(befehl);
        }
    }

    /**
     * Baut die Spielerliste links von dem Spielfeld
     *
     * @param onlineUsers Die anzuzeigenden Spieler mit Ihrem Status (0 = in
     * Lobby, 1 = Spielt gerade 2 = schaut gerade einem Spiel zu)
     */
    public void buildVBoxPlayers(HashMap<String, Integer> onlineUsers) {
        if (fxTextStaten == null) {
            fxTextStaten = new HashMap<>();
        } else {
            fxTextStaten.clear();
        }

        fxTextFlowUsernameAndStatus.getChildren().clear();
        Text title = new Text(languages.getString(48) + "\n");
        title.setFont(Font.font(font, 18));
        title.setFill(Color.valueOf("0078cc"));
        fxTextFlowUsernameAndStatus.getChildren().add(title);
        for (Map.Entry<String, Integer> entry : onlineUsers.entrySet()) {
            String username = entry.getKey();
            Integer pStatus = entry.getValue();
            String aktions = "";
            String status = "";
            if (pStatus != null) {
                switch (pStatus) {//0 = in Lobby, 1 = Spielt gerade 2 = schaut gerade einem Spiel zu
                    case 0:
                        aktions = languages.getString(28);
                        status = languages.getString(30);
                        break;
                    case 1:
                        aktions = languages.getString(29);
                        status = languages.getString(31);
                        break;
                    case 2:
                        aktions = languages.getString(39);
                        status = languages.getString(38);
                        break;
                    default:
                        break;
                }
            }
            fxTextFlowUsernameAndStatus.setMaxWidth(230);
            Text fxTextUsername = new Text("\n  " + username + "\n");
            Text fxTextStatus = new Text("  " + status + "\n");
            fxTextUsername.setFont(Font.font(font, 16));
            fxTextStatus.setFont(Font.font(font, 16));
            if (username.equals(player1)) {
                fxTextUsername.setFill(colorPlayer1);
            } else if (username.equals(player2)) {
                fxTextUsername.setFill(colorPlayer2);
            } else {
                fxTextUsername.setFill(Color.BLACK);
            }
            fxTextFlowUsernameAndStatus.getChildren().addAll(fxTextUsername, fxTextStatus);
            this.fxTextStaten.put(username, fxTextStatus);
        }
    }

    /**
     * Setzt den Clienten und setzt bei der Gelegenheit auch noch die
     * Sprachdatei und den Text des Zurück-Knopfes
     *
     * @param pClient Der Clienten
     */
    public void setClient(ClientManager pClient) {
        this.client = pClient;
        this.languages = client.getLanguages();
        fxButtonBack.setText(languages.getString(51));
    }

    /**
     * Setzt den Focus der Tasteneingaben auf das Chattextfeld
     */
    public void startFocus() {
        fxTextFieldChat.requestFocus();
    }

    /**
     * Fügt eine Nachricht zur TextPane hinzu, ACHTUNG: '\n' für eine neue Zeile
     * nicht vergessen
     *
     * @param pMessage Die anzuzeigende Nachricht
     * @param pColor Die Farbe der anzuzeigenden Nachricht, bei mehreren Farben,
     * bitte diese Methode mehrmals ohne '\n' aufrufen
     */
    public void addToTextPane(String pMessage, Color pColor) {
        Text text = new Text(pMessage);
        text.setFont(Font.font(font, 16));
        text.setFill(pColor);
        fxTextFlowChat.getChildren().addAll(text);
        scrollpane.setVvalue(fxTextFlowChat.getChildren().size());
        while (fxTextFlowChat.getChildren().size() > 250) {
            fxTextFlowChat.getChildren().remove(0, fxTextFlowChat.getChildren().size() - 250);
        }
    }

    /**
     * Sehr interressante Methode ;) Grundsätzlich überschreibt diese Klasse den
     * Status eines Spielers oder Zuschauers, wenn sich dieser in diesem Spiel
     * befindet. So steht z.B. statt 'Schaut einem Spiel zu' 'Schaut diesem
     * Spiel zu'
     *
     * @param members Die Spielmitglieder (Spieler und Zuschauer) als ArrayList
     */
    public void overrideStatusOfPlayers(ArrayList<String> members) {
        if (members == null || player1 == null) {
            System.err.println("Timing Problem!!");
            return;
        }
        for (String gameMember : members) {//Setzt alle Game-Members in der Game-Liste auf Schaut zu
            try {
                fxTextStaten.get(gameMember).setText("  " + languages.getString(49) + "\n");
                fxTextStaten.get(gameMember).setFill(Color.GREEN);
            } catch (Exception e) {
                System.err.println("DAS SOLLTE NICHT PASSIEREN (In der Game-Member-Liste ist mind. 1 Namen vorhanden, der nicht in der LobbyListe ist, Serverfehler!)");
                System.err.println("GameMember laut Server: " + gameMember + ", ist aber nicht in der Liste");
                for (Map.Entry<String, Text> entry : fxTextStaten.entrySet()) {
                    String key = entry.getKey();
                    Text value = entry.getValue();
                    System.err.print(", " + key);
                }
            }
        }
        try {
            if (!player1.equals("")) {
                fxTextStaten.get(player1).setText("  " + languages.getString(50) + "\n");
                fxTextStaten.get(player1).setFill(Color.GREEN);
            }
            if (!player2.equals("")) {
                fxTextStaten.get(player2).setText("  " + languages.getString(50) + "\n");
                fxTextStaten.get(player2).setFill(Color.GREEN);
            }
        } catch (Exception e) {
            System.err.println("DAS SOLLTE NICHT PASSIEREN (Die zwei Spieler sind nicht in der GameListe, also auch der LobbyListe vorhanden, ServerFehler!)");
            System.err.println("Spieler1 laut Server: " + player1 + ", Spieler2 laut Server: " + player2 + ", sind aber nicht in der Liste");
            for (Map.Entry<String, Text> entry : fxTextStaten.entrySet()) {
                String key = entry.getKey();
                Text value = entry.getValue();
                System.err.print(", " + key);
            }
        }
    }

    /**
     * Setzt den Versus Titel im Spiel, z.B. Tom vs Michael
     *
     * @param pFirstPlayer Der Name des ersten Spielers
     * @param pLastPlayer Der Name des zweiten Spielers
     */
    public void setHeadLine(String pFirstPlayer, String pLastPlayer) {
        this.player1 = pFirstPlayer;
        this.player2 = pLastPlayer;
        Text firstname = new Text(pFirstPlayer);
        firstname.setFont(Font.font(font, 24));
        firstname.setFill(colorPlayer1);
        Text vs = new Text(" vs ");
        vs.setFont(Font.font(font, 20));
        vs.setFill(Color.BLACK);
        Text lastname = new Text(pLastPlayer);
        lastname.setFont(Font.font(font, 24));
        lastname.setFill(colorPlayer2);
        fxTextFlowHeadline.getChildren().clear();
        fxTextFlowHeadline.getChildren().addAll(firstname, vs, lastname);
    }

    public void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timeline(new KeyFrame(
                Duration.millis(1000),
                event -> {//Ich habe meinen Zug nicht gemacht
                    Platform.runLater(() -> {
                        setInstructions(player);
                    });
                }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }

    /**
     * Schreibt unter dem Spielfeld die Anweisungen, z.B. wer an der Reihe ist
     *
     * @param pPlayer Der Spieler, der an der Reihe ist
     */
    public void setInstructions(String pPlayer) {
        if (pPlayer.equals(player)) {//Wenn der Spieler noch der selbe ist
            counter--;
        } else {
            counter = 10;
            if (timer != null) {
                timer.stop();
            }
        }
        this.player = pPlayer;
        if (pPlayer.equals(client.getUsername())) {//Wenn ich dran bin
            this.clickAllowed = true;
            Text instruction = new Text(languages.getString(63) + " (" + counter + ")");
            instruction.setFont(Font.font(font, 22));
            instruction.setFill(Color.BLACK);
            fxTextFlowInstructions.getChildren().clear();
            fxTextFlowInstructions.getChildren().add(instruction);
        } else {
            String instruction = languages.getString(67) + " (" + counter + ")";
            Text instruction2 = new Text(instruction.replace("[PLAYER]", pPlayer));
            instruction2.setFont(Font.font(font, 22));
            instruction2.setFill(Color.BLACK);
            fxTextFlowInstructions.getChildren().clear();
            fxTextFlowInstructions.getChildren().add(instruction2);
        }
    }

    /**
     * Baut das Spielfeld aufgrund der HashMap(Spielfeldnummer von 0-8,
     * Username) auf
     *
     * @param gameField Integer = Spielfeldnummer von 0-8, String = Username
     */
    public void buildGameField(HashMap<Integer, String> gameField) {//Integer = Spielfeldnummer von 0-8, String = Username
        Node node;//Ein Knoten, statt einem Canvas, so kann man das X und das O später eventuell durch ein Bils ersetzen oder sogar noch etwas anderes
        Integer gamefieldNumberFromZero;
        String username;
        int[] matrix;
        fxGridPaneGamefield.add(this.getField(), 0, 1);
        for (Map.Entry<Integer, String> entry : gameField.entrySet()) {
            gamefieldNumberFromZero = entry.getKey();
            username = entry.getValue();
            System.out.println("Feldnummer: " + gamefieldNumberFromZero + ", Benutzername: " + username);
            if (username.equals("")) {
                node = this.getEmpty();
                matrix = this.getMatrixNumberFromGamefieldNumber(gamefieldNumberFromZero);
                node.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
                    gamefieldClicked(entry.getKey());
                });
                fxGridPaneGamefield.add(node, matrix[0], matrix[1]);
            } else if (username.equals(player1)) {
                node = this.getX();//Spieler1 bekommt das X
                matrix = this.getMatrixNumberFromGamefieldNumber(gamefieldNumberFromZero);
                fxGridPaneGamefield.add(node, matrix[0], matrix[1]);
                GridPane.setHalignment(node, HPos.CENTER);
                GridPane.setValignment(node, VPos.CENTER);
            } else if (username.equals(player2)) {
                node = this.getO();//Spieler2 bekommt das O
                matrix = this.getMatrixNumberFromGamefieldNumber(gamefieldNumberFromZero);
                fxGridPaneGamefield.add(node, matrix[0], matrix[1]);
                GridPane.setHalignment(node, HPos.CENTER);
                GridPane.setValignment(node, VPos.CENTER);
            } else {
                System.err.println("Fehler, Spielername entspricht nicht den Kriterien: " + username);
            }
        }
    }

    /**
     * Wird aufgerufen, wenn man auf ein Feld geklickt hat
     *
     * @param gamefieldNumberFromZero Die Feldnummer (0-8) auf die man geklickt
     * hat
     */
    public void gamefieldClicked(int gamefieldNumberFromZero) {
        if (clickAllowed) {
            clickAllowed = false;
            System.out.println("CLICKED!: " + gamefieldNumberFromZero);
            String[] befehl = {"turned", String.valueOf(gamefieldNumberFromZero)};
            client.sendObject(befehl);
        }
    }

    /**
     * Konvertiert eine Spielfeldnummer (0-8) in eine Koordinatennummer (z.B.
     * 2,3)
     *
     * @param gameFieldnumber Die Spielfeldnummer von 0-8
     * @return Das int-Array mit der Koordinatennummer (Feldnummer 3 =
     * Koordinatennummern 0, 1)
     */
    public int[] getMatrixNumberFromGamefieldNumber(int gameFieldnumber) {
        int[] matrix = new int[2];
        if (gameFieldnumber > 5) {
            matrix[0] = gameFieldnumber - 6;
            matrix[1] = 2;
        } else if (gameFieldnumber > 2) {
            matrix[0] = gameFieldnumber - 3;
            matrix[1] = 1;
        } else if (gameFieldnumber >= 0) {
            matrix[0] = gameFieldnumber;
            matrix[1] = 0;
        }
        return matrix;
    }

    /**
     * Gibt das Canvas (Zeichenabjekt) für das Kreuz zurück
     *
     * @return Das Zeichenobjekt als Canvas für ein Kreuz
     */
    public Canvas getX() {
        Canvas canvas = new Canvas(90, 90);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(colorPlayer1);
        gc.setLineWidth(5);
        gc.strokeLine(75, 15, 15, 75);//x1, y1, x2, y2
        gc.strokeLine(15, 15, 75, 75);
        return canvas;
    }

    /**
     * Gibt das Canvas (Zeichenabjekt) für den Kreis zurück
     *
     * @return Das Zeichenobjekt als Canvas für ein Kreis
     */
    public Canvas getO() {
        Canvas canvas = new Canvas(90, 90);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(colorPlayer2);
        gc.setLineWidth(5);
        gc.strokeOval(15, 15, 60, 60);
        return canvas;
    }

    /**
     * Gibt ein leeres Canvas als Platzhalter für ein noch nicht belegtes
     * Spielfeld zurück
     *
     * @return Das leere Zeichenobjekt
     */
    public Canvas getEmpty() {
        Canvas canvas = new Canvas(90, 90);
        return canvas;
    }

    /**
     * Gibt das Canvas für die Spielfeldlinien zurück
     *
     * @return Das Zeichejekt für die Spielfeldlinien
     */
    public Canvas getField() {
        Canvas canvas = new Canvas(300, 300);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeLine(100, 0, 100, 300);
        gc.strokeLine(200, 0, 200, 300);
        gc.strokeLine(0, 100, 300, 100);
        gc.strokeLine(0, 200, 300, 200);
        return canvas;
    }

    /**
     * Überprüft, ob der zu entfernende Spieler kein Zuschauer ist, falls dies
     * Eintritt, setzt er ihn auf null
     *
     * @param pPlayer Der zu entfernende Spieler
     */
    public void removePlayer(String pPlayer) {
        if (player1.equals(pPlayer)) {
            player1 = "";
        } else if (player2.endsWith(pPlayer)) {
            player2 = "";
        }
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
}
