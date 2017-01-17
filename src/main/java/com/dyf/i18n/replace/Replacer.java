package com.dyf.i18n.replace;

/**
 * Created by yuiff on 2017/1/3.
 */
public interface Replacer {
    String put(String key, String value);

    String doReplace(String template);
}
