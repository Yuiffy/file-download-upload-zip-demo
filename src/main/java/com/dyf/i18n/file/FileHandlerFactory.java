package com.dyf.i18n.file;

import com.dyf.i18n.util.FileType;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Created by yuiff on 2017/1/18.
 */
public class FileHandlerFactory {
    public static KeyValueFileHandler createHandler(String fileString, FileType fileType) throws IOException, SAXException, ParserConfigurationException {
        switch (fileType) {
            case json:
                return new JsonFileHandler(fileString);
            case xml:
                return new XmlFileHandler(fileString);
            default:
                return new JsonFileHandler(fileString);
        }
    }
}
