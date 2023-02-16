import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private final ArrayList<Integer> numbers = new ArrayList<>();
    public String key;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the username for the group chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost",5600);
        Client client = new Client(socket,username);
        client.listenForMessage();
        client.sendMessage();
    }

    public Client(Socket socket, String username){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            key = dis.readUTF();
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
        catch (IOException e){
            closeEverything();
        }
    }

    public void sendMessage(){
        new Thread(() -> {
            try{
                Scanner scanner = new Scanner(System.in);
                while (socket.isConnected()){
                    String messageToSend = scanner.nextLine();
                    //encriptar y compresión va aquí
                    String encrypted = Encryption.encrypt(username + ": " + messageToSend,key);
                    encrypted = Compression.encodeString(encrypted);
                    bufferedWriter.write(encrypted);
                    bufferedWriter.newLine();
                    bufferedWriter.flush(); // sends to buffer
                }
            }
            catch (IOException e){
                closeEverything();
                e.printStackTrace();
            }
        }).start();
    }

    public void listenForMessage(){
        new Thread(() -> {
            String msfFromGroupChat = "";

            while(socket.isConnected()){
                try{
                    msfFromGroupChat = bufferedReader.readLine();
                    msfFromGroupChat = Compression.decodeString(msfFromGroupChat);
                    msfFromGroupChat = Encryption.decrypt(msfFromGroupChat,key);
                    int n = Integer.parseInt(msfFromGroupChat);
                    numbers.add(n);
                    System.out.println(numbers);
                }
                catch (IOException e){
                    closeEverything();
                }
                catch (NumberFormatException e){
                    System.out.println(msfFromGroupChat);
                }
            }
        }).start();
    }

    private void closeEverything() {
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
}
