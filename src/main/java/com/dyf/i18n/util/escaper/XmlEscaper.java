package com.dyf.i18n.util.escaper;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuiff on 2017/1/11.
 */
public class XmlEscaper implements Escaper{
    @Override
    public List<String> escape(List<String> list) {
        List<String> ret = new ArrayList<>(list.size());
        for(String item:list){
            ret.add(StringEscapeUtils.escapeXml11(item));
        }
        return ret;
    }

    @Override
    public List<String> unescape(List<String> list) {
        List<String> ret = new ArrayList<>(list.size());
        for(String item:list){
            ret.add(StringEscapeUtils.unescapeXml(item));
        }
        return ret;
    }
}
