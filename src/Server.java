import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    public static final String key = Encryption.setKey();
    private boolean canConnect = true;

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
                if(!canConnect) {
                    clientHandler.refuseHandler();
                    System.out.println("Refused client");
                } else if(clientHandler.getNumberOfClients() > 4){
                    canConnect = false;
                    ClientHandler.sendNumbers();
                        System.out.println("Game start");
                        ClientHandler.broadcastMessage("Game starting");
                    }
                }
        }
        catch (IOException e){
            closeServerSocket();
        }
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
