/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.Tests;

import general.Language;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Administrator
 */
public class LanguageTests {

    Language objectUnderTest = new Language();
    String mainLanguage = System.getProperty("user.language");

    @Test
    public void TestGetString() {
        if (mainLanguage.equals("en")) {
            assertEquals(objectUnderTest.getString(2), "existing user");
        }
        if (mainLanguage.equals("de")) {
            assertEquals(objectUnderTest.getString(2), "Bestehender Benutzer");
        }
        assertEquals(objectUnderTest.getString(999999999), "Error");
    }

    @Test
    public void TestGetLanguageShortcut() {
        if (mainLanguage.equals("en")) {
            assertEquals(objectUnderTest.getLanguageShortcut("English"), "en");
        }
        if (mainLanguage.equals("de")) {
            assertEquals(objectUnderTest.getLanguageShortcut("Deutsch"), "de");
        }
        assertEquals(objectUnderTest.getLanguageShortcut("asfkgvnso"), "xx");
    }
    

}
