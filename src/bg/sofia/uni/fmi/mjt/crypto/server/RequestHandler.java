package bg.sofia.uni.fmi.mjt.crypto.server;

import bg.sofia.uni.fmi.mjt.crypto.models.User;

public interface RequestHandler extends Runnable {
    User getPrincipal();
    void setPrincipal(User user);
}
