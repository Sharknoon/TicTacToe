/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.Tests;
import general.Check;
import general.Language;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author Niklas
 */
public class CheckTests {
    
    Check objectUnderTest = new Check(new Language());
    String systemLanguage = System.getProperty("user.language");
    
    @Test
    // if firstname shorter than 3 chars -> fails
    public void TestCheckFirstNameLength(){
        if(systemLanguage.equals("en")){
            assertTrue(objectUnderTest.checkFirstName("J").equalsIgnoreCase("first name is too short)"));
        }
        else if(systemLanguage.equals("de")){
            assertTrue(objectUnderTest.checkFirstName("J").equals("Vorname ist zu kurz"));
        }
    }
    
    @Test
    // if firstname contains chars else than UTF-8 -> fails
    public void TestFirstNameCheckCharacters() {
        System.out.println(systemLanguage);
        if(systemLanguage.equals("en")){
            assertTrue(objectUnderTest.checkFirstName("Jos@ua").equals("first name contains illegal characters"));
        }
        else if(systemLanguage.equals("de")){
            assertTrue(objectUnderTest.checkFirstName("Jos@ua").equals("Vorname beinhaltet unzulässige Zeichen"));
        }
    }
     
    @Test
    // if lastname shorter than 3 chars -> fails
    public void TestLastNameLenght(){
        if(systemLanguage.equals("en")){
            assertTrue(objectUnderTest.checkLastName("H").equals("last name is too short"));
        }
        else if(systemLanguage.equals("de")){
            assertTrue(objectUnderTest.checkLastName("h").equals("Nachname ist zu kurz"));
        }
    }
    
    @Test
    // if lastname contains other chars than UTF-8 -> fails
    public void TestLastNameCharacters(){
        if(systemLanguage.equals("en")){
            assertTrue(objectUnderTest.checkLastName("Fr!ank").equals("last name contains illegal characters"));
        }
        else if(systemLanguage.equals("de")){
            assertTrue(objectUnderTest.checkLastName("Fr!ank").equals("Nachname beinhaltet unzulässige Zeichen"));
        }
    }
    
    @Test
    // if password is to short -> fails
    //TODO:
    public void TestPasswordCheckLenght() {
        if(systemLanguage.equals("en")){
            assertTrue(objectUnderTest.checkPassword("abd", "abd").equals("password is too short"));
        }
        else if(systemLanguage.equals("de")){
            assertTrue(objectUnderTest.checkPassword("abd", "abd").equals("Passwort ist zu kurz"));
        }
    }
    
    @Test
    // if entered passwords aren't equal -> fails
    //TODO:
    public void TestPasswordCheckEqual() {
        if(systemLanguage.equals("en")){
            assertTrue(objectUnderTest.checkPassword("abcdefghi", "abcdefgh").equals("passwords are not equal"));
        }
        else if(systemLanguage.equals("de")){
            assertTrue(objectUnderTest.checkPassword("abcdefghi", "abcdefgh").equals("Passwörter stimmen nicht überein"));
        }
    }
    
    @Test 
    // tests if mail is checked correctly
    // TODO:
    public void TestMailCheck() {
        if(systemLanguage.equals("en")){
            assertFalse(objectUnderTest.checkMail("lasökdjf").isEmpty());
        }
        else if(systemLanguage.equals("de")){
            assertFalse(objectUnderTest.checkMail("lasökdjf").isEmpty());
        }
    }
}