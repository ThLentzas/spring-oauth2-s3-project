package com.example.oauth2.auth.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.example.oauth2.entity.User;

/*
    The reason why SSOUser has to implement Serializable is, because its part of the Authentication object(Principal) of
    the Security Context that is stored in Redis as the value of the SPRING_SECURITY_CONTEXT KEY. The Authentication
    object itself implements Serializable as well
 */
record SSOUser(User user) implements OidcUser, Serializable {

    @Override
    public Map<String, Object> getClaims() {
        return Collections.emptyMap();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(user.getRole().toString());
    }

    @Override
    public String getName() {
        return this.user.getName();
    }
}
