package com.dyf.i18n.util.escaper;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Created by yuiff on 2017/1/11.
 */
public class JsonEscaper extends AbstractEscaper implements Escaper {
    private static final String fileExtension = "json";

    @Override
    public String escape(String str) {
        return StringEscapeUtils.escapeJson(str);
    }

    @Override
    public String unescape(String str) {
        return StringEscapeUtils.unescapeJson(str);
    }

    @Override
    public String getFileExtension() {
        return JsonEscaper.fileExtension;
    }
}
