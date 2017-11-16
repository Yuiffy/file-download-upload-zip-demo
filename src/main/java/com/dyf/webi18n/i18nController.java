package com.dyf.webi18n;

import com.dyf.download.FileUploadController;
import com.dyf.download.storage.StorageService;
import com.dyf.i18n.service.FileConvertService;
import com.dyf.i18n.service.TableMergeService;
import com.dyf.i18n.service.ToolService;
import com.dyf.i18n.table.ExcelTableHolder;
import com.dyf.i18n.table.TableHolder;
import com.dyf.i18n.util.FileType;
import com.dyf.i18n.util.escaper.EscaperFactory;
import net.sf.json.JSONObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuiff on 2017/1/17.
 */
@Controller
@RequestMapping("/i18n")
public class i18nController {
    @Autowired
    StorageService storageService;


    @PostMapping("")
    public String handleFileUpload(MultipartFile file, MultipartFile file2,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file);
        storageService.store(file2);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + " AND " + file2.getOriginalFilename() + "!");

        return "redirect:/i18n";
    }


    @GetMapping("")
    public String listUploadedFiles(Model model) throws IOException {

        List<Path> paths = storageService
                .loadAll();
        List<String> urls = new ArrayList<>(paths.size());
        List<String> names = new ArrayList<>(paths.size());
        for (Path path : paths) {
            urls.add(MvcUriComponentsBuilder
                    .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                    .build().encode().toString());
            names.add(path.getFileName().toString());
        }

        List fileAndNames = new ArrayList<Map<String, String>>(urls.size());
        for (int i = 0; i < urls.size(); i++) {
            Map<String, String> mp = new HashMap<>();
            mp.put("url", (String) urls.get(i));
            mp.put("name", (String) names.get(i));
            fileAndNames.add(mp);
        }
        model.addAttribute("files", fileAndNames);
        return "upload/upload2";
    }

    @PostMapping("/excel2others.zip")
    @ResponseBody
    public byte[] excel2othersPost(MultipartFile file, MultipartFile file2, FileType fileType, String prefix, String suffix, String outfilePrefix,
                                   RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException {
        String template = new String(file2.getBytes());

        FileConvertService convertService = new FileConvertService();
        ByteArrayOutputStream out = convertService.excelToOtherZip(new ExcelTableHolder(file.getInputStream()), template, prefix, suffix, EscaperFactory.getEscaper(fileType), outfilePrefix);

        return out.toByteArray();
    }


    @GetMapping("/excel2others")
    public String excel2others(Model model) throws IOException {
        return "upload/excel2others";
    }

    @GetMapping("/others2excel")
    public String others2excel(Model model) throws IOException {
        return "upload/others2excel";
    }

    @PostMapping("/others2excel.xls")
    @ResponseBody
    public byte[] others2excelPost(MultipartFile[] files, FileType fileType,
                                   RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
        List<String> listString = new ArrayList<>();
        List<String> listName = new ArrayList<>();
        for (MultipartFile file : files) {
            String str = new String(file.getBytes(), ("UTF-8"));
            listString.add(str);
            String name = file.getOriginalFilename();
            //去除文件扩展名
            listName.add(name.substring(0, name.lastIndexOf(".")));
        }
        FileConvertService convertService = new FileConvertService();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        convertService.manyOtherToOneExcelFile(listString, listName, fileType, out);
        return out.toByteArray();
    }


    @GetMapping("/multiexcel")
    public String multiexcel2others() {
        return "upload/multiupload";
    }

    @PostMapping("/multiexcel.zip")
    @ResponseBody
    public byte[] multiexcel2othersPost(MultipartFile[] files, MultipartFile file2, FileType escapeType, FileType templateType, String prefix, String suffix, String outfilePrefix,
                                        RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
        String template = new String(file2.getBytes());
//        System.out.println("template:\n"+template);
//        template = new XmlFileHandler(template).getString();
//        System.out.println("template2:\n"+template);

        FileConvertService convertService = new FileConvertService();
        List<TableHolder> tableHolders = new ArrayList<>();
        for (int i = 0; i < files.length; i++)
            tableHolders.add(new ExcelTableHolder(files[i].getInputStream()));
        ByteArrayOutputStream out = convertService.excelToOtherZip(tableHolders, template, prefix, suffix, EscaperFactory.getEscaper(escapeType), templateType, outfilePrefix, null);

        return out.toByteArray();
    }

    @GetMapping("/xmls2excel")
    public String xmls2excel() {
        return "upload/xmls2excel";
    }

    @GetMapping("/simple")
    public String testexcel2simplejson() {
        return "upload/test/simple";
    }

    @PostMapping("/testexcel2simplejson.json")
    @ResponseBody
    public String testexcel2simplejson(MultipartFile excelFile) throws Exception {
        JSONObject json = excelToSimpleJson(excelFile);
        return json.toString(2);
    }

    public JSONObject excelToSimpleJson(MultipartFile excelFile) throws Exception {
        JSONObject json = new JSONObject();
        if (excelFile == null) {
            json.put("msg", "FAIL!");
            return json;
        } else {
            TableHolder tableHolder = new ExcelTableHolder(excelFile.getInputStream());
            Map<String, Object> table = new HashMap<>();
            for (Integer sheetCount = 0; sheetCount <= 0; sheetCount++) {
                List<String> engStrings = tableHolder
                        .getColStringWithOutFirstRow(0);
                List<String> langs = tableHolder.getFirstRowString();
                List<Map<String, String>> rows = new ArrayList<>();
                for (int rowNum = 0; rowNum < engStrings.size(); rowNum++) {
                    List<String> row = tableHolder.getRowString(1 + rowNum);
                    Map<String, String> jsRow = new HashMap<>();
                    jsRow.put("engstring", row.get(0));
                    for (int i = 1; i < row.size(); i++) {
                        jsRow.put(langs.get(i), row.get(i));
                    }
                    rows.add(jsRow);
                }
                Map<String, Object> thisSheet = new HashMap<>();
                thisSheet.put("rows", rows);
                thisSheet.put("language", langs.subList(1, langs.size()));
                table.put("sheet" + sheetCount, thisSheet);
            }

            json.put("msg", "SUCC!");
            json.put("data", table);
            System.out.println("JSON! " + json.toString());
        }

        return json;
    }


    @GetMapping("/excelmerge")
    public String excelMerge() {
        return "upload/excelMerge";
    }

    @PostMapping("/excelmerge.zip")
    @ResponseBody
    public byte[] excelMergePost(MultipartFile file1, MultipartFile file2, FileType escapeType, FileType templateType, String prefix, String suffix, String outfilePrefix,
                                 RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
//        System.out.println("template:\n"+template);
//        template = new XmlFileHandler(template).getString();
//        System.out.println("template2:\n"+template);
        TableHolder mainHolder = new ExcelTableHolder(file2.getInputStream());
        TableHolder littleHolder = new ExcelTableHolder(file1.getInputStream());
        TableMergeService service = new TableMergeService();
//        List<TableHolder> tableHolders = new ArrayList<>();
//        for (int i = 0; i < files.length; i++)
//            tableHolders.add(new ExcelTableHolder(files[i].getInputStream()));
//        ByteArrayOutputStream out = convertService.excelToOtherZip(tableHolders, template, prefix, suffix, EscaperFactory.getEscaper(escapeType), templateType, outfilePrefix, null);
        ByteArrayOutputStream out = service.mergeLittleTableIntoMainTableZipWithTip(littleHolder, mainHolder);
        return out.toByteArray();
    }

    @GetMapping("/excelrenametitle")
    public String excelRenameTitle() {
        return "upload/excelRenameTitle";
    }

    @PostMapping("/renamedExcel.xls")
    @ResponseBody
    public byte[] renamedExcel(MultipartFile file1, MultipartFile file2, FileType escapeType, FileType templateType, String prefix, String suffix, String outfilePrefix,
                               RedirectAttributes redirectAttributes) throws IOException, InvalidFormatException, ParserConfigurationException, SAXException {
        TableHolder mainHolder = new ExcelTableHolder(file1.getInputStream());
        TableHolder nameHolder = new ExcelTableHolder(file2.getInputStream());
        ToolService service = new ToolService();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.renameTitleNameByExcel(mainHolder, nameHolder);
        mainHolder.write(out);
        return out.toByteArray();
    }
}
