package com.github.model;

import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

public class Account implements Printable {
    private static Account ourInstance = new Account();

    private String accountId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String confirmationCode;
    private String password;
    private String role;
    private int balance;
    private String creationDate;

    public static Account getInstance() {
        return ourInstance;
    }

    private Account() { }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance() {
        DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
        balance = db.getValue(accountId);
    }
    public void addToBalance(int add){
        setBalance();
        DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
        db.setBalance(add,"Deposit", accountId);
        balance += add;
    }
    public void deductFromBalance(int deduction){
        DBConnection db = new DBConnection(DBConnection.ConnectionType.LOGIN_PROCESS);
        db.setBalance(deduction, "Payment", accountId);
        balance -=deduction;
    }


    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public <T> void printToPdf(T... account) throws IOException {


        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            String logo = this.getClass().getResource("/resources/img/logo.png").getPath();
            PDImageXObject pdImage = PDImageXObject.createFromFile(logo, doc);

            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            PDFont fontRegular = PDType1Font.HELVETICA;

            try (PDPageContentStream contents = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true)) {

                float scale = 0.5f;
                contents.drawImage(pdImage, 100, 680, pdImage.getWidth() * scale, pdImage.getHeight() * scale);

                contents.beginText();
                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(100, 650);
                contents.showText("Account ID: ");
                contents.setFont(fontRegular, 12);
                contents.showText(accountId);

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Name: ");
                contents.setFont(fontRegular, 12);
                contents.showText(firstName + " " + lastName);

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Email: ");
                contents.setFont(fontRegular, 12);
                contents.showText(email);

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Phone: ");
                contents.setFont(fontRegular, 12);
                contents.showText(phone);

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Balance: ");
                contents.setFont(fontRegular, 12);
                contents.showText(String.valueOf(balance));
                contents.endText();
            }

            FileChooser fc = new FileChooser();
            fc.setTitle("Save File");
            fc.setInitialFileName("account_info.pdf");
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fc.showSaveDialog(null);
            if (file != null) {
                doc.save(file);
            }
        }
    }
}
