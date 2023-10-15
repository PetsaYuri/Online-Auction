package com.OnlineAuction.Config;


import com.OnlineAuction.Exceptions.Users.UserIsBannedException;
import com.OnlineAuction.Models.User;
import com.OnlineAuction.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomAuthProvider implements AuthenticationProvider {

    private final UserServices userServices;

    @Autowired
    public CustomAuthProvider(UserServices userServices) {
        this.userServices = userServices;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = userServices.getUserByEmail(authentication.getName());
        BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();

        if (!bCrypt.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new BadCredentialsException("Password not match");
        }

        if (user.isBlocked()) {
            throw new UserIsBannedException();
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("user")
                .build();
        return new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
