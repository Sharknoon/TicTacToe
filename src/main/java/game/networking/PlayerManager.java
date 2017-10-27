/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.networking;

import game.logic.Game;

import core.networking.ClientManager;
import general.Constants;
import core.networking.ServerManager;
import game.database.GameDatabase;
import general.InstanceManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 *
 * @author Josua Frank
 */
public class PlayerManager {//Pro Client ein Object des GameMangers

    private Game game;
    private final ClientManager client;
    private boolean incomingRequestPending;
    private boolean outgoingRequestPending = false;
    private Timeline timerRequest;
    private Timeline timerTurn;

    public PlayerManager(ClientManager pClient) {
        client = pClient;
    }

    /**
     * Fügt der Spiele-Datenbank einen neuen Benutzer hinzu
     *
     * @param client Der zu hinzufügende Benutzer
     */
    public void receiveNewUser(ClientManager client) {
        InstanceManager.getGameDatabase().add(client.getUsername());
    }

    /**
     * Wird aufgerufen, wenn der Benutzer sich verbindet
     *
     */
    public void receiveConnect() {
        InstanceManager.getGameDatabase().setOnline(true, client.getUsername());
        String[] command = {Constants.USERJOINED, client.getUsername()};
        InstanceManager.getServerManager().sendToEveryone(command);
        reloadClientGUI();
    }

    /**
     * Wird aufgerufen, wenn ein Benutzer sich abmeldet
     *
     * @param crash Gibt an, ob die Abmeldung durch einen Absturz verursacht
     * wurde
     */
    public void receiveDisconnect(boolean crash) {
        GameDatabase gameDatabase = InstanceManager.getGameDatabase();
        ServerManager server = InstanceManager.getServerManager();
        gameDatabase.setOnline(false, client.getUsername());
        gameDatabase.setIngame(0, client.getUsername());
        if (crash) {
            String[] command = {Constants.USERCRASHED, client.getUsername()};
            server.sendToEveryone(command);//Letzter SOS, des untergegangenen Schiffes
        } else {
            String[] command = {Constants.USERLEFT, client.getUsername()};
            server.sendToEveryone(command);//Letzter SOS, befor das Schiff untergeht
        }
        reloadClientGUI();
        if (game != null) {
            if (game.isPlayer(client.getUsername())) {
                game.cancelGame(!crash, client.getUsername());
            } else {
                game.removeViewver(this);
            }
        }
    }

    /**
     * Empfängt Objekte vom Clienten, bis jetzt ungenutzt
     *
     * @param object Das zu empfangene Objekt
     */
    public void receiveObject(Object object) {
        //Alle Objecte vom Clienten kommen hier an, ausser Strings (Siehe receiveCommand)

    }

