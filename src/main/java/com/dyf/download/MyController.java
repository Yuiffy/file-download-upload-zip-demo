package com.dyf.download;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
