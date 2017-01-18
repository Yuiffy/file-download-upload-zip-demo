package com.dyf.i18n;

import com.dyf.i18n.excel.ExcelTableHolder;
import com.dyf.i18n.service.FileConvertService;
import com.dyf.i18n.util.FileType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

public class MainExcelToOthers {
    //convert excel to many files like timplate
    public static void main(String[] args) throws Exception {
        excel2jsons();
        excel2xmls();
    }

    public static void excel2jsons() throws IOException, InvalidFormatException {
        final String excelDirString = "./workfiles/excel2others/excelinput/";
        final String templateFilenameString = "./workfiles/excel2others/templateinput/template.json";
        final String outputDirString = "./workfiles/excel2others/outputFiles_json/";
        final String stringPrefix = ": \"";
        final String stringSuffix = "\"";

        FileConvertService fileCon = new FileConvertService();
        File excelDir = new File(excelDirString);
        File[] excelFiles = excelDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xls");
            }
        });
        if (excelFiles == null) System.out.println(excelDir.getAbsolutePath() + " have not .xls files in it!");
        for (File xlsfile : excelFiles) {
            System.out.println(xlsfile);
            fileCon.excelToOtherAndOutputToFile(new ExcelTableHolder(xlsfile),
                    new File(templateFilenameString),
                    outputDirString + xlsfile.getName() + "/",
                    stringPrefix, stringSuffix, FileType.json);
        }
    }


    public static void excel2xmls() throws IOException, InvalidFormatException {
        final String excelDirString = "./workfiles/excel2others/excelinput/";
        final String templateFilenameString = "./workfiles/excel2others/templateinput/template.xml";
        final String outputDirString = "./workfiles/excel2others/outputFiles_xml/";
        final String stringPrefix = ">";
        final String stringSuffix = "</string>";

        FileConvertService fileCon = new FileConvertService();
        File excelDir = new File(excelDirString);
        File[] excelFiles = excelDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xls");
            }
        });
        if (excelFiles == null) System.out.println(excelDir.getAbsolutePath() + " have not .xls files in it!");
        for (File xlsfile : excelFiles) {
            System.out.println(xlsfile);
            fileCon.excelToOtherAndOutputToFile(new ExcelTableHolder(xlsfile),
                    new File(templateFilenameString),
                    outputDirString + xlsfile.getName() + "/",
                    stringPrefix, stringSuffix, FileType.xml);
        }
    }
}
