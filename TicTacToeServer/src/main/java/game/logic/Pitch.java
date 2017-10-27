/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import java.util.HashMap;

/**
 *
 * @author Niklas
 */
public class Pitch {

    //Variablen und Felder
    private final HashMap<Integer, String> pitch = new HashMap<>();

    public Pitch() {
        for (int i = 0; i < 9; i++) {
            pitch.put(i, "");//Setze das gesamtSpiefeld auf leer, d.h. kein Spieler ist darauf
        }
        pitch.put(999, "");
    }

    //public Methoden
    /**
     * Überprüft, ob dieses Feld noch frei ist
     *
     * @param pField Das zu überprüfende Feld
     * @return Gibt an, ob das Feld noch frei ist
     */
    public boolean isFieldEmpty(int pField) {
        return pitch.get(pField).equals("");
    }

    //Getter und Setter
    /**
     * Gibt die Anzahl der noch freien Felder zurück
     *
     * @return Die Anzahl der freien Felder
     */
    public int getEmptyFields() {
        int freeFields = 0;
        for (int i = 0; i < 9; i++) {
            if (pitch.get(i).equals("")) {
                freeFields++;
            }
        }
        return freeFields;
    }

    /**
     * Belegt ein Feld mit einem Spieler
     *
     * @param fieldNumberFromZero Das zu belegende Feld
     * @param pUsername Der Spieler, der ein Feld belegt
     */
    public void setField(int fieldNumberFromZero, String pUsername) {
        if (fieldNumberFromZero < 0 || fieldNumberFromZero > 8) {
            System.err.println("Fehler! angeforderte Spielnummer ist nicht zwischen 0 und 8: " + fieldNumberFromZero);
        } else {
            pitch.put(fieldNumberFromZero, pUsername);
        }
    }

    /**
     * Gibt das Spielfeld zurück
     *
     * @return Das zu zurückgebende Spielfeld
     */
    public HashMap<Integer, String> getPitch() {
        return pitch;
    }
}
