package bg.sofia.uni.fmi.mjt.crypto.factory;

import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import bg.sofia.uni.fmi.mjt.crypto.services.security.Hasher;

public abstract class AbstractCommandFactory implements CommandFactory {
    private UserService userService;
    private RequestHandler requestHandler;
    private CoinService coinService;
    private Hasher hasher;

    public AbstractCommandFactory(UserService userService, RequestHandler requestHandler, CoinService coinService, Hasher hasher) {
        this.userService = userService;
        this.requestHandler = requestHandler;
        this.coinService = coinService;
        this.hasher = hasher;
    }

    protected UserService getUserService() {
        return this.userService;
    }

    protected Hasher getHasher() {
        return this.hasher;
    }

    protected RequestHandler getRequestHandler() {
        return this.requestHandler;
    }

    protected CoinService getCoinService() { return this.coinService; }
}
