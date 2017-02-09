package com.dyf.i18n.service;

import com.dyf.i18n.file.KeyValueFileHandler;
import com.dyf.i18n.file.XmlFileHandler;
import com.dyf.i18n.replace.NormalReplacer;
import com.dyf.i18n.replace.Replacer;
import com.dyf.i18n.replace.template.NormalTemplateHolder;
import com.dyf.i18n.replace.template.TemplateHolder;
import com.dyf.i18n.table.ExcelTableHolder;
import com.dyf.i18n.table.TableHolder;
import com.dyf.i18n.util.FileType;
import com.dyf.i18n.util.ListStringUtil;
import com.dyf.i18n.util.escaper.Escaper;
import com.dyf.i18n.util.escaper.JsonEscaper;
import com.dyf.i18n.util.escaper.XmlEscaper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by yuiff on 2017/1/3.
 */
public class FileConvertService {
    public Map<String, String> excelToOthersMap(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, FileType escapeType) throws IOException, InvalidFormatException {
        return excelToOthersMap(tableHolder, template, stringPrefix, stringSuffix, escapeType, null);
    }

    public Map<String, String> excelToOthersMap2(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, FileType escapeType, Set<String> languageOnly) throws IOException, InvalidFormatException {
        Map<String, String> ret = new HashMap<>();
        List<String> titles = tableHolder.getFirstRowString();
        Escaper escaper = getEscaper(escapeType);
        List<String> keyList = ListStringUtil.addPrefixSuffix(
                escaper.escape(
                        tableHolder.getColStringWithOutFirstRow(0)
                ), stringPrefix, stringSuffix);
        for (int i = 1; i < titles.size(); i++) {
            String lang = titles.get(i);
            if (languageOnly != null && !languageOnly.contains(lang)) continue;
            List<String> valueList = ListStringUtil.addPrefixSuffix(
                    escaper.escape(
                            tableHolder.getColStringWithOutFirstRow(i)
                    ), stringPrefix, stringSuffix);
            Map<String, String> kvMap = ListStringUtil.list2map(keyList, valueList);

            String outputString = getTranslatedString(template, kvMap);
            ret.put(lang, outputString);
            System.out.println("translated finish:" + lang);
        }
        return ret;
    }


    public Map<String, String> excelToOthersMap(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, FileType escapeType, Set<String> languageOnly) throws IOException, InvalidFormatException {
        Escaper escaper = getEscaper(escapeType);
        return excelToOthersMap(tableHolder, template, stringPrefix, stringSuffix, escaper, languageOnly);
    }

    private Escaper getEscaper(FileType escapeType) {
        Escaper escaper;
        if (escapeType.equals(FileType.xml)) escaper = new XmlEscaper();
        else if (escapeType.equals(FileType.json)) escaper = new JsonEscaper();
        else escaper = new JsonEscaper();
        return escaper;
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

    public void ManyXmlToOneExcelFile(List<File> xmlFiles, OutputStream excelOutputStream) throws IOException, SAXException, ParserConfigurationException {
        TableHolder excelHolder = new ExcelTableHolder();
        XmlFileHandler firstXmlHandler = new XmlFileHandler(xmlFiles.get(0));
        List<String> keyList = firstXmlHandler.getKeyList();
        excelHolder.setColumn("string_id", keyList, 0);
        for (int i = 0; i < xmlFiles.size(); i++) {
            File file = xmlFiles.get(i);
            KeyValueFileHandler xmlHandler = new XmlFileHandler(file);
            Map<String, String> kvMap = xmlHandler.getKeyValueMap();
            excelHolder.addColumn(file.getName(), kvMap, 0);
        }
        excelHolder.write(excelOutputStream);
    }

    public ByteArrayOutputStream excelToOtherZip(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, Escaper escaper, String outputFileNamePrefix) throws IOException, InvalidFormatException {
        return excelToOtherZip(tableHolder, template, stringPrefix, stringSuffix, escaper, outputFileNamePrefix, null);
    }

    public ByteArrayOutputStream excelToOtherZip(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, Escaper escaper, String outputFileNamePrefix, Set<String> languageLimit) throws IOException, InvalidFormatException {
        Map<String, String> textMap = excelToOthersMap(tableHolder, template, stringPrefix, stringSuffix, escaper, languageLimit);
        ByteArrayOutputStream ret = mapToZip(textMap, outputFileNamePrefix, "." + escaper.getFileExtension(), languageLimit);
        return ret;
    }

    public ByteArrayOutputStream excelToOtherZip(List<TableHolder> tableHolders, String template, String stringPrefix, String stringSuffix, Escaper escaper, String outputFileNamePrefix, Set<String> languageLimit) throws IOException, InvalidFormatException {
        TableHolder tableHolder = null;
        if (tableHolders != null && tableHolders.size() != 0)
            tableHolder = tableHolders.get(0);
        else tableHolder = new ExcelTableHolder();
        for (int i = 1; i < tableHolders.size(); i++)
            tableHolder.merge(tableHolders.get(i));
        Map<String, String> textMap = excelToOthersMap(tableHolder, template, stringPrefix, stringSuffix, escaper, languageLimit);

        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bo);
        mapToZip(zipOut,textMap, outputFileNamePrefix, "." + escaper.getFileExtension(), languageLimit);

        zipOut.putNextEntry(new ZipEntry("merged.xls"));
        tableHolder.write(zipOut);
        zipOut.closeEntry();
        zipOut.close();
        return bo;
    }

    public ByteArrayOutputStream excelToOtherZip(List<TableHolder> tableHolders, String template, String stringPrefix, String stringSuffix, FileType escapeType, String outputFileNamePrefix, Set<String> languageLimit) throws IOException, InvalidFormatException {
        Escaper escaper = getEscaper(escapeType);
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
        mapToZip(zipOut,textMap,outputFileNamePrefix,outputFileNameSuffix,keyLimit);
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

    public void ManyXmlToOneExcelFile(ZipInputStream zipInputStream, OutputStream excelOutputStream) throws IOException, SAXException, ParserConfigurationException {
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            //TODO
        }
    }
}
