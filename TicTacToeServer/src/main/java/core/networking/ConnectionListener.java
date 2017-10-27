/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.networking;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

/**
 *
 * @author i01frajos445
 */
public class ConnectionListener implements Callable<Void> {

    ClientManager client;
    Timer timer;

    public ConnectionListener(ClientManager pClient) {
        this.client = pClient;
    }

    @Override
    public Void call() throws Exception {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                if (!client.checkConnection(true)) {
                    timer.cancel();
                }
            }
            
        },1000,10000);
        return null;
    }
    
    public void stop(){
        timer.cancel();
    }

}
