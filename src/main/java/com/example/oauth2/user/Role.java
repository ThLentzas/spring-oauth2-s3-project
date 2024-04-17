package com.example.oauth2.user;

/*
    USER role is only assigned to the users that registered via username-password and not activated their account yet.
 */
public enum Role {
    ROLE_USER,
    ROLE_VERIFIED
}
