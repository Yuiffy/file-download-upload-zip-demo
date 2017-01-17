package com.dyf.i18n.file;
import java.util.List;
import java.util.Map;

/**
 * Created by yuiff on 2017/1/6.
 */
public interface KeyValueFileHandler {
    List<String> getKeyList();
    Map<String,String> getKeyValueMap();
    void put(String key, String value);
    String getString();
}
