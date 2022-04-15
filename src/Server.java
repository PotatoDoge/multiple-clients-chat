import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final ServerSocket serverSocket;
    private final String key = Encryption.setKey();

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
                ClientHandler clientHandler = new ClientHandler(socket,key);
                Thread thread = new Thread(clientHandler);
                thread.start();
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
