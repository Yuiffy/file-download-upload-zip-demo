package com.dyf.i18n.util.escaper;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuiff on 2017/1/11.
 */
public class JsonEscaper implements Escaper{
    @Override
    public List<String> escape(List<String> list) {
        List<String> ret = new ArrayList<>(list.size());
        for(String item:list){
            ret.add(StringEscapeUtils.escapeJson(item));
        }
        return ret;
    }

    @Override
    public List<String> unescape(List<String> list) {
        List<String> ret = new ArrayList<>(list.size());
        for(String item:list){
            ret.add(StringEscapeUtils.unescapeJson(item));
        }
        return ret;
    }
}
