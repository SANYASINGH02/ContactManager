package com.scm.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.scm.entities.User;
import com.scm.forms.UserForm;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

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

    // about
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

    // contact
    @RequestMapping("/contact")
    public String contactPage() {
        System.out.println("Contact Page Loading");
        return "contact";
    }

    // login
    @RequestMapping("/login")
    public String loginPage() {
        System.out.println("Login Page Loading");
        return "login";
    }

    // register
    @RequestMapping("/register")
    public String registerPage(Model model) {
        System.out.println("Register Page Loading");
        UserForm userForm = new UserForm(); // we are sending blank object(can also send default data) to register page
                                            // through model
        // userForm.setName("Sanya");
        userForm.setEmail("sanya@gmail.com"); // default values/data in the form
        userForm.setAbout("Hello");
        userForm.setPassword("12345");
        userForm.setPhoneNumber("67890");

        model.addAttribute("userForm", userForm);
        return "register";
    }

    // processing register/signUp
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult,
            HttpSession session) {
        System.out.println("Processing Registeration.....");

        /** fetch the form data (UserFornm) */
        System.out.println(userForm);

        /** validate the form data */
        if (rBindingResult.hasErrors()) {
            return "register";
        }

        /** save data to database - to save we need userService */
        // UserForm -> User
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setPassword(userForm.getPassword());
        user.setProfilePic("/Users/ssanyaa/Downloads/default-pfp.jpg");

        userService.saveUser(user);
        System.out.println("User saved");

        /** message = "Registration Successful" */
        Message message = Message.builder()
                .content("Registration Successful<br>Account verification link is sent to your registered email")
                .type(MessageType.green)
                .build();
        // add the message
        session.setAttribute("message", message);

        /** redirect to login page */
        return "redirect:/register";
    }
}
