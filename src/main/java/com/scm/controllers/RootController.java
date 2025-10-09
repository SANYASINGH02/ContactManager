package com.scm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.scm.entities.User;
import com.scm.helpers.Helper;
import com.scm.services.UserService;

@ControllerAdvice
public class RootController {
    // methods inside this will execute for all the requests

    @Autowired
    private UserService userService;
    Logger logger = LoggerFactory.getLogger(RootController.class);

    @ModelAttribute // this will run for each routes of /user/ route
    public void addLoggedInUserInfo(Model model, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        logger.info("Adding logged in user information to the model");
        String username = Helper.getEmailOfLoggedInUser(authentication);
        logger.info("User logged in : {}", username);
        // get user details from db
        User user = userService.getUserByEmail(username).orElse(null);
        model.addAttribute("loggedInUser", user); // we've stored the details of logged-in user in model from UI
    }
}
