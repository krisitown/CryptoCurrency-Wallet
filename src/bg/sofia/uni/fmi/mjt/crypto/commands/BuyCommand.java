package bg.sofia.uni.fmi.mjt.crypto.commands;

import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.InsufficientFundsException;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.dtos.AssetDto;
import bg.sofia.uni.fmi.mjt.crypto.models.Order;
import bg.sofia.uni.fmi.mjt.crypto.models.Wallet;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.CoinApiException;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;

import java.util.Date;

public class BuyCommand implements Command {
    private static final String DEFAULT_CURRENCY = "USD";
    private static final String ERROR = "Could not find asset with this offering code!";
    private static final String SUCCESS = "Buy order finished successfully!";
    private final static String NEGATIVE_AMOUNT = "Amount specified cannot be negative.";

    private RequestHandler requestHandler;
    private CoinService coinService;
    private UserService userService;

    private String offeringCode;
    private Double amount;

    public BuyCommand(RequestHandler requestHandler, CoinService coinService, UserService userService, String offeringCode, Double amount) {
        this.requestHandler = requestHandler;
        this.coinService = coinService;
        this.userService = userService;
        this.offeringCode = offeringCode;
        this.amount = amount;
    }

    @Override
    public String execute() {
        if (requestHandler.getPrincipal() == null) {
            throw new NotLoggedInException();
        }

        if (amount < 0) {
            return NEGATIVE_AMOUNT;
        }

        AssetDto asset;

        try {
            asset = this.coinService.getAsset(offeringCode);
        } catch (CoinApiException e) {
            e.printStackTrace();
            return ERROR;
        }

        Double price = asset.getPriceUsd();
        Double orderVolume = price * amount;

        Wallet wallet = requestHandler.getPrincipal().getWallet();
        Double availableFunds = wallet.getAssets().get(DEFAULT_CURRENCY);

        if (availableFunds < orderVolume) {
            throw new InsufficientFundsException();
        }

        wallet.getAssets().replace(DEFAULT_CURRENCY, availableFunds - orderVolume);

        if(wallet.getAssets().containsKey(offeringCode)) {
            Double previousAmount = wallet.getAssets().get(offeringCode);
            wallet.getAssets().replace(offeringCode, previousAmount + amount);
        } else {
            wallet.getAssets().put(offeringCode.toUpperCase(), amount);
        }

        wallet.addOrder(new Order(offeringCode.toUpperCase(), false, price, amount,
                new Date(System.currentTimeMillis())));

        userService.updateUser(requestHandler.getPrincipal());

        return SUCCESS;
    }
}
