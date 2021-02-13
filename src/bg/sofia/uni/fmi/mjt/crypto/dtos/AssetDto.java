package bg.sofia.uni.fmi.mjt.crypto.dtos;

public class AssetDto {
    private String asset_id;
    private String name;
    private Integer type_is_crypto;
    private Double price_usd;

    public AssetDto() {
    }

    public AssetDto(String asset_id, String name, Integer type_is_crypto, Double price_usd) {
        this.asset_id = asset_id;
        this.name = name;
        this.type_is_crypto = type_is_crypto;
        this.price_usd = price_usd;
    }

    public String getAssetId() {
        return asset_id;
    }

    public String getName() {
        return name;
    }

    public Integer getTypeIsCrypto() {
        return type_is_crypto;
    }

    public Double getPriceUsd() {
        return price_usd;
    }
}
