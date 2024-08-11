package com.example.oauth2.user;

import com.example.oauth2.entity.User;
import com.example.oauth2.socialaccount.dto.SocialAccountDTOMapper;
import com.example.oauth2.user.dto.UserProfile;

import java.util.function.Function;

public class UserProfileMapper implements Function<User, UserProfile> {
    private final SocialAccountDTOMapper socialAccountDTOMapper = new SocialAccountDTOMapper();

    @Override
    public UserProfile apply(User user) {
        return new UserProfile(
                user.getName(),
                user.getSocialAccounts().stream()
                        .map(socialAccountDTOMapper)
                        .toList()
        );
    }
}
