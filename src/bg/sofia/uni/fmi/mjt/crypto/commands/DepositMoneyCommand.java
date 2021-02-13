package bg.sofia.uni.fmi.mjt.crypto.commands;

import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.models.Wallet;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;

public class DepositMoneyCommand implements Command {
    private final static String DEFAULT_CURRENCY = "USD";
    private final static String SUCCESS = "Successfully deposited %.02f USD to account.";
    private final static String NEGATIVE_AMOUNT = "Amount specified cannot be negative.";

    private double amount;
    private RequestHandler handler;
    private UserService userService;

    public DepositMoneyCommand(double amount, RequestHandler handler, UserService userService) {
        this.amount = amount;
        this.handler = handler;
        this.userService = userService;
    }

    @Override
    public String execute() {
        if (handler.getPrincipal() == null) {
            throw new NotLoggedInException();
        }

        if(amount < 0) {
            return NEGATIVE_AMOUNT;
        }

        Wallet wallet = this.handler.getPrincipal().getWallet();
        wallet.getAssets().replace(DEFAULT_CURRENCY,
                    wallet.getAssets().get(DEFAULT_CURRENCY) + amount);

        this.userService.updateUser(this.handler.getPrincipal());

        return String.format(SUCCESS, amount);
    }
}
