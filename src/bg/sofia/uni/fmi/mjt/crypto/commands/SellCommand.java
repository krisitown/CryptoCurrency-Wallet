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

public class SellCommand implements Command {
    private static final String DEFAULT_CURRENCY = "USD";
    private static final String ERROR = "Could not find asset with this offering code!";
    private static final String SUCCESS = "Buy order finished successfully!";

    private RequestHandler requestHandler;
    private CoinService coinService;
    private UserService userService;

    private String offeringCode;

    public SellCommand(RequestHandler requestHandler, CoinService coinService, UserService userService, String offeringCode) {
        this.requestHandler = requestHandler;
        this.coinService = coinService;
        this.userService = userService;
        this.offeringCode = offeringCode;
    }

    @Override
    public String execute() {
        if (requestHandler.getPrincipal() == null) {
            throw new NotLoggedInException();
        }

        Wallet wallet = requestHandler.getPrincipal().getWallet();
        Double amount = wallet.getAssets().get(offeringCode);
        if (amount == null || amount == 0) {
            throw new InsufficientFundsException();
        }

        AssetDto asset;

        try {
            asset = this.coinService.getAsset(offeringCode);
        } catch (CoinApiException e) {
            e.printStackTrace();
            return ERROR;
        }

        Double orderVolume = asset.getPriceUsd() * amount;
        Double currentFund = wallet.getAssets().get(DEFAULT_CURRENCY);

        wallet.getAssets().replace(DEFAULT_CURRENCY, currentFund + orderVolume);
        wallet.getAssets().remove(offeringCode);

        wallet.addOrder(new Order(offeringCode.toUpperCase(), true, asset.getPriceUsd(), amount,
                new Date(System.currentTimeMillis())));

        userService.updateUser(requestHandler.getPrincipal());

        return SUCCESS;
    }
}
