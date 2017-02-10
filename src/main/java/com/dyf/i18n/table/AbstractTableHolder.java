package com.dyf.i18n.table;

import com.dyf.i18n.util.ListStringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by yuiff on 2017/1/3.
 */
public abstract class AbstractTableHolder implements TableHolder {
    @Override
    public List<String> getFirstRowString(){
        return getRowString(0);
    }

    /**
     * with out first row
     *
     * @param colnum
     * @return
     */
    @Override
    public abstract List<String> getColStringWithOutFirstRow(int colnum);

    /**
     * with out first row
     *
     */
    @Override
    public Map<String, String> getKeyValueMapByTwoCol(int keyColNum, int valueColNum, String prefix, String suffix) {
        List<String> keyList = getColStringWithOutFirstRow(keyColNum);
        List<String> valueList = getColStringWithOutFirstRow(valueColNum);
        if (keyList.size() != valueList.size()) {
            System.out.println("size error! " + keyList.size() + " != " + valueList.size());
            return null;
        }
        keyList = ListStringUtil.addPrefixSuffix(keyList, prefix, suffix);
        valueList = ListStringUtil.addPrefixSuffix(valueList, prefix, suffix);
        int siz = keyList.size();
        Map<String, String> mp = new HashMap<>();
        for (int i = 0; i < siz; i++) {
            mp.put(keyList.get(i), valueList.get(i));
        }
        return mp;
    }

    @Override
    public Map<String, String> getKeyValueMapByTwoCol(int keyColNum, int valueColNum) {
        return getKeyValueMapByTwoCol(keyColNum, valueColNum, null, null);
    }

    @Override
    public abstract void addColumn(String columnTitle, Map<String, String> kvMap, int keyColNum);

    @Override
    public abstract void setColumn(String columnTitle, List<String> column, int colNum);

    @Override
    public abstract void write(OutputStream outputStream) throws IOException;

    @Override
    public abstract void addRow(List<String> row);

    private void rowTitleMerge(TableHolder other){
        List<String> myRowTitles = this.getColStringWithOutFirstRow(0);
        List<String> otherRowTitles = other.getColStringWithOutFirstRow(0);
        Set<String> myRowTitlesSet = new HashSet(myRowTitles);
        for(String otherRowTitle:otherRowTitles){
            if(!myRowTitlesSet.contains(otherRowTitle)){
                System.out.println(this + " excel have not the row:\"" + otherRowTitle + "\", so add the row to it.");
                List<String> newRow = new ArrayList<>();
                newRow.add(otherRowTitle);
                this.addRow(newRow);
                myRowTitlesSet.add(otherRowTitle);
            }
        }
    }

    @Override
    public void merge(TableHolder other) {
        rowTitleMerge(other);

        List<String> otherTitles = other.getFirstRowString();
        for (int i = 1; i < otherTitles.size(); i++) {
            Map<String, String> kvMap = other.getKeyValueMapByTwoCol(0, i);
            this.addColumn(otherTitles.get(i), kvMap, 0);
        }
    }

    @Override
    public abstract List<String> getRowString(int rowIndex);
}
