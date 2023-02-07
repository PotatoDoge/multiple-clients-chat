import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.DatagramPacket;
import java.util.concurrent.TimeUnit;
public class MulticastServerTest {
    public static void main(String[] args) {
        try {
            MulticastSocket multicastSocket = new MulticastSocket(5000);
            InetAddress group = InetAddress.getByName("225.5.5.5");
            multicastSocket.joinGroup(group);
            multicastSocket.setTimeToLive(50);
            for (int i = 0; i < 12; i++) {
                String msg = "192.168.1.93";
                System.out.println("Sending packet with message: " + msg);
                DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), group, 5000);
                multicastSocket.send(packet);
                TimeUnit.SECONDS.sleep(5);
            }
            System.out.println("Done");
        } catch(Exception e) {
            System.out.println("Failed to create multicast socket");
            e.printStackTrace();
        }
    }
}
