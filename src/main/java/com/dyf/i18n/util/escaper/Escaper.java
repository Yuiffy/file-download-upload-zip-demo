package com.dyf.i18n.util.escaper;

import java.util.List;

/**
 * Created by yuiff on 2017/1/11.
 */
public interface Escaper {

    public List<String> escape(List<String> list);

    public List<String> unescape(List<String> list);

    public String getFileExtension();
}
