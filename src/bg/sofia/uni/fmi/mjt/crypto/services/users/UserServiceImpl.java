package bg.sofia.uni.fmi.mjt.crypto.services.users;

import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidUsernameException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.UsernameTakenException;
import bg.sofia.uni.fmi.mjt.crypto.services.file.FileService;

import java.util.Map;

public class UserServiceImpl implements UserService {
    private final static String FILE_PATH = "USERS.json";
    private FileService fileService;

    private Map<String, User> registeredUsers;

    public UserServiceImpl(Map<String, User> registeredUsers, FileService fileService) {
        this.registeredUsers = registeredUsers;
        this.fileService = fileService;
    }

    public Map<String, User> getRegisteredUsers() {
        return Map.copyOf(registeredUsers);
    }

    @Override
    public void updateUser(User user) {
        validateUser(user);
        if(registeredUsers.containsKey(user.getUsername())) {
            registeredUsers.replace(user.getUsername(), user);
        } else {
            registeredUsers.put(user.getUsername(), user);
        }
        this.fileService.updateFile(this.registeredUsers, FILE_PATH);
    }

    @Override
    public void registerUser(User user){
        validateUser(user);
        if(this.registeredUsers.containsKey(user.getUsername())) {
            throw new UsernameTakenException();
        }

        this.registeredUsers.put(user.getUsername(), user);
        this.fileService.updateFile(this.registeredUsers, FILE_PATH);
    }

    private void validateUser(User user){
        String username = user.getUsername();
        String password = user.getPassword();

        if(username == null || !username.matches("[A-Za-z0-9_]{4,}")){
            throw new InvalidUsernameException();
        }

        if(password == null || !password.matches(".{6,}")) {
            throw new InvalidPasswordException();
        }
    }
}
