import java.io.IOException;
import java.net.ServerSocket;

public class MainServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Server running!");
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
