package com.dyf.i18n.util.escaper;

import com.dyf.i18n.util.FileType;

/**
 * Created by yuiff on 2017/2/8.
 */
public class DontEscaper extends AbstractEscaper implements Escaper {
    private String fileExtension;

    public DontEscaper(){
        this("txt");
    }

    public DontEscaper(String fileExtension){
        this.fileExtension = fileExtension;
    }

    @Override
    public String escape(String str) {
        return str;
    }

    @Override
    public String unescape(String str) {
        return str;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    @Override
    public FileType getFileType() {
        return FileType.dontEscape;
    }
}
