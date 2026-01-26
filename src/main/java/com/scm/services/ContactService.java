package com.scm.services;

import com.scm.entities.Contact;
import com.scm.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ContactService {
    // save contact
    Contact save(Contact contact);

    // update contact
    Contact update(Contact contact);

    // get all contacts
    List<Contact> getAll();

    // get a contact by id
    Contact getById(String id);

    // delete contact
    void delete(String id);


    // get contacts by user id
    List<Contact> getByUserId(String userId);

    // get contacts by User
    Page<Contact> getByUser(User user, int page, int size, String sortField, String sortDirection);

    // search contacts by name
    Page<Contact> searchByName(String nameKeyword, int size, int page, String sortBy, String order, User user);

    // search contacts by email
    Page<Contact> searchByEmail(String emailKeyword, int size, int page, String sortBy, String order, User user);

    // search contacts by phone number
    Page<Contact> searchByPhoneNumber(String phoneNumberKeyword, int size, int page, String sortBy, String order, User user);
}
