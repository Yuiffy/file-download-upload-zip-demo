package com.dyf.webi18n;

import com.dyf.download.FileUploadController;
import com.dyf.download.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String handleFileUpload(MultipartFile file,MultipartFile file2,
                                   RedirectAttributes redirectAttributes) {

        storageService.store(file);
        storageService.store(file2);

        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename()+" AND "+file2.getOriginalFilename() + "!");

        return "redirect:/i18n";
    }


    @GetMapping("")
    public String listUploadedFiles(Model model) throws IOException {

        List<Path> paths = storageService
                .loadAll();
        List<String> urls = new ArrayList<>(paths.size());
        List<String> names = new ArrayList<>(paths.size());
        for(Path path:paths){
            urls.add(MvcUriComponentsBuilder
                    .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                    .build().encode().toString());
            names.add(path.getFileName().toString());
        }

        List fileAndNames = new ArrayList<Map<String,String>>(urls.size());
        for(int i=0; i<urls.size(); i++){
            Map<String,String> mp = new HashMap<>();
            mp.put("url",(String)urls.get(i));
            mp.put("name",(String)names.get(i));
            fileAndNames.add(mp);
        }
        model.addAttribute("files", fileAndNames);
        return "upload/upload2";
    }
}
