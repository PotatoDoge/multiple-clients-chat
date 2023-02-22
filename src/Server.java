import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    public static final String key = Encryption.setKey();
    private boolean gameStarted = false;
    private static final int minPlayers = 2;

    public static void main(String[] args) throws IOException {
        System.out.println("Server running!");
        ServerSocket serverSocket = new ServerSocket(5600);
        Server server = new Server(serverSocket);
        server.startServer();
    }

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(key); // sends key to clients
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.connectedClient();
                System.out.println("Number of connected clients: " + clientHandler.getNumberOfClients());
                Thread thread = new Thread(clientHandler);
                thread.start();
                if(!gameStarted && clientHandler.getNumberOfClients() > minPlayers) {
                    gameStarted = true;
                    announce("Start");
                    ClientHandler.sendNumbers();
                } else if(gameStarted){
                    clientHandler.refuseHandler();
                    System.out.println("Refused client");
                }
            }
        }
        catch (IOException e){
            closeServerSocket();
        }
    }

    public static void announce(String msg){
        msg = Encryption.encrypt(msg, key);
        msg = Compression.encodeString(msg);
        ClientHandler.broadcastMessage(msg);
    }

    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
