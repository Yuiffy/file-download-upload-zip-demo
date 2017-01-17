package com.dyf.i18n;

import com.dyf.i18n.file.KeyValueFileHandler;
import com.dyf.i18n.file.XmlFileHandler;
import com.dyf.i18n.service.FileConvertService;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yuiff on 2017/1/6.
 */
public class MainXmlToExcel {

    //collect many android xml i18n file to a excel file
    public static void main3(String[] args) throws Exception {
        final String xmlInputDirString = "./workfiles/xml2excel/xml_input/";
        final String outputDirString = "./workfiles/xml2excel/excel_output/";

        FileConvertService fileCon = new FileConvertService();
        File xmlInputDir = new File(xmlInputDirString);
        File[] xmlInputFiles = xmlInputDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });
        if(xmlInputFiles == null || xmlInputFiles.length==0){
            System.out.println("No xml files!");
            return;
        }
        OutputStream os = new FileOutputStream(outputDirString+"out_excel.xls");
        fileCon.ManyXmlToOneExcelFile(Arrays.asList(xmlInputFiles),os);
    }

    //test xml read and write
    public static void main2(String[] args) throws Exception {
        KeyValueFileHandler xmlFileHandler = new XmlFileHandler(new File("strings.xml"));
        List<String> keyList = xmlFileHandler.getKeyList();
        xmlFileHandler.put(keyList.get(0), "wow!");
        String fs = xmlFileHandler.getString();
        File outf = new File("gank.xml");
        try (PrintWriter out = new PrintWriter(outf)) {
            out.println(fs);
        }
    }
}
