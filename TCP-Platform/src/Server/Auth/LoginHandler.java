package Server.Auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

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
    public static boolean authenticate(BufferedReader input,
                                       PrintWriter output, Properties properties) throws IOException {
        String loginAttempt = input.readLine();

        if (loginAttempt == null || !loginAttempt.startsWith("login|")) {
            output.println("(!)|Invalid login format.");
            return false;
        }

        String[] parts = loginAttempt.split("\\|");
        if (parts.length != 3) {
            output.println("(!)|Invalid login format.");
            return false;
        }

        String username = parts[1];
        String password = parts[2];

        if (!username.equals(properties.getProperty("login.user.username"))
                || !password.equals(properties.getProperty("login.user.password"))) {
            output.println("(-)|Invalid username or password.");
            return false;
        }

        output.println("(+)|Login successful.");
        return true;
    }
}
