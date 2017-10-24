/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import general.Constants;
import game.database.GameDatabase;
import game.networking.PlayerManager;
import general.InstanceManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 *
 * @author Niklas
 */
public class Game {

    private final PlayerManager player1;
    private final PlayerManager player2;
    private PlayerManager activePlayer;
    private final Pitch pitch = new Pitch();
    private final HashMap<String, PlayerManager> gameMemberList = new HashMap<>();
    private final GameDatabase gameDatabase;
    private InstanceManager iManager;
    private Timeline timeline;

    public Game(InstanceManager pIManager, PlayerManager pPlayer1, PlayerManager pPlayer2) {
        iManager = pIManager;
        this.player1 = pPlayer1;
        this.player2 = pPlayer2;
        this.gameDatabase = iManager.getGameDatabase();
        iManager.printLine(player1.getUsername() + " fordert " + player2.getUsername() + " zu einem Duell heraus, möge das Spiel beginnen!");
        gameMemberList.put(player1.getUsername(), player1);
        gameMemberList.put(player2.getUsername(), player2);
        gameDatabase.setIngame(1, player1.getUsername());
        gameDatabase.setIngame(1, player2.getUsername());
        String[] command = {"showgame"};
        this.sendToEveryGameMember(command);//Öffnet die Spielescene
        String[] command2 = {"headline", player1.getUsername(), player2.getUsername()};
        this.sendToEveryGameMember(command2);//Sendet Player1 vs Player2, dadurch weiss der Client auch die 2 Spieler
        player1.reloadClientGUI();//Sendet allen Clienten die aktuellen Statusse aus der GameDatabase
        this.sendToEveryGameMember(this.getGameMemberNames());//Überschreibt die Statusse der Spieler, falls Sie in diesem Spiel sind (z.B. Schaut hier zu), hiermit lädt auch die gui neu
        this.gameStart();//Ab hier startet dann der Spiel Ablauf
    }

    /**
     * Hier beginnt das Spiel, hier wird der erste Spieler zufällig ausgewählt
     * und das Spielfeld versendet
     */
    private void gameStart() {
        int startPlayer = Math.round((float) Math.random());
        if (player1 != null) {
            if (startPlayer == 1) {
                activePlayer = player1;
            } else {
                activePlayer = player2;
            }
        }
        activePlayer.setAndStartTurnTimer();
        this.sendToEveryGameMember(pitch.getPitch());
        String[] command = {"turn", activePlayer.getUsername()};
        this.sendToEveryGameMember(command);
    }

    /**
     * Diese Methode versendet Objekte zu allen Spielmitgliedern
     *
     * @param object Das zu sendende Objekt
     */
    public void sendToEveryGameMember(Object object) {
        for (Map.Entry<String, PlayerManager> entry : gameMemberList.entrySet()) {
            PlayerManager gameMember = entry.getValue();
            gameMember.sendObject(object);
        }
    }

    /**
     * Fügt einen Zuschauer hinzu
     *
     * @param viewer Der zu hinzufügende Zuschauer
     */
    public void addViewer(PlayerManager viewer) {
        gameMemberList.put(viewer.getUsername(), viewer);
        gameDatabase.setIngame(2, viewer.getUsername());
        String[] command = {"showgame"};
        viewer.sendObject(command);
        String[] command2 = {"headline", player1.getUsername(), player2.getUsername()};
        viewer.sendObject(command2);//Sendet Player1 vs Player2, dadurch weiss der Client auch die 2 Spieler
        viewer.reloadClientGUI();//Lädt beim clienten die OnlineListe neu, auch die GameListe
        String[] command3 = {"newviewer", viewer.getUsername()};
        this.sendToEveryGameMember(command3);//Zeigt lediglich an, dass ein neuer Zuschauer dazugekommen ist
        this.sendToEveryGameMember(this.getGameMemberNames());//Überschreibt die GameList (bis dato = OnlineListe der Lobby) mit "Spielt dieses Spiel gerade" oder "schaut diesem Spiel gereade zu"
        this.sendToEveryGameMember(pitch.getPitch());//neues Spielfeld an Clienten schicken
        String[] command4 = {"turn", activePlayer.getUsername()};
        this.sendToEveryGameMember(command4);//Anzeigen, wer gerade an der Reihe ist
    }

