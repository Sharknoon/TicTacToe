/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.database;

import general.InstanceManager;
import java.util.UUID;
import org.dizitart.no2.Nitrite;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author frank
 */
public class CoreDatabaseTest {

    public CoreDatabaseTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        Nitrite db = Nitrite.builder()
                .filePath(System.getProperty("user.dir") + "\\TicTacToe.db")
                .openOrCreate();
        InstanceManager.addInstance(db);
        InstanceManager.addInstance(new CoreDatabase());
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of login method, of class CoreDatabase.
     */
    @Test
    public void testLogin() {
        String randomUser = "ffeed117-d7ad-46d7-b95f-e85c13ef7f41";
        String randomPW = "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e";
        if (InstanceManager.getCoreDatabase().isAccountAlreadyAssigned(randomUser, null)) {
            InstanceManager.getCoreDatabase().deletePlayer(randomUser);
        }
        assertEquals(0, InstanceManager.getCoreDatabase().login(randomUser, randomPW));
        InstanceManager.getCoreDatabase().register(randomUser, "Bot", "blub", randomPW, "i@i.de", "de");
        assertFalse(InstanceManager.getCoreDatabase().getMailConfirmed(randomUser));
        InstanceManager.getCoreDatabase().setMailConfirmed(true, randomUser);
        assertTrue(InstanceManager.getCoreDatabase().getMailConfirmed(randomUser));
        assertEquals(1, InstanceManager.getCoreDatabase().login(randomUser, randomPW));
        InstanceManager.getCoreDatabase().setOnline(true, randomUser);
        assertTrue(InstanceManager.getCoreDatabase().getOnline(randomUser));
        InstanceManager.getCoreDatabase().setOnline(false, randomUser);
        assertFalse(InstanceManager.getCoreDatabase().getOnline(randomUser));
        assertEquals("de", InstanceManager.getCoreDatabase().getLanguage(randomUser));
    }

}
