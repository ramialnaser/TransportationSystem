package com.github.controller;

import com.github.model.Printable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

public class Complaint extends RecursiveTreeObject<Complaint> implements Printable {

    StringProperty id;
    StringProperty username;
    StringProperty date;
    StringProperty isHandled;
    StringProperty message;

    public Complaint(){
        super();
    }

    public Complaint(String id,String username, String date, String isHandled,String message) {
        this.id = new SimpleStringProperty(id);
        this.username = new SimpleStringProperty(username);
        this.date = new SimpleStringProperty(date);
        this.isHandled = new SimpleStringProperty(isHandled);
        this.message = new SimpleStringProperty(message);
    }

//    public Complaint(String id, String username, String date, String isHandled) {
//        this.id = new SimpleStringProperty(id);
//        this.username = new SimpleStringProperty(username);
//        this.date = new SimpleStringProperty(date);
//        this.isHandled = new SimpleStringProperty(isHandled);
//    }

    @Override
    public <T> void printToPdf(T... list) throws IOException {

        Complaint[] complaintList = (Complaint[]) list;

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            String logo = this.getClass().getResource("/resources/img/logo.png").getPath();
            PDImageXObject pdImage = PDImageXObject.createFromFile(logo, doc);

            PDFont fontBold = PDType1Font.HELVETICA_BOLD;
            PDFont fontRegular = PDType1Font.HELVETICA;
            PDPageContentStream contents = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);

            float scale = 0.5f;
            contents.drawImage(pdImage, 100, 680, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
            int startY = 650;
            int diffY = 100;

            for (int i = 0; i < list.length; i++) {

                if (startY < 200) {
                    page = new PDPage();
                    doc.addPage(page);
                    contents.close();
                    contents = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
                    contents.drawImage(pdImage, 100, 680, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
                    startY = 750;
                }

                contents.beginText();
                contents.setFont(fontBold, 12);
                if (i == 0) {
                    contents.newLineAtOffset(100, startY);
                } else {
                    startY -= diffY;
                    contents.newLineAtOffset(100, startY);
                }
                contents.showText("Complaint ID: ");
                contents.setFont(fontRegular, 12);
                contents.showText(complaintList[i].id.getValue());

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Username: ");
                contents.setFont(fontRegular, 12);
                contents.showText(complaintList[i].username.getValue());

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Date: ");
                contents.setFont(fontRegular, 12);
                contents.showText(complaintList[i].date.getValue());

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Status: ");
                contents.setFont(fontRegular, 12);
                contents.showText(complaintList[i].isHandled.getValue());

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Message: ");
                contents.setFont(fontRegular, 12);
                contents.showText(complaintList[i].message.getValue());

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText(" ");

                contents.endText();
            }

            contents.close();

            FileChooser fc = new FileChooser();
            fc.setTitle("Save File");
            fc.setInitialFileName("complaint_info.pdf");
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fc.showSaveDialog(null);
            if (file != null) {
                doc.save(file);
            }
        }
    }
}
