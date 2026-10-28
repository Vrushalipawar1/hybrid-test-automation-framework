package com.framework.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/** Reads an .xlsx test-data sheet (from the classpath) into a TestNG DataProvider array. */
public final class ExcelReader {

    private ExcelReader() {
    }

    /**
     * @param resourcePath classpath location, e.g. "testdata/login-data.xlsx"
     * @param sheetName    sheet to read; the first row is treated as the header and skipped
     * @return one Object[] per data row, every cell as a String (blank cells become "")
     */
    public static Object[][] readSheet(String resourcePath, String sheetName) {
        try (InputStream in = ExcelReader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IllegalArgumentException("Test data file not found on classpath: " + resourcePath);
            }
            try (Workbook workbook = new XSSFWorkbook(in)) {
                Sheet sheet = workbook.getSheet(sheetName);
                if (sheet == null) {
                    throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + resourcePath);
                }
                int columns = sheet.getRow(0).getLastCellNum();
                DataFormatter formatter = new DataFormatter();
                List<Object[]> rows = new ArrayList<>();
                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null) {
                        continue;
                    }
                    Object[] values = new Object[columns];
                    for (int c = 0; c < columns; c++) {
                        Cell cell = row.getCell(c);
                        values[c] = (cell == null) ? "" : formatter.formatCellValue(cell);
                    }
                    rows.add(values);
                }
                return rows.toArray(new Object[0][]);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read " + resourcePath, e);
        }
    }
}
