package bg.sofia.uni.fmi.mjt.crypto.commands;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import bg.sofia.uni.fmi.mjt.crypto.services.security.Hasher;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.UsernameTakenException;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RegisterCommandTest {
    private final static String USERNAME_TAKEN = "A user with the same username already exists.";
    private final static String USERNAME_INVALID = "The specified username is not valid.";
    private final static String PASSWORD_INVALID = "The specified password is not valid.";
    private final static String SUCCESS = "Successfully registered.";

    @Mock
    private UserService userService;

    @Mock
    private Hasher hasher;

    @Before
    public void setUp(){
        when(hasher.hash(anyString())).thenReturn("hashed");
    }

    @Test
    public void execute_validUser_expectedSuccess() {
        RegisterCommand registerCommand = new RegisterCommand("valid", "valid", userService, hasher);

        String result = registerCommand.execute();

        assertEquals(SUCCESS, result);
    }

    @Test
    public void execute_usernameTaken_expectedUsernameTaken() {
        doThrow(UsernameTakenException.class).when(userService).registerUser(any());
        RegisterCommand registerCommand = new RegisterCommand("taken", "valid", userService, hasher);

        String result = registerCommand.execute();

        assertEquals(USERNAME_TAKEN, result);
    }

    @Test
    public void execute_usernameTaken_expectedUsernameInvalid() {
        doThrow(InvalidUsernameException.class).when(userService).registerUser(any());
        RegisterCommand registerCommand = new RegisterCommand("invalid", "valid", userService, hasher);

        String result = registerCommand.execute();

        assertEquals(USERNAME_INVALID, result);
    }

    @Test
    public void execute_usernameTaken_expectedPasswordInvalid() {
        doThrow(InvalidPasswordException.class).when(userService).registerUser(any());
        RegisterCommand registerCommand = new RegisterCommand("valid", "invalid", userService, hasher);

        String result = registerCommand.execute();

        assertEquals(PASSWORD_INVALID, result);
    }
}
