package com.scm.controllers;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.helpers.Helper;
import com.scm.services.ContactService;
import com.scm.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Optional;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {
    
    @Autowired
    ContactService contactService;

    @Autowired
    UserService userService;

    // add contact page/view: Handler, bydefault it is GET method
    @RequestMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        contactForm.setName("Anurag Tiwari");
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    // we're sending contact data to server using POST method (ref- add_contact form field method=post).
    // So this saveContact function need to be POST
    @RequestMapping(value="/add", method = RequestMethod.POST)
    public String saveContact(@ModelAttribute ContactForm contactForm, Authentication authentication) {
        System.out.println(contactForm);
        
        /* process the form data */

        // TODO: validate the form

        // convert the ContactForm to Contact entity because we have received ContactForm & we've to save Contact
        // form -> Contact
        // processing User of the Contact
         String username = Helper.getEmailOfLoggedInUser(authentication);
         User user = userService.getUserByEmail(username).orElse(null);

        // TODO: process the contact profile pic
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setAddress(contactForm.getAddress());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setUser(user);
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedinLink(contactForm.getLinkedinLink());
        // TODO: set profile pic

        // save the Contact
        contactService.save(contact);
        System.out.println("Saving Contact......");
        // TODO: set message to be displayed on the view
        return "redirect:/user/profile";
    }
}
