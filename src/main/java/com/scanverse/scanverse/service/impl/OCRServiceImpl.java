package com.scanverse.scanverse.service.impl;

import com.scanverse.scanverse.service.OCRService;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Implementation of the OCRService interface that provides methods to extract text from images and PDFs using Tesseract OCR.
 **/
@Service
public class OCRServiceImpl implements OCRService {

    private final Tesseract tesseract;

    @Value("${tesseract_path}")
    private String tesseractPath;

    /**
     * Constructs an instance of OCRServiceImpl and initializes Tesseract with the specified data path.
     */
    public OCRServiceImpl() {
        tesseract = new Tesseract();
        tesseract.setDatapath(tesseractPath);
    }

    /**
     * Extracts text from the given file.
     *
     * @param file The file from which text is to be extracted.
     * @return The extracted text.
     * @throws IOException        If an I/O error occurs while reading the file.
     * @throws TesseractException If an error occurs during text extraction.
     */
    @Override
    public String extractTextFromFile(File file) throws IOException, TesseractException {
        if (!file.exists()) {
            return null;
        }

        String fileName = file.getName();
        if (fileName.endsWith(".pdf")) {
            return extractTextFromPDF(file);
        } else {
            return extractTextFromImage(file);
        }
    }

    /**
     * Extracts text from the given image file using Tesseract OCR.
     *
     * @param imageFile The image file from which text is to be extracted.
     * @return The extracted text.
     * @throws TesseractException If an error occurs during text extraction.
     */
    private String extractTextFromImage(File imageFile) throws TesseractException {
        return tesseract.doOCR(imageFile);
    }

    /**
     * Extracts text from the given PDF file by rendering each page as an image and performing OCR on it.
     *
     * @param pdfFile The PDF file from which text is to be extracted.
     * @return The extracted text.
     * @throws IOException        If an I/O error occurs while reading the PDF file.
     * @throws TesseractException If an error occurs during text extraction.
     */
    private String extractTextFromPDF(File pdfFile) throws IOException, TesseractException {
        StringBuilder result = new StringBuilder();
        PDDocument document = Loader.loadPDF(pdfFile);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage pdfImage = pdfRenderer.renderImageWithDPI(page, 300);
            result.append(tesseract.doOCR(pdfImage));
        }
        document.close();
        return result.toString();
    }
}
