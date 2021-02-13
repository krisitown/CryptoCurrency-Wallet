package bg.sofia.uni.fmi.mjt.crypto.commands;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.dtos.AssetDto;
import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.CoinApiException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ListOfferingsCommandTest {
    private String ERROR = "An error occurred while fetching data. Please try again later...";

    @Mock
    private RequestHandler requestHandler;

    @Mock
    private CoinService coinService;

    @Before
    public void setUp(){
        User user = new User("username", "password");
        when(requestHandler.getPrincipal()).thenReturn(user);
    }

    @Test
    public void execute_userLoggedInAssetsAreProperlyReturned() {
        AssetDto btc = new AssetDto("BTC", "Bitcoin", 1, 40000.0);
        AssetDto eth = new AssetDto("ETH", "Ethereum", 1, 1200.0);
        List<AssetDto> assetList = Arrays.asList(btc, eth);
        when(coinService.listAssets(anyInt())).thenReturn(assetList);
        ListOfferingsCommand listOfferingsCommand = new ListOfferingsCommand(requestHandler, coinService);

        String result = listOfferingsCommand.execute();

        assertTrue("Offering code of asset is missing in list offerings command.", result.contains(btc.getAssetId()));
        assertTrue("Name of asset is missing in list offerings command.", result.contains(btc.getName()));
        assertTrue("Price of asset is missing in list offerings command.", result.contains(String.format("%.08f", btc.getPriceUsd())));

        assertTrue("Offering code of asset is missing in list offerings command.", result.contains(eth.getAssetId()));
        assertTrue("Name of asset is missing in list offerings command.",result.contains(eth.getName()));
        assertTrue("Price of asset is missing in list offerings command.", result.contains(String.format("%.08f", eth.getPriceUsd())));
    }

    @Test
    public void execute_userLoggedInCoinApiException_returnErrorMessage() {
        when(coinService.listAssets(anyInt())).thenThrow(CoinApiException.class);
        ListOfferingsCommand listOfferingsCommand = new ListOfferingsCommand(requestHandler, coinService);

        String result = listOfferingsCommand.execute();

        assertEquals(ERROR, result);
    }

    @Test(expected = NotLoggedInException.class)
    public void execute_userNotLoggedIn_expectedNotLoggedInException(){
        when(requestHandler.getPrincipal()).thenReturn(null);
        ListOfferingsCommand listOfferingsCommand = new ListOfferingsCommand(requestHandler, coinService);

        listOfferingsCommand.execute();
    }
}
