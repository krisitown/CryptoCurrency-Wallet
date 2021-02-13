package bg.sofia.uni.fmi.mjt.crypto.services;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import bg.sofia.uni.fmi.mjt.crypto.dtos.AssetDto;
import bg.sofia.uni.fmi.mjt.crypto.dtos.ExpiringAsset;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinServiceImpl;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.CoinApiException;
import bg.sofia.uni.fmi.mjt.crypto.services.file.FileService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class CoinServiceImplTest {
    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> mockedResponse;

    @Mock
    private FileService fileService;

    private Map<String, ExpiringAsset> expiringAssets;

    private CoinServiceImpl coinService;

    @Before
    public void setUp() throws IOException, InterruptedException {
        expiringAssets = new HashMap<>();

        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(mockedResponse);

        coinService = new CoinServiceImpl(httpClient, expiringAssets, fileService);
    }

    @Test
    public void listAssets_validResponseLimitOne_expectedSingleAssetReturned () {
        String responseBody = "[\n" +
                "  {\n" +
                "    \"asset_id\": \"BTC\",\n" +
                "    \"name\": \"Bitcoin\",\n" +
                "    \"type_is_crypto\": 1,\n" +
                "    \"price_usd\": 31304.448721266051267349441838\n" +
                "  },\n" +
                "  {\n" +
                "    \"asset_id\": \"XRP\",\n" +
                "    \"name\": \"Ripple\",\n" +
                "    \"type_is_crypto\": 1,\n" +
                "    \"price_usd\": 0.25124124121231\n" +
                "  }\n" +
                "]";

        when(mockedResponse.statusCode()).thenReturn(200);
        when(mockedResponse.body()).thenReturn(responseBody);

        List<AssetDto> result = coinService.listAssets(1);

        assertEquals(1, result.size());
        assertEquals("BTC", result.get(0).getAssetId());
        assertEquals("Bitcoin", result.get(0).getName());
        assertEquals(1, (int)result.get(0).getTypeIsCrypto());
    }

    @Test(expected = CoinApiException.class)
    public void listAssets_errorResponseCode_expectedCoinApiException() {
        when(mockedResponse.statusCode()).thenReturn(500);

        coinService.listAssets(1);
    }

    @Test(expected = CoinApiException.class)
    public void listAssets_nullBody_expectedCoinApiException() {
        when(mockedResponse.statusCode()).thenReturn(200);
        when(mockedResponse.body()).thenReturn(null);

        coinService.listAssets(1);
    }

    @Test(expected = CoinApiException.class)
    public void listAssets_httpClientThrowsIOException_expectedCoinApiException() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(IOException.class);

        try {
            coinService.listAssets(1);
        } catch (CoinApiException e){
            if (!(e.getCause() instanceof IOException)) {
                fail("CoinApiException should link to the cause, which is IOException.");
            }
            throw e;
        }
    }

    @Test(expected = CoinApiException.class)
    public void listAssets_httpClientThrowsInterruptedException_expectedCoinApiException()
            throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(InterruptedException.class);

        try {
            coinService.listAssets(1);
        } catch (CoinApiException e){
            if (!(e.getCause() instanceof InterruptedException)) {
                fail("CoinApiException should link to the cause, which is IOException.");
            }
            throw e;
        }
    }

    @Test
    public void getAsset_notCachedValidResponse_expectedValidAssetDto(){
        String responseBody = "[\n" +
                "  {\n" +
                "    \"asset_id\": \"BTC\",\n" +
                "    \"name\": \"Bitcoin\",\n" +
                "    \"type_is_crypto\": 1,\n" +
                "    \"price_usd\": 31304.448721266051267349441838\n" +
                "  }\n" +
                "]";

        when(mockedResponse.statusCode()).thenReturn(200);
        when(mockedResponse.body()).thenReturn(responseBody);

        AssetDto result = coinService.getAsset("BTC");

        assertEquals("BTC", result.getAssetId());
        assertEquals("Bitcoin", result.getName());
        assertEquals(1, (int)result.getTypeIsCrypto());
    }

    @Test
    public void getAsset_cachedValidResponse_expectedValidAssetDto(){
        AssetDto expected = new AssetDto("BTC", "Bitcoin", 1, 40000.0);
        expiringAssets.put("BTC", new ExpiringAsset(expected, new Date(System.currentTimeMillis() + 100000)));

        AssetDto result = coinService.getAsset("BTC");

        assertEquals(expected.getAssetId(), result.getAssetId());
        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getTypeIsCrypto(), result.getTypeIsCrypto());
    }

    @Test(expected = CoinApiException.class)
    public void getAsset_errorResponseCode_expectedCoinApiException(){
        when(mockedResponse.statusCode()).thenReturn(404);

        coinService.getAsset("BTC");
    }

    @Test(expected = CoinApiException.class)
    public void getAsset_nullResponse_expectedCoinApiException(){
        when(mockedResponse.statusCode()).thenReturn(200);
        when(mockedResponse.body()).thenReturn(null);

        coinService.getAsset("BTC");
    }

    @Test(expected = CoinApiException.class)
    public void getAsset_httpClientThrowsIOException_expectedCoinApiException() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(IOException.class);

        try {
            coinService.listAssets(1);
        } catch (CoinApiException e){
            if (!(e.getCause() instanceof IOException)) {
                fail("CoinApiException should link to the cause, which is IOException.");
            }
            throw e;
        }
    }

    @Test(expected = CoinApiException.class)
    public void getAsset_httpClientThrowsInterruptedException_expectedCoinApiException() throws IOException, InterruptedException {
        when(httpClient.send(any(HttpRequest.class), ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(InterruptedException.class);

        try {
            coinService.getAsset("BTC");
        } catch (CoinApiException e){
            if (!(e.getCause() instanceof InterruptedException)) {
                fail("CoinApiException should link to the cause, which is IOException.");
            }
            throw e;
        }
    }
}
