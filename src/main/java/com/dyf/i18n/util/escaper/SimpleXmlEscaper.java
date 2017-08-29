package com.dyf.i18n.util.escaper;

import com.dyf.i18n.replace.QuickReplacer;
import com.dyf.i18n.replace.Replacer;
import com.dyf.i18n.util.FileType;
import com.dyf.i18n.util.ListStringUtil;

/**
 * Created by yuiff on 2017/1/11.
 */
public class SimpleXmlEscaper extends AbstractEscaper implements Escaper {
    private static final String fileExtension = "xml";
    Replacer escapeReplacer;
    Replacer unescapeReplacer;

    public SimpleXmlEscaper() {
        final String[] before = {"<", "&", ">"};
        final String[] after = {"&lt;", "&amp;", "&gt;"};
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
        return SimpleXmlEscaper.fileExtension;
    }

    @Override
    public FileType getFileType() {
        return FileType.xml;
    }
}
