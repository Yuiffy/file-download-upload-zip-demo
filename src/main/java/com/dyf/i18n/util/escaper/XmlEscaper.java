package com.dyf.i18n.util.escaper;

import com.dyf.i18n.util.FileType;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Created by yuiff on 2017/1/11.
 */
public class XmlEscaper extends AbstractEscaper implements Escaper {
    private static final String fileExtension = "xml";

    @Override
    public String escape(String str) {
        return StringEscapeUtils.escapeXml(str);
    }

    @Override
    public String unescape(String str) {
        return StringEscapeUtils.unescapeXml(str);
    }

    @Override
    public String getFileExtension() {
        return XmlEscaper.fileExtension;
    }

    @Override
    public FileType getFileType() {
        return FileType.xml;
    }
}
