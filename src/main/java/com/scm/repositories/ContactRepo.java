package com.scm.repositories;

import com.scm.entities.Contact;
import com.scm.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {

    /* CUSTOM FINDER METHOD */
    // find the list of contacts by user -  this method we get bydefault bcz Contact entity has "user" field
    Page<Contact> findByUser(User user, Pageable pageable);

    // /* CUSTOM QUERY METHOD */
    // // This method can't be get by default bcz Contact entity doesn't have any "userId" field, so we need to write the logic
     @Query("SELECT c FROM contact c WHERE c.user.userId = :userId")
     List<Contact> findByUserId(@Param("userId") String userId);
}
