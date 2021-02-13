package bg.sofia.uni.fmi.mjt.crypto.services.users;

import bg.sofia.uni.fmi.mjt.crypto.models.User;

import java.util.Map;

public interface UserService {
    void registerUser(User user);
    Map<String, User> getRegisteredUsers();
    void updateUser(User user);
}
