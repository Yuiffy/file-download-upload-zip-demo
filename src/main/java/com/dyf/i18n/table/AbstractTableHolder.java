package com.dyf.i18n.table;

import com.dyf.i18n.util.ListStringUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuiff on 2017/1/3.
 */
public abstract class AbstractTableHolder implements TableHolder {
    @Override
    public abstract List<String> getFirstRowString();

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
    public void merge(TableHolder other) {
        List<String> titles = other.getFirstRowString();
        for (int i = 1; i < titles.size(); i++) {
            Map<String, String> kvMap = other.getKeyValueMapByTwoCol(0, i);
            this.addColumn(titles.get(i), kvMap, 0);
        }
    }
}
