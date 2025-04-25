package Server.Auth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

public class LoginHandler {
    public static boolean authenticate(BufferedReader input,
                                       PrintWriter output, Properties properties) throws IOException {
        String loginAttempt = input.readLine();
        if(loginAttempt == null || !loginAttempt.startsWith("login|")) {
            output.println("(!)|Invalid login format.");
            return false;
        }

        String[] parts = loginAttempt.split("\\|");
        if(parts.length != 3) {
            output.println("(!)|Invalid login format.");
            return false;
        }

        String username = parts[1];
        String password = parts[2];

        if(!username.equals(properties.getProperty("login.user.username"))
                || !password.equals(properties.getProperty("login.user.password"))) {
            output.println("(-)|Invalid username or password.");
            return false;
        }

        output.println("(+)|Login successful.");
        return true;
    }
}
