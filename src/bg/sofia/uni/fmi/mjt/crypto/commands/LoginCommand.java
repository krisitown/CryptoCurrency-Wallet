package bg.sofia.uni.fmi.mjt.crypto.commands;

import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.services.security.Hasher;

public class LoginCommand implements Command {
    private final static String USER_ALREADY_LOGGED_IN = "A user is already logged in.";
    private final static String INVALID_CREDENTIALS = "Invalid credentials.";
    private final static String SUCCESS = "Successfully logged in.";

    private String username;
    private String password;
    private UserService state;
    private Hasher hasher;
    private RequestHandler handler;

    public LoginCommand(String username, String password, UserService userService, RequestHandler handler, Hasher hasher) {
        this.username = username;
        this.password = password;
        this.state = userService;
        this.hasher = hasher;
        this.handler = handler;
    }

    @Override
    public String execute() {
        if (this.handler.getPrincipal() != null) {
            return USER_ALREADY_LOGGED_IN;
        }

        if (!this.state.getRegisteredUsers().containsKey(username)) {
            return INVALID_CREDENTIALS;
        }

        User user = this.state.getRegisteredUsers().get(username);
        String hashedPassword = user.getPassword();

        if (hasher.checkPassword(password, hashedPassword)) {
            this.handler.setPrincipal(user);
            return SUCCESS;
        }

        return INVALID_CREDENTIALS;
    }
}
