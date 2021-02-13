package bg.sofia.uni.fmi.mjt.crypto.server;

import bg.sofia.uni.fmi.mjt.crypto.commands.Command;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.InsufficientFundsException;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.crypto.factory.exception.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.crypto.factory.exception.UnsupportedCommandArgumentsException;
import bg.sofia.uni.fmi.mjt.crypto.factory.impl.ServerCommandFactory;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidPasswordException;
import bg.sofia.uni.fmi.mjt.crypto.services.exceptions.InvalidUsernameException;

public class CommandExecutor {
    private CommandFactory commandFactory;

    public CommandExecutor(ApplicationContext applicationContext, RequestHandler requestHandler){
        this.commandFactory = new ServerCommandFactory(applicationContext.getUserService(), requestHandler, applicationContext.getCoinService(),
                applicationContext.getHasher());
    }

    public void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

    public String executeCommand(String input) {
        String[] arguments = input.split("\\s+");
        try {
            Command command = this.commandFactory.createCommand(arguments);

            String result = command.execute();
            return result;
        } catch (CommandNotFoundException e) {
            return "Could not find command. Please check the documentation and try again.";
        } catch (UnsupportedCommandArgumentsException e) {
            return "The " + arguments[0] + " command was used improperly. Please check the documentation and try again.";
        } catch (NotLoggedInException e) {
            return "Not logged in! Please login to use this command.";
        } catch (InsufficientFundsException e) {
            return "There are not enough funds in your wallet to execute this operation.";
        } catch (InvalidUsernameException e) {
            return "The specified username is invalid.";
        } catch (InvalidPasswordException e) {
            return "The specified password is invalid.";
        }

    }
}
