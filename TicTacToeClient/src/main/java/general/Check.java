/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import java.util.ArrayList;

/**
 *
 * @author Niklas, Josua
 */
public final class Check {

    private Language languages;

    public Check(Language planguage) {
        this.languages = planguage;
    }

    public ArrayList<String> check(String pUsername, String pFirstName, String pLastName, String pEmail, String pPassword1, String pPassword2) {
        ArrayList<String> fehler = new ArrayList<>();
        String usernameErrors = this.checkUsername(pUsername);
        if (usernameErrors != null) {
            fehler.add(usernameErrors);
        }
        String FirstNameErrors = this.checkFirstName(pFirstName);
        if (FirstNameErrors != null) {
            fehler.add(FirstNameErrors);
        }
        String LastNameErrors = this.checkLastName(pLastName);
        if (LastNameErrors != null) {
            fehler.add(LastNameErrors);
        }
        ArrayList<String> mailErrors = this.checkMail(pEmail);
        if (!mailErrors.isEmpty()) {
            fehler.addAll(mailErrors);
        }
        String PasswordErrors = this.checkPassword(pPassword1, pPassword2);
        if (PasswordErrors != null) {
            fehler.add(PasswordErrors);
        }
        return fehler;
    }

    /**
     * Überprüft den Vornamen des sich registrierenden Benutzers
     *
     * @param name der zu überprüfende Namen
     */
    public String checkFirstName(String name) {
        if (name.length() < 2) {
            return languages.getString(17) + languages.getString(23);
        } else if (!name.matches("[A-Za-z]+")) {
            return languages.getString(17) + languages.getString(20);
        } else if (name.length() > 20) {
            return languages.getString(17) + languages.getString(92);
        } else {
            return null;
        }
    }

    /**
     * Überprüft den Nachnamen des sich registrierenden Benutzers
     *
     * @param name der zu überprüfende Namen
     */
    public String checkLastName(String name) {
        if (name.length() < 2) {
            return (languages.getString(18) + languages.getString(23));
        } else if (!name.matches("[A-Za-z]+")) {
            return (languages.getString(18) + languages.getString(20));
        } else if (name.length() > 20) {
            return languages.getString(18) + languages.getString(92);
        } else {
            return null;
        }
    }

    /**
     * Überprüft den Benutzernamen des sich registrierenden Benutzers
     *
     * @param username der zu überprüfende Benutzername
     */
    public String checkUsername(String username) {
        if (username.length() < 4) {
            return languages.getString(15) + languages.getString(23);
        } else if (!username.matches("[A-Za-z0-9_-]+")) {
            return languages.getString(15) + languages.getString(20);
        } else if (username.length() > 20) {
            return languages.getString(15) + languages.getString(92);
        } else {
            return null;
        }
    }

    /**
     * Prüft die Passwörter auf Gleichheit und Mindestlänge
     *
     * @param password1 Passwort 1 zum Überprüfen
     * @param password2 Passwort 2 zum Überprüfen
     */
    public String checkPassword(String password1, String password2) {
        if (password1.length() < 8) {
            return languages.getString(16) + languages.getString(23);
        } else if (!password1.equals(password2)) {
            return languages.getString(21);
        } else if (password1.length() > 20) {
            return languages.getString(16) + languages.getString(92);
        } else {
            return null;
        }
    }

    /**
     * Prüft, ob die Mailadresse syntaktisch zulässig ist
     *
     * @param mail Die E-Mailadresse, die zu überprüfen ist
     */
    public ArrayList<String> checkMail(String mail) {
        boolean mail_correct = false;
        ArrayList<String> ergebnis = new ArrayList<>();
        if (mail.length() > 30) {
            ergebnis.add(languages.getString(19) + languages.getString(92));
        } else if (!mail.contains("@") || !mail.contains(".")) {
            ergebnis.add(languages.getString(24));
        } else if (mail.length() < 6) {
            ergebnis.add(languages.getString(19) + languages.getString(23));
        } else {
            int indexofAT = mail.indexOf("@");
            int indexPointTopDomain = mail.lastIndexOf('.');
            boolean generalEmailError = false;
            boolean unmatchingError = false;

            String adresse = mail.substring(0, indexofAT);
            String domain = mail.substring(indexofAT + 1, indexPointTopDomain);
            String topdomain = mail.substring(indexPointTopDomain + 1);

            if (adresse.length() < 1) {
                generalEmailError = true;
                System.out.println("1");
            } else if (!adresse.matches("[A-Za-z0-9._-]+")) {
                unmatchingError = true;
            } else if (adresse.charAt(0) == '.' || adresse.charAt(0) == '_' || adresse.charAt(0) == '-') {
                generalEmailError = true;
                System.out.println("2");
            } else if (adresse.charAt(indexofAT - 1) == '.' | adresse.charAt(indexofAT - 1) == '_' | adresse.charAt(indexofAT - 1) == '-') {
                generalEmailError = true;
                System.out.println("3");
            } else if (domain.length() < 1) {
                generalEmailError = true;
                System.out.println("4");
            } else if (!domain.matches("[a-z0-9._-]+")) {
                unmatchingError = true;
            } else if (domain.charAt(0) == '.' || domain.charAt(0) == '-' || domain.charAt(0) == '_') {
                generalEmailError = true;
                System.out.println("5");
            } else if (domain.endsWith(".") || domain.endsWith("_") || domain.endsWith("-")) {
                generalEmailError = true;
                System.out.println("6");
            } else if (topdomain.length() < 2) {
                generalEmailError = true;
                System.out.println("7");
            } else if (!topdomain.matches("[a-z]+")) {
                unmatchingError = true;
            } else {
                for (int index = 0; index < mail.length(); index++) {
                    if (mail.charAt(index) == '.') {
                        if (mail.charAt(index + 1) == '.') {
                            generalEmailError = true;
                            System.out.println("8");
                        }
                    }
                }
            }
            if (generalEmailError) {
                ergebnis.add(languages.getString(25));
            }
            if (unmatchingError) {
                ergebnis.add(languages.getString(19) + languages.getString(20));
            }
        }
        return ergebnis;
    }
}
