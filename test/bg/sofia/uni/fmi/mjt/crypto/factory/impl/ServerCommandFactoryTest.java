package bg.sofia.uni.fmi.mjt.crypto.factory.impl;

import bg.sofia.uni.fmi.mjt.crypto.commands.*;
import bg.sofia.uni.fmi.mjt.crypto.factory.exception.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.crypto.factory.exception.UnsupportedCommandArgumentsException;
import bg.sofia.uni.fmi.mjt.crypto.services.security.Hasher;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ServerCommandFactoryTest {
    private UserService userService;
    private RequestHandler requestHandler;
    private CoinService coinService;
    private Hasher hasher;

    private ServerCommandFactory factory;

    @Before
    public void setUp(){
        factory = new ServerCommandFactory(userService, requestHandler, coinService, hasher);
    }

    @Test
    public void createCommand_validBuyCommand_expectedBuyCommandCreated(){
        factory.createCommand(new String[]{"buy", "--offering=BTC", "--money=1.0"});
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_malformedMoneyArgumentBuyCommand_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[]{"buy", "--offering=BTC", "--money=3.14.123"});
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_invalidMoneyArgumentBuyCommand_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[]{"buy", "--offering=BTC", "--invalid"});
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_invalidOfferingCodeBuyCommand_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[]{"buy", "--money=30.0", "--offring"});
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_wrongNumberBuyCommandArguments_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[]{"buy", "invalidSecondArg"});
    }

    @Test
    public void createCommand_validSellCommand_expectedSellCommandCreated(){
        factory.createCommand(new String[]{"sell", "--offering=BTC"});
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_invalidOfferingCodeSellCommand_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[]{"sell", "--offring=BTC"});
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_wrongNumberSellCommandArguments_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[]{"sell", "invalidSecondArg", "invalidThirdArg"});
    }

    @Test
    public void createCommand_validRegisterCommandArguments_expectedRegisterCommandCreated() {
        Command command = factory.createCommand(new String[] {"register", "username", "password"});
        assertTrue(command instanceof RegisterCommand);
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_wrongNumberRegisterCommandArguments_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[] {"register", "secondArgumentInvalid"});
    }

    @Test
    public void createCommand_validLoginCommandArguments_expectedLoginCommandCreated() {
        Command command = factory.createCommand(new String[] {"login", "username", "password"});
        assertTrue(command instanceof LoginCommand);
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_wrongNumberLoginCommandArguments_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[] {"login", "secondArgumentInvalid"});
    }

    @Test
    public void createCommand_validDepositMoneyCommandArguments_expectedDepositCommandCreated() {
        Command command = factory.createCommand(new String[] {"deposit-money", "1000.0"});
        assertTrue(command instanceof DepositMoneyCommand);
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_wrongNumberOfDepositMoneyCommandArguments_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[] {"deposit-money", "100.0", "thirdArgumentInvalid"});
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_invalidDepositMoneyCommandArguments_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[] {"deposit-money", "100.0.0"});
    }

    @Test
    public void createCommand_validListOfferingsCommandArguments_expectedListOfferingsCommandCreated() {
        Command command = factory.createCommand(new String[] {"list-offerings"});
        assertTrue(command instanceof ListOfferingsCommand);
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_invalidListOfferingsCommandArguments_expectedUnsupportedCommandArgumentsException() {
        factory.createCommand(new String[] {"list-offerings", "secondArgumentInvalid"});
    }

    @Test
    public void createCommand_validGetWalletSummaryCommandArguments_expectedGetWalletSummaryCommandCreated() {
        Command command = factory.createCommand(new String[] {"get-wallet-summary"});
        assertTrue(command instanceof GetWalletSummaryCommand);
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_invalidGetWalletSummaryCommandArguments_expectedGetWalletSummaryCommandCreated() {
        factory.createCommand(new String[] {"get-wallet-summary", "secondArgumentInvalid"});
    }

    @Test
    public void createCommand_validGetOverallWalletSummaryCommandArguments_expectedGetOverallWalletSummaryCommandCreated() {
        Command command = factory.createCommand(new String[] {"get-wallet-overall-summary"});
        assertTrue(command instanceof GetWalletOverallSummaryCommand);
    }

    @Test(expected = UnsupportedCommandArgumentsException.class)
    public void createCommand_invalidGetOverallWalletSummaryCommandArguments_expectedGetOverallWalletSummaryCommandCreated() {
        factory.createCommand(new String[] {"get-wallet-overall-summary", "secondArgumentInvalid"});
    }

    @Test(expected = CommandNotFoundException.class)
    public void createCommand_invalidCommandName_expectedCommandNotFoundException(){
        factory.createCommand(new String[] {"invalidCommand"});
    }

    @Test(expected = CommandNotFoundException.class)
    public void createCommand_emptyArguments_expectedCommandNotFoundException(){
        factory.createCommand(new String[] {});
    }

    @Test(expected = CommandNotFoundException.class)
    public void createCommand_nullArguments_expectedCommandNotFoundException(){
        factory.createCommand(null);
    }
}
