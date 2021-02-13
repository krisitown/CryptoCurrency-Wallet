package bg.sofia.uni.fmi.mjt.crypto.commands;

import bg.sofia.uni.fmi.mjt.crypto.ServerConstants;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.dtos.AssetDto;
import bg.sofia.uni.fmi.mjt.crypto.models.Wallet;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.CoinApiException;

import java.util.Map;

public class GetWalletSummaryCommand implements Command {
    private final static String NO_INFORMATION = "Unable to fetch information for %s";

    private RequestHandler requestHandler;
    private CoinService coinService;

    public GetWalletSummaryCommand(RequestHandler requestHandler, CoinService coinService) {
        this.requestHandler = requestHandler;
        this.coinService = coinService;
    }

    @Override
    public String execute() {
        if (requestHandler.getPrincipal() == null) {
            throw new NotLoggedInException();
        }

        Wallet wallet = requestHandler.getPrincipal().getWallet();

        StringBuilder summary = new StringBuilder();
        Double total = wallet.getAssets().get("USD");

        summary.append(String.format("USD: %.02f %s", total, ServerConstants.LINE_SEPERATOR));

        for(Map.Entry<String, Double> asset : wallet.getAssets().entrySet()) {
            if(asset.getKey().equalsIgnoreCase("USD")){
                continue;
            }

            AssetDto assetDto = null;
            try {
                assetDto = coinService.getAsset(asset.getKey());
            } catch (CoinApiException e) {
                summary.append(String.format(NO_INFORMATION, asset.getKey()));
                summary.append(ServerConstants.LINE_SEPERATOR);
                continue;
            }

            summary.append(String.format(
                    "%s: Name: %s, Amount: %.08f",
                    assetDto.getAssetId(),
                    assetDto.getName(), asset.getValue()
            ));

            summary.append(ServerConstants.LINE_SEPERATOR);

            total += (asset.getValue() * assetDto.getPriceUsd());
        }

        summary.append("-----------------------------------------");
        summary.append(String.format("Total worth: %.08f USD.", total));

        return summary.toString();
    }
}
