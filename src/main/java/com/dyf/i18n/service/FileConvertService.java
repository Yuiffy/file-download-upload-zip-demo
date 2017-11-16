package com.dyf.i18n.service;

import com.dyf.i18n.file.FileHandlerFactory;
import com.dyf.i18n.file.KeyValueFileHandler;
import com.dyf.i18n.replace.NormalReplacer;
import com.dyf.i18n.replace.Replacer;
import com.dyf.i18n.replace.template.MultiPrefixTemplateHolder;
import com.dyf.i18n.replace.template.NormalTemplateHolder;
import com.dyf.i18n.replace.template.TemplateHolder;
import com.dyf.i18n.table.ExcelTableHolder;
import com.dyf.i18n.table.TableHolder;
import com.dyf.i18n.table.TableHolderUtils;
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

    public Map<String, String> excelToOthersMap(TableHolder tableHolder, String template, String stringPrefix[], String stringSuffix[], FileType escapeType, Set<String> languageOnly) throws IOException, InvalidFormatException {
        Escaper escaper = EscaperFactory.getEscaper(escapeType);
        TemplateHolder templateHolder = new MultiPrefixTemplateHolder(tableHolder, escaper, template, stringPrefix, stringSuffix);
        return excelToOthersMap(languageOnly, templateHolder);
    }

    public Map<String, String> excelToOthersMap(TableHolder tableHolder, String template, String stringPrefix, String stringSuffix, FileType escapeType, Set<String> languageOnly) throws IOException, InvalidFormatException {
        Escaper escaper = EscaperFactory.getEscaper(escapeType);
        TemplateHolder templateHolder = new NormalTemplateHolder(tableHolder, escaper, template, stringPrefix, stringSuffix);
        return excelToOthersMap(languageOnly, templateHolder);
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

    // many language json/xml file, to one excel file with many columns, every column mean one language
    public void manyOtherToOneExcelFile(List<String> files, List<String> filesName, FileType fileType, OutputStream excelOutputStream) throws IOException, SAXException, ParserConfigurationException {
        TableHolder excelHolder = new ExcelTableHolder();
        KeyValueFileHandler firstXmlHandler = FileHandlerFactory.createHandler(files.get(0), fileType);
        List<String> keyList = firstXmlHandler.getKeyList();
        Map<String, String> keyMap = firstXmlHandler.getKeyValueMap();

        excelHolder.setColumn("string_id", keyList, 0);
        for (int i = 0; i < files.size(); i++) {
            String file = files.get(i);
            KeyValueFileHandler xmlHandler = FileHandlerFactory.createHandler(file, fileType);
            Map<String, String> kvMap = xmlHandler.getKeyValueMap();

            //if the file have new Key, add it to first column
            Map<String, String> newlyAdded = getNewlyAddedEntry(kvMap, keyMap);
            if (newlyAdded.size() > 0) {
                keyMap.putAll(newlyAdded);
                keyList.addAll(newlyAdded.keySet());
                excelHolder.setColumn("string_id", keyList, 0);
            }

            excelHolder.addColumn(filesName.get(i), kvMap, 0);
        }
        excelHolder.write(excelOutputStream);
    }

    //get new key in newMap of oldMap
    private Map<String, String> getNewlyAddedEntry(Map<String, String> newMap, Map<String, String> oldMap) {
        Map<String, String> ret = new HashMap<>();
        for (Map.Entry<String, String> entry : newMap.entrySet()) {
            if (!oldMap.containsKey(entry.getKey()) && !entry.getValue().isEmpty()) {
                ret.put(entry.getKey(), entry.getValue());
            }
        }
        return ret;
    }

    //many json/xml, get the translate values of them, list in the first column of excel
    public void manyEnglishToOneExcelFile(List<String> files, String columnName, FileType fileType, OutputStream excelOutputStream) throws IOException, SAXException, ParserConfigurationException {
        List<String> valueStringList = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String file = files.get(i);
            KeyValueFileHandler keyValueHandler = FileHandlerFactory.createHandler(file, fileType);
            Map<String, String> kvMap = keyValueHandler.getKeyValueMap();
            List<String> keyList = keyValueHandler.getKeyList();
            List<String> valueList = new ArrayList<>();
            for (String key : keyList) {
                valueStringList.add(kvMap.get(key));
            }
        }
        TableHolder excelHolder = new ExcelTableHolder();
        excelHolder.setColumn(columnName, valueStringList, 0);
        excelHolder.write(excelOutputStream);
    }

    public void manyOtherToOneExcelFile(List<File> files, FileType fileType, OutputStream excelOutputStream) throws IOException, SAXException, ParserConfigurationException {
        List<String> listString = new ArrayList<>();
        List<String> listName = new ArrayList<>();
        for (File file : files) {
            String str = new String(Files.readAllBytes(file.toPath()), ("UTF-8"));
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
        Map<String, String> textMap = excelToOthersMap(languageLimit, new NormalTemplateHolder(tableHolder, escaper, template, stringPrefix, stringSuffix));
        ByteArrayOutputStream ret = mapToZip(textMap, outputFileNamePrefix, "." + escaper.getFileExtension(), languageLimit);
        return ret;
    }

    public ByteArrayOutputStream excelToOtherZip(List<TableHolder> tableHolders, String template, String stringPrefix, String stringSuffix, Escaper escaper, FileType templateFileType, String outputFileNamePrefix, Set<String> languageLimit) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
        return excelToOtherZip(tableHolders, template, new String[]{stringPrefix}, new String[]{stringSuffix},
                escaper, templateFileType, outputFileNamePrefix, languageLimit);
    }

    public ByteArrayOutputStream excelToOtherZip(List<TableHolder> tableHolders, String template, String[] stringPrefix, String[] stringSuffix, Escaper escaper, FileType templateFileType, String outputFileNamePrefix, Set<String> languageLimit) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
        TableHolder tableHolder = TableHolderUtils.mergeAll(tableHolders);
        TemplateHolder templateHolder = new MultiPrefixTemplateHolder(tableHolder, escaper, template, stringPrefix, stringSuffix);
        return excelToOtherZip(tableHolder, template, escaper, templateFileType, outputFileNamePrefix, languageLimit, templateHolder);
    }

    public ByteArrayOutputStream excelToOtherZip(TableHolder tableHolder, String template, Escaper escaper, FileType templateFileType, String outputFileNamePrefix, Set<String> languageLimit, TemplateHolder templateHolder) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
        List<String> tableHaveNotList = new ArrayList<>();
        Map<String, String> titleChangeLog = new HashMap<>();
        fitRowTitleToTemplate(tableHolder, template, templateFileType, tableHaveNotList, titleChangeLog);

        Map<String, String> textMap = excelToOthersMap(languageLimit, templateHolder);

        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bo);
        mapToZip(zipOut, textMap, outputFileNamePrefix, "." + escaper.getFileExtension(), languageLimit);

        zipOut.putNextEntry(new ZipEntry("table_merged.xls"));
        tableHolder.write(zipOut);
        zipOut.closeEntry();

        TableHolder tableHaveNot = new ExcelTableHolder();
        tableHaveNot.setColumn("-", tableHaveNotList, 0);
        zipOut.putNextEntry(new ZipEntry("table_have_not_sentences_of_template.xls"));
        tableHaveNot.write(zipOut);
        zipOut.closeEntry();

        TableHolder tableTitleChange = new ExcelTableHolder();
        tableTitleChange.setColumn("error", new ArrayList<String>(titleChangeLog.keySet()), 0);
        tableTitleChange.addColumn("template", titleChangeLog, 0);
        zipOut.putNextEntry(new ZipEntry("table_english_different_between_table_and_template.xls"));
        tableTitleChange.write(zipOut);
        zipOut.closeEntry();

        TableHolder someLanguageHaveNot = getLanguageNotTranslate(tableHolder);
        zipOut.putNextEntry(new ZipEntry("table_sentence_some_language_not_translated.xls"));
        someLanguageHaveNot.write(zipOut);
        zipOut.closeEntry();

        zipOut.close();
        return bo;
    }

    public TableHolder getLanguageNotTranslate(TableHolder tableHolder) {
        TableHolder ret = new ExcelTableHolder();
        List<String> firstRowString = tableHolder.getFirstRowString();
        ret.addRow(firstRowString);
        List<String> firstColumn = tableHolder.getColStringWithOutFirstRow(0);
        for (int i = 0; i < firstColumn.size(); i++) {
            List<String> row = tableHolder.getRowString(1 + i);
            String engString = row.get(0);
            if (row.size() < firstRowString.size()) {//语言都没有填满，肯定有空的
                ret.addRow(row);
//                System.out.println("rowNum = "+i+", rowSize = "+row.size()+", ret.col.size="+ret.getColStringWithOutFirstRow(0).size());
            } else {
                for (int j = 1; j < row.size(); j++) {
                    String str = row.get(j);
                    if (isNotTranslated(str, engString)) {
                        ret.addRow(row);
                        break;
                    }
                }
            }
        }
        return ret;
    }

    private boolean isNotTranslated(String str, String engString) {
        return (str == null || str.isEmpty() || "*".equals(str) || str.equals(engString) || str.equals("数据库未找到"));
    }

    public ByteArrayOutputStream excelToOtherZip(List<TableHolder> tableHolders, String template, String stringPrefix, String stringSuffix, FileType escapeType, FileType templateFitType, String outputFileNamePrefix, Set<String> languageLimit) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
        Escaper escaper = EscaperFactory.getEscaper(escapeType);
        return excelToOtherZip(tableHolders, template, stringPrefix, stringSuffix, escaper, templateFitType, outputFileNamePrefix, languageLimit);
    }

    public void mapToZip(ZipOutputStream zipOut, Map<String, String> textMap, String outputFileNamePrefix, String outputFileNameSuffix, Set<String> keyLimit) throws IOException, InvalidFormatException {
        Set<String> nameSet = new HashSet<>();
        for (Map.Entry<String, String> entry : textMap.entrySet()) {
            String lang = entry.getKey();
            String outputFileName = outputFileNamePrefix + lang + outputFileNameSuffix;
            Integer aNumber = 0;
            while (nameSet.contains(outputFileName)) {
                System.out.println("Duplicate file will not output,we will change the name:" + outputFileName);
                outputFileName = outputFileNamePrefix + lang + aNumber.toString() + outputFileNameSuffix;
                aNumber++;
            }
            nameSet.add(outputFileName);
            String outputString = entry.getValue();
            zipOut.putNextEntry(new ZipEntry(outputFileName));
            zipOut.write(outputString.getBytes("UTF-8"));  //这个地方一定要加字符集，因为getBytes字符集是根据系统而定的，这里必须写死
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


    public Map<String, String> excelToOthersMap(Set<String> languageLimit, TemplateHolder holder) throws IOException, InvalidFormatException {
        List<String> titles = holder.getFirstRowString();
        Map<String, String> ret = new HashMap<>();
        for (int i = 1; i < titles.size(); i++) {
            String lang = titles.get(i);
            if (languageLimit != null && !languageLimit.contains(lang)) continue;
            if (ret.containsKey(lang)) continue;
            String outputString = holder.getRepacedTemplate(i);
            ret.put(lang, outputString);
            System.out.println("translated:" + lang);
        }
        return ret;
    }

    private String faker(String str) {
        return str.replaceAll("[　*| *| *|//s*]*", "").toLowerCase();
    }

    public void fitRowTitleToTemplate(TableHolder tableHolder, String template, FileType templateType) throws ParserConfigurationException, SAXException, IOException {
        fitRowTitleToTemplate(tableHolder, template, templateType, null, null);
    }

    public void fitRowTitleToTemplate(TableHolder tableHolder, String template, FileType templateType, List<String> excelHaveNotList, Map<String, String> titleChangeLog) throws ParserConfigurationException, SAXException, IOException {
        List<String> rowTitles = tableHolder.getColStringWithOutFirstRow(0);
        List<String> fakeRowTitles = new ArrayList<>(rowTitles);
        for (int i = 0; i < fakeRowTitles.size(); i++) {
            String s = fakeRowTitles.get(i);
            s = faker(s);
            fakeRowTitles.set(i, s);
        }
        Map<String, Integer> strIndexMap = new HashMap<>();
        Map<String, Integer> fakeMap = new HashMap<>();
        for (int i = 0; i < rowTitles.size(); i++) {
            strIndexMap.put(rowTitles.get(i), i);
            String fakeTitle = fakeRowTitles.get(i);
            //TODO:因为有Sports,Sport就没加进去；因为有option，Option就没加进去；待修改
            if (fakeMap.containsKey(fakeTitle)) {
                Integer repeatIndex = fakeMap.get(fakeTitle);
                System.out.println("fake title repeat! I will only add the first into fakeMap, won't add " + i + " : " + rowTitles.get(i) +
                        "\t" + repeatIndex + " : " + rowTitles.get(repeatIndex));
            } else
                fakeMap.put(fakeRowTitles.get(i), i);
        }

        KeyValueFileHandler templateHandler = FileHandlerFactory.createHandler(template, templateType);
        List<String> templateTitles = new ArrayList<>(templateHandler.getKeyValueMap().values());
        Set<String> excelHaveNotSet = new HashSet<>();
        Map<String, String> tempTitleChangeLog = new HashMap<>();
        for (String title : templateTitles) {
            if (strIndexMap.containsKey(title)) continue;
            String fakeTitle = faker(title);
            if (fakeMap.containsKey(fakeTitle)) {
                Integer index = fakeMap.get(fakeTitle);
                String realTitle = rowTitles.get(index);
                System.out.println("can't find a sentence in excel but find one without space and lowercase. I will change the title in excel to fit template");
                System.out.println("excel: " + realTitle);
                System.out.println("templ: " + title);
                tempTitleChangeLog.put(realTitle, title);
                rowTitles.set(index, title);
            } else {
                //the sentence in template didn't found in excel
                excelHaveNotSet.add(title);
            }
        }
        tableHolder.setColumn(null, rowTitles, 0);
        if (excelHaveNotList != null) excelHaveNotList.addAll(excelHaveNotSet);
        if (titleChangeLog != null) titleChangeLog.putAll(tempTitleChangeLog);
    }

    public ByteArrayOutputStream buildTable(TableHolder tableHolder, List<String> colNames, List<Map<String, String>> cellMapList) throws IOException {
        Map<String, Integer> nameIndexMap = new HashMap<>();
        for (Integer i = 0; i < colNames.size(); i++) {
            nameIndexMap.put(colNames.get(i), i);
        }

        tableHolder.addRow(colNames);

        for (Map<String, String> colCellMap : cellMapList) {
            List<String> row = new ArrayList<>(colNames.size());
            for (Map.Entry<String, String> entry : colCellMap.entrySet()) {
                String colName = entry.getKey();
                Integer colIndex = nameIndexMap.get(colName);
                String CellValue = entry.getValue();
                row.add(colIndex, CellValue);
            }
            tableHolder.addRow(row);
        }

        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        tableHolder.write(bo);
        return bo;
    }
}
