import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    public static final String key = Encryption.setKey();
    private boolean canConnect = true;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
                while(!serverSocket.isClosed()){
                    Socket socket = serverSocket.accept();
                    ClientHandler.sendNumbers();
                    System.out.println("A new client has connected!");
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    dos.writeUTF(key); // sends key to clients
                    ClientHandler clientHandler = new ClientHandler(socket,key);
                    clientHandler.connectedClient();
                    System.out.println("Number of connected clients: " + clientHandler.getNumberOfClients());
                    if(clientHandler.getNumberOfClients() > 4){
                        canConnect = false;
                    }
                    Thread thread = new Thread(clientHandler);
                    thread.start();

                    if(!canConnect){
                        System.out.println("refused client");
                        clientHandler.refuseHandler();
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
