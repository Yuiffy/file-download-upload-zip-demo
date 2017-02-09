package com.dyf.i18n.service;

import com.dyf.i18n.file.FileHandlerFactory;
import com.dyf.i18n.file.KeyValueFileHandler;
import com.dyf.i18n.replace.NormalReplacer;
import com.dyf.i18n.replace.Replacer;
import com.dyf.i18n.replace.template.NormalTemplateHolder;
import com.dyf.i18n.replace.template.TemplateHolder;
import com.dyf.i18n.table.ExcelTableHolder;
import com.dyf.i18n.table.TableHolder;
import com.dyf.i18n.util.FileType;
import com.dyf.i18n.util.escaper.Escaper;
import com.dyf.i18n.util.escaper.EscaperFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by yuiff on 2017/1/3.
 */
public class FileConvertService {
    public Map<String, String> excelToOthersMap(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, FileType escapeType) throws IOException, InvalidFormatException {
        return excelToOthersMap(tableHolder, template, stringPrefix, stringSuffix, escapeType, null);
    }

    public Map<String, String> excelToOthersMap(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, FileType escapeType, Set<String> languageOnly) throws IOException, InvalidFormatException {
        Escaper escaper = EscaperFactory.getEscaper(escapeType);
        return excelToOthersMap(tableHolder, template, stringPrefix, stringSuffix, escaper, languageOnly);
    }

    public String getOutputFileName(String templateFilename, String outputDir, String lang) {
        String[] baseAndExtension = templateFilename.split("\\.(?=[^\\.]+$)");
        String outputFileNameHead = baseAndExtension[0] + "_";
        String outputFIleNameTail = baseAndExtension[1];
        String outputFileName = outputDir + outputFileNameHead + lang + "." + outputFIleNameTail;
        return outputFileName;
    }

    private String getTranslatedString(String template, Map<String, String> kvMap) {
        Replacer replacer = new NormalReplacer(kvMap);
        return replacer.doReplace(template);
    }

    public void manyOtherToOneExcelFile(List<String> files, List<String> filesName, FileType fileType, OutputStream excelOutputStream) throws IOException, SAXException, ParserConfigurationException {
        TableHolder excelHolder = new ExcelTableHolder();
        KeyValueFileHandler firstXmlHandler = FileHandlerFactory.createHandler(files.get(0), fileType);
        List<String> keyList = firstXmlHandler.getKeyList();
        excelHolder.setColumn("string_id", keyList, 0);
        for (int i = 0; i < files.size(); i++) {
            String file = files.get(i);
            KeyValueFileHandler xmlHandler = FileHandlerFactory.createHandler(file, fileType);
            Map<String, String> kvMap = xmlHandler.getKeyValueMap();
//            System.out.println(kvMap);
            excelHolder.addColumn(filesName.get(i), kvMap, 0);
        }
        excelHolder.write(excelOutputStream);
    }

    public void manyOtherToOneExcelFile(List<File> files, FileType fileType, OutputStream excelOutputStream) throws IOException, SAXException, ParserConfigurationException {
        List<String> listString = new ArrayList<>();
        List<String> listName = new ArrayList<>();
        for (File file : files) {
            String str = new String(Files.readAllBytes(file.toPath()));
            listString.add(str);
            listName.add(file.getName());
        }
        manyOtherToOneExcelFile(listString, listName, fileType, excelOutputStream);
    }

    public void manyXmlToOneExcelFile(List<File> files, OutputStream excelOutputStream) throws IOException, SAXException, ParserConfigurationException {
        manyOtherToOneExcelFile(files, FileType.xml, excelOutputStream);
    }

    public ByteArrayOutputStream excelToOtherZip(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, Escaper escaper, String outputFileNamePrefix) throws IOException, InvalidFormatException {
        return excelToOtherZip(tableHolder, template, stringPrefix, stringSuffix, escaper, outputFileNamePrefix, null);
    }

    public ByteArrayOutputStream excelToOtherZip(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, Escaper escaper, String outputFileNamePrefix, Set<String> languageLimit) throws IOException, InvalidFormatException {
        Map<String, String> textMap = excelToOthersMap(tableHolder, template, stringPrefix, stringSuffix, escaper, languageLimit);
        ByteArrayOutputStream ret = mapToZip(textMap, outputFileNamePrefix, "." + escaper.getFileExtension(), languageLimit);
        return ret;
    }

    public ByteArrayOutputStream excelToOtherZip(List<TableHolder> tableHolders, String template, String stringPrefix, String stringSuffix, Escaper escaper, String outputFileNamePrefix, Set<String> languageLimit) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
        TableHolder tableHolder = null;
        if (tableHolders != null && tableHolders.size() != 0) {
            tableHolder = tableHolders.get(0);
            for (int i = 1; i < tableHolders.size(); i++)
                tableHolder.merge(tableHolders.get(i));
        } else tableHolder = new ExcelTableHolder();

