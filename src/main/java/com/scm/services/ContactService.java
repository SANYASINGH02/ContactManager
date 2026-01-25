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

    // search contacts
    List<Contact> search(String name, String email, String phoneNumber);

    // get contacts by user id
    List<Contact> getByUserId(String userId);

    // get contacts by User
    Page<Contact> getByUser(User user, int page, int size, String sortField, String sortDirection);
}
