/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 *
 * @author Josua Frank
 */
public class InputListener implements Callable<Void> {

    private Socket socket;
    private ClientManager client;

    public InputListener(Socket pSocket, ClientManager pClient) {
        this.socket = pSocket;
        this.client = pClient;

    }

    @Override
    public Void call() {
        ObjectInputStream rein;
        boolean loop = true;
        while (loop) {
            try {
                rein = new ObjectInputStream(socket.getInputStream());
                Object object = rein.readObject();
                if (object instanceof String[]) {
                    //System.out.println("String empfangen: " + object);
                    client.receiveStringArray((String[]) object);
                } else {
                    //System.out.println("Object empfangen: " + object);
                    client.receiveObject(object);
                }
            } catch (IOException | ClassNotFoundException ex) {
                if (socket.isClosed()) {
                    System.exit(0);//Falls der Eigene Socket schon geschlossen wurde, er hier aber noch eine Nachricht vom Server bekommt, sofort auch hier nochmals beenden
                } else {
                    if (ex.equals("java.net.SocketException: Connection reset")) {
                        loop = false;
                        System.out.println("Setze InputListener aus....");
                    }

                }
            }
        }
        return null;
    }
}
