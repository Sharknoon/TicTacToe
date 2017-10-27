/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general;

import javafx.scene.Scene;

/**
 *
 * @author i01frajos445
 */
public interface Oberflaeche {

    /**
     * Gibt die Szene der jeweiligen Oberfläche zurück
     *
     * @return Das Objekt der Szene der Oberfläche
     */
    public Scene getScene();

    /**
     * Gibt die Größe der jeweiligen Szene zurück
     *
     * @return Die Größe als int-Array (int[0] = x, int[1] = y)
     */
    public int[] getSize();

    /**
     * Setzt die Größe einer jeweiligen Szene, indem es die Bevorzugte Größe der
     * AnchorPane anpasst
     *
     * @param size Die Größe als int-Array (int[0] = x, int[1] = y)
     */
    public void setSize(int[] size);
}
