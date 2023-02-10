import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

public class ClientHandler implements Runnable {

    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // holds all the clients running
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private String key;
    private static int clients = 0;

    public ClientHandler(Socket socket, String key) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            this.key = key;
            clientHandlers.add(this);
            String msg = "SERVER: " + clientUsername + " has entered the chat!";
            msg = Encryption.encrypt(msg,key);
            msg = Compression.encodeString(msg);
            broadcastMessage(msg);
        } catch (Exception e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        String msg = "SERVER: " + clientUsername + " has left the chat!";
        msg = Encryption.encrypt(msg,key);
        msg = Compression.encodeString(msg);
        broadcastMessage(msg);
    }

    public void refuseHandler() throws IOException {
        String encrypted = Encryption.encrypt("refused connection",key);
        encrypted = Compression.encodeString(encrypted);
        this.bufferedWriter.write(encrypted);
        this.bufferedWriter.newLine();
        this.bufferedWriter.flush();
        closeEverything(this.socket, this.bufferedReader, this.bufferedWriter);
        clients--;
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                int ranNum = r.nextInt((8)) + 11; // [11,19]
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (ClientHandler clientHandler : clientHandlers) {
                    try {
                            String encrypted = Encryption.encrypt(String.valueOf(ranNum),  Server.key);
                            encrypted = Compression.encodeString(encrypted);
                            clientHandler.bufferedWriter.write(encrypted);
                            clientHandler.bufferedWriter.newLine();
                            clientHandler.bufferedWriter.flush();

                    }
                    catch (Exception e){
                        System.out.println(e);
                    }
                }
            }
        }).start();
    }
}
