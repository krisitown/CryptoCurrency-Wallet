package bg.sofia.uni.fmi.mjt.crypto.services.coin;

import bg.sofia.uni.fmi.mjt.crypto.dtos.AssetDto;
import bg.sofia.uni.fmi.mjt.crypto.dtos.ExpiringAsset;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.CoinApiException;
import bg.sofia.uni.fmi.mjt.crypto.services.file.FileService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoinServiceImpl implements CoinService {
    private final static Long CACHE_LIFE = 1800000l;
    private final static String FILE_PATH = "ASSETS.json";
    private final static String API_KEY = "9FADA14A-0A88-4836-A138-F1F169E9DE2B";
    private final static String API_URL = "https://rest.coinapi.io/v1/assets/";

    private Map<String, ExpiringAsset> cache;
    private FileService fileService;
    private Gson gson;
    private HttpClient client;

    public CoinServiceImpl(HttpClient client, Map<String, ExpiringAsset> cachedAssets, FileService fileService) {
        this.cache = new HashMap<>();
        this.gson = new GsonBuilder().create();
        this.client = client;
        this.cache = cachedAssets;
        this.fileService = fileService;
    }

    @Override
    public List<AssetDto> listAssets(int limit) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(API_URL))
                .header("X-CoinAPI-Key", API_KEY)
                .GET().build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() >= 400) {
                throw new CoinApiException();
            }

            AssetDto[] assets = gson.fromJson(response.body(), AssetDto[].class);
            if(assets == null) {
                throw new CoinApiException();
            }

            List<AssetDto> results = Stream.of(assets).filter(asset -> asset.getTypeIsCrypto() == 1 && asset.getPriceUsd() != null)
                    .limit(limit).collect(Collectors.toList());
            for(AssetDto result : results) {
                ExpiringAsset expiringWrapper = new ExpiringAsset(result,
                        getExpirationDate());

                if(this.cache.containsKey(result.getAssetId())) {
                    this.cache.replace(result.getAssetId(), expiringWrapper);
                } else {
                    this.cache.put(result.getAssetId(), expiringWrapper);
                }

                this.fileService.updateFile(this.cache, FILE_PATH);
            }

            return results;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CoinApiException(e);
        } catch (InterruptedException e) {
            throw new CoinApiException(e);
        }
    }

    @Override
    public AssetDto getAsset(String offeringCode) {
        ExpiringAsset asset = this.cache.get(offeringCode.toUpperCase());
        if (asset != null && !asset.isExpired()) {
            return asset.getAsset();
        }

        HttpRequest request = HttpRequest.newBuilder(URI.create(API_URL + offeringCode.toUpperCase()))
                .header("X-CoinAPI-Key", API_KEY)
                .GET().build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() >= 400) {
                throw new CoinApiException();
            }

            System.out.println(response.body());

            AssetDto[] result = this.gson.fromJson(response.body(), AssetDto[].class);
            if(result == null) {
                throw new CoinApiException();
            }

            ExpiringAsset expiringResult = new ExpiringAsset(result[0], getExpirationDate());

            if (this.cache.containsKey(result[0].getAssetId())) {
                this.cache.replace(result[0].getAssetId(), expiringResult);
            } else {
                this.cache.put(result[0].getAssetId(), expiringResult);
            }

            this.fileService.updateFile(this.cache, FILE_PATH);

            return result[0];
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new CoinApiException(e);
        }
    }

    private Date getExpirationDate() {
        return new Date(System.currentTimeMillis() + CACHE_LIFE);
    }
}
