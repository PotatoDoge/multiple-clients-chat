import java.util.*;

public class Encryption {

    public static String encrypt(String input, String key) {
        StringBuilder cyphered = new StringBuilder();
        long newValue;
        int s = 1;
        for (int i = 0; i < input.length(); i++) {
            newValue = ((long) input.charAt(i) + ((long) key.charAt(i % key.length()) - 32) * s);
            if (newValue > 254) {
                newValue = 32 + (newValue - 255);
            } else if (newValue < 32) {
                newValue = 255 - (32 - newValue);
            }
            cyphered.append((char) newValue);
            s = s * -1;
        }
        return cyphered.toString();
    }

    public static String decrypt(String input, String key) {
        StringBuilder cyphered = new StringBuilder();
        long newValue;
        int s = -1;
        for (int i = 0; i < input.length(); i++) {
            newValue = ((long) input.charAt(i) + ((long) key.charAt(i % key.length()) - 32) * s);
            if (newValue > 254) {
                newValue = 32 + (newValue - 255);
            } else if (newValue < 32) {
                newValue = 255 - (32 - newValue);
            }
            cyphered.append((char) newValue);
            s = s * -1;
        }
        return cyphered.toString();
    }

    public static String setKey() {
        StringBuilder k = new StringBuilder();
        Random r = new Random();
        int ranLength = r.nextInt((3)) + 6; // [6,9]
        for (int i = 0; i < ranLength; i++) {
            long ranChar = r.nextInt(222) + 33; // [33,65502]
            // k.append(alphabet.get((int) (Math.random() * 100 % 26)));
            k.append((char) ranChar);
        }
        return k.toString();
    }
}
