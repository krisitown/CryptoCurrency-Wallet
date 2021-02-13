package bg.sofia.uni.fmi.mjt.crypto.services.security;

public interface Hasher {
    String hash(String hash);
    boolean checkPassword(String plain, String hashed);
}
