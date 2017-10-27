/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Niklas
 * Klasse zum Hashen der Passwörter, bevor diese in der DB gespeichert werden
 */
public class PasswordHasher {
    private static final String saltConstant = "H5Bsd6!l@HFl0t";
    
    /**
     * Saltet value mit Konstante und Hasht das resultat davon
     * 
     * @param value übertragenes Passwort von client (sollte bereits einmal gesaltet + gehashed sein)
     * @return 
     */
    public static String getHash(String value) {
        MessageDigest md;
        StringBuilder sb = new StringBuilder();
        
        String saltedPW;
        saltedPW = value + saltConstant;
        
        try {
            md = MessageDigest.getInstance("SHA-512");
            md.update(saltedPW.getBytes());
            byte byteData[] = md.digest();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException ex) {
            System.err.println("Algorithmus nicht verfügbar: " + ex.getMessage());
        }
        return sb.toString();
    }
}
