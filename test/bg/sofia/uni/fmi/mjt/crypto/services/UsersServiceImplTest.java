package bg.sofia.uni.fmi.mjt.crypto.services;

import static org.junit.Assert.*;

import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.UsernameTakenException;
import bg.sofia.uni.fmi.mjt.crypto.services.file.FileService;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class UsersServiceImplTest {
    @Mock
    private FileService fileService;

    private UserServiceImpl userService;

    @Before
    public void setUp(){
        userService = new UserServiceImpl(new HashMap<>(), fileService);
    }

    @Test
    public void registerUser_validNotTakenUsername_expectedUserAddedToRegisteredUsers() {
        String username = "dante";
        User validUser = new User(username, "password");

        userService.registerUser(validUser);

        assertEquals(validUser, userService.getRegisteredUsers().get(username));
    }

    @Test(expected = UsernameTakenException.class)
    public void registerUser_takenUsername_expectedUsernameTakenException (){
        String username = "taken";
        User userOne = new User(username, "password");
        User userTwo = new User(username, "password");
        userService.registerUser(userOne);

        userService.registerUser(userTwo);
    }

    @Test(expected = InvalidUsernameException.class)
    public void registerUser_invalidUsername_expectedInvalidUsername() {
        String username = "inv@lid";
        User user = new User(username, "password");

        userService.registerUser(user);
    }

    @Test(expected = InvalidPasswordException.class)
    public void registerUser_invalidPassword_expectedInvalidUsername() {
        String username = "valid";
        User user = new User(username, "in");

        userService.registerUser(user);
    }

    @Test
    public void updateUser_nonExistingUser_expectedNewUserAdded() {
        User user = new User("dante", "password");
        userService.updateUser(user);

        assertEquals(user, userService.getRegisteredUsers().get(user.getUsername()));
    }

    @Test
    public void updateUser_existingUser_expectedUserIsUpdated() {
        String username = "dante";
        User userOne = new User(username, "password");
        User userTwo = new User(username, "password2");
        userService.registerUser(userOne);

        userService.updateUser(userTwo);

        assertEquals(userTwo, userService.getRegisteredUsers().get(username));
    }

    @Test(expected = InvalidPasswordException.class)
    public void updateUser_existingUserInvalidChange_expectedInvalidUsernameException(){
        String username = "dante";
        User userOne = new User(username, "validpassword");
        User userTwo = new User(username, "");
        userService.registerUser(userOne);

        userService.updateUser(userTwo);
    }
}
