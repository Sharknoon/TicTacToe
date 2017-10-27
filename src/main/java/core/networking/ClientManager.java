/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import game.networking.PlayerManager;
import general.InstanceManager;
import java.net.InetAddress;

/**
 *
 * @author Josua Frank
 */
public class ClientManager implements Callable<Void> {

    private String username;
    private String email;
    private String language;
    private final Socket socket;
    private String verificationCode;
    private String[] registerParameters;
    private ExecutorService threadpool;
    private ConnectionListener connectionListener;
    private boolean connected = true;
    private int remainingAttempts;
    private PlayerManager player;
    private int state = 0;//0 = Launcher, 1 = Lobby, 2 = Game

    public ClientManager(Socket pSocket) {
        socket = pSocket;
        threadpool = Executors.newFixedThreadPool(10);//Max. gleichzeitug zu erledigende Aufgaben, um DDoS vorzubeugen
        player = new PlayerManager(this);
    }

    /**
     * Wir bei Aufruf aufgerufen und startet den Inputlistener im Hintergrund
     *
     * @return
     */
    @Override
    public Void call() {
        InstanceManager.printLine("Client verbunden (IP: " + socket.getInetAddress() + " )");
        threadpool.submit(new InputListener(this));
        connectionListener = new ConnectionListener(this);
        threadpool.submit(connectionListener);
        return null;
    }

    /**
     * Wird vom InputListener aufgerufen, wenn eine Nachricht einkommt
     *
     * @param message Die einkommende Nachricht
     */
    public void receiveStringArray(String[] message) {
        Command command = new Command(message, this);
    }

    /**
     * Wird vom InputListener aufgerufen, wenn ein neues Objekt einkommt
     *
     * @param object Das einkommende Objekt
     */
    public void receiveObject(Object object) {
        this.player.receiveObject(object);//Der ClientManager braucht keine Objecte, die keine Strings sind. Ob Sie Strings sind, wird im InputListener geprüft
    }

    /**
     * Sended Nachrichten zum Clienten.
     *
     * @param object Das Object, das zum Client geschickt werden soll
     */
    public void sendObject(Object object) {
        try {
            ObjectOutputStream raus = new ObjectOutputStream(socket.getOutputStream());
            raus.writeObject(object);
            raus.flush();
        } catch (Exception e) {
            if (e.toString().contains("Socket is closed")) {
                InstanceManager.printError("Kann Objekt nicht senden, trenne Verbindung");
                this.disconnect(false);
            } else if (e.toString().contains("socket write error")) {

            } else {
                InstanceManager.printError("Object(" + object.toString() + ") nicht gesendet werden: " + e);
            }
        }
    }

    /**
     * Trennt die Verbindung zum Clienten
     *
     * @param crash Gibt an, ob die Verbindung durch einen Crash beendet wurde
     */
    public void disconnect(boolean crash) {
        connectionListener.stop();
        InstanceManager.getCoreDatabase().setOnline(false, username);
        InstanceManager.getServerManager().removeFromClientManagerList(this);
        player.receiveDisconnect(crash);
        if (socket != null) {
            try {
                socket.close();
                InstanceManager.printLine("Client hast sich abgemeldet: " + username + " (" + socket.getInetAddress() + ")");
            } catch (IOException e) {
                InstanceManager.printError("Client konnte sich nicht abmelden: " + e);
            }
        }
        //Folgendes dient zur Sicherheit, um einem MemoryLeak vorzubeugen
        connectionListener = null;
        player = null;
        threadpool.shutdownNow();
        threadpool = null;
    }

    public boolean checkConnection(boolean input) {//true = server fragt an false = client meldet sich
        if (input && connected) {//Server fragt an und es kam eine Rückmeldung in den letzten 2 sek
            String[] command = {"ping"};
            sendObject(command);
            connected = false;
            return true;
        } else if (!input) {//Client meldet sich
            connected = true;
            return true;
        } else if (input && !connected) {//Server fragt an und zw. den letzten x sek kam keine Rückmeldung
            //printLine(username+" hat die Verbindung verloren! :(");
            this.disconnect(true);
            return false;
        }
        return false;
    }

    //Setter und Getter
    /**
     * Gibt den Benutzernamen des Clienten zurück
     *
     * @return Der Benutzername des Clienten
     */
    public String getUsername() {
        if (username != null) {
            return this.username;
        } else {
            return String.valueOf(socket.hashCode());
        }
    }

    public InetAddress getIP() {
        return socket.getInetAddress();
    }

    public String getEmail() {
        return this.email;
    }

    public String getLanguage() {
        return this.language;
    }
    
    public PlayerManager getPlayerManager(){
        return this.player;
    }
    
    public Socket getSocket(){
        return this.socket;
    }
    
    public int getState(){
        return this.state;
    }

    /**
     * Gibt den Benutzernamen dieses Clienten zurück
     *
     * @param pUsername Der zu zurückgebende Username
     */
    public void setUsername(String pUsername) {
        this.username = pUsername;
        ServerManager server = InstanceManager.getServerManager();
        server.addUsernameToMap(this);
        server.reloadGUI();
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setState(int state){
        this.state = state;
    }
    /**
     * Gibt den Verifikationscode dieses dieses Clienten zurück
     *
     * @return Der zu zurückgebende Verificationscode
     */
    public String getVerificationCode() {
        if (remainingAttempts > 0) {
            remainingAttempts--;
            return this.verificationCode;
        }
        return "";
    }

    /**
     * Erzeugt eine zufällige Zahl zwischen 00000000 und 99999999
     *
     * @return Die zufällige zahl
     */
    public String createVerificationCode() {
        remainingAttempts = 3;
        String tempCode = "";
        for (int i = 0; i < 8; i++) {
            tempCode = tempCode.concat(String.valueOf((int) Math.floor(Math.random() * (9 - 0 + 1))));
        }
        this.verificationCode = tempCode;
        return this.verificationCode;
    }

    /**
     * Speichert "lokal" die Parameter, falls derjenige ein Troll ist, damit die
     * Datenbank nicht überläuft, er speichert sie erst permanent, wenn der
     * Emailcode bestätigt worden ist
     *
     * @param pParameters Die Registrierungsparameter
     */
    public void setRegisterParameters(String[] pParameters) {
        this.registerParameters = pParameters;
    }

    /**
     * Gibt die "lokal" gespeicherten Registrierungsparameter zurück
     *
     * @return Die Registrierungsparameter
     */
    public String[] getRegisterParameters() {
        return this.registerParameters;
    }

}
