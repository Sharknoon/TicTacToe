/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.database;

import general.InstanceManager;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import static org.dizitart.no2.Document.createDocument;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import static org.dizitart.no2.filters.Filters.eq;

/**
 *
 * @author Josua Frank
 */
public class CoreDatabase {

    private final Nitrite db;
    private final NitriteCollection col;

    public CoreDatabase() {
        db = Nitrite.builder()
                .filePath(System.getProperty("user.dir") + "\\TicTacToe.db")
                .openOrCreate();
        col = db.getCollection("users");
    }

    /**
     * Überprüft die Logindaten
     *
     * @param username Der Benutzername des Benutzeres
     * @param password Und sein Passwort
     * @return Gibt zurück, ob der Login erfolgreich war
     */
    public int login(String username, String password) {//0 falsches pw, 1 richtiges pw, 2 SQL-Injection versuch
        if (password.length() != 128 || password.contains("*") || password.contains("'") || password.contains("%") || password.contains(".")) {
            return 2;
        }
        String hashedPassword = PasswordHasher.getHash(password);

        Cursor cursor = col.find(eq("username", username));
        if (cursor.size() != 1) {
            InstanceManager.printError("More than one user named " + username);
            return 0;
        } else {
            if (cursor.firstOrDefault().get("password").equals(hashedPassword)) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Registriert einen neuen Benutzer
     *
     * @param username Der Benutzername des Benutzers z.B. Sharknoon
     * @param firstname Der Vorname des Benutzers
     * @param name Der Nachname des Benutzers
     * @param password Sein Passwort als 512Bit SHA
     * @param mail Seine E-Mail-Adresse
     * @param language Seine Sprache
     */
    public void register(String username, String firstname, String name, String password, String mail, String language) {
        String hashedPassword = PasswordHasher.getHash(password);

        Document user = createDocument("username", username)
                .put("firstname", firstname)
                .put("name", name)
                .put("password", hashedPassword)
                .put("mail", mail)
                .put("language", language);

        col.insert(user);
    }

    /**
     * Überprüft, ob der Account mit dem Benutzername oder der Email schon
     * registriert ist
     *
     * @param username Der zu überprüfende Benutzername
     * @param email Die zu überprüfende E-Mail-Adresse
     * @return Der Boolean-Wert, ob er schon registriert ist, oder nicht
     */
    public boolean isAccountAlreadyAssigned(String username, String email) {
        Cursor usernames = col.find(eq("username", username));
        Cursor emails = col.find(eq("mail", email));
        return (usernames.hasMore() || emails.hasMore());
    }

    /**
     * Setzt in der Datenbank, ob der User online ist
     *
     * @param value der zu setzende Wert
     * @param pUsername Der Benutzer, der auf online gesetzt werden soll
     */
    public void setOnline(boolean value, String pUsername) {
        col.update(eq("username", pUsername), createDocument("online", value));
    }

    /**
     * Setzt in der Datenbank, ob die Emailadresse des Users bestätigt ist
     *
     * @param value der zu setzende Wert
     * @param pUsername Der Benutzername, dessen E-Mailbestätigung gesetzt
     * werden soll
     */
    public void setMailConfirmed(boolean value, String pUsername) {
        col.update(eq("username", pUsername), createDocument("mailconfirmed", value));
    }

    /**
     * setzt neues Passwort nach Passwortreset
     *
     * @param email
     * @param password
     */
    public void setPassword(String email, String password) {
        col.update(eq("mail", email), createDocument("password", password));
    }

    /**
     * Gibt den Benutzername anhand der Emailadresse zurück
     *
     * @param email Die Email, deren Benutzer gesucht wird
     * @return Gibt den Benutzernamen zurück
     */
    public String getUsername(String email) {
        Cursor users = col.find(eq("mail", email));
        if (users.size() > 0) {
            return users.firstOrDefault().get("username").toString();
        } else {
            InstanceManager.printError("Could not find email: " + email);
            return null;
        }
    }

    public String getLanguage(String username) {
        Cursor users = col.find(eq("username", username));
        if (users.size() > 0) {
            return users.firstOrDefault().get("language").toString();
        } else {
            InstanceManager.printError("Could not find user: " + username);
            return null;
        }
    }

    public Boolean getOnline(String username) {
        Cursor users = col.find(eq("username", username));
        if (users.size() > 0) {
            return (Boolean) users.firstOrDefault().get("online");
        } else {
            InstanceManager.printError("Could not find user: " + username);
            return null;
        }
    }

    public Boolean getMailConfirmed(String username) {
        Cursor users = col.find(eq("username", username));
        if (users.size() > 0) {
            return (Boolean) users.firstOrDefault().get("mailconfirmed");
        } else {
            InstanceManager.printError("Could not find user: " + username);
            return null;
        }
    }

    /**
     * Setzt die Online-Einträge nach Neustart des Servers wieder zurück
     */
    public void resetOnlineStatusOfPlayers() {
        col.update(null, createDocument("online", false));
    }
}
