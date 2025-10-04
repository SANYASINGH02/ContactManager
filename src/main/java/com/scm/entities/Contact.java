package com.scm.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "contact") 
@Table(name = "contact") 
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class Contact {

    @Id
private String contactId;
private String name;
private String email;
private String phoneNumber;
private String address;
private String picture;
@Column(length = 1000)
private String dsecription;
private boolean favorite = false;

@OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
// fetch = FetchType.EAGER means when we fetch contact details then social link should also got fetched
private List<SocialLink> socialLinks = new ArrayList<>();

@ManyToOne
private User user;

}