    /**
     * Empfängt Befehle vom Clienten
     *
     * @param parameters [0] = Befehl, [...] = Parameters
     */
    public void receiveCommand(String[] parameters) {
        //Alle Befehle ausser disconnect, register und login kommen hier vom Clienten an, der Chat wurde ebenfalls schon herausgefiltert (siehe receiveStringForChat)
        GameDatabase gameDatabase = InstanceManager.getGameDatabase();
        ServerManager server = InstanceManager.getServerManager();
        int state = client.getState();
        switch (parameters[0]) {
            case Constants.LOBBYCHATFROMCLIENT:
                if (state == 1) {
                    String lobbychatmessage = parameters[1];
                    InstanceManager.printLine(client.getUsername() + ": " + lobbychatmessage);
                    String[] command = {"lobbychat", client.getUsername(), lobbychatmessage};
                    server.sendToEveryone(command);
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.GAMEREQUEST://Ich frage das Spiel an
                if (state == 1) {
                    this.outgoingRequestPending = true;
                    PlayerManager empfaenger = server.getClientManager(parameters[1]).getPlayerManager();
                    if (empfaenger.getIncomingRequestPending()) {//Falls der Angefragte schon eine eine Anfrage hat
                        String[] command2 = {"IncomingRequestPending"};
                        client.sendObject(command2);
                    } else if (empfaenger.getOutgoingRequestPending()) {//Falls der Angefragte selbst schon am Anfragen ist
                        String[] command6 = {"OutgoingRequestPending"};
                        client.sendObject(command6);
                    } else {//Falls der Angefragte noch keine eine Anfrage hat und keine eigene Anfrage gestellt hat
                        String[] command2 = {Constants.REQUESTFROM, client.getUsername()};
                        server.sendTo(server.getClientManager(parameters[1]), command2);
                        empfaenger.setIncomingRequestPending(true);
                        empfaenger.setAndStartRequestTimer(client.getUsername());
                    }
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.ACCEPTED://Ich habe accepted, parameters[1] ist der Username des Herausforderers
                if (state == 1) {
                    timerRequest.stop();
                    //neues game erstellen
                    InstanceManager.printLine(client.getUsername() + " hat die Spielanfrage von " + parameters[1] + " angenommen.");
                    incomingRequestPending = false;
                    PlayerManager herausforderer = server.getClientManager(parameters[1]).getPlayerManager();
                    herausforderer.setOutgoingRequestPending(false);
                    server.getClientManager(parameters[1]).setState(2);
                    client.setState(2);
                    game = new Game(herausforderer, this);
                    herausforderer.setGame(game);
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.DECLINED://Ich habe abgelehnt
                if (state == 1) {
                    timerRequest.stop();
                    incomingRequestPending = false;
                    String[] command3 = {Constants.DECLINED, client.getUsername()};
                    ClientManager herausforderer2 = server.getClientManager(parameters[1]);
                    server.sendTo(herausforderer2, command3);
                    herausforderer2.getPlayerManager().setOutgoingRequestPending(false);
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.UNANSWERED://Ich habe auf Cancel geklickt
                if (state == 1) {
                    timerRequest.stop();
                    incomingRequestPending = false;
                    String[] command2 = {"unanswered", client.getUsername()};
                    ClientManager herausforderer3 = server.getClientManager(parameters[1]);
                    server.sendTo(herausforderer3, command2);//Sende zu meinem Herausforderer, dass ich nicht geantwortet habe
                    String[] command4 = {"notreacted"};
                    client.sendObject(command4);//Sende zu mir, dass ich nicht geantwortet habe (schließt z.b. mein Requestfenster)
                    herausforderer3.getPlayerManager().setOutgoingRequestPending(false);
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.GAMECHAT:
                if (state == 2) {
                    String gamechatmessage = parameters[1];
                    InstanceManager.printLine(client.getUsername() + ": " + gamechatmessage);
                    if (game != null) {
                        String[] command5 = {Constants.GAMECHAT, client.getUsername(), gamechatmessage};
                        game.sendToEveryGameMember(command5);
                    } else {
                        InstanceManager.printError("Es kam eine Gamenachricht an, obwohl " + client.getUsername() + " in keinem Spiel mehr ist, Nachricht wird verschluckt ☺");
                    }
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.WATCHGAME://Ich möchte parameters[0] zusehen
                if (state == 1) {
                    PlayerManager player = server.getClientManager(parameters[1]).getPlayerManager();
                    game = player.getGame();
                    client.setState(2);
                    game.addViewer(this);
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.WATCHALSOGAME://Ich möchte dem zusehen, dem parameters[0] zusieht
                if (state == 1) {
                    PlayerManager watcher = server.getClientManager(parameters[1]).getPlayerManager();
                    System.out.print("WATCHALSO: Watcher: " + watcher.getUsername());
                    PlayerManager player2 = server.getClientManager(watcher.getUsername()).getPlayerManager();
                    System.out.println(", Player2: " + player2.getUsername());
                    game = player2.getGame();
                    client.setState(2);
                    game.addViewer(this);
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.TURNED:
                if (state == 2) {
                    if (game != null) {
                        game.playerHasTurned(Integer.valueOf(parameters[1]), this);
                    }
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.ENDWATCH:
                if (state == 2) {
                    client.setState(1);
                    game.removeViewver(this);
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }

                break;
            case Constants.QUITGAME:
                if (state == 2) {
                    if (game != null) {
                        client.setState(1);
                        game.cancelGame(true, client.getUsername());
                    } else {
                        InstanceManager.printError("Sollte eigentlich durch ein cancelGame abgefangen sein!!, reconnect nach Verbindungsverlust??!?");
                    }
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            case Constants.TURN:
                if (state == 2) {
                    game.playerHasTurned(Integer.valueOf(parameters[1]), this);
                } else {
                    String[] command = {"msg", "Not allowed in the current context"};
                    client.sendObject(command);
                }
                break;
            default:
                InstanceManager.printError("Konnte Befehl nicht verarbeiten: " + parameters[0]);
                break;
        }
    }

    /**
     * Sendet ein Objekt an den Clienten
     *
     * @param object Das zu sendende Objekt
     */
    public void sendObject(Object object) {
        client.sendObject(object);
    }

    //Hier neue Methoden einfügen
    /**
     * Gibt den Benutzernamen zurück
     *
     * @return Der Benutzername
     */
    public String getUsername() {
        return client.getUsername();
    }

    /**
     * Gibt dem mir 10 Sekunden Zeit, auf die Anfrage zu antworten
     *
     * @param requester Der Anfrager
     */
    public void setAndStartRequestTimer(String requester) {
        ServerManager server = InstanceManager.getServerManager();
        timerRequest = new Timeline(new KeyFrame(
                Duration.millis(10000),
                event -> {//Ich habe nicht reagiert
                    this.incomingRequestPending = false;
                    String[] command = {"unanswered", client.getUsername()};
                    server.sendTo(server.getClientManager(requester), command);//Sende zu meinem Herausforderer, dass ich nicht geantwortet habe
                    String[] command2 = {"notreacted"};
                    client.sendObject(command2);//Sende zu mir, dass ich nicht geantwortet habe (schließt z.b. mein Requestfenster)
                }));
        timerRequest.play();
    }

    public void setAndStartTurnTimer() {
        timerTurn = new Timeline(new KeyFrame(
                Duration.millis(10000),
                event -> {//Ich habe meinen Zug nicht gemacht
                    if (game != null) {
                        game.notReacted(client.getUsername());
                    } else {
                        InstanceManager.printError("Irgendwo wurde vergessen, den Timer für " + getUsername() + " zu stoppen! Servercodeerror!");
                    }
                }));
        timerTurn.play();
    }

    public void stopTurnTimer() {
        if (timerTurn != null) {
            timerTurn.stop();
        }
    }

    /**
     * Gibt zurück, ob bereits eine Anfrage an mich gestellt wurde
     *
     * @return Ob ich bereits eine Anfrage erhalten habe
     */
    public boolean getIncomingRequestPending() {
        return this.incomingRequestPending;
    }

    /**
     * Gibt zurück, ob ich bereits eine Anfrage gestellt habe
     *
     * @return Ob ich bereits eine Anfrage gestellt habe
     */
    public boolean getOutgoingRequestPending() {
        return this.outgoingRequestPending;
    }

    /**
     * Setzt, ob ich bereits eine Anfrage erhalten habe
     *
     * @param flag Obich bereits eine Anfrage erhalten habe
     */
    public void setIncomingRequestPending(boolean flag) {
        this.incomingRequestPending = flag;
    }

    /**
     * Setzt, ob ich bereits eine Anfrage gestellt habe
     *
     * @param flag Ob ich bereits eine Anfrage gestellt habe
     */
    public void setOutgoingRequestPending(boolean flag) {
        this.outgoingRequestPending = flag;
    }

    /**
     * Lädt bei allen Clienten die Onlineliste und die Scoreliste neu
     */
    public void reloadClientGUI() {//Für die Lobby
        ServerManager server = InstanceManager.getServerManager();
        GameDatabase gameDatabase = InstanceManager.getGameDatabase();
        server.sendToEveryone(gameDatabase.getOnlineUsersAndState());//Lädt bei allen Clienten die OnlineListe neu
        server.sendToEveryone(gameDatabase.getOnlineUsersAndScore());
    }

    /**
     * Speichert die Referenz des Game-Objektes
     *
     * @param pGame Das Game-Objekt
     */
    public void setGame(Game pGame) {
        this.game = pGame;
    }

    /**
     * Gibt das Game-Objekt zurück
     *
     * @return Das Game-Objekt
     */
    public Game getGame() {
        return this.game;
    }
}
