package general;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Josua Frank
 */
public class Constants {

    //---------------------Core-Befehle-----------------------
    /*Befehle von dem Clienten*/
    public static final String LOGIN = "login";//Befehl vom Clienten, der sich einloggen möchte
    public static final String REGISTER = "register";//Befehl vom Clienten, der sich neu registriren möchte
    public static final String DISCONNECT = "disconnect";//Befehl vom Clienten, der sich abmelden möchte
    public static final String EMAILCODE = "emailcode";//Der Bestätigungscode vom Clienten, um seine E-Mailadresse zu verifizieren NEU
    public static final String RESENDEMAILCODE = "resend";//Der Client bittet um eine zweite E-Mail NEU
    public static final String REQUESTNEWPASSWORD = "passwordforgotten";//Der Client bittet um ein neues Passwort
    public static final String NEWPASSWORD = "newpassword"; //Der Client sendet sein neues Passwort samt Bestätigungscode

    /*Befehle an den Clienten*/
    public static final String PING = "ping";//Überprüfung des Clienten, ob der Server noch da ist
    public static final String LOGINSUCCESSFUL = "loginsuccessful";//Befehl an Clienten, dass er sich erfolgrecih angemeldet hat
    public static final String ALREADYLOGGEDIN = "alreadyloggedin";//Befehl an Clienten, dass dieser Account bereits angemeldet ist
    public static final String LOGINUNSUCCESSFUL = "loginunsuccessful";//Befehl an Clienten, dass die Anmeldung nicht erfolgreich war (meist PW oder Username)
    public static final String ACCOUNTALREADYASSIGNED = "accountalreadyassigned";//Befehl an Clienten, dass dieser Account bereits registriert ist
    public static final String REGISTERSUCCESSFUL = "registersuccessful";//Befehl an Clienten, dass das Registrieren seines Account erfolgreich verlaufen ist
    public static final String EMAILCODENOTVALID = "emailcodenotvalid";//Befehl an Clienten, dass der eingegebene E-Mailcode nicht richtig ist NEU
    public static final String EMAILCODEVALID = "emailcodevalid";//Befehl an Clienten, dass der eingegebene E-Mailcode richtig ist NEU
    public static final String REGISTERABORT = "registerabort";//Befehl an den Clienten, dass die Registrierung abgebrochen wurde, da er auf Abbrechen gecklickt hat oder keinen E-Mailcode eingegeben hat
    public static final String SHOWPASSWORDCONFIRMATION = "showPasswordConfirmation";//Zeigt beim Clienten das Fesnster mit dem Codefeld und den 2 Passwortfeldern an
    public static final String EMAILDOESNOTEXIST = "emaildoesnotexist";//Fehler an den Clienten, dass die eingegebene Emailadresse nicht existiert
    public static final String PASSWORTCODENOTVALID = "passwordcodenotvalid";//Der eingegeben Code des Clienten stimmt nicht
    public static final String PASSWORDSNOTEQUAL = "passwordsnotequal";//Befehl an den clienten, dass seine 2 neuen Passwörter nicht übereinstimmen
    public static final String PASSWORDCODEVALID = "passwordcodevalid";//Befehl an den Clienten, dass der eingegebene Code stimmt
    public static final String NIXSQLINJECTION = "nixsqlinjection";//Befehl an den Clienten, wenn er versucht, per SQL-Injection das Passwort zu umgehen
    public static final String NOMOREATTEMPTS = "nomoreattemps";//Befehl an den Clienten, dass er bei der Validierungscodeeingabe keine Versuche mehr übrig hat

    //---------------------Game-Befehle-----------------------
    /*Befehle von dem Clienten*/
    public static final String LOBBYCHATFROMCLIENT = "lobbychat";//Befehl vom Clienten, der eine Chatnachricht an den Lobbychat senden möchte
    public static final String GAMEREQUEST = "play";//Befehl vom Clienten, der eine Spielanfrage an einen Clienten stellt
    public static final String WATCHGAME = "watch";//Befehl vom Clienten, der einem Clienten beim Spiel zusehen möchte
    public static final String WATCHALSOGAME = "watchalso";//Befehl vom Clienten, der dem selben Spiel zuschauen möchte, wie dem, den er angeklickt hat
    public static final String ENDWATCH = "nolongerwatching";//Befehl vom Clienten, der jetzt nicht mehr dem Spiel zusieht
    public static final String TURN = "turn";//Befehl vom Clienten, wenn ein Spieler einen Zug gemacht hat
    public static final String TURNED = "turned";//Befehl vom Clienten, wenn er seinen Spielzug gemacht hat
    public static final String UNANSWERED = "requestunanswered";//Befehl vom Angefragten, wenn er auf seine Request nicht antwortet

    /*Befehle an den Clienten*/
    public static final String USERJOINED = "hello";//Befehl an alle Clienten, wenn ein neuer User beigetreten ist
    public static final String USERCRASHED = "crash";//Befehl an alle Clienten, wenn ein User seinen Clienten crasht
    public static final String USERLEFT = "goodbye";//Befehl an alle Clienten, wenn ein User das Spiel verlassen hat
    public static final String USERNOLONGERWATCHING = "nolongerwatching";//Befehl an alle Game-Member, wenn ein User ein Spiel nicht mehr zusieht.
    public static final String REQUESTFROM = "requestfrom";//Befehl an den Clienten, dass jemand ihn zu einem Spiel herausfordert

    /*Bidrektionale Befehle*/
    public static final String QUITGAME = "quitgame";//Befehl vom oder an Clienten, wenn er sein Spiel abbricht
    public static final String GAMECHAT = "gamechat";//Befehl vom oder an Clienten, die eine Chatnachricht an die Game-Member schreiben
    public static final String ACCEPTED = "accept";//Befehl vom oder an Clienten, der eine Anfrage angenommen hat
    public static final String DECLINED = "declined";//Befehl vom oder an Clienten, der eine Anfrage abgelehnt hat
}
