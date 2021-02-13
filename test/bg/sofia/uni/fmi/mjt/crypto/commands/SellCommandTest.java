package bg.sofia.uni.fmi.mjt.crypto.commands;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import bg.sofia.uni.fmi.mjt.crypto.commands.exception.InsufficientFundsException;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.dtos.AssetDto;
import bg.sofia.uni.fmi.mjt.crypto.models.Order;
import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.CoinApiException;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SellCommandTest {
    private static final String DEFAULT_CURRENCY = "USD";
    private static final String ERROR = "Could not find asset with this offering code!";
    private static final String SUCCESS = "Buy order finished successfully!";

    @Mock
    private RequestHandler requestHandler;

    @Mock
    private CoinService coinService;

    @Mock
    private UserService userService;

    @Test
    public void execute_loggedInEnoughFunds_expectedSoldSuccessfully() {
        AssetDto btc = new AssetDto("BTC", "Bitcoin", 1, 40000.0);
        User user = new User("username", "password");
        user.getWallet().getAssets().put("BTC", 1.0);
        when(requestHandler.getPrincipal()).thenReturn(user);
        when(coinService.getAsset("BTC")).thenReturn(btc);
        SellCommand sellCommand = new SellCommand(requestHandler, coinService, userService, "BTC");

        String result = sellCommand.execute();

        assertEquals(SUCCESS, result);
        assertFalse(user.getWallet().getAssets().containsKey("BTC"));
        assertEquals((Double)40000.0, user.getWallet().getAssets().get(DEFAULT_CURRENCY));
        assertEquals(1, user.getWallet().getLedger().size());

        Order order = user.getWallet().getLedger().get(0);
        assertNotNull(order);
        assertEquals((Double)1.0, (Double)order.getAmount());
        assertEquals((Double)40000.0, (Double)order.getPrice());
        assertEquals("BTC", order.getAsset());
        assertTrue(order.isSell());
    }

    @Test
    public void execute_loggedInEnoughFundsCoinApiException_expectedErrorMessage() {
        User user = new User("username", "password");
        user.getWallet().getAssets().put("BTC", 1.0);
        when(requestHandler.getPrincipal()).thenReturn(user);
        when(coinService.getAsset("BTC")).thenThrow(CoinApiException.class);
        SellCommand sellCommand = new SellCommand(requestHandler, coinService, userService, "BTC");

        String result = sellCommand.execute();

        assertEquals(ERROR, result);
    }

    @Test(expected = NotLoggedInException.class)
    public void execute_notLoggedInUser_expectedNotLoggedInException() {
        when(requestHandler.getPrincipal()).thenReturn(null);
        SellCommand sellCommand = new SellCommand(requestHandler, coinService, userService, "BTC");

        sellCommand.execute();
    }

    @Test(expected = InsufficientFundsException.class)
    public void execute_loggedInUserWithNullAsset_expectedInsufficientFundsException(){
        User user = new User("username", "password");
        when(requestHandler.getPrincipal()).thenReturn(user);
        SellCommand sellCommand = new SellCommand(requestHandler, coinService, userService, "BTC");

        sellCommand.execute();
    }

    @Test(expected = InsufficientFundsException.class)
    public void execute_loggedInUserWithZeroAsset_expectedInsufficientFundsException(){
        User user = new User("username", "password");
        user.getWallet().getAssets().put("BTC", 0.0);
        when(requestHandler.getPrincipal()).thenReturn(user);
        SellCommand sellCommand = new SellCommand(requestHandler, coinService, userService, "BTC");

        sellCommand.execute();
    }
}
