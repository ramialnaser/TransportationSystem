package com.github.controller;

import com.github.model.*;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    @FXML private Button exitLoginButton;
    @FXML private Pane loginPane, registrationPane, passwordPane, resetPasswordPane;
    @FXML private JFXTextField tfAccountLogin, tfFirstName, tfLastName, tfUsernameReg, tfPhoneReg, tfEmailReg, tfAccountPass, tfEmailReset;
    @FXML private JFXPasswordField pfPasswordLogin, pfPasswordPass, pfPasswordConfirm, pfConfirmationCode;
    @FXML private Label newUserMsgLabel, resetPasswordMsgLabel, loginButtonPressed;
    private SMS_Manager sms;

    public void initialize() {
        ExtendedButton.setFunction(exitLoginButton, ExtendedButton.Type.EXIT_PLATFORM);
        sms = new SMS_Manager();
    }

    // LOGIN PANE
    @FXML
    private void loginButtonPressed() {
        DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
        if (validateLogin(tfAccountLogin.getText(), pfPasswordLogin.getText())) {
            loadAccount(tfAccountLogin.getText());
            if (db.getStatus(Account.getInstance().getAccountId())>0){
                handleForgotPasswordButtonPressed();
                System.out.println("new account");
                tfAccountLogin.setText("");
                pfPasswordLogin.setText("");
            }else {
                login(Account.getInstance().getAccountId());
                tfAccountLogin.setText("");
                pfPasswordLogin.setText("");
            }
        } else {
            invalidLogin();
        }
    }

    private void loadAccount(String userName) {
        DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
        ArrayList<String> userDetails = db.getAccountDetails(userName);
        Account.getInstance().setAccountId(userDetails.get(0));
        Account.getInstance().setFirstName(userDetails.get(1));
        Account.getInstance().setLastName(userDetails.get(2));
        Account.getInstance().setEmail(userDetails.get(3));
        Account.getInstance().setPhone(userDetails.get(4));
        Account.getInstance().setRole(userDetails.get(5));
        Account.getInstance().setCreationDate(userDetails.get(6));
        Account.getInstance().setBalance();
    }

    private void login(String userName){
        DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
        switch (db.getRole(userName)) {
            case "USER":
                try {
                    StageManager.getInstance().getUserScreen();
                }catch (Exception e){
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, e);
                }
                break;
            case "ADMIN":
                try {
                    StageManager.getInstance().getAdminScreen();
                }catch (Exception e){
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, e);
                }
                break;
            case "TRAIN_DRIVER": case "BUS_DRIVER":
                try {
                    StageManager.getInstance().getDriverScreen();
                }catch (Exception e){
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, e);
                }
                break;
            case "TAXI_DRIVER":
                try {
                    StageManager.getInstance().getTaxiScreen();

                }catch (Exception e){
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, e);

                }
                break;
        }
    }

    private void invalidLogin() {
        Alert a = new Alert(Alert.AlertType.INFORMATION, "Invalid login.\nCheck your Account ID and/or password.", ButtonType.OK);
        a.showAndWait();
    }

    private boolean validateLogin(String account, String password) {
        DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
        return  db.validateLogin(account, password);
    }

    // fades in new user registration pane and fades out login pane
    @FXML
    private void handleNewUserButtonPressed() {
        newUserMsgLabel.setText("If you require an account with special access contact help desk");
        paneFadeTransition(loginPane, registrationPane);
    }

    // fades in forgot password pane and fades out login pane
    @FXML
    private void handleForgotPasswordButtonPressed() {
        paneFadeTransition(loginPane, resetPasswordPane);
    }

    // RESET PASSWORD PANE
    @FXML
    private void handleResetPasswordNextButtonPressed() {
        if (!tfEmailReset.getText().trim().isEmpty()) {

            // create random confirmation code and add it to database
            String confirmationCode = generateConfirmationCode();
            if (sendConfirmationCodeEmail(tfEmailReset.getText(), confirmationCode)) {

                DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
                db.addConfirmationCode(tfEmailReset.getText(), confirmationCode);
                Alert a = new Alert(Alert.AlertType.INFORMATION, "An email was sent to " +
                        tfEmailReset.getText() + " and your mobile phone with the confirmation code.");
                a.showAndWait();
                paneFadeTransition(resetPasswordPane, passwordPane);
                DBConnection db1 = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
                DBConnection db2 = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);

                if(db1.getStatus(Account.getInstance().getAccountId())>0){
                    db2.setIsActive(Account.getInstance().getAccountId());
                }

            }



        } else {
            Alert a = new Alert(Alert.AlertType.INFORMATION, "Invalid email address.", ButtonType.OK);
            a.showAndWait();
        }
    }

    private String generateConfirmationCode() {
        SecureRandom random = new SecureRandom();
        String confirmationCode = "";

        for (int i = 0; i < 8; i++) {
            confirmationCode += random.nextInt(9);
        }

        return confirmationCode;
    }

    // fade out reset password and fade in login pane
    @FXML
    private void handleExitResetPasswordButton() {
        paneFadeTransition(resetPasswordPane, loginPane);
        resetPasswordMsgLabel.setText("");
    }

    // NEW ACCOUNT REGISTRATION PANE
    @FXML
    private void handleRegistrationPaneNextButtonPressed() {

        if (validateField(tfFirstName) && validateField(tfLastName) && validateField(tfPhoneReg)
                && validateField(tfUsernameReg) && validateField(tfEmailReg)) {

            // create random confirmation code
            String confirmationCode =  generateConfirmationCode();

            String accountId = tfUsernameReg.getText();
            String firstName = tfFirstName.getText();
            String lastName = tfLastName.getText();
            String email = tfEmailReg.getText();
            String phone = tfPhoneReg.getText();

            Account.getInstance().setAccountId(accountId);
            Account.getInstance().setFirstName(firstName);
            Account.getInstance().setLastName(lastName);
            Account.getInstance().setEmail(email);
            Account.getInstance().setPhone(phone);
            Account.getInstance().setConfirmationCode(confirmationCode);

            // add user to db and send email with confirmation code to setup password
            DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);

            // TODO: needs better handling (account is being added with certain invalid email addresses)
            if (sendConfirmationCodeEmail(email, confirmationCode)) {
                db.addUser(accountId, firstName, lastName, email, phone, confirmationCode);
                // fade out registration pane and fade in password pane
                Alert a = new Alert(Alert.AlertType.INFORMATION, "Confirmation code was sent to " + email + " and to your mobile phone.", ButtonType.OK);
                a.showAndWait();
                paneFadeTransition(registrationPane, passwordPane);
            }


        }
    }

    private boolean validateField(TextField tf) {
        //TODO: add phone regex and email
        boolean ok = true;
        String regex1 = "\\p{L}+";  //only letters
        String regex2 = "^\\S+$";   //anything that isn't whitespace

        if ((tf == tfFirstName || tf == tfLastName) && (tf.getText().isEmpty() || !tf.getText().matches(regex1))) {
            invalidFieldAlert(tf);
            ok = false;
        }
        if (tf == tfPhoneReg && tf.getText().trim().isEmpty()) {
            invalidFieldAlert(tf);
            ok = false;
        }
        if (tf == tfUsernameReg && !tf.getText().matches(regex2)) {
            invalidFieldAlert(tf);
            ok = false;
        } else if (tf == tfUsernameReg) {
            if (tf.getLength() > 10) {
                Alert a = new Alert(Alert.AlertType.WARNING, "'Account ID' has 10 characters limit.\n" +
                        "Choose a different one.", ButtonType.OK);
                a.showAndWait();
                ok = false;
            }
            DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
            if (db.usernameExists(tf.getText())) {
                Alert a = new Alert(Alert.AlertType.WARNING, "'Account ID' already taken.\n" +
                        "Choose a different one.", ButtonType.OK);
                a.showAndWait();
                ok = false;
            }
        }
        if (tf == tfEmailReg && !tf.getText().matches(regex2)) {
            invalidFieldAlert(tf);
            ok = false;
        } else if (tf == tfEmailReg) {
            DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
            if (db.emailExists(tf.getText())) {
                Alert a = new Alert(Alert.AlertType.WARNING, "'Email' already taken.\n" +
                        "Choose a different one.", ButtonType.OK);
                a.showAndWait();
                ok = false;
            }
        }
        return ok;
    }

    private void invalidFieldAlert(TextField tf) {
        if (tf == tfFirstName) {
            Alert a = new Alert(Alert.AlertType.WARNING, "'First Name' is a mandatory field."
                    + "\nOnly letters are accepted.", ButtonType.OK);
            a.showAndWait();
        }
        if (tf == tfLastName) {
            Alert a = new Alert(Alert.AlertType.WARNING, "'Last Name' is a mandatory field."
                    + "\nOnly letters are accepted.", ButtonType.OK);
            a.showAndWait();
        }
        if (tf == tfPhoneReg) {
            Alert a = new Alert(Alert.AlertType.WARNING, "'Phone number' is a mandatory field.", ButtonType.OK);
            a.showAndWait();
        }
        if (tf == tfUsernameReg) {
            Alert a = new Alert(Alert.AlertType.WARNING, "'Account ID' is a mandatory field." +
                    "\nWhitespace is not allowed.", ButtonType.OK);
            a.showAndWait();
        }
        if (tf == tfEmailReg) {
            Alert a = new Alert(Alert.AlertType.WARNING, "'Email' is a mandatory field." +
                    "\nWhitespace is not allowed.", ButtonType.OK);
            a.showAndWait();
        }
    }

    private boolean sendConfirmationCodeEmail(String email, String confirmationCode) {
        boolean emailSent = false;
        Properties prop = new Properties();
        try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("resources/properties/db.properties")) {
            prop.load(in);
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(prop.getProperty("sendEmailUsername"), prop.getProperty("sendEmailPassword"));
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(prop.getProperty("sendEmailUsername")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Westeros Traffic: Confirmation code");
            message.setText("Use the following confirmation code to complete your account creation and setup your password: " + confirmationCode);
            Transport.send(message);
            sms.sendSMS("Confirmation code: " + confirmationCode);
            emailSent = true;
        } catch (MessagingException e) {
            e.printStackTrace();
            Alert a = new Alert(Alert.AlertType.WARNING, "Email not sent." +
                    "\nCheck if you entered a valid email.", ButtonType.OK);
            a.showAndWait();
        }


        return emailSent;
    }

    // fades in login pane and fades out registration pane
    @FXML
    private void handleExitRegistrationButton() {
        paneFadeTransition(registrationPane, loginPane);
    }

    // PASSWORD PANE
    @FXML
    private void handleFinishButtonPressed() {
        String account = tfAccountPass.getText();
        String confirmationCode = pfConfirmationCode.getText();
        String password = pfPasswordPass.getText();
        String passwordConfirmation = pfPasswordConfirm.getText();

        if (validateConfirmationCode(account, confirmationCode)) {
            DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
            if (!pfPasswordPass.getText().trim().isEmpty() && password.equals(passwordConfirmation)) {
                if (db.setupPassword(account, password)) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION, "Your account is setup." +
                            " You'll be taken to the login pane.");
                    a.showAndWait();
                    paneFadeTransition(passwordPane, loginPane);
                }
            } else {
                Alert a = new Alert(Alert.AlertType.WARNING, "Password does not match with confirmation password.", ButtonType.OK);
                a.showAndWait();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.WARNING, "Incorrect confirmation code.", ButtonType.OK);
            a.showAndWait();
        }
    }

    private boolean validateConfirmationCode(String account, String confirmationCode) {
        DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
        return db.validateConfirmationCode(account, confirmationCode);
    }

    // fades in login pane and fades out password pane
    @FXML
    private void handleExitPasswordPaneButton() {
        paneFadeTransition(passwordPane, loginPane);
    }

    // fading pane
    private void paneFadeTransition(Pane fadeOutPane, Pane fadeInPane) {
        if (!fadeInPane.equals(loginPane)) {
            fadeInPane.setVisible(true);
            fadeInPane.setOpacity(0);
        }
        Timeline timeline = new Timeline();
        KeyValue kv1 = new KeyValue(fadeInPane.opacityProperty(), 1);
        KeyValue kv2 = new KeyValue(fadeOutPane.opacityProperty(), 0);
        KeyFrame kf = new KeyFrame(Duration.millis(1000), kv1, kv2);
        timeline.getKeyFrames().add(kf);
        timeline.setCycleCount(1);
        timeline.play();
        if (!fadeOutPane.equals(loginPane)) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fadeOutPane.setVisible(false);
            }).start();
        }
    }
}
