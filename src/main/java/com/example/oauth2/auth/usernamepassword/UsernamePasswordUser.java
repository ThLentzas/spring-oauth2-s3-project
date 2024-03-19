package com.example.oauth2.auth.usernamepassword;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import com.example.oauth2.entity.User;

/*
    The reason why UsernamePasswordUser DOES NOT HAVE to implement Serializable, despite the fact that its part of
    the Authentication object of the Security Context that is stored in Redis as the value of
    the SPRING_SECURITY_CONTEXT KEY, is because UserDetails DOES. The Authentication object itself implements
    Serializable as well
 */
public record UsernamePasswordUser(User user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(user.getRole().toString());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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
        return true;
    }
}
