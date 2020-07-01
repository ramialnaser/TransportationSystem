package com.github.controller;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Employee extends RecursiveTreeObject<Employee> {
    StringProperty username;
    StringProperty firstName;
    StringProperty lastName;
    StringProperty email;
    StringProperty phone;
    StringProperty role;
    StringProperty creationDate;
    public Employee(){
        super();
    }

    public Employee(String username, String firstName, String lastName, String email, String phone, String role,
                    String creationDate) {
        this.username = new SimpleStringProperty(username);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.role = new SimpleStringProperty(role);
        this.creationDate = new SimpleStringProperty(creationDate);
    }
}
