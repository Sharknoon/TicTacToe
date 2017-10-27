/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import guiGame.GameOberflaeche;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import general.Language;
import guiLauncher.LauncherOberflaeche;
import guiLobby.LobbyOberflaeche;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author Josua Frank
 */
public class ClientManager {

    private static final String saltConstant = "H2Gq7p@H!9a";

    private Stage stage;
    private Language languages = new Language();
    private LauncherOberflaeche launcher;
    private LobbyOberflaeche lobby;
    private GameOberflaeche game;
    public Socket socket = null;
    private ExecutorService threadPool;
    public boolean bereitsAngemeldet = false;
    private String username;
    private HashMap<String, Integer> onlineUsers;
    public Boolean serverReachable = false;
    private int aktiveScene = 0;

    private String ip = "localhost";
    //private String ip = "ns323149.ip-213-251-184.eu";
    //private String ip= "frankdns.selfhost.eu";

    /**
     * Ist die Verbindung zum Server
     *
     * @param pOberflaeche Die Oberfläche, die dieses Objekt erzeugt hat
     * @param pStage Die Stage, damit auf Ihr die Scenes gewechselt werden
     * können
     */
    public ClientManager(LauncherOberflaeche pOberflaeche, Stage pStage) {
        this.launcher = pOberflaeche;
        this.stage = pStage;
        launcher.setStatusFeld(" ");//Damit die Leiste nicht erst später erscheint, während er versucht, zu connecten
    }

