package bg.sofia.uni.fmi.mjt.crypto.dtos;

import java.util.Date;

public class ExpiringAsset {
    private AssetDto asset;
    private Date expirationDate;

    public ExpiringAsset(AssetDto asset, Date expirationDate) {
        this.asset = asset;
        this.expirationDate = expirationDate;
    }

    public boolean isExpired(){
        return new Date(System.currentTimeMillis()).compareTo(expirationDate) > 0;
    }

    public AssetDto getAsset() {
        return asset;
    }
}
