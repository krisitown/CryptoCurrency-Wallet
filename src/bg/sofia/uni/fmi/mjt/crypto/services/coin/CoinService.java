package bg.sofia.uni.fmi.mjt.crypto.services.coin;

import bg.sofia.uni.fmi.mjt.crypto.dtos.AssetDto;

import java.util.List;

public interface CoinService {
    List<AssetDto> listAssets(int limit);
    AssetDto getAsset(String offeringCode);
}
