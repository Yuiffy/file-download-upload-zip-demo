package com.dyf.webi18n;

import com.dyf.download.FileUploadController;
import com.dyf.download.storage.StorageService;
import com.dyf.i18n.excel.ExcelTableHolder;
import com.dyf.i18n.replace.template.NormalTemplateHolder;
import com.dyf.i18n.replace.template.TemplateHolder;
import com.dyf.i18n.service.FileConvertService;
import com.dyf.i18n.util.FileType;
import com.dyf.i18n.util.escaper.EscaperFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
}
