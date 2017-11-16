package com.dyf.i18n.table;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by yuiff on 2017/1/4.
 */
public interface TableHolder {

    List<String> getFirstRowString();

    List<String> getColStringWithOutFirstRow(int colIndex);

    Map<String, String> getKeyValueMapByTwoCol(int keyColNum, int valueColNum, String prefix, String suffix);

    Map<String, String> getKeyValueMapByTwoCol(int keyColNum, int valueColNum);

    void addColumn(String columnTitle, Map<String, String> kvMap, int keyColNum);

    void setColumn(String columnTitle, List<String> column, int colNum);

    void addRow(List<String> row);

    List<String> getRowString(int rowIndex);

    //编辑行，不会新建行。没有这行的话返回false，编辑成功返回true。
    Boolean setRowString(int rowIndex, List<String> rowList);

    void write(OutputStream outputStream) throws IOException;

    void merge(TableHolder other);
}
