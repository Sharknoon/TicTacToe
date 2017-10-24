/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import core.database.CoreDatabase;
import game.database.GameDatabase;
import general.InstanceManager;
import java.util.HashMap;
import javax.net.ServerSocketFactory;

/**
 *
 * @author Josua Frank
 */
public class ServerManager implements Runnable {

    private final InstanceManager iManager;
    private ServerSocketFactory socketFactory;
    private ServerSocket server = null;
    private ExecutorService threadPool;
    private CoreDatabase coreDatabase;
    private GameDatabase gameDatabase;
    private ObjectOutputStream raus;
    private final HashMap<String, ClientManager> clientManagerMap = new HashMap<>();//Nur die eingeloggten Clienten (Geht somit schneller zum getUsername)
   

    /**
     * Hier wird der Server gestartet und der Threadpool initialisiert.
     *
     * @param iManager
     * @param loginData
     */
    public ServerManager(InstanceManager iManager, String[] loginData) {
        this.iManager = iManager;
        iManager.addInstance(this);
        iManager.addInstance(new Email(loginData[0], loginData[1]));
    }

    /**
     * Sucht unendlich lang nach Clients, die sich verbinden möchten. Sobald
     * sich ein Client meldet, weist er ihm einen eigenen Thread (ClientManager)
     * zu.
     */
    private void connect() {
        boolean loop = true;
        Socket socket;
        while (loop) {
            socket = null;
            try {
                socket = server.accept();
                if (socket == null) {
                    break;
                }
                ClientManager client = new ClientManager(iManager, socket);
                threadPool.submit(client);

                this.clientManagerMap.put(String.valueOf(socket.hashCode()), client);
//                this.clientManagerList.add(client);
                iManager.getGUI().setListView();
            } catch (IOException e) {
                if (e.toString().contains("Socket is closed")) {
                    iManager.printLine("Socket geschlossen...");
                    loop = false;
                }
            }
        }
    }

    /**
     * Muss bei Disconnect des Servers aufgerufen werden
     *
     */
    public void disconnect() {
        if (server != null) {
            try {
                String[] command = {"serverclosing"};
                this.sendToEveryone(command);
                server.close();
                System.exit(0);
            } catch (IOException e) {
                iManager.printError("Socket konnte nicht geschlossen werden: " + e.getMessage());
            }
        }
    }

    /**
     * Sendet ein Objekt zu allen verbundenen Clienten
     *
     * @param object Das zu sendende Objekt
     */
    public void sendToEveryone(Object object) {
        clientManagerMap.entrySet().stream().forEach((entry) -> {
            try {
                raus = new ObjectOutputStream(entry.getValue().getSocket().getOutputStream());
                raus.writeObject(object);
                raus.flush();
            } catch (IOException ex) {
                iManager.printError("Nachricht konnte nicht an alle verteilt werden: " + ex + "  " + object);
            }
        });
    }

    /**
     * Sendet ein Objekt zu einem spezifischen Clienten, definiert durch den
     * ClientManager
     *
     * @param client Der ClientManager des Cleinten
     * @param object Das zu sendende Objekt
     */
    public void sendTo(ClientManager client, Object object) {
        client.sendObject(object);
    }

    /**
     * Gibt den ClientManager des jeweiigen Benutzers zurück
     *
     * @param username Der Benutzer, dessen ClientManager gesucht wird
     * @return Der ClientManager des Benutzers
     */
    public ClientManager getClientManager(String username) {
        return clientManagerMap.get(username);
    }

    /**
     * Entfernt einen Clienten von der Clientmanagerliste, z.B. falls er seine
     * Verbindung trennt
     *
     * @param client Der zu entfernende ClientManager
     */
    public void removeFromClientManagerList(ClientManager client) {
        clientManagerMap.remove(client.getUsername());
        clientManagerMap.remove(String.valueOf(client.getSocket().hashCode()));//Falls er nich nciht eingelogggt war
        iManager.getGUI().setListView();
    }

    /**
     * Gibt die ClientMangerMap zurück
     *
     * @return Die zu zurückgende ClientManagerMap
     */
    public HashMap<String, ClientManager> getClientManagerMap() {
        return this.clientManagerMap;
    }

    public void addUsernameToMap(ClientManager client) {
        clientManagerMap.remove(String.valueOf(client.getSocket().hashCode()));
        clientManagerMap.put(client.getUsername(), client);
    }

    /**
     * Lädt die Oberfläche neu, z.B. nach einer Abmeldung eines Clienten
     */
    public void reloadGUI() {
        iManager.getGUI().setListView();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(3141);
        } catch (java.net.BindException e) {
            iManager.printError("Adresse schon vergeben, läuft schon ein Server?: " + e.getMessage());
            iManager.printError("Server wird beendet, um doppelte Server sofort zu unterbinden!");
            System.exit(0);
        } catch (IOException ex) {
            iManager.printError("Server konnte nicht gestartet werden: " + ex.getMessage());
        }

        this.threadPool = Executors.newCachedThreadPool();//Es können nur 100 Clienten sich maximal gleichzeitig verbinden, doch nicht, von FixedThreadPool zu CachedThredPool gewechselt wegen Performance
        coreDatabase = new CoreDatabase();
        coreDatabase.resetOnlineStatusOfPlayers();
        iManager.addInstance(coreDatabase);
        gameDatabase = new GameDatabase();
        gameDatabase.resetOnlineStatusOfPlayers();
        iManager.addInstance(gameDatabase);
        iManager.printLine("Server erreichbar unter " + server.getInetAddress() + ":" + server.getLocalPort());
        this.connect();
    }

}
