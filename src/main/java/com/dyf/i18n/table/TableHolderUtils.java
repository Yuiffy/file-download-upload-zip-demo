package com.dyf.i18n.table;

import com.dyf.i18n.util.ListStringUtil;

import java.util.List;

/**
 * Created by yuiff on 2017/2/10.
 */
public class TableHolderUtils {
    public static TableHolder mergeAll(List<TableHolder> tableHolders) {
        TableHolder tableHolder = null;
        if (tableHolders != null && tableHolders.size() != 0) {
            tableHolder = tableHolders.get(0);
            for (int i = 1; i < tableHolders.size(); i++)
                tableHolder.merge(tableHolders.get(i));
        } else tableHolder = new ExcelTableHolder();
        return tableHolder;
    }

    public static TableHolder deleteEmptyRows(TableHolder tableHolder){
        TableHolder newTableHolder = createTableHolder(tableHolder);
        List<String> firstCol = tableHolder.getColStringWithOutFirstRow(0);
        newTableHolder.addRow(tableHolder.getRowString(0));
        for(int i=0; i<firstCol.size(); i++){
            List<String> row = tableHolder.getRowString(i+1);
            boolean isEmptyRow = true;
            for(String cell:row)
                if(!ListStringUtil.isLookLikeEmpty(cell)){
                    isEmptyRow = false;
                    break;
                }
            if(!isEmptyRow)newTableHolder.addRow(row);
        }
        return newTableHolder;
    }

    public static TableHolder createTableHolder(TableHolder tableHolder){
        if(tableHolder instanceof ExcelTableHolder) return new ExcelTableHolder();
        return new ExcelTableHolder();
    }
}
