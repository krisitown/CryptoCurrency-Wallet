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
public class BuyCommandTest {
    private static final String DEFAULT_CURRENCY = "USD";
    private static final String ERROR = "Could not find asset with this offering code!";
    private static final String SUCCESS = "Buy order finished successfully!";
    private final static String NEGATIVE_AMOUNT = "Amount specified cannot be negative.";

    @Mock
    private RequestHandler requestHandler;

    @Mock
    private CoinService coinService;

    @Mock
    private UserService userService;

    @Test
    public void execute_loggedInInvalidOfferingCode_expectedErrorMessage() {
        User user = new User("username", "password");
        when(requestHandler.getPrincipal()).thenReturn(user);
        when(coinService.getAsset("INVALID")).thenThrow(CoinApiException.class);
        BuyCommand buyCommand = new BuyCommand(requestHandler, coinService, userService, "INVALID", 1000.0);

        String result = buyCommand.execute();

        assertEquals(ERROR, result);
        assertFalse(user.getWallet().getAssets().containsKey("INVALID"));
    }

    @Test(expected = NotLoggedInException.class)
    public void execute_notLoggedInUser_expectedNotLoggedInException(){
        when(requestHandler.getPrincipal()).thenReturn(null);
        BuyCommand buyCommand = new BuyCommand(requestHandler, coinService, userService, "BTC", 100.0);

        buyCommand.execute();
    }

    @Test
    public void execute_loggedInUserAndNegativeAmount_expectedNegativeAmountMessage() {
        User user = new User("username", "password");
        when(requestHandler.getPrincipal()).thenReturn(user);
        BuyCommand buyCommand = new BuyCommand(requestHandler, coinService, userService, "BTC", -3.14);

        String result = buyCommand.execute();

        assertEquals(NEGATIVE_AMOUNT, result);
        assertFalse(user.getWallet().getAssets().containsKey("BTC"));
    }

    @Test(expected = InsufficientFundsException.class)
    public void execute_loggedInUserNoFunds_expectedInsufficientFundsException(){
        User user = new User("username", "password");
        AssetDto btc = new AssetDto("BTC", "Bitcoin", 1, 40000.0);
        when(requestHandler.getPrincipal()).thenReturn(user);
        when(coinService.getAsset("BTC")).thenReturn(btc);
        BuyCommand buyCommand = new BuyCommand(requestHandler, coinService, userService, "BTC", 1.0);

        buyCommand.execute();
    }

    @Test
    public void execute_loggedInUserEnoughFundsNotOwningPreviousBTC_expectedInsufficientFundsException(){
        User user = new User("username", "password");
        user.getWallet().getAssets().replace(DEFAULT_CURRENCY, 40000.0);
        AssetDto btc = new AssetDto("BTC", "Bitcoin", 1, 40000.0);
        when(requestHandler.getPrincipal()).thenReturn(user);
        when(coinService.getAsset("BTC")).thenReturn(btc);
        BuyCommand buyCommand = new BuyCommand(requestHandler, coinService, userService, "BTC", 1.0);

        String result = buyCommand.execute();

        assertEquals(SUCCESS, result);
        assertEquals((Double)1.0, user.getWallet().getAssets().get("BTC"));
        assertEquals((Double)0.0, user.getWallet().getAssets().get(DEFAULT_CURRENCY));
        assertEquals(1, user.getWallet().getLedger().size());

        Order order = user.getWallet().getLedger().get(0);
        assertNotNull(order);
        assertEquals((Double)1.0, (Double)order.getAmount());
        assertEquals((Double)40000.0, (Double)order.getPrice());
        assertEquals("BTC", order.getAsset());
        assertFalse(order.isSell());
    }

    @Test
    public void execute_loggedInUserEnoughFundsOwningPreviousBTC_expectedInsufficientFundsException(){
        User user = new User("username", "password");
        user.getWallet().getAssets().replace(DEFAULT_CURRENCY, 40000.0);
        user.getWallet().getAssets().put("BTC", 1.0);
        AssetDto btc = new AssetDto("BTC", "Bitcoin", 1, 40000.0);
        when(requestHandler.getPrincipal()).thenReturn(user);
        when(coinService.getAsset("BTC")).thenReturn(btc);
        BuyCommand buyCommand = new BuyCommand(requestHandler, coinService, userService, "BTC", 1.0);

        String result = buyCommand.execute();

        assertEquals(SUCCESS, result);
        assertEquals((Double)2.0, user.getWallet().getAssets().get("BTC"));
        assertEquals((Double)0.0, user.getWallet().getAssets().get(DEFAULT_CURRENCY));
        assertEquals(1, user.getWallet().getLedger().size());

        Order order = user.getWallet().getLedger().get(0);
        assertNotNull(order);
        assertEquals((Double)1.0, (Double)order.getAmount());
        assertEquals((Double)40000.0, (Double)order.getPrice());
        assertEquals("BTC", order.getAsset());
        assertFalse(order.isSell());
    }
}
