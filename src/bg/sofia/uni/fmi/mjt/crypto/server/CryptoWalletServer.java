package bg.sofia.uni.fmi.mjt.crypto.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CryptoWalletServer {
    private static final int DEFAULT_PORT = 4444;
    private static final int MAX_EXECUTOR_THREADS = 10;

    private int port = 4444;
    private ServerSocket serverSocket;
    private ApplicationContext applicationContext;

    public CryptoWalletServer(int port){
        this.port = port;
        this.applicationContext = new ApplicationContext();
    }

    public void start(){
        ExecutorService executor = Executors.newFixedThreadPool(MAX_EXECUTOR_THREADS);

        try {
            this.serverSocket = new ServerSocket(this.port);
            Socket clientSocket;

            while (true) {
                clientSocket = serverSocket.accept();

                System.out.println("Accepted connection request from client " + clientSocket.getInetAddress());
                CryptoWalletRequestHandler clientHandler = new CryptoWalletRequestHandler(clientSocket, applicationContext);
                executor.execute(clientHandler);
            }

        } catch (IOException e) {
            System.out.println("There is a problem with the server socket");
            e.printStackTrace();
        }
    }

    public void stop(){
        try {
            if(this.serverSocket == null || this.serverSocket.isClosed()){
                System.out.println("Trying to close not running server!");
                return;
            }
            this.serverSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }

        CryptoWalletServer server = new CryptoWalletServer(port);
        server.start();
    }
}

