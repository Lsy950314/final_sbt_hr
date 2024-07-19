package com.example.sbt_final_hr.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainPageController {

    @GetMapping("/index2")
    public String getMainPage (Model model) {
        return "mainPage/index";
    }
}
