package com.OnlineAuction.Config;


import com.OnlineAuction.Exceptions.User.UserIsBannedException;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Services.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthProvider implements AuthenticationProvider {

    private final UserService userService;

    @Autowired
    public CustomAuthProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = userService.getUserByEmail(authentication.getName());
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();

        if (user == null) {
            throw new BadCredentialsException("Email doesn't exist");
        }

        if (!bCrypt.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("Password not match");
        }

        if (user.isBlocked()) {
            throw new UserIsBannedException();
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        return newAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
