package com.ted.app.logger.composite.export;

import com.ted.app.logger.Exporter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileExporter implements Exporter {

    private String fileName;

    // 相對於工作目錄（GYM6C-logger 專案根目錄）
    private String filePath = "src/main/resources/";

    public FileExporter(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void export(String msg) {
        String fullPath = filePath + fileName;

        try (FileWriter writer = new FileWriter(fullPath, fileExists(fullPath))) {
            writer.write(msg);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
}
