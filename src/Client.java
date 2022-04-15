import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public String key;
    private boolean keyReceived = false;

    public Client(Socket socket, String username){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }
        catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(){
        try{

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                // encriptar y compresión va aquí
                String encrypted = Encryption.encrypt(username + ": " + messageToSend,key);
                bufferedWriter.write(encrypted);
                bufferedWriter.newLine();
                bufferedWriter.flush(); // sends to buffer
            }
        }
        catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
            e.printStackTrace();
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msfFromGroupChat;

                while(socket.isConnected()){
                    try{
                        // Constantly listens
                        // aquí se debería desencriptar, descomprimir,imprimir
                        msfFromGroupChat = bufferedReader.readLine();
                        msfFromGroupChat = Encryption.decrypt(msfFromGroupChat,key);
                        System.out.println(msfFromGroupChat);
                    }
                    catch (IOException e){
                        closeEverything(socket,bufferedReader,bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void getKeyFromServer() throws IOException {
        if(!keyReceived){
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            key = dis.readUTF();
            keyReceived = true;
        }
    }
}
