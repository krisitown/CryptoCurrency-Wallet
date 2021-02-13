package bg.sofia.uni.fmi.mjt.crypto.commands;

import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.models.Order;
import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetWalletOverallSummaryCommandTest {
    private final static String ASSET_LINE = "%s: Profit: %.08f USD";
    private final static String POSITION_LINE = "%s: Amount: %.08f";

    @Mock
    private RequestHandler requestHandler;

    @Test
    public void execute_userLoggedInSingleClosedPosition_expectedOpenPositionShown() {
        User user = new User("username", "password");
        user.getWallet().addOrder(new Order("BTC", false, 100.0, 1.0, new Date(System.currentTimeMillis() - 100000)));
        user.getWallet().addOrder(new Order("BTC", true, 40000.0, 1.0, new Date(System.currentTimeMillis())));
        when(requestHandler.getPrincipal()).thenReturn(user);
        GetWalletOverallSummaryCommand getWalletOverallSummaryCommand = new GetWalletOverallSummaryCommand(requestHandler);
        String result = getWalletOverallSummaryCommand.execute();

        assertNotNull(result);
        assertTrue(result.contains(String.format(ASSET_LINE, "BTC", 39900.0)));
    }

    @Test
    public void execute_userLoggedInSingleOpenPosition_expectedOpenPositionShown() {
        User user = new User("username", "password");
        user.getWallet().addOrder(new Order("BTC", false, 100.0, 1.0, new Date(System.currentTimeMillis() - 100000)));
        when(requestHandler.getPrincipal()).thenReturn(user);
        GetWalletOverallSummaryCommand getWalletOverallSummaryCommand = new GetWalletOverallSummaryCommand(requestHandler);
        String result = getWalletOverallSummaryCommand.execute();

        assertNotNull(result);
        assertTrue(result.contains(String.format(POSITION_LINE, "BTC", 1.0)));
    }

    @Test(expected = NotLoggedInException.class)
    public void execute_userNotLoggedIn_expectedNotLoggedInException() {
        when(requestHandler.getPrincipal()).thenReturn(null);
        GetWalletOverallSummaryCommand getWalletOverallSummaryCommand = new GetWalletOverallSummaryCommand(requestHandler);

        getWalletOverallSummaryCommand.execute();
    }
}