    /**
     * Hier wird versucht, eine Verbindug zum Server herzustellen
     */
    public void connect() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, 3141), 500);
            threadPool = Executors.newCachedThreadPool();
            threadPool.submit(new InputListener(socket, this));
            System.out.println("Erfolgreich verbunden");
            launcher.setStatusFeld("Erfolgreich zu Server verbunden");
            serverReachable = true;
        } catch (UnknownHostException e) {
            System.err.println("Unbekannter Host: " + e.getMessage());
            launcher.setStatusFeld("Unbekannter Host: " + e.getMessage());
            this.showServerUnreachableDialog();
        } catch (IOException e) {
            System.err.println("Server konnte nicht erreicht werden: " + e.getMessage());
            launcher.setStatusFeld(languages.getString(6) + ": " + e.getMessage());
            this.showServerUnreachableDialog();
        }
    }

    /**
     * Wandelt saltet ein Passwort und wandelt es dann in einen Hash-Wert (512
     * Bit SHA) um
     *
     * @param value Das umzuwandelnde Passwort
     * @return Der 512 Bit SHA String
     */
    public String getHash(String value) {
        MessageDigest md;
        StringBuilder sb = new StringBuilder();

        String saltedPW;
        System.out.println("username " + username + " Value: " + value);

        saltedPW = value + saltConstant + username;

        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(saltedPW.getBytes());
            byte byteData[] = md.digest();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Algorithmus nicht verfügbar: " + ex.getMessage());
        }
        return sb.toString();
    }

    /**
     * Meldet sich mit Benutzername und Passwort am Server an
     *
     * @param pUsername Der Benutzername zum anmelden
     * @param pPassword Das Passwort zum anmelden
     */
    public void login(String pUsername, String pPassword) {
        if (socket == null) {
            this.connect();
        }
        if (bereitsAngemeldet) {//Prüft, ob bereits jemand an diesem Clienten angemeldet ist
            launcher.setStatusFeld(languages.getString(10));
            return;
        }
        if (socket != null) {//Prüft, ob eine Verbindung besteht
            username = pUsername;
            String hashedPassword = getHash(pPassword);
            String[] befehl = {"login", username, hashedPassword};
            sendObject(befehl);//Username darf KEINE Leerzeichen beinhalten
        }
    }

    /**
     * Falls der Anmeldevorgang erfolgerich war, wird hier das Spiel gestartet
     */
    public void loginErfolgreich() {
        bereitsAngemeldet = true;
        //Hier startet dann das Spiel
        lobby = new LobbyOberflaeche(this, stage);
        setScene(1);
    }

    /**
     * Hier wird die Registrierungsbenachrichtigung an den Server geschickt
     *
     * @param username Der Benutzername
     * @param firstname Der Vorname
     * @param name Der Nachname
     * @param password Sein Passwort als Hash-Wert
     * @param password2 Das wiederholte Passwort
     * @param email Seine E-Mailadresse
     * @param language Seine gewählte Sprache
     * @return true, wenn alles geklappt hat
     */
    public boolean register(String username, String firstname, String name, String password, String password2, String email, String language) {
        if (socket == null) {
            this.connect();
        }
        if (socket != null) {
            this.username = username;
            String hashedPassword = getHash(password);
            String hashedPassword2 = getHash(password2);
            String[] befehl = {"register", username, firstname, name, hashedPassword, hashedPassword2, email, language};
            sendObject(befehl);
            return true;
        } else {
            launcher.setStatusFeld(languages.getString(7));
            return false;
        }
    }

    /**
     * Hier wird vom Server ein neues Passwort angefordert
     *
     * @param email Die Emailadresse zur verifizierung
     * @param username Dessen Benutzername
     */
    public void requestNewPassword(String email, String username) {
        if (socket == null) {
            this.connect();
        }
        if (socket != null) {
            String[] command = {"passwordforgotten", email, username};
            sendObject(command);
        }
    }

    public void sendNewPassword(String pCode, String password1, String password2) {
        if (socket == null) {
            this.connect();
        }
        if (socket != null) {
            String hashedPassword1 = getHash(password1);
            String hashedPassword2 = getHash(password2);
            String[] befehl = {"newpassword", pCode, hashedPassword1, hashedPassword2};
            sendObject(befehl);
        }
    }

    /**
     * Hier wird die Verbindung zum Server getrennt
     */
    public void verbindungTrennen() {
        if (socket == null) {
            System.out.println("Server unerreichbar, beende daher sofort...");
            System.exit(0);
        } else {
            String[] befehl = {"disconnect"};
            sendObject(befehl);
            try {
                socket.close();
                System.out.println("Habe mich abgemeldet und beende mich jetzt...");
                System.exit(0);
            } catch (IOException ex) {
                System.out.println("Konnte Socket nicht schliessen!");
            }

        }
    }

    /**
     * Sended Nachrichten zum Clienten.
     *
     * @param object Das Object, das zum Client geschickt werden soll
     */
    public void sendObject(Object object) {
//        if (object.getClass(). > 1000) {
//            this.zeigeFehlerAn("Command too long");
//            return;
//        }
        if (socket == null) {
            this.connect();
        }
        try {
            ObjectOutputStream raus = new ObjectOutputStream(socket.getOutputStream());
            raus.writeObject(object);
            raus.flush();
            if (object instanceof String[]) {
                String array[] = (String[]) object;
                System.out.print("Gesendet: ");
                for (String parameter : array) {
                    System.out.print(parameter + " ");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Object konnte nicht gesendet werden, da der Server unerreichbar ist: " + e);
            this.showServerUnreachableDialog();
        }
    }

    /**
     * Hier empfängt er aus der Klasse InputListener Nachrichten
     *
     * @param message Die empfangene Nachricht
     */
    public void receiveStringArray(String[] message) {
        System.out.print("Empfangen: ");
        for (String parameter : message) {
            System.out.print(parameter + " ");
        }
        System.out.println();
        try {
            new Command(message, this, launcher, lobby, game);
        } catch (Throwable ex) {
            System.err.println("Konnte Command nicht ausführen: " + ex);
        }
    }

    /**
     * Hier empfängt er aus der Klasse InputListener Objekte
     *
     * @param object Das empfangene Objekt
     */
    public void receiveObject(Object object) {
        if (object instanceof HashMap) {
            if (((HashMap) object).containsKey(999)) {
                System.out.println("Gamefield empfangen");
                HashMap<Integer, String> gameField = (HashMap<Integer, String>) object;
                gameField.remove(999);
                if (bereitsAngemeldet) {
                    game.buildGameField(gameField);
                }
            } else if (((HashMap) object).containsKey("online users")) {
                System.out.println("Online Users empfangen");
                this.onlineUsers = (HashMap<String, Integer>) object;
                this.onlineUsers.remove("online users");
                if (bereitsAngemeldet) {
                    lobby.buildList(onlineUsers);
                    if (game != null) {
                        game.buildList(onlineUsers);
                    }
                }
            } else if (((HashMap) object).containsKey("users score")) {
                System.out.println("User Score empfangen");
                HashMap<String, int[]> usersScore = (HashMap<String, int[]>) object;
                usersScore.remove("users score");
                if (bereitsAngemeldet) {
                    lobby.buildScore(usersScore);
                }
            } else {
                System.err.println("Object nicht erkannt!");
            }
        } else if (object instanceof ArrayList) {
            if (game != null) {
                game.overrideStatusOfPlayers((ArrayList<String>) object);
            } else {
                System.err.println("Fehler, Game noch null!!");
            }
        }
    }

    /**
     * Hier gibt er das Language-Object zurück, damit diese die selbe Main
     * Language haben
     *
     * @return Das Object von Language
     */
    public Language getLanguages() {
        return this.languages;
    }

    /**
     * Hier gibt er den Benutzernamen zurück, gegebenfalls auch null;
     *
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Hier öffnet er den Server-Unerreichbar-Dialog mit den Optionan Programm
     * schließen und erneut versuchen
     */
    public void showServerUnreachableDialog() {
        Platform.runLater(() -> {
            try {
                socket.close();
            } catch (Exception e) {
                System.err.println("Konnte Socket nicht schließen");
            }
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(languages.getString(46));
            alert.setHeaderText(null);
            alert.setContentText(languages.getString(40));

            ButtonType retry = new ButtonType(languages.getString(70));
            ButtonType buttonTypeCancel = new ButtonType(languages.getString(71), ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(retry, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == retry) {
                this.connect();
            } else {
                System.out.println("BEENDE, weil auf knopf schliesen geklickt wurde");
                System.exit(0);
            }
        });
    }

    /**
     * Hier setzt er die jeweilige Szene (z.B. Game, Launcher oder Lobby) in den
     * Vordergrund
     *
     * @param number 0 = Launcher 1 = Lobby 2 = Game
     */
    public void setScene(int number) {
        switch (number) {
            case 0:
                Platform.runLater(() -> {
                    launcher.setSize(getSizeFromActiveScene());
                    game = null;
                    stage.setScene(launcher.getScene());
                    aktiveScene = 0;
                });
                break;
            case 1:
                Platform.runLater(() -> {
                    stage.setTitle(username);
                    lobby.setSize(getSizeFromActiveScene());
                    game = null;//Brauche game noch bei getSizeFrom...
                    stage.setScene(lobby.getScene());
                    lobby.executescheduledTasks();
                    aktiveScene = 1;
                });
                break;
            case 2:
                game = new GameOberflaeche(this, stage, lobby);
                Platform.runLater(() -> {
                    game.setSize(getSizeFromActiveScene());
                    stage.setScene(game.getScene());
                    aktiveScene = 2;
                });
                break;
        }
    }

    /**
     * Holt die Größe der gerade Aktiven Szene und gibt diese zurück
     *
     * @return die Größe als int-Array(int[0] = x, int[1] = y) zurück
     */
    private int[] getSizeFromActiveScene() {
        switch (aktiveScene) {
            case 0:
                int[] size = launcher.getSize();
                System.out.println("Launchergröße: " + size[0] + ", " + size[1]);
                return launcher.getSize();
            case 1:
                System.out.println("Lobbygröße: " + lobby.getSize().toString());
                return lobby.getSize();
            case 2:
                return game.getSize();
        }
        return null;
    }

    /**
     * Zeigt ein einfaches Fehlerfesnter an
     *
     * @param string Die anzuzeigende Nachricht
     */
    public void zeigeFehlerAn(String string) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(languages.getString(44));
            alert.setHeaderText(null);
            alert.setContentText(string);
            alert.showAndWait();
        });
    }

    public void setUsername(String pUsername) {
        this.username = pUsername;
    }

}
