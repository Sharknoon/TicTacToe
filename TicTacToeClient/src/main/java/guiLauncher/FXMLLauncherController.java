/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiLauncher;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import networking.ClientManager;
import general.Check;
import general.Language;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Josua Frank
 */
public class FXMLLauncherController implements Initializable {

    @FXML
    public AnchorPane anchorPane;

    //Login
    @FXML
    private Label fxLabelLoginUsername;
    @FXML
    private TextField fxTextFieldLoginUsername;
    @FXML
    private Label fxLabelLoginPassword;
    @FXML
    private PasswordField fxPasswordFieldLoginPassword;
    @FXML
    private Button fxButtonLoginLogin;
    @FXML
    private Label fxLabelPasswordForgotten;

    //Registrierung
    @FXML
    private Label fxLabelRegisterUsername;
    @FXML
    private TextField fxTextFieldRegisterUsername;
    @FXML
    private Label fxLabelRegisterFirstName;
    @FXML
    private TextField fxTextFieldRegisterFirstName;
    @FXML
    private Label fxLabelRegisterLastName;
    @FXML
    private TextField fxTextFieldRegisterLastName;
    @FXML
    private Label fxLabelRegisterEmail;
    @FXML
    private TextField fxTextFieldRegisterEmail;
    @FXML
    private Label fxLabelRegisterPassword1;
    @FXML
    private PasswordField fxPasswordFieldRegisterPassword1;
    @FXML
    private Label fxLabelRegisterPassword2;
    @FXML
    private PasswordField fxPasswordFieldRegisterPassword2;
    @FXML
    private Button fxButtonRegisterRegister;

    //Generell
    @FXML
    private Label fxLabelState;
    @FXML
    private ComboBox fxComboBoxLanguage;
    @FXML
    private ImageView fxImageViewHeader;
    @FXML
    private ClientManager client;
    private Language languages;
    private LauncherOberflaeche oberflaeche;

    /**
     * Wird aufgerufen, wenn das Fenster verändert wird
     *
     * @param x Die X-Koordinate, eine von beiden ist meist immer null
     * @param y Die Y-Koordinate, eine von beiden ist meist immer null
     */
    public void windowResized(int x, int y) {
        if (x == 0) {
            fxImageViewHeader.setFitHeight(y);
        } else {
            fxImageViewHeader.setFitWidth(x);
        }
    }

    /**
     * Diese Methode wird aufgerufen, wenn man in der Combobox eine Änderung
     * vornimmt, sie ändert die Labels entsprechend um
     */
    public void ComboBoxSelected() {
        oberflaeche.aendereSprache(languages.getLanguageShortcut((String) fxComboBoxLanguage.getValue()));
    }

    /**
     * Sie wird aufgerufen, wenn man den Login-Knopf anklickt
     */
    public void fxButtonLoginPRESSED() {
        if (fxTextFieldLoginUsername.getText().length() > 35 || fxPasswordFieldLoginPassword.getText().length() > 35) {
            oberflaeche.zeigeFehlerAn(languages.getString(91).replace("[NUMBER]", "35"));
        } else {
            client.login(fxTextFieldLoginUsername.getText(), fxPasswordFieldLoginPassword.getText());
        }
        this.clearFields();
    }

    /**
     * Sie wird aufgerufen, wenn man in einem der beiden Loginfelder eine Taste
     * drückt
     *
     * @param event Das Event, das die gedrückte Taste beinhaltet
     */
    public void fxTextfieldLoginKeyPRESSED(Event event) {
        KeyEvent evt = (KeyEvent) event;
        if (evt.getCode().toString().equals("ENTER")) {
            if (!fxTextFieldLoginUsername.getText().equals("") || !fxPasswordFieldLoginPassword.getText().equals("")) {
                this.fxButtonLoginPRESSED();
            }
        }
    }

    /**
     * Sie wird aufgerufen, wenn man den Registrierungsknopf drückt
     */
    public void fxButtonRegisterPRESSED() {
        ArrayList<String> fehler = new Check(languages).check(fxTextFieldRegisterUsername.getText(), fxTextFieldRegisterFirstName.getText(), fxTextFieldRegisterLastName.getText(), fxTextFieldRegisterEmail.getText(), fxPasswordFieldRegisterPassword1.getText(), fxPasswordFieldRegisterPassword2.getText());
        if (fehler.isEmpty()) {
            if (client.register(fxTextFieldRegisterUsername.getText(), fxTextFieldRegisterFirstName.getText(), fxTextFieldRegisterLastName.getText(), fxPasswordFieldRegisterPassword1.getText(), fxPasswordFieldRegisterPassword2.getText(), fxTextFieldRegisterEmail.getText(), languages.getLanguageShortcut((String) fxComboBoxLanguage.getValue()))) {
                this.clearFields();
            }
        } else {
            oberflaeche.zeigeFehlerAn(fehler);
        }

    }

