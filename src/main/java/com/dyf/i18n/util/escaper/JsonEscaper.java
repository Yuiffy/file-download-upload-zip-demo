package com.dyf.i18n.util.escaper;

import com.dyf.i18n.util.FileType;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by yuiff on 2017/1/11.
 */
public class JsonEscaper extends AbstractEscaper implements Escaper {
    private static final String fileExtension = "json";

    @Override
    public String escape(String str) {
        return StringEscapeUtils.escapeJavaScript(str);
    }

    @Override
    public String unescape(String str) {
        return StringEscapeUtils.unescapeJavaScript(str);
    }

    @Override
    public String getFileExtension() {
        return JsonEscaper.fileExtension;
    }

    @Override
    public FileType getFileType() {
        return FileType.json;
    }
}
