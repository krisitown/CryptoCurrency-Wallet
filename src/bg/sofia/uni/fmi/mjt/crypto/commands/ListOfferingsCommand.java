package bg.sofia.uni.fmi.mjt.crypto.commands;

import bg.sofia.uni.fmi.mjt.crypto.ServerConstants;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.dtos.AssetDto;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.CoinApiException;

import java.util.List;

public class ListOfferingsCommand implements Command {
    private String ERROR = "An error occurred while fetching data. Please try again later...";
    private static final int ASSET_LIMIT = 50;

    private RequestHandler requestHandler;
    private CoinService coinService;

    public ListOfferingsCommand(RequestHandler requestHandler, CoinService coinService) {
        this.requestHandler = requestHandler;
        this.coinService = coinService;
    }

    @Override
    public String execute() {
        if (requestHandler.getPrincipal() == null) {
            throw new NotLoggedInException();
        }

        try {
            List<AssetDto> assets = this.coinService.listAssets(ASSET_LIMIT);

            StringBuilder output = new StringBuilder();
            for (AssetDto asset : assets) {
                output.append(String.format(
                        "%s: Name: %s Current price: %.08f",
                        asset.getAssetId(),
                        asset.getName(),
                        asset.getPriceUsd()
                ));
                output.append(ServerConstants.LINE_SEPERATOR);
            }

            return output.toString();
        } catch (CoinApiException e) {
            return ERROR;
        }
    }
}
