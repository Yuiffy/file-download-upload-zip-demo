package com.dyf.i18n.util.escaper;

import com.dyf.i18n.replace.QuickReplacer;
import com.dyf.i18n.replace.Replacer;
import com.dyf.i18n.util.FileType;
import com.dyf.i18n.util.ListStringUtil;

/**
 * Created by yuiff on 2017/1/11.
 */
public class SimpleJsonEscaper extends AbstractEscaper implements Escaper {
    private static final String fileExtension = "json";
    Replacer escapeReplacer;
    Replacer unescapeReplacer;

    public SimpleJsonEscaper() {
        final String[] before = {"\\", "\"", "\b", "\t", "\n", "\f", "\r"};
        final String[] after = {"\\\\", "\\\"", "\\b", "\\t", "\\n", "\\f", "\\r"};
        this.escapeReplacer = new QuickReplacer(ListStringUtil.array2map(before, after));
        this.unescapeReplacer = new QuickReplacer(ListStringUtil.array2map(after, before));
    }

    @Override
    public String escape(String str) {
        return escapeReplacer.doReplace(str);
    }

    @Override
    public String unescape(String str) {
        return unescapeReplacer.doReplace(str);
    }

    @Override
    public String getFileExtension() {
        return SimpleJsonEscaper.fileExtension;
    }

    @Override
    public FileType getFileType() {
        return FileType.json;
    }
}