        fitRowTitleToTemplate(tableHolder,template,escaper.getFileType());
        Map<String, String> textMap = excelToOthersMap(tableHolder, template, stringPrefix, stringSuffix, escaper, languageLimit);

        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bo);
        mapToZip(zipOut, textMap, outputFileNamePrefix, "." + escaper.getFileExtension(), languageLimit);

        zipOut.putNextEntry(new ZipEntry("merged.xls"));
        tableHolder.write(zipOut);
        zipOut.closeEntry();
        zipOut.close();
        return bo;
    }

    public ByteArrayOutputStream excelToOtherZip(List<TableHolder> tableHolders, String template, String stringPrefix, String stringSuffix, FileType escapeType, String outputFileNamePrefix, Set<String> languageLimit) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
        Escaper escaper = EscaperFactory.getEscaper(escapeType);
        return excelToOtherZip(tableHolders, template, stringPrefix, stringSuffix, escaper, outputFileNamePrefix, languageLimit);
    }

    public void mapToZip(ZipOutputStream zipOut, Map<String, String> textMap, String outputFileNamePrefix, String outputFileNameSuffix, Set<String> keyLimit) throws IOException, InvalidFormatException {
        Set<String> nameSet = new HashSet<>();
        for (Map.Entry<String, String> entry : textMap.entrySet()) {
            String lang = entry.getKey();
            String outputFileName = outputFileNamePrefix + lang + outputFileNameSuffix;
            if (nameSet.contains(outputFileName)) {
                System.out.println("Duplicate file will not output, name:" + outputFileName);
                continue;
            }
            nameSet.add(outputFileName);
            String outputString = entry.getValue();
            zipOut.putNextEntry(new ZipEntry(outputFileName));
            zipOut.write(outputString.getBytes());
            zipOut.closeEntry();
            System.out.println("zip File finish:" + outputFileName);
        }
    }

    public ByteArrayOutputStream mapToZip(Map<String, String> textMap, String outputFileNamePrefix, String outputFileNameSuffix, Set<String> keyLimit) throws IOException, InvalidFormatException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bo);
        mapToZip(zipOut, textMap, outputFileNamePrefix, outputFileNameSuffix, keyLimit);
        zipOut.close();
        return bo;
    }


    public Map<String, String> excelToOthersMap(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, Escaper escaper, Set<String> languageLimit) throws IOException, InvalidFormatException {
        TemplateHolder templateHolder = new NormalTemplateHolder(tableHolder, escaper, template, stringPrefix, stringSuffix);
        List<String> titles = templateHolder.getFirstRowString();
        Map<String, String> ret = new HashMap<>();
        for (int i = 1; i < titles.size(); i++) {
            String lang = titles.get(i);
            if (languageLimit != null && !languageLimit.contains(lang)) continue;
            if (ret.containsKey(lang)) continue;
            String outputString = templateHolder.getRepacedTemplate(i);
            ret.put(lang, outputString);
            System.out.println("translated:" + lang);
        }
        return ret;
    }

    private String faker(String str) {
        return str.replaceAll("[　*| *| *|//s*]*", "").toLowerCase();
    }

    public void fitRowTitleToTemplate(TableHolder tableHolder, String template, FileType templateType) throws ParserConfigurationException, SAXException, IOException {
        List<String> rowTitles = tableHolder.getColStringWithOutFirstRow(0);
        List<String> fakeRowTitles = new ArrayList<>(rowTitles);
        for (int i = 0; i < fakeRowTitles.size(); i++) {
            String s = fakeRowTitles.get(i);
            s = faker(s);
            fakeRowTitles.set(i, s);
        }
        System.out.println(rowTitles);
        System.out.println(fakeRowTitles);
        Map<String, Integer> strIndexMap = new HashMap<>();
        Map<String, Integer> fakeMap = new HashMap<>();
        for (int i = 0; i < rowTitles.size(); i++) {
            strIndexMap.put(rowTitles.get(i), i);
            String fakeTitle = fakeRowTitles.get(i);
            if (fakeMap.containsKey(fakeTitle)) {
                System.out.println("fake title repeat! I will only handle the first, won't add " + i + " : " + rowTitles.get(i));
                Integer repeatIndex = fakeMap.get(fakeTitle);
                System.out.println(repeatIndex + " : " + rowTitles.get(repeatIndex));
                System.out.println(i + " : " + rowTitles.get(i));
            } else
                fakeMap.put(fakeRowTitles.get(i), i);
        }

        KeyValueFileHandler templateHandler = FileHandlerFactory.createHandler(template, templateType);
        List<String> templateTitles = new ArrayList<>(templateHandler.getKeyValueMap().values());

        for (String title : templateTitles) {
            if (strIndexMap.containsKey(title)) continue;
            String fakeTitle = faker(title);
            if (fakeMap.containsKey(fakeTitle)) {
                Integer index = fakeMap.get(fakeTitle);
                System.out.println("can't find a sentence in excel but find one without space and lowercase. I will change the title in excel to fit template");
                System.out.println("excel: " + rowTitles.get(index));
                System.out.println("templ: " + title);
                rowTitles.set(index, title);
            } else {
                //the sentence in template didn't found in excel
            }
        }
        tableHolder.setColumn(null, rowTitles, 0);
    }
}
