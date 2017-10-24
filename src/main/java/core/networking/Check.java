/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.networking;

import java.util.ArrayList;

/**
 *
 * @author Niklas, Josua
 */
public final class Check {

    /**
     * String[0] = username, String[1] = firstname, String[2] = lastname,
     * String[3] = email, String[4] = PW1, String[5] = PW2
     * @param regData
     */
    public static ArrayList<String> check(String[] regData) {
        ArrayList<String> fehler = new ArrayList<>();
        String usernameErrors = Check.checkUsername(regData[0]);
        if (usernameErrors != null) {
            fehler.add(usernameErrors);
        }
        String FirstNameErrors = Check.checkFirstName(regData[1]);
        if (FirstNameErrors != null) {
            fehler.add(FirstNameErrors);
        }
        String LastNameErrors = Check.checkLastName(regData[2]);
        if (LastNameErrors != null) {
            fehler.add(LastNameErrors);
        }
        ArrayList<String> mailErrors = Check.checkMail(regData[3]);
        if (!mailErrors.isEmpty()) {
            fehler.addAll(mailErrors);
        }
        String PasswordErrors = Check.checkPassword(regData[4], regData[5]);
        if (PasswordErrors != null) {
            fehler.add(PasswordErrors);
        }
        return fehler;
    }

    /**
     * Überprüft den Vornamen des sich registrierenden Benutzers
     *
     * @param name der zu überprüfende Namen
     * @return 
     */
    public static String checkFirstName(String name) {
        if (name.length() < 2) {
            return "firstnametooshort";
        } else if (!name.matches("[A-Za-z]+")) {
            return "firstnamewrongcharacters";
        } else if (name.length() > 20) {
            return "firstnametoolong";
        } else {
            return null;
        }
    }

    /**
     * Überprüft den Nachnamen des sich registrierenden Benutzers
     *
     * @param name der zu überprüfende Namen
     * @return 
     */
    public static String checkLastName(String name) {
        if (name.length() < 2) {
            return "lastnametooshort";
        } else if (!name.matches("[A-Za-z]+")) {
            return "lastnamewrongcharacters";
        } else if (name.length() > 20) {
            return "lastnametoolong";
        } else {
            return null;
        }
    }

    /**
     * Überprüft den Benutzernamen des sich registrierenden Benutzers
     *
     * @param username der zu überprüfende Benutzername
     * @return 
     */
    public static String checkUsername(String username) {
        if (username.length() < 4) {
            return "usernametooshort";
        } else if (!username.matches("[A-Za-z0-9_-]+")) {
            return "usernamewrongcharacters";
        } else if (username.length() > 20) {
            return "usernametoolong";
        } else {
            return null;
        }
    }

    /**
     * Prüft die Passwörter auf Gleichheit und Mindestlänge
     *
     * @param password1 Passwort 1 zum Überprüfen
     * @param password2 Passwort 2 zum Überprüfen
     * @return 
     */
    public static String checkPassword(String password1, String password2) {
        if (password1.length() < 8) {
            return "pwtooshort";
        } else if (!password1.equals(password2)) {
            return "pwnotequal";
        } else if (password1.length() > 150) {
            return "pwtoolong";
        } else {
            return null;
        }
    }

    /**
     * Prüft, ob die Mailadresse syntaktisch zulässig ist
     *
     * @param mail Die E-Mailadresse, die zu überprüfen ist
     * @return 
     */
    public static ArrayList<String> checkMail(String mail) {
        boolean mail_correct = false;
        ArrayList<String> ergebnis = new ArrayList<>();
        if (mail.length() > 30) {
            ergebnis.add("mailtoolong");
        } else if (!mail.contains("@") || !mail.contains(".")) {
            ergebnis.add("mailmissing@or.");
        } else if (mail.length() < 6) {
            ergebnis.add("mailtooshort");
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
                ergebnis.add("mailwrongsyntax");
            }
            if (unmatchingError) {
                ergebnis.add("mailwrongcharacters");
            }
        }
        return ergebnis;
    }
}
