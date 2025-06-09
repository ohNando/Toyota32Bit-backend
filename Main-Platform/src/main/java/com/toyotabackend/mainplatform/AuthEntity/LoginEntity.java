package com.toyotabackend.mainplatform.AuthEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents user login credentials with a username and password.
 * Used for authentication purposes.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginEntity {

    /**
     * Username of the user attempting to log in.
     */
    private String username;

    /**
     * Password of the user attempting to log in.
     */
    private String password;
}
