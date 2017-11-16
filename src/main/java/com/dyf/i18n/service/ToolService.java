package com.dyf.i18n.service;

import com.dyf.i18n.table.ExcelTableHolder;
import com.dyf.i18n.table.TableHolder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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