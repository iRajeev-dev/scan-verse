package com.scanverse.scanverse.service;

import net.sourceforge.tess4j.TesseractException;

import java.io.File;
import java.io.IOException;

public interface OCRService {
    String extractTextFromFile(File file) throws IOException, TesseractException;
}
