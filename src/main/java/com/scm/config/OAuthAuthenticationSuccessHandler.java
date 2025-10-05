package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // component - so that bean of this class is available
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthAuthenticationSuccessHandler");

        /** save user data in database */

        // fetch user details from login
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        logger.info("UserName: {}", oAuth2User.getName());
        oAuth2User.getAttributes().forEach((key, value) -> {
            logger.info("{} --> {}", key, value);
        });
        logger.info("Authorities: {}", oAuth2User.getAuthorities().toString());

        // create a new user and set its values according to login info
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setProviderUserId(oAuth2User.getName());
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setPassword("dummy");

        /** Identify the Provider - so that we can fetch accurate attributes */
        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        logger.info("OAuth2ClientRegistrationId: {}", authorizedClientRegistrationId); // google or GITHUB

        /** Google Provider */
        if (authorizedClientRegistrationId.equalsIgnoreCase("google")) {
            user.setEmail(oAuth2User.getAttribute("email").toString());
            user.setName(oAuth2User.getAttribute("name").toString());
            user.setProfilePic(oAuth2User.getAttribute("picture").toString());
            user.setAbout("This account is created using google");
            user.setProvider(Providers.GOOGLE);
        }
        /** GITHUB Provider */
        else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            /*
             * CHECK THIS LATER, WHY WE'RE DOING IN THIS WAY for email BCZ IT'S NOT
             * NECESSARY THAT
             * login+@gmail.com is the real email id for that user like my
             * login=SANYASINGH02 but email is singhsanya002@gmail.com
             */
            String email = oAuth2User.getAttribute("email") != null ? oAuth2User.getAttribute("email").toString()
                    : oAuth2User.getAttribute("login").toString() + "@gmail.com";
            user.setEmail(email);

            String name = oAuth2User.getAttribute("login").toString();
            String picture = oAuth2User.getAttribute("avatar_url").toString();

            user.setEmail(email);
            user.setName(name);
            user.setProfilePic(picture);
            user.setAbout("This account is created using github");
            user.setProvider(Providers.GITHUB);

        } else {
            logger.info("OAuthAuthenticationSuccessHandler : Unknown Provider");
        }

        // save user
        User userToBeSave = userRepo.findByEmail(user.getEmail()).orElse(null);
        if (userToBeSave == null) {
            userRepo.save(user);
            logger.info("User Saved in DB");
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }

}
