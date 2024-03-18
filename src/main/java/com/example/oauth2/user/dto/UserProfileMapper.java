package com.example.oauth2.user.dto;

import com.example.oauth2.entity.User;
import com.example.oauth2.socialaccount.dto.SocialAccountDTOMapper;

import java.util.function.Function;
import java.util.stream.Collectors;

public class UserProfileMapper implements Function<User, UserProfile> {
    private final SocialAccountDTOMapper socialAccountDTOMapper = new SocialAccountDTOMapper();

    @Override
    public UserProfile apply(User user) {
        return new UserProfile(
                user.getName(),
                user.getSocialAccounts().stream()
                        .map(socialAccountDTOMapper)
                        .collect(Collectors.toSet())
        );
    }
}
