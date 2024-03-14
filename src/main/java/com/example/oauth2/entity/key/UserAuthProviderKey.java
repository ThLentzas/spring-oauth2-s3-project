package com.example.oauth2.entity.key;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode(of = {"userId", "authProviderId"})
public class UserAuthProviderKey implements Serializable {
    private Long userId;
    private Long authProviderId;

    public UserAuthProviderKey() {
    }

    public UserAuthProviderKey(Long userId, Long authProviderId) {
        this.userId = userId;
        this.authProviderId = authProviderId;
    }
}
