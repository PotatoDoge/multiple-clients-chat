import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private final ArrayList<Integer> numbers = new ArrayList<>();
    public String key;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the username for the group chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost",5600);
        Client client = new Client(socket,username);
        client.listenForMessage();
    }

    public Client(Socket socket, String username){
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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

    private final Thread sendMessage = new Thread(() -> {
        try{
            int res = numbers.get(0);
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){

                System.out.println("Números");
                System.out.println(numbers);

                System.out.println("Selecciona el índice del número");
                int target = numbers.get(scanner.nextInt());
                scanner.nextLine();

                System.out.println("Opciones:\n1: Suma\n2: Resta\n 3: Multiplicación\n 4: División");
                int op = scanner.nextInt();
                scanner.nextLine();

                switch (op){
                    case 1:
                        res += target;
                    case 2:
                        res -= target;
                    case 3:
                        res *= target;
                    case 4:
                        res /= target;
                }

                System.out.println("Resultado: " + res);

                if (res == 100){
                    System.out.println("Ganaste!");
                    String msg = Encryption.encrypt("win",key);
                    msg = Compression.encodeString(msg);
                    bufferedWriter.write(msg);
                    bufferedWriter.newLine();
                    bufferedWriter.flush(); // sends to buffer
                }
            }
        }
        catch (IOException e){
            closeEverything();
            e.printStackTrace();
        }
    });


    public void listenForMessage(){
        new Thread(() -> {
            String msg = "";

            while(socket.isConnected()){
                try{
                    msg = bufferedReader.readLine();
                    msg = Compression.decodeString(msg);
                    msg = Encryption.decrypt(msg,key);
                    int n = Integer.parseInt(msg);
                    numbers.add(n);
                    System.out.println("Nuevo número: " + n);
                    System.out.println(numbers);

                    if (numbers.size() > 25){
                        System.out.println("Perdiste :(");
                        msg = Encryption.encrypt("loose",key);
                        msg = Compression.encodeString(msg);
                        bufferedWriter.write(msg);
                        bufferedWriter.newLine();
                        bufferedWriter.flush(); // sends to buffer
                        closeEverything();
                    }
                }
                catch (IOException e){
                    closeEverything();
                }
                catch (NumberFormatException e){
                    if (msg.equals("Start")){
                        System.out.println("El juego ha comenzado.");
                        sendMessage.start();
                    } else {
                    System.out.println("Perdiste, el ganador es: " + msg);
                    }
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
