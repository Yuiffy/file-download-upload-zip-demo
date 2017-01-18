package com.dyf.i18n.util.escaper;

import com.dyf.i18n.util.FileType;

/**
 * Created by yuiff on 2017/1/18.
 */
public class EscaperFactory {
    public static Escaper getEscaper(FileType fileType) {
        switch (fileType) {
            case json:
                return new JsonEscaper();
            case xml:
                return new XmlEscaper();
            default:
                return new JsonEscaper();
        }
    }
}