    /**
     * Entfernt einen Zuschauer
     *
     * @param viewer Der zu entfernende Zuschauer
     */
    public void removeViewver(PlayerManager viewer) {
        gameMemberList.remove(viewer.getUsername());
        gameDatabase.setIngame(0, viewer.getUsername());
        viewer.reloadClientGUI();//Lädt beim clienten die OnlineListe neu
        viewer.setGame(null);
        String[] command = {Constants.USERNOLONGERWATCHING, viewer.getUsername()};
        this.sendToEveryGameMember(command);
        this.sendToEveryGameMember(this.getGameMemberNames());
    }

    /* Ablauf 
     prüfe, welcher Spieler gerade Zugrecht hat
     Spieler setzt
     prüfe Unentschieden
     wenn Unentschieden -> beende mit entsprechender Meldung
     prüfe Gewinn
     wenn Gewinn -> beende mit entsprechender Meldung
     */
    /**
     * Wechselt den aktiven Spieler
     */
    public void playerChange() {
        activePlayer.stopTurnTimer();
        if (activePlayer.equals(player1)) {
            activePlayer = player2;
        } else {
            activePlayer = player1;
        }
        activePlayer.setAndStartTurnTimer();
    }

    /**
     * Wird aufgerufen, wenn ein Spieler einen Zug vollbracht hat, hier wird
     * überprüft, ob der Zug Legitim war und ob er damit gewonnen hat
     *
     * @param fieldNumber Die Feldnummer, auf der der Spieler ein Kreuz/Kreis
     * gesetzt hat
     * @param spieler Der Spieler als GameManager
     */
    public void playerHasTurned(int fieldNumber, PlayerManager spieler) {
        if (pitch.isFieldEmpty(fieldNumber) && spieler.equals(activePlayer)) {//Zug validieren
            pitch.getPitch().put(fieldNumber, activePlayer.getUsername());
            String sieger = Logic.checkWin(pitch.getPitch());
            if (sieger != null) {//Ein Sieger steht fest
                gameDatabase.addWin(sieger);
                gameDatabase.addDefeat(getOtherPlayer(sieger));
                activePlayer.stopTurnTimer();
                this.setAndStartGameFinishTimer();
                this.sendToEveryGameMember(pitch.getPitch());
                String[] command = {"won", sieger};
                this.sendToEveryGameMember(command);//Spieler an Client schicken
                this.exitGame();
            } else if (pitch.getEmptyFields() == 0) {//Falls es unentschieden steht
                gameDatabase.addDraw(player1.getUsername());
                gameDatabase.addDraw(player2.getUsername());
                activePlayer.stopTurnTimer();
                this.setAndStartGameFinishTimer();
                this.sendToEveryGameMember(pitch.getPitch());
                String[] command = {"undecided"};
                this.sendToEveryGameMember(command);//Nachricht an client
                this.exitGame();
            } else {//Falls das Spiel einfach weitergeht
                this.sendToEveryGameMember(pitch.getPitch());//neues Spielfeld an Clienten schicken
                this.playerChange();//nächster Spieler ist am Zug
                String[] command = {"turn", activePlayer.getUsername()};
                this.sendToEveryGameMember(command);
            }
        } else {
            String[] command = {"misstake"};
            activePlayer.sendObject(command);//SPÄTER NOCH GENAUER SPEZIFIZIEREN!!!!, evtl auch noch turn schicken
        }
    }

    /**
     * Gibt eine Liste mit den Benutzernamen der Spieler zurück
     *
     * @return Die ArrayListe mit den Benutzernamen der Spieler
     */
    private ArrayList<String> getGameMemberNames() {
        ArrayList<String> members = new ArrayList<>();
        for (Map.Entry<String, PlayerManager> entry : gameMemberList.entrySet()) {
            String username = entry.getKey();
            members.add(username);
        }
        return members;
    }

