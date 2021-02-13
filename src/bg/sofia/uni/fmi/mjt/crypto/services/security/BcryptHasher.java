package bg.sofia.uni.fmi.mjt.crypto.services.security;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class BcryptHasher implements Hasher {

    @Override
    public String hash(String hash) {
        return BCrypt.withDefaults().hashToString(BCrypt.MIN_COST, hash.toCharArray());
    }

    @Override
    public boolean checkPassword(String plain, String hashed) {
        return BCrypt.verifyer().verify(plain.toCharArray(), hashed.toCharArray()).verified;
    }
}
