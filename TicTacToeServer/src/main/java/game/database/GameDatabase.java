/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.database;

import general.InstanceManager;
import java.util.HashMap;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import static org.dizitart.no2.filters.Filters.eq;

/**
 *
 * @author Josua Frank
 */
public class GameDatabase {

    /**
     *
     * @author Josua Frank
     */
    private final Nitrite db;
    private final NitriteCollection col;

    public GameDatabase() {
        db = InstanceManager.getDB();
        col = db.getCollection("players");
    }

    /**
     * Nimmt einen neuen Spieler in die Datenbank auf
     *
     * @param pUsername Der Benutzername des aufzunehmenden Spielers
     */
    public void add(String pUsername) {
        Document user = Document.createDocument("username", pUsername)
                .put("wins", 0)
                .put("defeats", 0)
                .put("draws", 0);
        col.insert(user);
    }

    public void setIngame(Integer value, String pUsername) {
        col.update(eq("username", pUsername), Document.createDocument("ingame", value));
    }

    public void setOnline(boolean value, String pUsername) {
        col.update(eq("username", pUsername), Document.createDocument("online", value));
    }

    public void addWin(String username) {
        Integer ratio[] = getWinLostRatio(username);
        Integer wins = ratio[0] + 1;
        this.setWins(wins, username);
    }

    private void setWins(int wins, String username) {
        Document user = col.find(eq("username", username)).firstOrDefault();
        user.put("wins", wins);
        col.update(user);
    }

    public void addDefeat(String username) {
        Integer ratio[] = this.getWinLostRatio(username);
        Integer defeats = ratio[1] + 1;
        this.setDefeats(defeats, username);
    }

    private void setDefeats(int defeats, String username) {
        Document user = col.find(eq("username", username)).firstOrDefault();
        user.put("defeats", defeats);
        col.update(user);
    }

    public void addDraw(String username) {
        Integer ratio[] = this.getWinLostRatio(username);
        Integer draws = ratio[2] + 1;
        this.setDraws(draws, username);
    }

    private void setDraws(int draws, String username) {
        Document user = col.find(eq("username", username)).firstOrDefault();
        user.put("draws", draws);
        col.update(user);
    }

    /**
     * Gibt die Anzahl der gewonnen, verlorenen und unentschiedenen Spiele an
     *
     * @param username Der Benutzer, dessen Anzahl zurückgegeben werden soll
     * @return String[0] = Wins, String[1] = Defeats, String[2] = Draws
     */
    public Integer[] getWinLostRatio(String username) {
        Integer[] ratio = new Integer[3];
        Document user = col.find(eq("username", username)).firstOrDefault();
        ratio[0] = user.get("wins", Integer.class);
        ratio[1] = user.get("defeats", Integer.class);
        ratio[2] = user.get("draws", Integer.class);
        return ratio;
    }

    /**
     * Gibt eine HashMap mit den Benutzern und ihren Ingamestatus zurück
     *
     * @return Eine HashMap(String Benutzername, int ingamestatus) //0 = in
     * Lobby, 1 = Spielt gerade 2 = schaut gerade einem Spiel zu
     */
    public HashMap<String, Integer> getOnlineUsersAndState() {
        HashMap<String, Integer> onlineUsersAndTheirState = new HashMap<>();
        Cursor c = col.find(eq("online", true));
        for (Document u : c) {
            onlineUsersAndTheirState.put(u.get("username", String.class), u.get("ingame", Integer.class));
        }
        onlineUsersAndTheirState.put("online users", 0);
        return onlineUsersAndTheirState;
    }

    /**
     * Gibt eine HashMap mit den Benutzern und ihren Scores zurück
     *
     * @return Eine HashMap(String Benutzername, int[] score) int[0] = wins,
     * int[1] = defeats, int[2] = draws
     */
    public HashMap<String, int[]> getOnlineUsersAndScore() {
        HashMap<String, int[]> onlineUsersAndScore = new HashMap<>();
        Cursor users = col.find(eq("online", true));
        for (Document u : users) {
            int[] scores = new int[3];//wins, defeats, draws
            scores[0] = u.get("wins", Integer.class);
            scores[1] = u.get("defeats", Integer.class);
            scores[2] = u.get("draws", Integer.class);
            onlineUsersAndScore.put(u.get("username", String.class), scores);
        }
        onlineUsersAndScore.put("users score", null);
        return onlineUsersAndScore;
    }

    public void resetOnlineStatusOfPlayers() {
        col.update(null, Document.createDocument("ingame", 0));
        col.update(null, Document.createDocument("online", false));
    }

}
