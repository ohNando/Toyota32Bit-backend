package main.Auth;

import main.User.User;

import java.io.IOException;

/**
 * Handles user login authentication.
 */
public class LoginHandler {

    /**
     * Authenticates the user based on the provided login attempt.
     *
     * <p>Expects the login format "login|<username>|<password>". If the format is
     * invalid or the username/password does not match the values in the properties,
     * it returns an error. Otherwise, it returns a success message.</p>
     *
     * @param input The input stream to read the login attempt.
     * @param output The output stream to send authentication results.
     * @param properties The properties containing the valid username and password.
     * @return {@code true} if authentication is successful, {@code false} otherwise.
     * @throws IOException If an I/O error occurs.
     */
    public static boolean authenticate(String szLine) throws IOException {
        User adminUser = new User("admin","12345");

        String[] parts = szLine.split("\\|");
        if (parts.length != 3) {
            return false;
        }

        String username = parts[1];
        String password = parts[2];

        return username.equals(adminUser.getUsername()) && password.equals(adminUser.getPassword());
    }
}
