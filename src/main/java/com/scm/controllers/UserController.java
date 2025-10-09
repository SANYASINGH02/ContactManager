package com.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

  /** user dashboard page */

  @RequestMapping("/dashboard")
  public String userDashboard() {
    System.out.println("User Dashboard");
    return "user/dashboard";
  }

  /** user's profile page */
  @RequestMapping("/profile")
  public String userProfile(Model model, Authentication authentication) {
    System.out.println("User Profile");
    // addLoggedInUserInfo(model, authentication);

    return "user/profile";
  }

  /** add contacts page */

  /** view contacts page */

  /** edit contact page */

  /** delete contact */
}
