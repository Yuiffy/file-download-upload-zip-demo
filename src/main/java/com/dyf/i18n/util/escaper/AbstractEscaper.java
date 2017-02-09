package com.dyf.i18n.util.escaper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuiff on 2017/2/8.
 */
public abstract class AbstractEscaper implements Escaper {
    @Override
    public abstract String escape(String str);

    @Override
    public abstract String unescape(String str);

    @Override
    public List<String> escape(List<String> list) {
        List<String> ret = new ArrayList<>(list.size());
        for (String item : list) {
            ret.add(escape(item));
        }
        return ret;
    }

    @Override
    public List<String> unescape(List<String> list) {
        List<String> ret = new ArrayList<>(list.size());
        for (String item : list) {
            ret.add(unescape(item));
        }
        return ret;
    }
}