    /**
     * Sie wird aufgerufen, wenn man in einem der 6 Registrierungsfelder eine
     * Taste gedrückt hat
     *
     * @param event Das Event, das die gedrückte Taste beinhaltet
     */
    public void fxTextFieldRegisterKeyPRESSED(Event event) {
        KeyEvent evt = (KeyEvent) event;
        if (evt.getCode().toString().equals("ENTER")) {
            this.fxButtonRegisterPRESSED();
        }
    }

    /**
     * Sie wird aufgerufen, wenn man den Passwort vergessen Text angeklickt hat
     *
     * @param event Das Event, das das gedrückte Label beinhaltet
     */
    public void fxLabelPasswordForgottenPRESSED(Event event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(languages.getString(79));
        String header = languages.getString(80);
        dialog.setHeaderText(header);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ButtonType ok = new ButtonType(languages.getString(82));
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        Label email = new Label(languages.getString(81));
        Label  username = new Label(languages.getString(15)+ ": "); 
        grid.add(email, 0, 0);
        grid.add(username, 0, 1);

        TextField mailfield = new TextField();
        TextField usernamefield = new TextField();
        grid.add(mailfield, 1, 0);
        grid.add(usernamefield, 1,1);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> mailfield.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ok) {
            Platform.runLater(() -> {
                if (new Check(languages).checkMail(mailfield.getText()).isEmpty() &&  new Check(languages).checkUsername(usernamefield.getText()) == null) {
                    client.setUsername(usernamefield.getText());
                    client.requestNewPassword(mailfield.getText(), usernamefield.getText());
                } else {
                    oberflaeche.zeigeFehlerAn(languages.getString(95));
                    mailfield.clear();
                    usernamefield.clear();
                    mailfield.requestFocus();
                    dialog.showAndWait();
                }
            });
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fxComboBoxLanguage.getStyleClass().add("combobox");
        anchorPane.getStyleClass().add("pane");
    }

    /**
     * Setzt den Focus am Start des Programms auf das Username Feld, damit man
     * da nicht noch extra reinklicken muss
     */
    public void startFocus() {
        fxTextFieldLoginUsername.requestFocus();
    }

    /**
     * Speichert die Referenz des Clienten
     *
     * @param pClient Die Referenz zum Clienten
     */
    public void setClient(ClientManager pClient) {
        this.client = pClient;
    }

    /**
     * Speichert die Referenz der Launcheroberfläche
     *
     * @param pOberflaeche Die Referenz zur LauncherOberfläche
     */
    public void setOberflaeche(LauncherOberflaeche pOberflaeche) {
        this.oberflaeche = pOberflaeche;
    }

    /**
     * Specihert die Referenz zum Languages-Objekt
     *
     * @param pLanguages Die Referenz zum Languages-Objekt
     */
    public void setLanguages(Language pLanguages) {
        this.languages = pLanguages;
    }

    /**
     * Setzt die Statuszeile am unteren Rand des Fensters
     *
     * @param pMessage Die dort anzuzeigende Nachricht
     */
    public void setLabelStatus(String pMessage) {
        fxLabelState.setText(pMessage);
    }

    /**
     * Setzt die Einträge in der Sprachauswahlcombobox
     *
     * @param model Das Stringarray mit den Sprachen in ihrer jeweiligen
     * Landessprache
     * @param selectedIndex Der Intex, der das jeweilige Objekt in der ComboBox
     * auswähltF
     */
    public void setComboBoxLanuage(String[] model, int selectedIndex) {
        fxComboBoxLanguage.getItems().addAll((Object[]) model);
        fxComboBoxLanguage.setValue(model[selectedIndex]);
    }

    /**
     * Setzt den Text für das Loginfeld Benutzername
     *
     * @param value Der anzuzeigende Text für das Loginfeld Benutzername
     */
    public void setLabelLoginUsername(String value) {
        fxLabelLoginUsername.setText(value);
    }

    /**
     * Setzt den Text für das Loginfeld Passwort
     *
     * @param value Der anzuzeigende Text für das Loginfeld Passwort
     */
    public void setLabelLoginPassword(String value) {
        fxLabelLoginPassword.setText(value);
    }

    /**
     * Setzt den Text für das Registrierungsfeld Benutzername
     *
     * @param value Der anzuzeigende Text für das Registrierungsfeld
     * Benutzername
     */
    public void setLabelRegiserUsername(String value) {
        fxLabelRegisterUsername.setText(value);
    }

    /**
     * Setzt den Text für das Registrierungsfeld Vorname
     *
     * @param value Der anzuzeigende Text für das Registrierungsfeld Vorname
     */
    public void setLabelRegisterFirstName(String value) {
        fxLabelRegisterFirstName.setText(value);
    }

