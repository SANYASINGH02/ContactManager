package com.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class PageController {

    @RequestMapping("/home")
     public String home(Model model) {
        System.out.println("Home page handler - welcome");

        // sending data to view
        model.addAttribute("name", "Sanya Singh");
        model.addAttribute("email", "sanya@gmail.com");
        model.addAttribute("company", "Amazon");
        model.addAttribute("role", "SDE-1");
        model.addAttribute("linkedin", "https://www.linkedin.com/in/sanyasingh0209/");
        System.out.println("Home page Loading");
        return "home";
    }

    // about route
    @RequestMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        System.out.println("About Page Loading");
        return "about";
    }
    


    // services
       @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("Services Page Loading");
        return "services";
    }
}
