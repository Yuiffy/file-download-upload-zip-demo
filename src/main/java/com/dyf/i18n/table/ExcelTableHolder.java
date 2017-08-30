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
    public List<String> getRowString(int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        List<String> list = new ArrayList<>();
        if (row == null) return list;
        for (Cell cell : row) {
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
        int lastIndex = sheet.getLastRowNum();
        for (int i = 1; i <= lastIndex; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            Cell cell = row.getCell(colnum);
            String value = (cell == null) ? "" : cell.toString();
            list.add(value);
        }
        return list;
    }

    @Override
    public void addColumn(String columnTitle, Map<String, String> kvMap, int keyColNum) {
        Row firstRow = sheet.getRow(0);
        int colNum = firstRow.getLastCellNum();
        Cell titleCell = firstRow.createCell(colNum);
        titleCell.setCellValue(columnTitle);
        int lastRowIndex = sheet.getLastRowNum();
        for (int i = 1; i <= lastRowIndex; i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(keyColNum) == null) {
                System.out.println("skip a empty row: " + i + " " + row + " ,<last = " + lastRowIndex);
                continue;
            }
            String key = row.getCell(keyColNum).getStringCellValue();
            String value = kvMap.get(key);
//            if(!kvMap.containsKey(key)) System.out.println("can`t found the key:\""+key+"\" in language "+columnTitle);
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
//        workbook.write(outputStream);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);//如果是HSSF的workbook，这个write会直接close掉流，导致zip流不能继续添加别的东西。所以搞个byte的流再write。
        bos.writeTo(outputStream);
    }

    @Override
    public void addRow(List<String> row) {
        int lastRowIndex = sheet.getLastRowNum();
        if (lastRowIndex == 0) {
            if (sheet.getPhysicalNumberOfRows() == 0) lastRowIndex = -1;
        }
//        System.out.println("add Row! now lastRowIndex is "+lastRowIndex +" , I will add "+(lastRowIndex+1));
        Row newRow = sheet.createRow(lastRowIndex + 1);
        if (row != null) {
            for (int i = 0; i < row.size(); i++) {
                int cellNum = i;
                String str = (row.get(i) != null) ? row.get(i) : "";
                newRow.createCell(cellNum).setCellValue(str);
            }
        }
    }
}
