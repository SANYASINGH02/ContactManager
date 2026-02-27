package com.scm.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "user") // by default entity name would be user i.e. class name
@Table(name = "users") // by default table name would be user i.e. class name
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    // by adding `implements UserDetails` , now User became UserDetails i.e.
    // wherever we need to use UserDetails - we can use User
    @Id
    private String userId;
    @Column(name = "user_name", nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    @Column(length = 1000)
    private String about;
    @Column(length = 5000)
    private String profilePic;
    private String phoneNumber;

    // @Getter(value = AccessLevel.NONE) - by adding this to a field, get method for
    // particular field will not be generated automatically

    // information---
    // private String gender;
    private boolean enabled = false;
    private boolean emailVerified = true;
    private boolean phoneVerified = false;

    @Enumerated(value = EnumType.STRING)
    // SELF, GOOGLE, FACEBOOK, GITHUB, TWITTER, LINKEDIN
    private Providers provider = Providers.SELF;
    private String providerUserId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    // fetch = FetchType.LAZY means that we wouldn't run query in DB until we need
    // contacts for a user
    // orphanRemoval = true means if a user is deleted then all its contacts will
    // also be deleted
    private List<Contact> contacts = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roleList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // converted the List of roles(USER, ADMIN) to the Collections of
        // SimpleGrantedAuthority[roles{USER,ADMIN}]
        Collection<SimpleGrantedAuthority> roles = roleList.stream().map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
        return roles;
    }

    @Override
    public String getUsername() {
        return this.email; // we're considering email id as username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /*
     * UserDetails also have getPassword() method which is unimplemented but not
     * added here automatically -> this is
     * because we've a field named 'password' and we've added @Getter to this USer
     * class so getPassword() is
     * automatically implemented, similar for other fields.
     */

}
