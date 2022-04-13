import java.util.*;

public class Encryption {

    public Encryption(){}

    public static String encrypt(String input){
        return null;
    }

    public static String decrypt(String input){
        return null;
    }

    public static String setKey(){
        StringBuilder k = new StringBuilder();
        char []alphabet = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                'h', 'i', 'j', 'k', 'l', 'm', 'n',
                'o', 'p', 'q', 'r', 's', 't', 'u',
                'v', 'w', 'x', 'y', 'z' };
        Random r = new Random();
        int ranLength = r.nextInt((3)) + 6; // [6,9]
        for (int i = 0; i < ranLength; i++) { k.append(alphabet[(int) (Math.random() * 100 % 26)]); }
        //generatedKey = true;
        return k.toString();
    }
}
