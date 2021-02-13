package bg.sofia.uni.fmi.mjt.crypto.commands;

import bg.sofia.uni.fmi.mjt.crypto.ServerConstants;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.models.Order;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GetWalletOverallSummaryCommand implements Command {
    private final static String PROFITS_LINE = "===========PROFITS===========";
    private final static String ASSET_LINE = "%s: Profit: %.08f USD";
    private final static String OPEN_POSITIONS_LINE = "========OPEN POSITIONS=======";
    private final static String POSITION_LINE = "%s: Amount: %.08f";

    private RequestHandler requestHandler;

    public GetWalletOverallSummaryCommand(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    @Override
    public String execute() {
        if (requestHandler.getPrincipal() == null) {
            throw new NotLoggedInException();
        }

        List<Order> ledger = requestHandler.getPrincipal().getWallet().getLedger();

        HashMap<String, List<Order>> ledgersByCurrency = new HashMap<>();

        for(Order order : ledger) {
            if (!ledgersByCurrency.containsKey(order.getAsset())) {
                ledgersByCurrency.put(order.getAsset(), new LinkedList<>());
            }
            ledgersByCurrency.get(order.getAsset()).add(order);
        }

        HashMap<String, Double> profitByAsset = new HashMap<>();
        HashMap<String, Double> openPositions = new HashMap<>();

        for(String asset : ledgersByCurrency.keySet()) {
            List<Order> orders = ledgersByCurrency.get(asset);
            Double profit = 0.0;
            for(int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                if(i == orders.size() - 1 && !order.isSell()) {
                    openPositions.put(asset, order.getAmount());
                    continue;
                }

                if(order.isSell()) {
                    profit += order.getAmount() * order.getPrice();
                } else {
                    profit -= order.getAmount() * order.getPrice();
                }
            }

            profitByAsset.put(asset, profit);
        }

        StringBuilder output = new StringBuilder();
        iterateMap(profitByAsset, output, true);
        iterateMap(openPositions, output, false);

        return output.toString();
    }

    private void iterateMap(HashMap<String, Double> profitByAsset, StringBuilder output, boolean isProfit) {
        output.append(isProfit ? PROFITS_LINE : OPEN_POSITIONS_LINE);
        output.append(ServerConstants.LINE_SEPERATOR);

        for (Map.Entry<String, Double> profitEntry : profitByAsset.entrySet()) {
            output.append(String.format(isProfit ? ASSET_LINE : POSITION_LINE, profitEntry.getKey(),
                    profitEntry.getValue()));
            output.append(ServerConstants.LINE_SEPERATOR);
        }
    }
}
