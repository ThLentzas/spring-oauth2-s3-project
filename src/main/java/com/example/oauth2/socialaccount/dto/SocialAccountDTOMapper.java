package com.example.oauth2.socialaccount.dto;

import com.example.oauth2.entity.SocialAccount;

import java.util.function.Function;

public class SocialAccountDTOMapper implements Function<SocialAccount, SocialAccountDTO> {

    @Override
    public SocialAccountDTO apply(SocialAccount socialAccount) {
        return new SocialAccountDTO(
                socialAccount.getId(),
                socialAccount.getAccountLink()
        );
    }
}
