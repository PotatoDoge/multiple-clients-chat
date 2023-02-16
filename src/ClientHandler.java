import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class ClientHandler implements Runnable {

    private static final ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // holds all the clients running
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private static int clients = 0;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            String msg = "SERVER: " + clientUsername + " has entered the chat!";
            msg = Encryption.encrypt(msg,Server.key);
            msg = Compression.encodeString(msg);
            sendMessage(msg);
        } catch (Exception e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                sendMessage(messageFromClient);
            } catch (Exception e) {
                closeEverything();
                break;
            }
        }
    }

    public void whisperMessage(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch (Exception e) {
            closeEverything();
        }
    }

    public static void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.whisperMessage(messageToSend);
            } catch (Exception e) {
                clientHandler.closeEverything();
            }
        }
    }

    public void sendMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.whisperMessage(messageToSend);
                }
            } catch (Exception e) {
                closeEverything();
            }
        }
    }

    public void refuseHandler() throws IOException {
        this.whisperMessage(Encryption.encrypt(Compression.encodeString("Can't connect, game already started."), Server.key));
        closeEverything();
        clients--;
    }

    public void closeEverything() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void connectedClient(){
        clients++;
    }

    public int getNumberOfClients(){
        return clients;
    }

    public static void sendNumbers(){
        new Thread(() -> {
            Random r = new Random();
            int ranNum = r.nextInt((8)) + 11; // [11,19]
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            broadcastMessage(String.valueOf(ranNum));
        }).start();
    }
}
