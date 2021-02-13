package bg.sofia.uni.fmi.mjt.crypto.factory.impl;

import bg.sofia.uni.fmi.mjt.crypto.factory.exception.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.crypto.factory.exception.UnsupportedCommandArgumentsException;
import bg.sofia.uni.fmi.mjt.crypto.server.RequestHandler;
import bg.sofia.uni.fmi.mjt.crypto.commands.*;
import bg.sofia.uni.fmi.mjt.crypto.services.coin.CoinService;
import bg.sofia.uni.fmi.mjt.crypto.services.users.UserService;
import bg.sofia.uni.fmi.mjt.crypto.factory.AbstractCommandFactory;
import bg.sofia.uni.fmi.mjt.crypto.services.security.Hasher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerCommandFactory extends AbstractCommandFactory {
    public ServerCommandFactory(UserService userService, RequestHandler requestHandler, CoinService coinService, Hasher hasher) {
        super(userService, requestHandler, coinService, hasher);
    }

    @Override
    public Command createCommand(String[] arguments) {
        if (arguments == null || arguments.length == 0) {
            throw new CommandNotFoundException();
        }

        if(arguments[0].equalsIgnoreCase("login")) {
            if (arguments.length != 3) {
                throw new UnsupportedCommandArgumentsException();
            }

            return new LoginCommand(arguments[1], arguments[2], getUserService(), getRequestHandler(), getHasher());
        } else if (arguments[0].equalsIgnoreCase("register")) {
            if (arguments.length != 3) {
                throw new UnsupportedCommandArgumentsException();
            }

            return new RegisterCommand(arguments[1], arguments[2], getUserService(), getHasher());
        } else if (arguments[0].equalsIgnoreCase("deposit-money")) {
            if (arguments.length != 2) {
                throw new UnsupportedCommandArgumentsException();
            }

            Double amount;
            try {
                amount = Double.parseDouble(arguments[1]);
            } catch (NumberFormatException e) {
                throw new UnsupportedCommandArgumentsException(e);
            }

            return new DepositMoneyCommand(amount, getRequestHandler(), getUserService());
        } else if (arguments[0].equalsIgnoreCase("list-offerings")) {
            if(arguments.length != 1) {
                throw new UnsupportedCommandArgumentsException();
            }

            return new ListOfferingsCommand(getRequestHandler(), getCoinService());
        } else if (arguments[0].equalsIgnoreCase("buy")) {
            if(arguments.length != 3) {
                throw new UnsupportedCommandArgumentsException();
            }

            String offeringCode = null;
            String amount = null;

            String argumentLine = String.join(" ", arguments);
            Pattern offeringPattern = Pattern.compile("--offering=(?<code>[A-Za-z0-9]+)");
            Matcher offeringMatcher = offeringPattern.matcher(argumentLine);

            if(offeringMatcher.find()) {
                offeringCode = offeringMatcher.group("code");
            }

            Pattern amountPattern = Pattern.compile("--money=(?<amount>[0-9\\.]+)");
            Matcher amountMatcher = amountPattern.matcher(argumentLine);

            if(amountMatcher.find()) {
                amount = amountMatcher.group("amount");
            }

            if(amount == null || amount.isBlank()) {
                throw new UnsupportedCommandArgumentsException("Amount cannot be empty when placing a buy order.");
            }

            if(offeringCode == null || offeringCode.isBlank()) {
                throw new UnsupportedCommandArgumentsException("Asset cannot by empty when placing a buy order.");
            }

            Double amountValue = null;

            try {
                amountValue = Double.parseDouble(amount);
            } catch (NumberFormatException e) {
                throw new UnsupportedCommandArgumentsException("Amount is must be a decimal number when placing a buy order.");
            }

            return new BuyCommand(getRequestHandler(), getCoinService(), getUserService(), offeringCode, amountValue);
        } else if(arguments[0].equalsIgnoreCase("sell")) {
            if(arguments.length != 2) {
                throw new UnsupportedCommandArgumentsException();
            }

            String offeringCode = null;


            String argumentLine = String.join(" ", arguments);
            Pattern offeringPattern = Pattern.compile("--offering=(?<code>[A-Za-z0-9]+)");
            Matcher offeringMatcher = offeringPattern.matcher(argumentLine);

            if(offeringMatcher.find()) {
                offeringCode = offeringMatcher.group("code");
            }

            if(offeringCode == null || offeringCode.isBlank()) {
                throw new UnsupportedCommandArgumentsException("Asset cannot by empty when placing a buy order.");
            }

            return new SellCommand(getRequestHandler(), getCoinService(), getUserService(), offeringCode);
        } else if (arguments[0].equalsIgnoreCase("get-wallet-summary")) {
            if(arguments.length != 1) {
                throw new UnsupportedCommandArgumentsException();
            }

            return new GetWalletSummaryCommand(getRequestHandler(), getCoinService());
        } else if (arguments[0].equalsIgnoreCase("get-wallet-overall-summary")) {
            if(arguments.length != 1) {
                throw new UnsupportedCommandArgumentsException();
            }

            return new GetWalletOverallSummaryCommand(getRequestHandler());
        } else {
            throw new CommandNotFoundException();
        }
    }
}
