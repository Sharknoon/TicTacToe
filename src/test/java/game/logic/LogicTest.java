/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.logic;

import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Josua Frank
 */
public class LogicTest {

    /**
     * Test of checkWin method, of class Logic.
     */
    @Test
    public void testCheckWin() {
        //Check Horizontally
        HashMap<Integer, String> testMap = new HashMap<>();
        testMap.put(0, "testuser");
        testMap.put(1, "testuser");
        testMap.put(2, "testuser");
        testMap = this.fillUpTheRest(testMap);
        assertEquals(Logic.checkWin(testMap), "testuser");

        //Check Vertically
        testMap.clear();
        testMap.put(0, "testuser");
        testMap.put(3, "testuser");
        testMap.put(6, "testuser");
        testMap = this.fillUpTheRest(testMap);
        assertEquals(Logic.checkWin(testMap), "testuser");

        //Check Top Left to Bottom Right
        testMap.clear();
        testMap.put(0, "testuser");
        testMap.put(4, "testuser");
        testMap.put(8, "testuser");
        testMap = this.fillUpTheRest(testMap);
        assertEquals(Logic.checkWin(testMap), "testuser");

        //Check Top Right to Bottom Left
        testMap.clear();
        testMap.put(2, "testuser");
        testMap.put(4, "testuser");
        testMap.put(6, "testuser");
        testMap = this.fillUpTheRest(testMap);
        assertEquals(Logic.checkWin(testMap), "testuser");

        //Check fail if there are different names
        testMap.clear();
        testMap.put(0, "hanspeter");
        testMap.put(1, "hanswurst");
        testMap.put(2, "hansmirdochegal");
        testMap = this.fillUpTheRest(testMap);
        assertNull(Logic.checkWin(testMap));

        //Check fail if the names are on different places where they dont win
        testMap.clear();
        testMap.put(0, "hanspeter");
        testMap.put(3, "hanspeter");
        testMap.put(2, "hanspeter");
        testMap = this.fillUpTheRest(testMap);
        assertNull(Logic.checkWin(testMap));
    }

    public HashMap<Integer, String> fillUpTheRest(HashMap<Integer, String> testMap) {
        for (int i = 0; i < 9; i++) {
            if (testMap.get(i) == null) {
                testMap.put(i, "");
            }
        }
        return testMap;
    }

}
