package com.github.controller;

import com.github.model.Account;
import com.github.model.DBConnection;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ViewProfile implements Initializable {
    @FXML
    private JFXButton editButton,saveButton;
    @FXML private JFXTextField userNameTextField,firstNameTextField,lastNameTextField, emailTextField,phoneNbrTextField,roleTextField, createdDateTextField;
    @FXML private JFXPasswordField newPasswordPF,confirmPasswordPF;
    @FXML private Tab viewProfileTab;
    @FXML private VBox textFieldsWrapper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        editButton.setOnAction(event -> editButtonPressed());
//        saveButton.setOnAction(event -> saveButtonPressed());
        viewProfile();
    }
    public void editButtonPressed(){


        DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);


        ArrayList<TextField> textFields = new ArrayList<>(Arrays.asList(userNameTextField,firstNameTextField,lastNameTextField, emailTextField,phoneNbrTextField,roleTextField,newPasswordPF,confirmPasswordPF, createdDateTextField));
        for (TextField t: textFields) {
            t.setDisable(false);
            t.setEditable(true);
        }
    }
    public void saveButtonPressed(){
        ArrayList<TextField> textFields = new ArrayList<>(Arrays.asList(userNameTextField,firstNameTextField,lastNameTextField, emailTextField,phoneNbrTextField,roleTextField,newPasswordPF,confirmPasswordPF, createdDateTextField));
        for (TextField t: textFields) {
            t.setDisable(true);
            t.setEditable(false);
        }
    }

    private void viewProfile() {
        viewProfileTab.setOnSelectionChanged(t -> {
            if (viewProfileTab.isSelected()) {
                userNameTextField.setText(Account.getInstance().getAccountId());
                firstNameTextField.setText(Account.getInstance().getFirstName());
                lastNameTextField.setText(Account.getInstance().getLastName());
                emailTextField.setText(Account.getInstance().getEmail());
                phoneNbrTextField.setText(Account.getInstance().getPhone());
                roleTextField.setText(Account.getInstance().getRole());
                createdDateTextField.setText(Account.getInstance().getCreationDate());
            }
        });
    }

    @FXML
    private void handleEditButtonPressed() {
        List<Node> allNodes = textFieldsWrapper.getChildren();
        for (Node n : allNodes) {
            if (n instanceof TextField) {
                n.setDisable(false);
                ((TextField) n).setEditable(true);
                userNameTextField.setDisable(true);
                roleTextField.setDisable(true);
                emailTextField.setDisable(true);
                createdDateTextField.setDisable(true);
            }
        }
        saveButton.setDisable(false);
    }

    @FXML
    private void handleSaveButtonPressed() {
        boolean status = true;
        if (firstNameTextField.getText().trim().isEmpty()||lastNameTextField.getText().trim().isEmpty()||
            phoneNbrTextField.getText().trim().isEmpty()||newPasswordPF.getText().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid");
            alert.setHeaderText("Fill the fields");
            alert.setContentText("Please make sure that all the fields are filled with your info");
            alert.showAndWait();
            status = false;
        }
        if (!newPasswordPF.getText().equals(confirmPasswordPF.getText())){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid");
            alert.setHeaderText("Confirmation password doesn't match");
            alert.setContentText("Please make sure that both the new password and confirmation password match");
            newPasswordPF.setText("");
            confirmPasswordPF.setText("");
            alert.showAndWait();
            status =false;
        }if (status){
            ArrayList<TextField> textFields = new ArrayList<>(Arrays.asList(userNameTextField,firstNameTextField,lastNameTextField, emailTextField,phoneNbrTextField,roleTextField,newPasswordPF,confirmPasswordPF, createdDateTextField));
            for (TextField t: textFields) {
                t.setDisable(true);
                t.setEditable(false);
            }

            DBConnection dbConnection = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
            dbConnection.updateAccountDetails(firstNameTextField.getText(),lastNameTextField.getText(),phoneNbrTextField.getText(),
                newPasswordPF.getText());

        }

    }

    @FXML
    private void handleExportButtonPressed() throws IOException {
        Account.getInstance().printToPdf();
    }

}
