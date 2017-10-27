/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import java.util.HashMap;

/**
 *
 * @author Josua Frank
 */
public class Logic {

    /**
     * Überprüft, ob diese Spielfeldkonstellation einen Gewinn darstellt
     *
     * @param pitch Das zu überprüfende Spielfeld
     * @return Der Benutzername des eventuellen Gewinners, falls kein Gewinner
     * fest steht, wird null zurückgegeben
     */
    public static String checkWin(HashMap<Integer, String> pitch) {
        String one, two, three;

        for (int i = 0; i <= 6; i += 3) {//Prüfe Horizontal
            one = pitch.get(i);
            two = pitch.get(i + 1);
            three = pitch.get(i + 2);
            if (one.equals(two) && one.equals(three) && !one.equals("")) {
                return one;
            }
        }

        for (int i = 0; i <= 2; i++) {//Prüfe Vertikal
            one = pitch.get(i);
            two = pitch.get(i + 3);
            three = pitch.get(i + 6);
            if (one.equals(two) && one.equals(three) && !one.equals("")) {
                return one;
            }
        }

        //Diagonal von oben links nach unten rechts
        one = pitch.get(0);
        two = pitch.get(4);
        three = pitch.get(8);
        if (one.equals(two) && one.equals(three) && !one.equals("")) {
            return one;
        }

        //Diagonal von unten links nach oben rechts
        one = pitch.get(6);
        two = pitch.get(4);
        three = pitch.get(2);
        if (one.equals(two) && one.equals(three) && !one.equals("")) {
            return one;
        }
        return null;
    }
}
