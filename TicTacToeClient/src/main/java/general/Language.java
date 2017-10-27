/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

/**
 *
 * @author Josua Frank
 */
public class Language {

    private int amountLanguages;
    private String mainLanguage = System.getProperty("user.language");

    private String[] supportedLanguages
            = {"English", "Deutsch"};

    private String[] en = {
        /*0-8*/"0", "en", "existing user", "new user", "settings", "login", "server could not be reached", "login failed, server not connected!", "register",
        /*9-13*/ "login successful", "already logged in!", "wrong username or password!", "account already exists", "confirm your email to save your account, otherwise it will be deleted",
        /*14-21*/ "language", "username", "password", "first name", "last name", "emailadress", " contains illegal characters", "passwords are not equal",
        /*22-27*/ "password too short", " is too short", "missing charackters in the email", "invalid email", " has left the game", " has joined the game",
        /*28-35*/ "challenge", "watch", "online", "in a game", " wants to challenge you", "yes", "no", " has declined the gamerequest",
        /*36-41*/ " is watching the game", " is no longer watching the game", "watching a game", "also watch", "server unreachable, check your connection and try again", " can not be reached :(",
        /*42-48*/ "do you accept it? (you have 10 seconds to decide)", "error", "unfortunately an error has occured", "launcher", "information", "challenge-request", "players online",
        /*49-53*/ "is watching this game", "is playing this game", "back to lobby", "player", " has left the game, returning to lobby",
        /*54-57*/ "email verification", "please enter your emailcode, we send to [EMAIL]", "code", "che entered code is valid, please log in",
        /*58-60*/ "resend email", "sucessfully created account, you can log in now", "the entered code isnt working, use the 'resend email' button, to receive the email again",
        /*61-66*/ "registration aborted", "game", "its your turn!", "congratz [PLAYER], you have won the game!", "[PLAYER] has won the game!", "the game ended in a tie.",
        /*67-72*/ "its [PLAYER]s turn!", "player has already received a request, try again later", " has not answered your request", "try again", "close", "server has closed",
        /*73-81*/ "scores", "games\nplayed", "winratio", "accept", "decline", "cancel", "password forgotten", "Please enter your email adress and username", "email: ",
        /*82-86*/ "request new password", "Please enter your code and twice your new password", "Successfully changed password", "The entered code is wrong", "Email does not exist",
        /*87-90*/ "Connection lost", " has not reacted in the last 10 seconds!", "You havent reacted in the last 10 seconds, aborting game", "player has already send a request to someone else, try again later",
        /*91-xx*/ "Input exceed [NUMBER] characters", " is too long", "Emailcode ", "No more attempts, sending verificationmail again", "invalid mail or username"
    };

