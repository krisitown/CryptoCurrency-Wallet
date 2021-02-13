package bg.sofia.uni.fmi.mjt.crypto.commands;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertEquals;

import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.services.security.Hasher;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class LoginCommandTest {
    private final static String USER_ALREADY_LOGGED_IN = "A user is already logged in.";
    private final static String INVALID_CREDENTIALS = "Invalid credentials.";
    private final static String SUCCESS = "Successfully logged in.";

    @Mock
    private UserService userService;

    @Mock
    private Hasher hasher;

    @Mock
    private RequestHandler handler;

    @Before
    public void setUp() {
        String username = "registered";
        User registeredUser = new User(username, "password");
        Map<String, User> registeredUsers = new HashMap<>();
        registeredUsers.put(username, registeredUser);
        when(userService.getRegisteredUsers()).thenReturn(registeredUsers);
    }

    @Test
    public void execute_validCredentials_expectedSuccessfulLogin() {
        when(handler.getPrincipal()).thenReturn(null);
        when(hasher.checkPassword(anyString(), anyString())).thenReturn(true);
        LoginCommand loginCommand = new LoginCommand("registered", "password",
                userService, handler, hasher);

        String result = loginCommand.execute();

        assertEquals(SUCCESS, result);
    }

    @Test
    public void execute_alreadyLoggedIn_expectedAlreadyLoggedInResponse() {
        when(handler.getPrincipal()).thenReturn(new User("registered", "password"));
        LoginCommand loginCommand = new LoginCommand("registered", "password", userService, handler, hasher);

        String result = loginCommand.execute();

        assertEquals(USER_ALREADY_LOGGED_IN, result);
    }

    @Test
    public void execute_invalidUsername_expectedInvalidCredentialsResponse() {
        when(handler.getPrincipal()).thenReturn(null);
        LoginCommand loginCommand = new LoginCommand("unregistered", "password", userService, handler, hasher);

        String result = loginCommand.execute();

        assertEquals(INVALID_CREDENTIALS, result);
    }

    @Test
    public void execute_invalidPassword_expectedInvalidCredentialsResponse() {
        when(handler.getPrincipal()).thenReturn(null);
        when(hasher.checkPassword(anyString(), anyString())).thenReturn(false);
        LoginCommand loginCommand = new LoginCommand("registered", "password", userService, handler, hasher);

        String result = loginCommand.execute();

        assertEquals(INVALID_CREDENTIALS, result);
    }
}
