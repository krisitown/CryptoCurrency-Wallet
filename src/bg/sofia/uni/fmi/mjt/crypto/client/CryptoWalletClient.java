package bg.sofia.uni.fmi.mjt.crypto.client;

import bg.sofia.uni.fmi.mjt.crypto.ServerConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CryptoWalletClient {
    private static final int SERVER_PORT = 4444;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", SERVER_PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();

                if ("disconnect".equals(message)) {
                    break;
                }

                writer.println(message);

                String reply = reader.readLine();
                if(reply == null) {
                    System.out.println("Response from server is NULL!");
                    continue;
                }
                System.out.println(reply.replaceAll(ServerConstants.LINE_SEPERATOR, System.lineSeparator()));
            }

            System.out.println("Disconnected from server");

        } catch (IOException e) {
            System.out.println("There is a problem with the network communication");
            e.printStackTrace();
        }
    }
}
