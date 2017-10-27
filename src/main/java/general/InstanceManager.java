/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import core.database.CoreDatabase;
import core.gui.GUI;
import core.gui.GUIController;
import core.networking.Email;
import core.networking.ServerManager;
import game.database.GameDatabase;
import javafx.scene.paint.Color;
import org.dizitart.no2.Nitrite;

/**
 *
 * @author Josua Frank
 */
public class InstanceManager {

    static CoreDatabase coreDatabase;
    static GameDatabase gameDatabase;
    static ServerManager server;
    static Email email;
    static GUI gui;
    static GUIController guiController;
    static Nitrite db;

    public InstanceManager() {

    }

    public static void addInstance(Object object) {
        if (object instanceof CoreDatabase) {
            coreDatabase = (CoreDatabase) object;
        } else if (object instanceof GameDatabase) {
            gameDatabase = (GameDatabase) object;
        } else if (object instanceof ServerManager) {
            server = (ServerManager) object;
        } else if (object instanceof Email) {
            email = (Email) object;
        } else if (object instanceof GUI) {
            gui = (GUI) object;
        } else if (object instanceof GUIController) {
            guiController = (GUIController) object;
        } else if (object instanceof Nitrite) {
            db = (Nitrite) object;
        }
    }

    public static CoreDatabase getCoreDatabase() {
        return coreDatabase;
    }

    public static GameDatabase getGameDatabase() {
        return gameDatabase;
    }

    public static ServerManager getServerManager() {
        return server;
    }

    public static Email getEmail() {
        return email;
    }

    public static GUI getGUI() {
        return gui;
    }

    public static GUIController getGUIController() {
        return guiController;
    }

    public static Nitrite getDB() {
        return db;
    }

    /**
     * Gibt eine Nachricht auf der GUI und in der Console aus
     *
     * @param message Die auszugebende Nachricht
     */
    public static void printLine(String message) {
        java.util.Date date = new java.util.Date();
        System.out.println("[" + date.toString() + "] {Core} (ServerManager): " + message);
        if (gui != null) {
            gui.addToTextFlow("[" + date.toString() + "] {Core} (ServerManager): " + message, Color.BLACK);
        }
    }

    /**
     * Gibt einn Fehler auf der GUI und in der Console aus
     *
     * @param errorMessage Der auszugebende Fehler
     */
    public static void printError(String errorMessage) {
        java.util.Date date = new java.util.Date();
        System.err.println("[" + date.toString() + "] {Core} (ServerManager): " + errorMessage);
        if (gui != null) {
            gui.addToTextFlow("[" + date.toString() + "] {Core} (ServerManager): " + errorMessage, Color.RED);
        }
    }

}