    /**
     * Setzt den Text für das Registrierungsfeld Nachname
     *
     * @param value Der anzuzeigende Text für das Registrierungsfeld Nachname
     */
    public void setLabelRegisterLastName(String value) {
        fxLabelRegisterLastName.setText(value);
    }

    /**
     * Setzt den Text für das Registrierungsfeld Email
     *
     * @param value Der anzuzeigende Text für das Registrierungsfeld Email
     */
    public void setLabelRegisterEmail(String value) {
        fxLabelRegisterEmail.setText(value);
    }

    /**
     * Setzt den Text für das Registrierungsfeld Passwort 1
     *
     * @param value Der anzuzeigende Text für das Registrierungsfeld Passwort 1
     */
    public void setLabelRegisterPassword1(String value) {
        fxLabelRegisterPassword1.setText(value);
    }

    /**
     * Setzt den Text für das Registrierungsfeld Passwort 2
     *
     * @param value Der anzuzeigende Text für das Registrierungsfeld Passwort 2
     */
    public void setLabelRegisterPassword2(String value) {
        fxLabelRegisterPassword2.setText(value);
    }

    /**
     * Setzt den Text für das Passwort-Vergessen Label
     *
     * @param value Der anzuzeigende Text für das Label
     */
    public void setLabelPasswordForgotten(String value) {
        fxLabelPasswordForgotten.setText(value);
    }

    /**
     * Setzt den Text für
     *
     * @param value Der anzuzeigende Text für
     */
    public void setButtonTextLogin(String value) {
        fxButtonLoginLogin.setText(value);
    }

    /**
     * Setzt den Text für
     *
     * @param value Der anzuzeigende Text für
     */
    public void setButtonTextRegister(String value) {
        fxButtonRegisterRegister.setText(value);
    }

    /**
     * Löscht alle Felder, z.B. nach einem fehlgeschlagenen Einlogversuch
     */
    public void clearFields() {
        fxTextFieldLoginUsername.clear();
        fxPasswordFieldLoginPassword.clear();
        fxTextFieldRegisterUsername.clear();
        fxTextFieldRegisterFirstName.clear();
        fxTextFieldRegisterLastName.clear();
        fxTextFieldRegisterEmail.clear();
        fxPasswordFieldRegisterPassword1.clear();
        fxPasswordFieldRegisterPassword2.clear();
        fxTextFieldLoginUsername.requestFocus();
    }

    /**
     * Zeigt das Fenster für die Eingabe des Emailcodes an
     */
    public void showCodeVerification() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(languages.getString(54));
        String header = languages.getString(55).replace("[EMAIL]", fxTextFieldRegisterEmail.getText());
        dialog.setHeaderText(header);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label content = new Label(languages.getString(56) + ":");
        grid.add(content, 0, 0);

        TextField textfield = new TextField();
        grid.add(textfield, 1, 0);

        Button resend = new Button(languages.getString(58));
        resend.setOnAction((ActionEvent event) -> {
            Platform.runLater(() -> {
                String[] befehl = {"resend"};
                client.sendObject(befehl);
            });
        });
        grid.add(resend, 2, 0);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> textfield.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.runLater(() -> {
                System.out.println("OK");
                if (textfield.getText().equals("")) {
                    String[] befehl = {"emailcode", "empty"};
                    client.sendObject(befehl);
                } else if (textfield.getText().length() > 15) {
                    oberflaeche.zeigeFehlerAn(languages.getString(93) + languages.getString(92));
                } else {
                    String[] befehl = {"emailcode", textfield.getText()};
                    client.sendObject(befehl);
                }
            });
        } else if (result.get() == ButtonType.CANCEL) {
            String[] befehl = {"emailcode", "cancel"};
            client.sendObject(befehl);
        }
    }

    /**
     * Zeigt das Fenster für das neue Passwort an
     */
    public void showNewPasswordDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(languages.getString(79));
        String header = languages.getString(83);
        dialog.setHeaderText(header);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label lcode = new Label(languages.getString(56));
        grid.add(lcode, 0, 0);
        TextField code = new TextField();
        grid.add(code, 1, 0);

        Label lpass1 = new Label(languages.getString(16) + ": ");
        grid.add(lpass1, 0, 1);
        PasswordField pass1 = new PasswordField();
        grid.add(pass1, 1, 1);

        Label lpass2 = new Label(languages.getString(16) + ": ");
        grid.add(lpass2, 0, 2);
        PasswordField pass2 = new PasswordField();
        grid.add(pass2, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> code.requestFocus());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.runLater(() -> {
                if (code.getText().length() > 15 || pass1.getText().length() > 35 || pass2.getText().length() > 35) {
                    oberflaeche.zeigeFehlerAn(languages.getString(91).replace("[NUMBER]", "35"));
                } else {
                    client.sendNewPassword(code.getText(), pass1.getText(), pass2.getText());
                }
            });
        }
    }

}
