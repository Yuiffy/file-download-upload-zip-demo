package com.dyf.i18n.service;

import com.dyf.i18n.table.TableHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuiff on 2017/8/29.
 */
public class ToolService {

    public void renameTitleNameByExcel(TableHolder mainTableHolder, TableHolder nameTableHolder) {
        List<String> firstRow = nameTableHolder.getRowString(0);
        List<String> secondRow = nameTableHolder.getRowString(1);
        Map<String, String> renameMap = new HashMap<>();
        for(int i=0; i<firstRow.size(); i++){
            renameMap.put(firstRow.get(i), secondRow.get(i));
        }
        List<String> titleRow = mainTableHolder.getFirstRowString();
        for(int i=0; i<titleRow.size(); i++){
            String title = titleRow.get(i);
            if(renameMap.containsKey(title)){
                titleRow.set(i, renameMap.get(title));
            }
        }
        mainTableHolder.setRowString(0, titleRow);
    }
}