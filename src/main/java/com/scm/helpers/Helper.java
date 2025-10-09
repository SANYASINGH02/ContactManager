package com.scm.helpers;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper {

    public static String getEmailOfLoggedInUser(Authentication authentication) {
        // Principal principal = authentication.getPrincipal();
        if (authentication.getPrincipal() instanceof OAuth2AuthenticatedPrincipal) {
            var clientId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
            var oauth2User = (OAuth2User) authentication.getPrincipal();
            String username = "";
            if (clientId.equalsIgnoreCase("google")) {
                System.out.println("Getting email from google");

                username = oauth2User.getAttribute("email");

            } else if (clientId.equalsIgnoreCase("github")) {
                System.out.println("Getting email from github");
                username = oauth2User.getAttribute("email") != null ? oauth2User.getAttribute("email").toString()
                        : oauth2User.getAttribute("login").toString() + "@gmail.com";
            }
            return username;
        } else {
            System.out.println("Getting email from local database");
            return authentication.getName(); // Authentication extends Principal and Principal has getName()
                                             // method,that's why it's working otherwise we would use below way
            // return ((DefaultOAuth2User) authentication.getPrincipal()).getName();
        }
    }
}