    /**
     * Beendetdas Spiel regulär
     */
    public void exitGame() {
        for (Map.Entry<String, PlayerManager> entry : gameMemberList.entrySet()) {
            PlayerManager gameMember = entry.getValue();
            gameDatabase.setIngame(0, gameMember.getUsername());
            gameMember.setGame(null);
            iManager.getServerManager().getClientManager(gameMember.getUsername()).setState(1);
        }
        player1.reloadClientGUI();//Lädt bei allen die LobbyListe neu
    }

    /**
     * Bricht das Spiel ab
     *
     * @param intendedQuitting Wenn der Spieler absichtlich das Spiel beendet,
     * um dem Verlorenen Match zu entgehen
     * @param pUsernameOfQuitterPlayer Der Spieler, der das Spiel abgebrochen
     * hat
     */
    public void cancelGame(boolean intendedQuitting, String pUsernameOfQuitterPlayer) {
        gameDatabase.addDefeat(pUsernameOfQuitterPlayer);
        gameDatabase.addWin(getOtherPlayer(pUsernameOfQuitterPlayer));
        activePlayer.stopTurnTimer();
        for (Map.Entry<String, PlayerManager> entry : gameMemberList.entrySet()) {
            PlayerManager gameMember = entry.getValue();
            gameDatabase.setIngame(0, gameMember.getUsername());
            String[] command = {"quitgame", pUsernameOfQuitterPlayer};
            gameMember.sendObject(command);//Auch der Abbrecher braucht Quitgame, da er erst dann seine Lobby angezeigt bekommt
            gameMember.setGame(null);
            iManager.getServerManager().getClientManager(gameMember.getUsername()).setState(1);
        }
        player1.reloadClientGUI();//Lädt bei allen die LobbyListe neu
    }

    public void notReacted(String pUsernameOfLazyPlayer) {
        gameDatabase.addDefeat(pUsernameOfLazyPlayer);
        gameDatabase.addWin(getOtherPlayer(pUsernameOfLazyPlayer));
        for (Map.Entry<String, PlayerManager> entry : gameMemberList.entrySet()) {
            PlayerManager gameMember = entry.getValue();
            gameDatabase.setIngame(0, gameMember.getUsername());
            String[] command = {"quitgamenotreacted", pUsernameOfLazyPlayer};
            gameMember.sendObject(command);//Auch der Abbrecher braucht Quitgame, da er erst dann seine Lobby angezeigt bekommt
            gameMember.setGame(null);
            iManager.getServerManager().getClientManager(gameMember.getUsername()).setState(1);
        }
        player1.reloadClientGUI();//Lädt bei allen die LobbyListe neu
    }

    /**
     * Gibt zurück, ob der angegebene Benutzername ein Spieler ist oder nicht
     *
     * @param pUsername Der zu überprüfende Benutzername
     * @return Ob dieser Benutzer ein Spieler ist oder nicht
     */
    public boolean isPlayer(String pUsername) {
        return player1.getUsername().equals(pUsername) || player2.getUsername().equals(pUsername);
    }

    /**
     * Gibt den Spielern 3 Sekunden Zeit, sich das Ergebnis anzusehen, bevor sie
     * in die Lobby zurückgebracht werden, falls sie nicht schon vorher auf OK
     * klicken
     */
    public void setAndStartGameFinishTimer() {
        timeline = new Timeline(new KeyFrame(
                Duration.millis(3000),
                event -> {//Ich habe nicht reagiert
                    String[] command = {"closegamefinishmessage"};
                    this.sendToEveryGameMember(command);
                }));
        timeline.play();
    }

    /**
     * Gibt den konkurrierenden Spieler zurück
     *
     * @param pUsername Der Spieler A
     * @return Der Spieler B
     */
    private String getOtherPlayer(String pUsername) {
        if (pUsername.equals(player1.getUsername())) {
            return player2.getUsername();
        } else {
            return player1.getUsername();
        }
    }
}
