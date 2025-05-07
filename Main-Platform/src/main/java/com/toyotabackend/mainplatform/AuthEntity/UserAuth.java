package com.toyotabackend.mainplatform.AuthEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the result of a user authentication attempt.
 * 
 * Contains status information and an optional message.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAuth {

    /**
     * Authentication status (e.g., "success", "fail").
     */
    private String status;

    /**
     * Additional message about the authentication result.
     */
    private String message;
}
