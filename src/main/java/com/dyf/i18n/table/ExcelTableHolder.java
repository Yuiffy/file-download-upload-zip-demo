package com.dyf.i18n.table;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuiff on 2017/1/3.
 */
public class ExcelTableHolder extends AbstractTableHolder implements TableHolder {
    private Sheet sheet;
    private Workbook workbook;

    public ExcelTableHolder(String filename) throws IOException, InvalidFormatException {
        this(new File(filename));
    }

    public ExcelTableHolder(File file) throws IOException, InvalidFormatException {
        //根据上述创建的输入流 创建工作簿对象
        this(new FileInputStream(file));
    }

    public ExcelTableHolder(InputStream inp) throws IOException, InvalidFormatException {
        //根据上述创建的输入流 创建工作簿对象
        workbook = WorkbookFactory.create(inp);
        sheet = workbook.getSheetAt(0);
    }

    public ExcelTableHolder() {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet();
    }

    @Override
    public List<String> getFirstRowString() {
        Row firstRow = sheet.getRow(0);
        List<String> list = new ArrayList<>();
        if (firstRow == null) return list;
        for (Cell cell : firstRow) {
            list.add(cell.toString());
        }
        return list;
    }

    /**
     * with out first row
     *
     * @param colnum
     * @return
     */
    @Override
    public List<String> getColStringWithOutFirstRow(int colnum) {
        List<String> list = new ArrayList<>();
        int siz = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < siz; i++) {
            Row row = sheet.getRow(i);
            Cell cell = row.getCell(colnum);
            list.add(cell.toString());
        }
        return list;
    }

    @Override
    public void addColumn(String columnTitle, Map<String, String> kvMap, int keyColNum) {
        Row firstRow = sheet.getRow(0);
        int colNum = firstRow.getLastCellNum();
        Cell titleCell = firstRow.createCell(colNum);
        titleCell.setCellValue(columnTitle);
        int last = sheet.getLastRowNum();
        for (int i = 1; i <= last; i++) {
            Row row = sheet.getRow(i);
            String key = row.getCell(keyColNum).getStringCellValue();
            String value = kvMap.get(key);
            Cell newCell = row.createCell(colNum);
            newCell.setCellValue(value);
        }
    }

    @Override
    public void setColumn(String columnTitle, List<String> column, int colNum) {
        int nowRowNums = sheet.getPhysicalNumberOfRows();
        int expectedRowNums = 1 + column.size();
        for (int i = nowRowNums; i < expectedRowNums; i++) {
            sheet.createRow(i);
        }
        Row firstRow = sheet.getRow(0);
        if (firstRow.getCell(colNum) == null) firstRow.createCell(colNum);
        firstRow.getCell(colNum).setCellValue(columnTitle);
        for (int i = 0; i < column.size(); i++) {
            Row row = sheet.getRow(i + 1);
            if (row.getCell(colNum) == null) row.createCell(colNum);
            row.getCell(colNum).setCellValue(column.get(i));
        }
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        workbook.write(outputStream);
    }
}
