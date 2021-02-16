package com.bubalex.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class PdfWriterImpl implements PdfWriter {
    public void create() {
        // Create a document and add a page to it
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        // Create a new font object selecting one of the PDF base fonts
        PDFont font = PDType1Font.HELVETICA_BOLD;
        try (document) {
            // Start a new content stream which will "hold" the to be created content
            writeDataToDocument(document, page, font);
            // Save the results and ensure that the document is properly closed:
            document.save("Hello World.pdf");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void copyPdf(String src) {

    }

    private void writeDataToDocument(PDDocument document, PDPage page, PDFont font) {
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            // Define a text content stream using the selected font, moving the cursor and drawing the text "Hello World"
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Hello World");
            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
