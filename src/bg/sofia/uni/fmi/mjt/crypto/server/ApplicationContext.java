package bg.sofia.uni.fmi.mjt.crypto.server;

import bg.sofia.uni.fmi.mjt.crypto.dtos.ExpiringAsset;
import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.services.security.BcryptHasher;
import bg.sofia.uni.fmi.mjt.crypto.services.security.Hasher;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinServiceImpl;
import bg.sofia.uni.fmi.mjt.crypto.services.file.FileService;
import bg.sofia.uni.fmi.mjt.crypto.services.file.FileServiceImpl;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private final static String USERS_FILE = "USERS.json";
    private final static String ASSETS_FILE = "ASSETS.json";

    private CoinService coinService;
    private UserService userService;
    private Hasher hasher;
    private Map<String, User> registeredUsers;
    private Map<String, ExpiringAsset> cachedAssets;
    private FileService fileService;
    private Gson serializer;

    //This class I used to wire all the dependencies together
    //and also to recover the state of the server
    public ApplicationContext() {
        this.fileService = new FileServiceImpl();
        this.serializer = new Gson();
        getRegisteredUsers();
        getCachedAssets();
        HttpClient client = HttpClient.newBuilder().build();

        this.coinService = new CoinServiceImpl(client, this.cachedAssets, this.fileService);
        this.userService = new UserServiceImpl(this.registeredUsers, fileService);
        this.hasher = new BcryptHasher();
    }

    public CoinService getCoinService() {
        return coinService;
    }

    public UserService getUserService() {
        return userService;
    }

    public Hasher getHasher() {
        return hasher;
    }

    private void getRegisteredUsers() {
        File backup = new File(USERS_FILE);
        if(backup.exists()) {
            StringBuilder jsonString = this.fileService.readFile(backup);
            Type type = new TypeToken<HashMap<String, User>>(){}.getType();
            this.registeredUsers = serializer.fromJson(jsonString.toString(), type);
        } else {
            this.registeredUsers = new HashMap<>();
        }
    }

    private void getCachedAssets() {
        File assetsFile = new File(ASSETS_FILE);
        if(assetsFile.exists()) {
            StringBuilder jsonString = this.fileService.readFile(assetsFile);

            Type type = new TypeToken<HashMap<String, ExpiringAsset>>(){}.getType();
            this.cachedAssets = serializer.fromJson(jsonString.toString(), type);
        } else {
            this.cachedAssets = new HashMap<>();
        }
    }
}
