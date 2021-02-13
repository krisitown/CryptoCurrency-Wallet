package bg.sofia.uni.fmi.mjt.crypto.commands;

import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.UsernameTakenException;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.services.security.Hasher;

public class RegisterCommand implements Command {
    private final static String USERNAME_TAKEN = "A user with the same username already exists.";
    private final static String USERNAME_INVALID = "The specified username is not valid.";
    private final static String PASSWORD_INVALID = "The specified password is not valid.";
    private final static String SUCCESS = "Successfully registered.";

    private String username;
    private String password;
    private UserService applicationState;
    private Hasher hasher;

    public RegisterCommand(String username, String password, UserService userService, Hasher hasher) {
        this.username = username;
        this.password = password;
        this.applicationState = userService;
        this.hasher = hasher;
    }

    @Override
    public String execute() {
        String hashedPassword = hasher.hash(password);
        User user = new User(username, hashedPassword);

        try {
            this.applicationState.registerUser(user);
        } catch (UsernameTakenException e) {
            return USERNAME_TAKEN;
        } catch (InvalidUsernameException e) {
            return USERNAME_INVALID;
        } catch (InvalidPasswordException e ) {
            return PASSWORD_INVALID;
        }

        return SUCCESS;
    }
}
