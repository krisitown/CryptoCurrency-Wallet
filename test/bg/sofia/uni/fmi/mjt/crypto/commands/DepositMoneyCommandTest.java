package bg.sofia.uni.fmi.mjt.crypto.commands;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.models.User;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DepositMoneyCommandTest {
    private final static String SUCCESS = "Successfully deposited %.02f USD to account.";
    private final static String NEGATIVE_AMOUNT = "Amount specified cannot be negative.";

    @Mock
    private RequestHandler requestHandler;

    @Mock
    private UserService userService;

    @Test
    public void execute_loggedInUserPositiveAmount_expectedSuccessfulDeposit(){
        Double amount = 1000.560656;
        User user = new User("username", "password");
        when(requestHandler.getPrincipal()).thenReturn(user);
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(amount, requestHandler, userService);

        String result = depositMoneyCommand.execute();

        assertEquals(String.format(SUCCESS, amount), result);
        assertEquals(amount, user.getWallet().getAssets().get("USD"));
    }

    @Test
    public void execute_loggedInUserNegativeAmount_expectedNegativeAmountResponseAndNoChangeInWallet(){
        Double amount = -3.1456123;
        User user = new User("username", "password");
        when(requestHandler.getPrincipal()).thenReturn(user);
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(amount, requestHandler, userService);

        String result = depositMoneyCommand.execute();

        assertEquals(NEGATIVE_AMOUNT, result);
        assertEquals((Double)0.0, user.getWallet().getAssets().get("USD"));
    }

    @Test(expected = NotLoggedInException.class)
    public void execute_notLoggedInUser_expectedNotLoggedInExceptionAndNoChangeInWallet() {
        when(requestHandler.getPrincipal()).thenReturn(null);
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(3.14, requestHandler, userService);

        depositMoneyCommand.execute();
    }
}
