package org.helper.doc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import com.google.common.collect.Lists;

/**
 * Created by zongzhehu on 17-2-13.
 */
public class ExcelHelper {
    
    public static class ExcelPOIHelper {

        public List<String[]> readExcel(String fileLocation) throws IOException {
            List<String[]> data = Lists.newArrayList();

            FileInputStream file = new FileInputStream(new File(fileLocation));
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            int i = 0;
            for (Row row : sheet) {
                String[] temp = new String[row.getRowNum()];
                for (Cell cell : row) {
                    temp[i] = cell.getStringCellValue();
                }
                data.add(temp);
                i++;
            }
            if (workbook != null) {
                workbook.close();
            }
            return data;
        }

        //excel最长为65535,这只是一个示例!
        public void writeExcel(String sheetName, String fullFilePath) throws IOException {
            Workbook workbook = new XSSFWorkbook();

            try {
                Sheet sheet = workbook.createSheet(sheetName);
                sheet.setColumnWidth(0, 6000);
                sheet.setColumnWidth(1, 4000);

                Row header = sheet.createRow(0);

                CellStyle headerStyle = workbook.createCellStyle();

                headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                XSSFFont font = ((XSSFWorkbook) workbook).createFont();
                font.setFontName("Arial");
                font.setFontHeightInPoints((short) 16);
                font.setBold(true);
                headerStyle.setFont(font);

                Cell headerCell = header.createCell(0);
                headerCell.setCellValue("Name");
                headerCell.setCellStyle(headerStyle);

                headerCell = header.createCell(1);
                headerCell.setCellValue("Age");
                headerCell.setCellStyle(headerStyle);

                CellStyle style = workbook.createCellStyle();
                style.setWrapText(true);

                Row row = sheet.createRow(2);
                Cell cell = row.createCell(0);
                cell.setCellValue("John Smith");
                cell.setCellStyle(style);

                cell = row.createCell(1);
                cell.setCellValue(20);
                cell.setCellStyle(style);

                FileOutputStream outputStream = new FileOutputStream(fullFilePath);
                workbook.write(outputStream);
            } finally {
                if (workbook != null) {
                    workbook.close();
                }
            }
        }

    }

    public static class WordDocument {
        public static String logo = "logo-leaf.png";
        public static String paragraph1 = "poi-word-para1.txt";
        public static String paragraph2 = "poi-word-para2.txt";
        public static String paragraph3 = "poi-word-para3.txt";
        public static String output = "rest-with-spring.docx";

        public void handleSimpleDoc() throws Exception {
            XWPFDocument document = new XWPFDocument();

            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText("Build Your REST API with Spring");
            titleRun.setColor("009933");
            titleRun.setBold(true);
            titleRun.setFontFamily("Courier");
            titleRun.setFontSize(20);

            XWPFParagraph subTitle = document.createParagraph();
            subTitle.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun subTitleRun = subTitle.createRun();
            subTitleRun.setText("from HTTP fundamentals to API Mastery");
            subTitleRun.setColor("00CC44");
            subTitleRun.setFontFamily("Courier");
            subTitleRun.setFontSize(16);
            subTitleRun.setTextPosition(20);
            subTitleRun.setUnderline(UnderlinePatterns.DOT_DOT_DASH);

            XWPFParagraph image = document.createParagraph();
            image.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun imageRun = image.createRun();
            imageRun.setTextPosition(20);
            Path imagePath = Paths.get(ClassLoader.getSystemResource(logo).toURI());
            imageRun.addPicture(Files.newInputStream(imagePath), XWPFDocument.PICTURE_TYPE_PNG, imagePath.getFileName().toString(), Units.toEMU(50), Units.toEMU(50));

            XWPFParagraph sectionTitle = document.createParagraph();
            XWPFRun sectionTRun = sectionTitle.createRun();
            sectionTRun.setText("What makes a good API?");
            sectionTRun.setColor("00CC44");
            sectionTRun.setBold(true);
            sectionTRun.setFontFamily("Courier");

            XWPFParagraph para1 = document.createParagraph();
            para1.setAlignment(ParagraphAlignment.BOTH);
            String string1 = convertTextFileToString(paragraph1);
            XWPFRun para1Run = para1.createRun();
            para1Run.setText(string1);

            XWPFParagraph para2 = document.createParagraph();
            para2.setAlignment(ParagraphAlignment.RIGHT);
            String string2 = convertTextFileToString(paragraph2);
            XWPFRun para2Run = para2.createRun();
            para2Run.setText(string2);
            para2Run.setItalic(true);

            XWPFParagraph para3 = document.createParagraph();
            para3.setAlignment(ParagraphAlignment.LEFT);
            String string3 = convertTextFileToString(paragraph3);
            XWPFRun para3Run = para3.createRun();
            para3Run.setText(string3);

            FileOutputStream out = new FileOutputStream(output);
            document.write(out);
            out.close();
            document.close();
        }

        public String convertTextFileToString(String fileName) {
            return null;
        }

    }

}
