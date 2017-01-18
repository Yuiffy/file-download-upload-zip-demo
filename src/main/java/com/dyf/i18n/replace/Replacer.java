package com.dyf.i18n.replace;

import java.util.Map;

/**
 * Created by yuiff on 2017/1/3.
 */
public interface Replacer {
    String put(String key, String value);
    void reset(Map<String, String> kvMap);
    String doReplace(String template);
}
