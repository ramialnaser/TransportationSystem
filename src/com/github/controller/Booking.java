package com.github.controller;

import com.github.model.Printable;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.File;
import java.io.IOException;

public class Booking extends RecursiveTreeObject<Booking> implements Printable {

    StringProperty bookingId;
    StringProperty accountUserName;
    StringProperty fromStation;
    StringProperty toStation;
    StringProperty routId;
    StringProperty amount;
    StringProperty date;

    public Booking() {
        super();
    }

    public Booking(String bookingId, String accountUserName, String fromStation, String toStation, String routId,
                   String amount, String date) {
        this.bookingId = new SimpleStringProperty(bookingId);
        this.accountUserName = new SimpleStringProperty(accountUserName);
        this.fromStation = new SimpleStringProperty(fromStation);
        this.toStation = new SimpleStringProperty(toStation);
        this.routId = new SimpleStringProperty(routId);
        this.amount = new SimpleStringProperty(amount);
        this.date = new SimpleStringProperty(date);
    }

    public Booking(String accountUserName, String fromStation, String toStation, String amount) {
        this.accountUserName = new SimpleStringProperty(accountUserName);
        this.fromStation = new SimpleStringProperty(fromStation);
        this.toStation = new SimpleStringProperty(toStation);
        this.amount = new SimpleStringProperty(amount);
    }

    public Booking(String accountUserName, String fromStation, String toStation, String amount, String date) {
        this.accountUserName = new SimpleStringProperty(accountUserName);
        this.fromStation = new SimpleStringProperty(fromStation);
        this.toStation = new SimpleStringProperty(toStation);
        this.amount = new SimpleStringProperty(amount);
        this.date = new SimpleStringProperty(date);
    }

    @Override
    public <T> void printToPdf(T... list) throws IOException {

        Booking[] bookingList = (Booking[]) list;

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
            int diffY = 120;

            for (int i = 0; i < list.length; i++) {

                if (startY < 200) {
                    page = new PDPage();
                    doc.addPage(page);
                    contents.close();
                    contents = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
                    contents.drawImage(pdImage, 100, 680, pdImage.getWidth() * scale, pdImage.getHeight() * scale);
                    startY = 770;
                }

                contents.beginText();
                contents.setFont(fontBold, 12);
                if (i == 0) {
                    contents.newLineAtOffset(100, startY);
                } else {
                    startY -= diffY;
                    contents.newLineAtOffset(100, startY);
                }

                if (bookingList[i].bookingId != null) {
                    contents.showText("Booking ID: ");
                    contents.setFont(fontRegular, 12);
                    contents.showText(bookingList[i].bookingId.getValue());
                }

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Username: ");
                contents.setFont(fontRegular, 12);
                contents.showText(bookingList[i].accountUserName.getValue());

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("From: ");
                contents.setFont(fontRegular, 12);
                contents.showText(bookingList[i].fromStation.getValue());

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("To: ");
                contents.setFont(fontRegular, 12);
                contents.showText(bookingList[i].toStation.getValue());

                if (bookingList[i].routId != null) {
                    contents.setFont(fontBold, 12);
                    contents.newLineAtOffset(0, -15);
                    contents.showText("Route ID: ");
                    contents.setFont(fontRegular, 12);
                    contents.showText(bookingList[i].routId.getValue());
                }

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -15);
                contents.showText("Amount: ");
                contents.setFont(fontRegular, 12);
                contents.showText(bookingList[i].amount.getValue());

                if (bookingList[i].date != null) {
                    contents.setFont(fontBold, 12);
                    contents.newLineAtOffset(0, -15);
                    contents.showText("Date: ");
                    contents.setFont(fontRegular, 12);
                    contents.showText(bookingList[i].date.getValue());
                }

                contents.setFont(fontBold, 12);
                contents.newLineAtOffset(0, -30);
                contents.showText(" ");

                contents.endText();
            }

            contents.close();

            FileChooser fc = new FileChooser();
            fc.setTitle("Save File");
            fc.setInitialFileName("booking_info.pdf");
            fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            File file = fc.showSaveDialog(null);
            if (file != null) {
                doc.save(file);
            }
        }
    }
}