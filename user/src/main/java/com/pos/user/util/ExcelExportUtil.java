package com.pos.user.util;

import com.pos.user.exception.ExcelExportException;
import com.pos.user.util.ExcelColumn;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.*;

@Component
public class ExcelExportUtil {

    public <T> byte[] exportMultiSheet(
            Map<String, List<T>> sheetsData,
            Class<T> clazz,
            Map<String, String> filters,
            boolean addSummary
    ) {

        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {

            for (Map.Entry<String, List<T>> entry : sheetsData.entrySet()) {

                String sheetName = entry.getKey();
                List<T> dataList = entry.getValue();

                if (dataList == null || dataList.isEmpty()) {
                    continue;
                }

                SXSSFSheet sheet = workbook.createSheet(sheetName);
                sheet.trackAllColumnsForAutoSizing();

                // Remove RTL if not required
                sheet.setRightToLeft(false);

                List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                        .filter(f -> f.isAnnotationPresent(ExcelColumn.class))
                        .sorted(Comparator.comparingInt(
                                f -> f.getAnnotation(ExcelColumn.class).order()
                        ))
                        .toList();

                int rowIndex = 0;

                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle boldStyle = createBoldStyle(workbook);

                if (filters != null && !filters.isEmpty()) {
                    for (Map.Entry<String, String> filter : filters.entrySet()) {

                        Row row = sheet.createRow(rowIndex);

                        Cell keyCell = row.createCell(0);
                        keyCell.setCellValue(filter.getKey() + ":");
                        keyCell.setCellStyle(boldStyle);

                        Cell valueCell = row.createCell(1);
                        valueCell.setCellValue(
                                filter.getValue() != null ? filter.getValue() : ""
                        );

                        sheet.addMergedRegion(
                                new CellRangeAddress(rowIndex, rowIndex, 1, 3)
                        );

                        rowIndex++;
                    }
                    rowIndex++;
                }

                Row headerRow = sheet.createRow(rowIndex);

                for (int i = 0; i < fields.size(); i++) {
                    ExcelColumn col = fields.get(i).getAnnotation(ExcelColumn.class);
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(col.name());
                    cell.setCellStyle(headerStyle);
                }

                sheet.createFreezePane(0, rowIndex + 1);

                rowIndex++;

                long totalViews = 0;

                for (T item : dataList) {

                    Row row = sheet.createRow(rowIndex++);

                    for (int col = 0; col < fields.size(); col++) {

                        fields.get(col).setAccessible(true);
                        Object value = fields.get(col).get(item);

                        Cell cell = row.createCell(col);

                        if (value instanceof Number number) {
                            cell.setCellValue(number.doubleValue());

                            if (fields.get(col).getName().equals("viewCount")) {
                                totalViews += number.longValue();
                            }

                        } else if (value != null) {
                            cell.setCellValue(value.toString());
                        } else {
                            cell.setCellValue("");
                        }
                    }
                }

                if (addSummary) {

                    Row summaryRow = sheet.createRow(rowIndex);

                    Cell labelCell = summaryRow.createCell(0);
                    labelCell.setCellValue("Total Views");
                    labelCell.setCellStyle(boldStyle);

                    Cell valueCell = summaryRow.createCell(fields.size() - 1);
                    valueCell.setCellValue(totalViews);
                    valueCell.setCellStyle(boldStyle);
                }

                sheet.setAutoFilter(
                        new CellRangeAddress(
                                filters != null ? filters.size() + 1 : 0,
                                rowIndex,
                                0,
                                fields.size() - 1
                        )
                );

                for (int i = 0; i < fields.size(); i++) {
                    sheet.autoSizeColumn(i);
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.dispose();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new ExcelExportException("Excel generation failed");
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }

    private CellStyle createBoldStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        return style;
    }
}