    private String[] de = {
        "1", "de", "Bestehender Benutzer", "Neuer Benutzer", "Einstellungen", "Anmelden", "Server konnte nicht erreicht werden", "Anmelden fehlgeschlagen, Server nicht verbunden!", "Registrieren",
        "Erfolgreich angemeldet", "Bereits angemeldet!", "Falscher Benutzername oder Passwort!", "Benutzerkonto bereits vorhanden!", "E-Mail Verifikation erforderlich, um dein Benutzerkonto zu speichern, ansonsten wird es gelöscht",
        "Sprache", "Benutzername", "Passwort", "Vorname", "Nachname", "E-Mailadresse", " beinhaltet unzulässige Zeichen", "Passwörter stimmen nicht überein",
        "Passwort ist zu kurz", " ist zu kurz", "Es fehlen Zeichen in der E-Mailadresse", "Unzulässige E-Mail", " hat das Spiel verlassen", " ist dem Spiel beigetreten",
        "Herausfordern", "Zusehen", "Online", "Im Spiel", " möchte Sie herausfordern", "Ja", "Nein", " hat die Spieleinladung abgelehnt",
        " schaut dem Spiel zu", " schaut dem Spiel nicht mehr zu", "Schaut einem Spiel zu", "Ebenfalls zusehen", "Server unerreichbar, überprüfe deine Internetverbindung und versuche es erneut", " ist nicht mehr erreichbar :(",
        "Möchten Sie die Herausforderung annehmen? (Du hast 10 Sekunden, dich zu entscheiden)", "Fehler", "Leider ist ein Fehler aufgetreten", "Launcher", "Information", "Herausforderung", "Spieler online",
        "Schaut diesem Spiel zu", "Spielt dieses Spiel gerade", "Zurück zur Lobby", "Spieler", " hat das Spiel verlassen, kehre zur Lobby zurück",
        "E-Mail Verifikation", "Bitte gebe den E-Mailcode ein, den wir an die E-Mailadresse [EMAIL] geschickt haben", "Code", "Der eingegebene Code stimmt, nun bitte normal anmelden",
        "E-Mail erneut senden", "Benutzerkonto erfolgreich erstellt, du kannst dich jetzt anmelden", "Der eingegebene E-Mailcode ist leider falsch, benutze den 'E-Mail erneut senden' Knopf, um die E-Mail erneut zugesendet zu bekommen",
        "Registrierung abgebrochen", "Spiel", "Du bist an der Reihe!", "Glückwunsch [PLAYER], du hast das Spiel gewonnen!", "[PLAYER] hat das Spiel gewonnen!", "Das Spiel endet unentschieden.",
        "[PLAYER] ist an der Reihe", "Spieler hat bereits eine Spielanfrage bekommen, versuche es später nochmals", " hat auf deine Anfrage nicht geantwortet", "Erneut versuchen", "Schließen", "Server wurde geschlossen",
        "Bestenliste", "Gespielte\nSpiele", "Gewinn-\nverhältnis", "Akzeptieren", "Ablehnen", "Abbrechen", "Passwort vergessen", "Bitte gebe deine E-Mailadresse und deinen Benutzernamen ein", "E-Mail: ",
        "Neues Passwort beantragen", "Bitte gebe deinen Code und zweimal dein neues Passwort ein", "Passwort erfolgreich geändert", "Der eingegebene Code ist falsch", "E-Mail existiert nicht",
        "Verbindung verloren", " hat in den letzten 10 Sekunden nicht reagiert!", "Du hast nicht innerhalb von 10 Sekunden reagiert!","Spieler hat bereits einem anderem Spieler eine Anfrage geschickt, versuche es später nochmals",
        "Eingaben überschreiten [NUMBER] Zeichen", " ist zu lang", "Emailcode ", "Keine Verusche mehr übrig, sende Bestätigungsemail erneut", "Ungültige E-Mailadresse oder Benutzername"
    };

    public Language() {
        this.amountLanguages = supportedLanguages.length;
    }

    /**
     * Gibt das bestimmt Wort (durch ID bestimmt) zurück, welches in der Sprache
     * "language" ausgewählt wurde
     *
     * @param id Gibt an, welches Wort der Sprache zurückgegeben werden soll
     * @return Gibt das Wort zurück
     */
    public String getString(int id) {
        switch (mainLanguage) {
            case "de":
                if (id > (de.length - 1)) {
                    return "Error";
                } else {
                    return de[id];
                }
            case "en":
                if (id > (en.length - 1)) {
                    return "Error";
                } else {
                    return en[id];
                }
            default:
                return "Error, non-existing language";

        }
    }

    /**
     * Gibt aus einer ID das Sprachkürzel zurück
     *
     * @param pLanguage Die Sprache (z.B. "Deutsch", "English")
     * @return Gibt das Sprachkürzel zurück (z.B. "en")
     */
    public String getLanguageShortcut(String pLanguage) {
        switch (pLanguage) {
            case "English":
                return "en";
            case "Deutsch":
                return "de";
            default:
                return "xx";
        }
    }

    /**
     * Gibt für die Dropdownliste alle Sprachbezeichnungen zurück
     *
     * @return Die Sprache languageToDisplay in der Sprache language
     */
    public String[] getLanguage() {
        return this.supportedLanguages;
    }

    /**
     * Setzt die Hauptsprache
     *
     * @param languageShortcut Das Shortcut der zu setzenden Hauptsprache
     */
    public void setMainLanguage(String languageShortcut) {
        System.out.println("Setzte Standartlanguage " + languageShortcut);
        this.mainLanguage = languageShortcut;
    }

    /**
     * Gibt die Hauptsprache zurück
     *
     * @return die Hauptsprache als Kürzel
     */
    public String getMainLanguage() {
        return this.mainLanguage;
    }

    /*
     * Gibt die Anzahl der unterstützen Sprachen zurück
     */
    public int getAmountOfLanguages() {
        return this.amountLanguages;
    }

}
