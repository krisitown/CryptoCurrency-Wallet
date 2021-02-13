package bg.sofia.uni.fmi.mjt.crypto.server;

import bg.sofia.uni.fmi.mjt.crypto.commands.Command;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.InsufficientFundsException;
import bg.sofia.uni.fmi.mjt.crypto.commands.exception.NotLoggedInException;
import bg.sofia.uni.fmi.mjt.crypto.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.crypto.factory.exception.CommandNotFoundException;
import bg.sofia.uni.fmi.mjt.crypto.factory.exception.UnsupportedCommandArgumentsException;
import bg.sofia.uni.fmi.mjt.crypto.factory.impl.ServerCommandFactory;
import bg.sofia.uni.fmi.mjt.crypto.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CryptoWalletRequestHandler implements RequestHandler {
    private Socket socket;
    private User principal;
    private CommandExecutor commandExecutor;

    public CryptoWalletRequestHandler(Socket socket, ApplicationContext applicationContext) {
        this.socket = socket;
        this.commandExecutor = new CommandExecutor(applicationContext, this);
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(commandExecutor.executeCommand(inputLine));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public User getPrincipal() {
        return principal;
    }

    public void setPrincipal(User principal) {
        this.principal = principal;
    }
}
