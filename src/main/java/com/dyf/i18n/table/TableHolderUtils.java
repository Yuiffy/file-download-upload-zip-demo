package com.dyf.i18n.table;

import java.util.List;

/**
 * Created by yuiff on 2017/2/10.
 */
public class TableHolderUtils {
    public static TableHolder mergeAll(List<TableHolder> tableHolders){
        TableHolder tableHolder = null;
        if (tableHolders != null && tableHolders.size() != 0) {
            tableHolder = tableHolders.get(0);
            for (int i = 1; i < tableHolders.size(); i++)
                tableHolder.merge(tableHolders.get(i));
        } else tableHolder = new ExcelTableHolder();
        return tableHolder;
    }
}
