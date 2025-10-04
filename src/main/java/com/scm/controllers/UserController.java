package com.scm.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user")
public class UserController {

    /** dashboard page */

    @RequestMapping("/dashboard")
    public String userDashboard() {
        System.out.println("User Dashboard");
          return "user/dashboard";
    }

    /** user's profile page */
     @RequestMapping("/profile")
    public String userProfiled() {
        System.out.println("User Profile");
          return "user/profile";
    }
   

    /** add contacts page */

    /** view contacts page */

    /** edit contact page */

    /** delete contact */
}
