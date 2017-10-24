/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.networking;

import general.InstanceManager;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 *
 * @author Josua Frank
 */
public final class InputListener implements Callable<Void> {

    InstanceManager iManager;
    ClientManager client;
    Socket socket;

    /**
     * Wiederholt unendlich oft die Methode receiveString()
     *
     * @param pIManager
     * @param pClient
     */
    public InputListener(InstanceManager pIManager, ClientManager pClient) {
        this.iManager = pIManager;
        this.client = pClient;
        this.socket = client.getSocket();
    }

    @Override
    public Void call() {

        while (!socket.isClosed() && socket.isConnected() && socket.isBound()) {
            try {
                ObjectInputStream rein = new ObjectInputStream(socket.getInputStream());
                Object object = rein.readObject();
                if (object instanceof String[]) {
                    //System.out.println("Message von " + client.getUsername() + "(" + socket.hashCode() + "): " + object);
                    client.receiveStringArray((String[]) object);
                } else {
                    client.receiveObject(object);
                }
            } catch (ClassNotFoundException ex) {
                iManager.printError("Konnte einkommende Nachricht nicht lesen: " + ex);
            } catch (IOException exe) {
                if (exe.toString().equals("java.net.SocketException: Connection reset") || exe.toString().equals("java.net.SocketException: Socket closed")) {
                    iManager.printLine("Client hat die Verbindung verloren: " + exe + ", beende daher die Verbindungen zum Clienten");
                    client.disconnect(true);
                    return null;
                }
                if (exe.toString().equals("java.io.EOFException")) {
                    iManager.printError("EndOfFile-Fehler beim Empfangen einer Nachricht: " + exe);
                    iManager.printError("TODO");
                    //System.exit(0);
                    return null;
                }
                if (exe.toString().equals("invalid stream header")) {//Jemand sendet zu lange Strings
                    client.sendObject("invalidcommand");
                    client.disconnect(true);
                }
                iManager.printError("Konnte einkommende Nachricht nicht lesen: " + exe);
            }
        }
        return null;
    }

}
