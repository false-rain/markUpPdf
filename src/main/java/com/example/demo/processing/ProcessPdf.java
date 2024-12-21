package com.example.demo.processing;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessPdf {

    public static File processPdf(MultipartFile file) {
        String photoDirPath = "com/example/demo/processing/trash";
        createDirectory(photoDirPath);
        pdf2photos(photoDirPath, file);
        String markedPhotoDir = photoDirPath + "_marked";
        createDirectory(markedPhotoDir);
        for (String img : getFiles(photoDirPath)) {
            markPhotos(img, "src/main/resources/templates/translucent-image.png", markedPhotoDir+"\\"+getLastPathElement(img));
        }
        File convertedFile = images2Pdf(getFiles(markedPhotoDir), "com/example/demo/processing/file.pdf");
        deleteDirectoryWithFiles(photoDirPath);
        deleteDirectoryWithFiles(markedPhotoDir);
        return convertedFile;
    }

    private static void createDirectory(String directoryPath) {
        Path path = Paths.get(directoryPath);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при создании папки. " + directoryPath);
        }
    }

    private static void deleteDirectoryWithFiles(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.exists()) {
            deleteRecursive(directory);
        }
    }

    private static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursive(child);
                }
            }
        }
        file.delete();
    }

    private static void markPhotos(String imagePath, String waterMarkPath, String savePath) {
        try {
            BufferedImage baseImage = ImageIO.read(new File(imagePath));

            // Загружаем изображение водяного знака
            BufferedImage watermarkImage = ImageIO.read(new File(waterMarkPath));

            // Определяем масштаб водяного знака (например, до 50% от ширины основного изображения)
            int scaledWidth = baseImage.getWidth() / 2;
            int scaledHeight = (watermarkImage.getHeight() * scaledWidth) / watermarkImage.getWidth();

            // Масштабируем водяной знак
            java.awt.Image scaledWatermark = watermarkImage.getScaledInstance(scaledWidth, scaledHeight,
                    java.awt.Image.SCALE_SMOOTH);

            // Создаем графический объект для наложения водяного знака
            Graphics2D graphics = baseImage.createGraphics();

            // Определяем координаты для центра водяного знака
            int x = (baseImage.getWidth() - scaledWidth) / 2;
            int y = (baseImage.getHeight() - scaledHeight) / 2;

            // Накладываем увеличенный водяной знак по центру основного изображения
            graphics.drawImage(scaledWatermark, x, y, null);

            graphics.dispose();

            ImageIO.write(baseImage, "png", new File(savePath));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при добавлении водяного знака." + savePath);
        }
    }

    private static void pdf2photos(String outputDir, MultipartFile file) {
        List<BufferedImage> images = new ArrayList<>();
        try (PDDocument document = PDDocument.load(convertMultipartFileToFile(file))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300);
                images.add(bufferedImage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < images.size(); i++) {
            try {
                ImageIO.write(images.get(i), "png", new File(outputDir + "/page_" + getLetter().get(i) + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }

    private static List<String> getLetter() {
        List<String> letters = new ArrayList<>();
        letters.add("A");
        letters.add("B");
        letters.add("C");
        letters.add("D");
        letters.add("E");
        letters.add("F");
        letters.add("G");
        letters.add("H");
        letters.add("I");
        letters.add("J");
        letters.add("K");
        letters.add("L");
        letters.add("M");
        letters.add("N");
        letters.add("O");
        letters.add("P");
        letters.add("Q");
        letters.add("R");
        letters.add("S");
        letters.add("T");
        letters.add("U");
        letters.add("V");
        letters.add("W");
        letters.add("X");
        letters.add("Y");
        letters.add("Z");
        letters.add("Z_A");
        letters.add("Z_B");
        letters.add("Z_C");
        letters.add("Z_D");
        letters.add("Z_E");
        letters.add("Z_F");
        letters.add("Z_G");
        letters.add("Z_H");
        letters.add("Z_I");
        letters.add("Z_J");
        letters.add("Z_K");
        letters.add("Z_L");
        letters.add("Z_M");
        letters.add("Z_N");
        letters.add("Z_O");
        letters.add("Z_P");
        letters.add("Z_Q");
        letters.add("Z_R");
        letters.add("Z_S");
        letters.add("Z_T");
        letters.add("Z_U");
        letters.add("Z_V");
        letters.add("Z_W");
        letters.add("Z_X");
        letters.add("Z_Y");
        letters.add("Z_Z");
        return letters;
    }

    private static String getLastPathElement(String path) {
        String[] parts = path.split("\\\\");
        return parts[parts.length - 1].isEmpty() ? parts[parts.length - 2] : parts[parts.length - 1];
    }

    private static File images2Pdf(List<String> paths, String outputPdfPath) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputPdfPath));
            document.open();

            for (String imagePath : paths) {
                Image img = Image.getInstance(imagePath);
                document.setPageSize(img);
                document.newPage();
                img.setAbsolutePosition(0, 0);
                img.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
                document.add(img);
            }

           return new File(outputPdfPath);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при создании PDF.");
        } finally {
            document.close();
        }
        return null;
    }

    private static List<String> getFiles(String directoryPath) {
        List<String> pdfPaths = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            pdfPaths = paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка при чтении файлов из папки. " + directoryPath);
        }
        return pdfPaths;
    }
}
