import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.TimeUnit;

public class MulticastListenerTest {
    public static void main(String[] args) {
        try {
            MulticastSocket multicastSocket = new MulticastSocket(5000);
            InetAddress group = InetAddress.getByName("225.5.5.5");
            multicastSocket.joinGroup(group);
            for (int i = 0; i < 12; i++) {
                byte[] buf = new byte[20];
                DatagramPacket receive = new DatagramPacket(buf, buf.length);
                System.out.println("Receiving packet...");
                multicastSocket.receive(receive);
                System.out.println("Received packet with message: " + new String(receive.getData()));
                TimeUnit.SECONDS.sleep(5);
            }
            System.out.println("Done");
        } catch(Exception e) {
            System.out.println("Failed to create multicast socket");
            e.printStackTrace();
        }
    }
}
