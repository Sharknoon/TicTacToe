/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import guiGame.GameOberflaeche;
import java.util.concurrent.Callable;
import general.Language;
import guiLobby.LobbyOberflaeche;
import guiLauncher.LauncherOberflaeche;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 *
 * @author Josua Frank
 */
public class Command implements Callable<Void> {

    private ClientManager client;
    private LauncherOberflaeche launcher;
    private LobbyOberflaeche lobby;
    private GameOberflaeche game;
    private Language languages;
    private String befehl1;

    /**
     * Führt Befehle aus
     *
     * @param pBefehl Der auszuführende Befehl Beispielbefehl:
     * /loginunsuccessful
     * @param pClient Dieser Client, um Befehle weiterzuleiten
     * @param pLauncher Die Oberfläche, um das Fehlerfenstger zu öffnen und eine
     * Fehlermeldung im Fenster auszugeben
     * @param pLobby um Chatbefehle weiterzuleiten
     */
    public Command(String[] parameters, ClientManager pClient, LauncherOberflaeche pLauncher, LobbyOberflaeche pLobby, GameOberflaeche pGame) throws Throwable {
        this.client = pClient;
        this.languages = client.getLanguages();
        this.launcher = pLauncher;
        this.lobby = pLobby;
        this.game = pGame;
        try {

            switch (parameters[0]) {
                case "ping":
                    String[] befehl = {"ping"};
                    client.sendObject(befehl);
                    break;
                case "serverclosing":
                    client.bereitsAngemeldet = false;
                    client.socket = null;
                    client.setScene(0);
                    launcher.setStatusFeld(languages.getString(72));
                    break;
                case "logout":
                    client.bereitsAngemeldet = false;
                    client.setScene(0);
                    launcher.setStatusFeld(languages.getString(87));
                    break;
                case "loginsuccessful":
                    launcher.setStatusFeld(languages.getString(9));
                    client.loginErfolgreich();
                    break;

                case "alreadyloggedin":
                    launcher.zeigeFehlerAn(languages.getString(10));
                    break;

                case "loginunsuccessful":
                    launcher.zeigeFehlerAn(languages.getString(11));
                    break;

                case "accountalreadyassigned":
                    launcher.zeigeFehlerAn(languages.getString(12));
                    break;

                case "registersuccessful":
                    launcher.showCodeVerification();
                    launcher.setStatusFeld(languages.getString(13));
                    break;
                case "emailcodevalid":
                    zeigeNachrichtAn(languages.getString(57));
                    launcher.setStatusFeld(languages.getString(59));
                    break;
                case "emailcodenotvalid":
                    launcher.showCodeVerification();
                    launcher.setStatusFeld(languages.getString(60));
                    break;
                case "nomoreattemps":
                    zeigeFehlerAn(languages.getString(94));
                    break;
                case "emaildoesnotexist":
                    launcher.showEmailInput();
                    zeigeFehlerAn(languages.getString(86));
                    break;
                case "showPasswordConfirmation":
                    launcher.showPasswordVerification();
                    break;
                case "passwordcodenotvalid":
                    launcher.showPasswordVerification();
                    zeigeFehlerAn(languages.getString(85));
                    break;
                case "passwordsnotequal":
                    launcher.showPasswordVerification();
                    zeigeFehlerAn(languages.getString(21));
                    break;
                case "passwordcodevalid":
                    zeigeNachrichtAn(languages.getString(84));
                    break;
                case "registerabort":
                    zeigeNachrichtAn(languages.getString(61));
                    launcher.setStatusFeld(languages.getString(61));
                    break;
                case "goodbye":
                    if (client.bereitsAngemeldet) {
                        lobby.addToLobbyChatLeftTheGame(parameters[1]);
                    }
                    break;
                case "crash":
                    if (client.bereitsAngemeldet) {
                        lobby.addToLobbyChatCrashedIt(parameters[1]);
                    }
                    break;
                case "nolongerwatching":
                    if (client.bereitsAngemeldet) {
                        game.addToGameChatNoLongerWatchingTheGame(parameters[1]);
                    }
                    break;
                case "hello":
                    if (client.bereitsAngemeldet) {
                        if (client.getUsername().equals(parameters[1])) {
                            //tue nichts, d.h. begrüße dich nicht selbst, weil der Server schneller ist, nach dem anmelden als du mit der Oberfläche aufbauen
                        } else {
                            lobby.addToLobbyChatJoinedTheGame(parameters[1]);
                        }
                    }
                    break;
                case "requestfrom":
                    if (client.bereitsAngemeldet) {
                        lobby.setAllListButtonsEnabled(false);
                        lobby.zeigeRequestAn(parameters[1]);
                    }
                    break;
                case "IncomingRequestPending":
                    if (client.bereitsAngemeldet) {
                        lobby.setAllListButtonsEnabled(true);
                        zeigeNachrichtAn(languages.getString(68));
                    }
                    break;
                case "OutgoingRequestPending":
                    if (client.bereitsAngemeldet) {
                        lobby.setAllListButtonsEnabled(true);
                        zeigeNachrichtAn(languages.getString(90));
                    }
                    break;
                case "unanswered"://Wenn das Gegenüber nicht geantwortet hat
                    if (client.bereitsAngemeldet) {
                        lobby.setAllListButtonsEnabled(true);
                        zeigeNachrichtAn(parameters[1] + languages.getString(69));
                    }
                    break;
                case "notreacted"://Wenn ich nicht geantwortet habe
                    if (client.bereitsAngemeldet) {
                        lobby.entferneRequest();
                        lobby.setAllListButtonsEnabled(true);
                    }
                    break;
                case "declined":
                    if (client.bereitsAngemeldet) {
                        lobby.setAllListButtonsEnabled(true);
                        zeigeNachrichtAn(parameters[1] + languages.getString(35));
                    }
                    break;
                case "showgame":
                    if (client.bereitsAngemeldet) {
                        lobby.setAllListButtonsEnabled(true);
                        client.setScene(2);
                    }
                    break;
                case "gamechat":
                    if (client.bereitsAngemeldet) {
                        String chatmessage = parameters[1];
                        if (parameters.length == 3) {
                            game.addToGameChat(parameters[1], parameters[2]);
                        } else {
                            System.err.println("Fehler beim Gamechat, Parameteranzahl falsch (!=3) !!!");
                        }
                    }
                    break;
                case "lobbychat":
                    if (client.bereitsAngemeldet) {
                        String message = parameters[1];
                        if (parameters.length == 3) {
                            lobby.addToLobbyChat(parameters[1], parameters[2]);
                        } else {
                            System.err.println("Fehler beim Gamechat, Parameteranzahl falsch (!=3) !!!");
                        }
                    }
                    break;
                case "newviewer":
                    if (client.bereitsAngemeldet) {
                        if (!parameters[1].equals(client.getUsername())) {//Ich möchte mir 1. nicht selbst begrüßen und 2. ist dieser Command schneller da, befor die Gameoberfläche fertig ist
                            game.addToGameChatWatchingTheGame(parameters[1]);
                        }
                    }
                    break;
                case "disconnect":
                    client.verbindungTrennen();
                    break;
                case "headline":
                    if (client.bereitsAngemeldet) {
                        game.setHeadline(parameters[1], parameters[2]);
                    }
                    break;
                case "quitgame":
                    if (client.bereitsAngemeldet) {
                        if (!client.getUsername().equals(parameters[1])) {//Wenn nicht ich der Abbrecher bin
                            this.zeigeNachrichtAn(languages.getString(52) + " " + parameters[1] + languages.getString(53));
                        }
                        client.setScene(1);
                    }
                    break;
                case "turn":
                    game.setInstructions(parameters[1]);
                    break;
                case "won":
                    if (client.bereitsAngemeldet) {
                        game.showMessage(0, parameters[1]);
                    }
                    break;
                case "undecided":
                    if (client.bereitsAngemeldet) {
                        game.showMessage(1, null);
                    }
                    break;
                case "closegamefinishmessage":
                    if (game != null) {//Falls derjenige schon auf ok bei gameFinish Dialog geklickt hat
                        game.removeGameFinishMessage();
                    }
                    break;
                case "message":
                    zeigeNachrichtAn(parameters[1]);
                    break;
                case "msg":
                    zeigeNachrichtAn(parameters[1]);
                    break;
                case "nixsqlinjection":
                    zeigeFehlerAn("No SQL-Injection for you today, come back later");
                    break;
                case "quitgamenotreacted":
                    if (client.bereitsAngemeldet) {
                        if (!client.getUsername().equals(parameters[1])) {//Wenn nicht ich der Lazyplayer bin
                            this.zeigeNachrichtAn(languages.getString(52) + " " + parameters[1] + languages.getString(88));
                        } else {
                            this.zeigeNachrichtAn(languages.getString(89));
                        }
                        client.setScene(1);
                    }
                    break;
                case "invalidcommand":
                    this.zeigeFehlerAn("Invalid command, are you outside of the official usage of the client?");
                    break;
                default:
                    System.err.println("Befehl nicht bekannt '" + parameters[0] + "'");
                    break;
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Befehl-Verarbeiten: '" + befehl1 + "', " + e);
            e.printStackTrace(System.err);
        }
    }

    /**
     * Zeigt ein einfaches Nachrichtenfesnter an, z.B. Für Serverwartungen, etc
     *
     * @param string Die anzuzeigende Nachricht
     */
    private void zeigeNachrichtAn(String string) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(languages.getString(46));
            alert.setHeaderText(null);
            alert.setContentText(string);
            alert.showAndWait();
        });
    }

    /**
     * Zeigt ein einfaches Fehlerfesnter an
     *
     * @param string Die anzuzeigende Nachricht
     */
    private void zeigeFehlerAn(String string) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(languages.getString(44));
            alert.setHeaderText(null);
            alert.setContentText(string);
            alert.showAndWait();
        });
    }

    @Override
    public Void call() throws Exception {
        return null;
    }

}
