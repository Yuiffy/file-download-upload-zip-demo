package com.dyf.i18n;

import com.dyf.i18n.service.FileConvertService;
import com.dyf.i18n.table.ExcelTableHolder;
import com.dyf.i18n.util.FileType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MainExcelToOthers {
    //convert table to many files like timplate
    public static void main(String[] args) throws Exception {
//        excel2xmls();
//            excel2jsons_map();
        excel2jsons_map_limit_language();
    }

    public static void excel2jsons_map() throws IOException, InvalidFormatException {
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
        Map<String, String> all = new HashMap<>();
        for (File xlsfile : excelFiles) {
            System.out.println(xlsfile);
            Map<String, String> temp =
                    fileCon.excelToOthersMap(new ExcelTableHolder(xlsfile),
                            new String(Files.readAllBytes(Paths.get(templateFilenameString))),
                            stringPrefix, stringSuffix, FileType.json);
            all.putAll(temp);
            String outputDir = outputDirString + xlsfile.getName() + "/";
            new File(outputDir).mkdirs();
            for (Map.Entry<String, String> entry : temp.entrySet()) {
                String lang = entry.getKey();
                String outputFileName = fileCon.getOutputFileName(new File(templateFilenameString).getName(), outputDir, lang);
                try (PrintWriter out = new PrintWriter(outputFileName)) {
                    out.println(entry.getValue());
                }
                System.out.println("output File finish:" + outputFileName);
            }
        }
//        System.out.println(all);
    }


    public static void excel2jsons_map_limit_language() throws IOException, InvalidFormatException {
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
        Map<String, String> all = new HashMap<>();
        String[] a = {"XXX", "拼音"};
        Set<String> languageLimit = new HashSet<>(Arrays.asList(a));
        for (File xlsfile : excelFiles) {
            System.out.println(xlsfile);
            Map<String, String> temp =
                    fileCon.excelToOthersMap(new ExcelTableHolder(xlsfile),
                            new String(Files.readAllBytes(Paths.get(templateFilenameString))),
                            stringPrefix, stringSuffix, FileType.json, languageLimit);
            all.putAll(temp);
            String outputDir = outputDirString + xlsfile.getName() + "/";
            new File(outputDir).mkdirs();
            for (Map.Entry<String, String> entry : temp.entrySet()) {
                String lang = entry.getKey();
                String outputFileName = fileCon.getOutputFileName(new File(templateFilenameString).getName(), outputDir, lang);
                try (PrintWriter out = new PrintWriter(outputFileName)) {
                    out.println(entry.getValue());
                }
                System.out.println("output File finish:" + outputFileName);
            }
        }
//        System.out.println(all);
    }
//
//    public static void excel2xmls() throws IOException, InvalidFormatException {
//        final String excelDirString = "./workfiles/excel2others/excelinput/";
//        final String templateFilenameString = "./workfiles/excel2others/templateinput/template.xml";
//        final String outputDirString = "./workfiles/excel2others/outputFiles_xml/";
//        final String stringPrefix = ">";
//        final String stringSuffix = "</string>";
//
//        FileConvertService fileCon = new FileConvertService();
//        File excelDir = new File(excelDirString);
//        File[] excelFiles = excelDir.listFiles(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return name.endsWith(".xls");
//            }
//        });
//        if (excelFiles == null) System.out.println(excelDir.getAbsolutePath() + " have not .xls files in it!");
//        for (File xlsfile : excelFiles) {
//            System.out.println(xlsfile);
//            fileCon.excelToOtherAndOutputToFile(new ExcelTableHolder(xlsfile),
//                    new File(templateFilenameString),
//                    outputDirString + xlsfile.getName() + "/",
//                    stringPrefix, stringSuffix, FileType.xml);
//        }
//    }
}
