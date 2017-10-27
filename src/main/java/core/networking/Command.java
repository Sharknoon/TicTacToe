/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.networking;

import general.Constants;
import java.util.concurrent.Callable;
import core.database.CoreDatabase;
import game.networking.PlayerManager;
import general.InstanceManager;
import java.util.ArrayList;

/**
 *
 * @author Josua Frank
 */
public class Command implements Callable<Void> {

    /**
     * Führt Befehle aus
     *
     * @param pCommand Der auszuführende Befehl Beispielbefehl: z.B. /login
     * admin 1234
     * @param pClient Die GameManager, um Befehle zum clienten zu schicken
     */
    public Command(String[] pCommand, ClientManager pClient) {
        ClientManager client = pClient;
        PlayerManager player = client.getPlayerManager();
        CoreDatabase coreDatabase = InstanceManager.getCoreDatabase();
        ServerManager server = InstanceManager.getServerManager();
        Email email = InstanceManager.getEmail();

        boolean correctStringLenth = true;
        for (String command : pCommand) {
            if (command.length() > 200) {
                correctStringLenth = false;
            }
        }
        if (pCommand.length > 10) {
            InstanceManager.printError(client.getUsername() + " versucht, den Server zu hacken (Befehlsarray zu lang)");
            client.disconnect(true);
        } else if (!correctStringLenth) {
            InstanceManager.printError(client.getUsername() + " versucht, den Server zu hacken (Mindestens ein Befehl ist zu lang)");
            client.disconnect(true);
        } else {
            int state = client.getState();
            try {
                switch (pCommand[0]) {
                    case Constants.PING:
                        client.checkConnection(false);
                        break;
                    case Constants.LOGIN:
                        if (state == 0) {
                            int login = coreDatabase.login(pCommand[1], pCommand[2]);
                            switch (login) {
                                case 1:
                                    //Hier die Überprüfung ob die Login-Daten richtig sind (Username und PW)
                                    if (!coreDatabase.getOnline(pCommand[1])) {//Hier die Überprüfung, ob er schon angemeldet ist
                                        if (coreDatabase.getMailConfirmed(pCommand[1])) {//Hier die Überprüfung, ob er seine E-Mailadresse schon bestätigt hat
                                            InstanceManager.printLine("Erfolgreicher Login von " + pCommand[1]);
                                            client.setUsername(pCommand[1]);
                                            coreDatabase.setOnline(true, pCommand[1]);
                                            client.setState(1);
                                            String[] command = {Constants.LOGINSUCCESSFUL};
                                            client.sendObject(command);
                                            player.receiveConnect();
                                        } else {
                                            InstanceManager.printLine("Fehlgeschlagener Login-Versuch aufgrund fehlender Email-verifizierung");//Darf eig. nicht sein, weil man erst in die DB eingetragen wird, wenn man die Email confirmed hat
                                        }
                                    } else {
                                        InstanceManager.printLine("Session von " + pCommand[1] + " läuft schon.");
                                        String[] command = {Constants.ALREADYLOGGEDIN};
                                        client.sendObject(command);
                                    }
                                    break;
                                case 0: {
                                    InstanceManager.printLine("Fehlgeschlagener Login von " + pCommand[1]);
                                    String[] command = {Constants.LOGINUNSUCCESSFUL};
                                    client.sendObject(command);
                                    break;
                                }
                                default: {
                                    InstanceManager.printLine("Versuchter SQL-Injection-Angriff von dem vermeintlichen " + pCommand[1] + " (" + client.getIP() + ")");
                                    String[] command = {Constants.NIXSQLINJECTION};
                                    client.sendObject(command);
                                    break;
                                }
                            }
                        } else {
                            String[] command = {"msg", "Not allowed in the current context"};
                            client.sendObject(command);
                        }
                        break;

                    case Constants.REGISTER://{register, username, firstname, lastname, pw1, pw2, email, language}
                        if (state == 0) {
                            String[] check = {pCommand[1], pCommand[2], pCommand[3], pCommand[6], pCommand[4], pCommand[5]};
                            if (coreDatabase.isAccountAlreadyAssigned(pCommand[1], pCommand[6])) {//Prüfung, ob Account bereits mit Usernamen oder Email schoin reigstriert ist
                                InstanceManager.printLine("Account von " + pCommand[1] + " bereits registriert.");
                                String[] command = {Constants.ACCOUNTALREADYASSIGNED};
                                client.sendObject(command);
                            } else if (!Check.check(check).isEmpty()) {
                                String[] command = {"msg", "Wrong Register parameters"};
                                client.sendObject(command);
                                ArrayList<String> errorList = Check.check(check);
                                errorList.forEach((error) -> {
                                    InstanceManager.printError("Registierungsfehler: " + error);
                                });
                            } else {
                                client.setUsername(pCommand[1]);
                                client.setRegisterParameters(pCommand);
                                String verificationCode = String.valueOf(client.createVerificationCode());
                                email.sendConfirmationMail(pCommand[6], pCommand[7], verificationCode);
                                InstanceManager.printLine("Sende Bestätigungsmail mit dem Code " + verificationCode);
                                String[] command = {Constants.REGISTERSUCCESSFUL};
                                client.sendObject(command);
                            }
                        } else {
                            String[] command = {"msg", "Not allowed in the current context"};
                            client.sendObject(command);
                        }
                        break;

                    case Constants.DISCONNECT:
                        client.disconnect(false);
                        break;

                    case Constants.EMAILCODE:
                        if (state == 0) {
                            String[] parameter = client.getRegisterParameters();
                            //Bei empty hat der Client auf OK geklickt ohne etwas einzugeben
                            //Bei cancel hat der Client auf Cancel geklickt
                            //Bei Länge unter 8 hat er einen Fehler gemacht
                            String verificationCode = client.getVerificationCode();
                            if (pCommand[1].equals("empty") || pCommand[1].length() < 8) {//Er hat einen dummen Fehler gemacht
                                InstanceManager.printLine(client.getUsername() + " hat keinen Code eingegeben oder er war zu kurz (" + pCommand[1] + "), aber auf OK geklickt -> neue Codeanfrage schicken");
                                if (!(verificationCode.equals(""))) {
                                    String[] command = {Constants.EMAILCODENOTVALID};
                                    client.sendObject(command);
                                } else {
                                    String[] command2 = {Constants.EMAILCODENOTVALID};
                                    client.sendObject(command2);
                                    String[] command = {Constants.NOMOREATTEMPTS};
                                    client.sendObject(command);
                                    String[] regParameter = client.getRegisterParameters();
                                    String newValidationCode = String.valueOf(client.createVerificationCode());
                                    email.sendConfirmationMail(regParameter[5], regParameter[6], newValidationCode);
                                    InstanceManager.printLine("Sende erneut Bestätigungsmail mit dem Code " + newValidationCode);
                                }
                            } else if (pCommand[1].equals("cancel")) {//Er möchte abbrechen
                                InstanceManager.printLine(client.getUsername() + " hat auf Abbrechen geklickt -> Registrierung abbrechen");
                                String[] command = {Constants.REGISTERABORT};
                                client.sendObject(command);
                            } else if (pCommand[1].equals(verificationCode)) {//Sein Code stimmt
                                coreDatabase.register(parameter[1], parameter[2], parameter[3], parameter[4], parameter[6], parameter[7]);
                                InstanceManager.printLine("Der Emailcode stimmt überein -> erfolgreiche Registrierung von " + parameter[1] + " (" + parameter[2] + " " + parameter[3] + ")");
                                player.receiveNewUser(server.getClientManager(parameter[1]));
                                coreDatabase.setMailConfirmed(true, client.getUsername());
                                String[] command = {Constants.EMAILCODEVALID};
                                client.sendObject(command);
                            } else {//Sein Code stimmt nicht
                                InstanceManager.printLine("Eingegebener E-Mailcode von " + client.getUsername() + " stimmt nicht überein: " + pCommand[1] + " ungleich " + verificationCode);
                                if (!(verificationCode.equals(""))) {
                                    String[] command = {Constants.EMAILCODENOTVALID};
                                    client.sendObject(command);
                                } else {
                                    String[] command = {Constants.EMAILCODENOTVALID};
                                    client.sendObject(command);
                                    String[] command2 = {Constants.NOMOREATTEMPTS};
                                    client.sendObject(command2);
                                    String[] regParameter = client.getRegisterParameters();
                                    String newValidationCode = String.valueOf(client.createVerificationCode());
                                    email.sendConfirmationMail(regParameter[5], regParameter[6], newValidationCode);
                                    InstanceManager.printLine("Sende erneut Bestätigungsmail mit dem Code " + newValidationCode);
                                }
                            }
                        } else {
                            String[] command = {"msg", "Not allowed in the current context"};
                            client.sendObject(command);
                        }
                        break;

                    case Constants.RESENDEMAILCODE:
                        if (state == 0) {
                            String[] regParameter = client.getRegisterParameters();
                            String newValidationCode = String.valueOf(client.createVerificationCode());
                            email.sendConfirmationMail(regParameter[5], regParameter[6], newValidationCode);
                            InstanceManager.printLine("Sende erneut Bestätigungsmail mit dem Code " + newValidationCode);
                        } else {
                            String[] command = {"msg", "Not allowed in the current context"};
                            client.sendObject(command);
                        }
                        break;

                    case Constants.REQUESTNEWPASSWORD:
                        //Mailcheck
                        if (state == 0) {
                            try {
                                String language = coreDatabase.getLanguage(coreDatabase.getUsername(pCommand[1]));
                                client.setEmail(pCommand[1]);
                                client.setUsername(pCommand[2]);
                                client.setLanguage(language);
                                String verificationCode2 = String.valueOf(client.createVerificationCode());
                                email.sendNewPasswordMail(pCommand[1], language, verificationCode2);
                                InstanceManager.printLine("Sende Passwort-Vergessenmail mit dem Code " + verificationCode2 + " an " + pCommand[1]);
                                String[] command = {Constants.SHOWPASSWORDCONFIRMATION};
                                client.sendObject(command);
                            } catch (Exception e) {
                                InstanceManager.printLine("Eingegebene Emailadresse existiert nicht: " + pCommand[1]);
                                String[] command = {Constants.EMAILDOESNOTEXIST};
                                client.sendObject(command);
                            }
                        } else {
                            String[] command = {"msg", "Not allowed in the current context"};
                            client.sendObject(command);
                        }
                        break;

                    case Constants.NEWPASSWORD:
                        if (state == 0) {
                            String verificationCode3 = client.getVerificationCode();
                            if (!pCommand[1].equals(verificationCode3)) {
                                if (!(verificationCode3.equals(""))) {
                                    InstanceManager.printLine("Der eingegebene PasswortVergessenCode stimmt nicht überein (" + pCommand[1] + " != " + verificationCode3 + ")");
                                    String[] command = {Constants.PASSWORTCODENOTVALID};
                                    client.sendObject(command);
                                } else {
                                    String[] command2 = {Constants.SHOWPASSWORDCONFIRMATION};
                                    client.sendObject(command2);
                                    String[] command = {Constants.NOMOREATTEMPTS};
                                    client.sendObject(command);
                                    String newValidationCode2 = String.valueOf(client.createVerificationCode());
                                    email.sendNewPasswordMail(client.getEmail(), client.getLanguage(), newValidationCode2);
                                    InstanceManager.printLine("Sende erneut Bestätigungsmail mit dem Code " + newValidationCode2);
                                }
                            } else if (!pCommand[2].equals(pCommand[3])) {
                                InstanceManager.printLine("Die eingegebenen Passwörter stimmen nicht überein (" + pCommand[2] + " != " + pCommand[3] + ")");
                                String[] command = {Constants.PASSWORDSNOTEQUAL};
                                client.sendObject(command);
                            } else {
                                String email2 = client.getEmail();
                                String username = coreDatabase.getUsername(email2);
                                InstanceManager.printLine("Setze neues Passwort für " + username);
                                coreDatabase.setPassword(email2, pCommand[2]);
                                String[] command = {Constants.PASSWORDCODEVALID};
                                client.sendObject(command);
                            }
                        } else {
                            String[] command = {"msg", "Not allowed in the current context"};
                            client.sendObject(command);
                        }
                        break;

                    default:
                        player.receiveCommand(pCommand);//Falls der command nichts mit dem Core zu tun hat, sondern mit dem eigentlichen Spiel
                        break;
                }
            } catch (Exception e) {
                String error = "Fehler von '" + client.getUsername() + "' beim Befehl-Verarbeiten:";
                for (String pCommand1 : pCommand) {
                    error += " '" + pCommand1 + "'";
                }
                InstanceManager.printError(error);
                e.printStackTrace(System.err);
            }
        }
    }

    @Override
    public Void call() throws Exception {
        return null;
    }

}
