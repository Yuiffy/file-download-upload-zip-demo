package com.dyf.i18n.file;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.*;

/**
 * Created by yuiff on 2017/2/8.
 */
public class JsonFileHandler implements KeyValueFileHandler {
    JSONObject json;
    Map<String, JsonItemSaver> kvMap;

    public JsonFileHandler(String jsonString) {
        this.json = JSONObject.fromObject(jsonString);
        this.kvMap = new HashMap<>();
        dfsJson(json);
    }


    private void dfsJson(JSONObject json) {
        dfsJSONObject("", json);
    }

    private void dfsObject(String parent, Object data) {
        if (data instanceof JSONObject) {
            dfsJSONObject(parent, (JSONObject) data);
        } else if (data instanceof JSONArray) {
            dfsJSONArray(parent, (JSONArray) data);
        } else {
            System.out.println("Json Parse Error! " + parent + data);
        }
    }

    private void dfsJSONObject(String parent, JSONObject json) {
        Set<Map.Entry<String, Object>> entrySet = json.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            String key = entry.getKey();
            Object data = entry.getValue();
            if (data instanceof String) {
                String fullKey = parent + "." + key;
                kvMap.put(fullKey, new JsonObjectSaver(entry));
//                System.out.println(fullKey);
            } else {
                String childKey = parent.isEmpty() ? key : parent + "." + key;
                dfsObject(childKey, data);
            }
        }
    }

    private void dfsJSONArray(String parent, JSONArray json) {
        for (int i = 0; i < json.size(); i++) {
            Object data = json.get(i);
            if (data instanceof String) {
                String fullKey = parent + '[' + i + ']';
                kvMap.put(fullKey, new JsonArraySaver(json, i));
//                System.out.println(fullKey);
            } else {
                dfsObject(parent + '[' + i + ']', data);
            }
        }
    }

    @Override
    public List<String> getKeyList() {
        List<String> keyList = new ArrayList<>(kvMap.keySet());
        return keyList;
    }

    @Override
    public Map<String, String> getKeyValueMap() {
        Map<String, String> keyValueMap = new HashMap<>();
        for (Map.Entry<String, JsonItemSaver> entry : this.kvMap.entrySet()) {
            keyValueMap.put(entry.getKey(), entry.getValue().get());
        }
        return keyValueMap;
    }

    @Override
    public void put(String key, String value) {
        this.kvMap.get(key).set(value);
    }

    @Override
    public String getString() {
        return json.toString();
    }

    interface JsonItemSaver {
        String get();

        void set(String value);
    }

    class JsonObjectSaver implements JsonItemSaver {
        private Map.Entry<String, Object> inner;

        public JsonObjectSaver(Map.Entry<String, Object> entry) {
            this.inner = entry;
        }

        @Override
        public String get() {
            return (String) inner.getValue();
        }

        @Override
        public void set(String value) {
            inner.setValue(value);
        }
    }

    class JsonArraySaver implements JsonItemSaver {
        private int index;
        private JSONArray array;

        public JsonArraySaver(JSONArray array, int index) {
            this.array = array;
            this.index = index;
        }

        @Override
        public String get() {
            return (String) array.get(index);
        }

        @Override
        public void set(String value) {
            array.set(index, value);
        }
    }
}
