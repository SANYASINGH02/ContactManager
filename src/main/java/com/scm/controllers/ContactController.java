package com.scm.controllers;

import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.forms.ContactForm;
import com.scm.forms.ContactSearchForm;
import com.scm.helpers.AppConstants;
import com.scm.helpers.Helper;
import com.scm.helpers.Message;
import com.scm.helpers.MessageType;
import com.scm.services.ContactService;
import com.scm.services.ImageService;
import com.scm.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    // add contact page/view: Handler, bydefault it is GET method
    @RequestMapping("/add")
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();
        contactForm.setName("Sanya");
        contactForm.setEmail("sanya@gmail.com");
        contactForm.setAddress("telibagh");
        contactForm.setPhoneNumber("1234567890");
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    // we're sending contact data to server using POST method (ref- add_contact form
    // field method=post).
    // So this saveContact function need to be POST
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session) {
        System.out.println(contactForm);

        /* process the form data */

        // 1. validate the form
        if (result.hasErrors()) {
            result.getAllErrors().forEach(error -> logger.info(error.getDefaultMessage()));
            session.setAttribute("message",
                    Message.builder()
                            .content("Please correct the following errors")
                            .type(MessageType.red)
                            .build());
            return "user/add_contact";
        }

        // 2. convert the ContactForm to Contact entity because we have received
        // ContactForm & we've to save Contact
        // form -> Contact

        // 2.1 processing User of the Contact

        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username).orElse(null);

        // 2.2 process the contact picture/image
        String fileUrl = null;
        String fileName = null;

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            logger.info("file information : {}", contactForm.getContactImage().getOriginalFilename());
            fileName = UUID.randomUUID().toString();
            fileUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
        }

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
        // set profile pic & its public id
        contact.setPicture(fileUrl);
        contact.setCloudinaryImagePublicId(fileName);

        // save the Contact
        contactService.save(contact);
        logger.info("Saving Contact......{}", contact);
        // set message to be displayed on the view
        session.setAttribute("message",
                Message.builder()
                        .content("You have successfully added a new contact")
                        .type(MessageType.green)
                        .build());
        return "redirect:/user/contacts/add";
    }

    @RequestMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication) {
        // example of request url :
        // http://localhost:8081/user/contacts?size=2&page=2&sortBy=email

        // load all the logged-in user's contacts
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username).orElse(null);
        Page<Contact> contactsPage = contactService.getByUser(user, page, size, sortBy, direction);
        model.addAttribute("contactsPage", contactsPage);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm", new ContactSearchForm());
        return "user/contacts";
    }

    // search handler
    @RequestMapping("/search")
    public String searchHandler(
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication
    ) {
        String field = contactSearchForm.getField();
        String keyword = contactSearchForm.getValue();
        logger.info("Search field {} and keyword {}", field, keyword);

        User user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication)).orElse(null);
        Page<Contact> contactsPage = null;
        if(field.equalsIgnoreCase("name")) {
            contactsPage = contactService.searchByName(keyword, size, page, sortBy, direction, user);
        } else if(field.equalsIgnoreCase("email")) {
            contactsPage = contactService.searchByEmail(keyword, size, page, sortBy, direction, user);
        } else {
            contactsPage = contactService.searchByPhoneNumber(keyword, size, page, sortBy, direction, user);
        }
        model.addAttribute("contactsPage", contactsPage);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm", contactSearchForm);
        return "user/search";
    }


    // delete contact handler
    @RequestMapping("/delete/{contactId}")
    public String deleteContactHandler(
            @PathVariable String contactId,
            HttpSession session,
            Authentication authentication
    ) {
        /*
        TODO: delete the image from cloudinary as well
        // remove the image from cloudinary
        // get the contact by id
        Contact contact = contactService.getById(contactId);
        // delete the image from cloudinary
        imageService.deleteImage(contact.getCloudinaryImagePublicId());
        */

        // delete the contact
        contactService.delete(contactId);

        session.setAttribute("message",
                Message.builder()
                        .content("Contact deleted successfully")
                        .type(MessageType.green)
                        .build());
        return "redirect:/user/contacts";
    }

    // update contact form view
    // TODO: set better naming convention for path url (view)
    @GetMapping("/view/{contactId}")
    public String updateContactFormView(
            @PathVariable String contactId,
            Model model
    ) {
        Contact contact = contactService.getById(contactId);
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setAddress(contact.getAddress());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedinLink(contact.getLinkedinLink());
        contact.setPicture(contact.getPicture());

        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);
        return "user/update_contact_view";
    }

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContactHandler(
            @PathVariable String contactId,
            @Valid @ModelAttribute ContactForm contactForm,
            Model model,
            BindingResult result,
            HttpSession session,
            Authentication authentication
    ) {
        // 1. validate the form
        if(result.hasErrors()) {
            result.getAllErrors().forEach(error -> logger.info(error.getDefaultMessage()));
            session.setAttribute("message",
                    Message.builder()
                            .content("Please correct the following errors")
                            .type(MessageType.red)
                            .build());
            return "user/update_contact_view";
        }

        // 2. update the contact details
        Contact contact = contactService.getById(contactId);
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setAddress(contactForm.getAddress());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedinLink(contactForm.getLinkedinLink());

        // 3. update the image if present
        if(contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            logger.info("file information : {}", contactForm.getContactImage().getOriginalFilename());
            String fileName = UUID.randomUUID().toString();
            String fileUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            contact.setPicture(fileUrl);
            contact.setCloudinaryImagePublicId(fileName);
            contactForm.setPicture(fileUrl);
        }

        var updatedContact = contactService.update(contact);
        logger.info("Updated Contact {}", updatedContact);
        model.addAttribute("contact", updatedContact);
        session.setAttribute("message",
                Message.builder()
                        .content("Contact updated successfully")
                        .type(MessageType.green)
                        .build());
        return "redirect:/user/contacts/view/" + contactId;
    }

    // export all contacts to Excel
    // previously used way for export was only exporting the current page contacts and not the all contacts(from all pages)
    @GetMapping("/export")
    public void exportContacts(
            Authentication authentication,
            HttpServletResponse response
    ) throws Exception {
        String username = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username).orElse(null);
        
        List<Contact> contacts = contactService.getByUser(user);
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Contacts");
        
        // Header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Email");
        headerRow.createCell(2).setCellValue("Phone");
        headerRow.createCell(3).setCellValue("Address");
        headerRow.createCell(4).setCellValue("Description");
        headerRow.createCell(5).setCellValue("Website");
        headerRow.createCell(6).setCellValue("LinkedIn");
        
        // Data rows
        int rowNum = 1;
        for (Contact contact : contacts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(contact.getName());
            row.createCell(1).setCellValue(contact.getEmail());
            row.createCell(2).setCellValue(contact.getPhoneNumber());
            row.createCell(3).setCellValue(contact.getAddress());
            row.createCell(4).setCellValue(contact.getDescription());
            row.createCell(5).setCellValue(contact.getWebsiteLink());
            row.createCell(6).setCellValue(contact.getLinkedinLink());
        }
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=contacts.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
