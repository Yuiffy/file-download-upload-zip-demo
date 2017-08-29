package com.dyf.i18n.util.escaper;

import com.dyf.i18n.util.FileType;

import java.util.List;

/**
 * Created by yuiff on 2017/1/11.
 */
public interface Escaper {
    public String escape(String str);

    public String unescape(String str);

    public List<String> escape(List<String> list);

    public List<String> unescape(List<String> list);

    public String getFileExtension();

    public FileType getFileType();
}
