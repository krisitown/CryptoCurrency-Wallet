package bg.sofia.uni.fmi.mjt.crypto.commands;

import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.dtos.AssetDto;
import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.CoinApiException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetWalletSummaryCommandTest {
    private final static String NO_INFORMATION = "Unable to fetch information for %s";

    @Mock
    private RequestHandler requestHandler;

    @Mock
    private CoinService coinService;

    @Test
    public void execute_userLoggedIn_expectedOpenPositionsAndUsdToBePresentInResult(){
        User user = new User("username", "password");
        when(requestHandler.getPrincipal()).thenReturn(user);
        user.getWallet().getAssets().replace("USD", 100000.0);
        user.getWallet().getAssets().put("BTC", 1.0);
        user.getWallet().getAssets().put("ETH", 32.5);
        when(coinService.getAsset("BTC")).thenThrow(CoinApiException.class);
        when(coinService.getAsset("ETH")).thenReturn(new AssetDto("ETH", "Ethereum", 1, 1200.0));
        GetWalletSummaryCommand getWalletSummaryCommand = new GetWalletSummaryCommand(requestHandler, coinService);

        String result = getWalletSummaryCommand.execute();

        assertTrue("Command did not indicate lack of information for asset.", result.contains(String.format(NO_INFORMATION, "BTC")));
        assertTrue("Command did not display information about asset position.", result.matches(".*ETH.*Ethereum.*"));
        assertTrue("Command did not display information about asset position.", result.matches(".*USD.*100000\\.00.*"));
    }

    @Test(expected = NotLoggedInException.class)
    public void execute_userNotLoggedIn_expectedNotLoggedInException() {
        when(requestHandler.getPrincipal()).thenReturn(null);
        GetWalletSummaryCommand getWalletSummaryCommand = new GetWalletSummaryCommand(requestHandler, coinService);

        getWalletSummaryCommand.execute();
    }
}
