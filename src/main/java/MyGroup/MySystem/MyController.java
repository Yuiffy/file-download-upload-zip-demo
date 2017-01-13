package MyGroup.MySystem;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;

@Controller
public class MyController {
    @RequestMapping("/")
    String onePage() {
        return "indexpage";
    }

    @RequestMapping("/body")
    @ResponseBody
    String bodyTest() {
        return "responseBody!返回博迪！";
    }

    @RequestMapping("/testAction")
    String testAction(@RequestParam("numA") int x, @RequestParam("numB") int y, Model model) {
        model.addAttribute("numC", x + y);
        return "onepage";
    }
}
