import java.util.ArrayList;
import java.util.Arrays;

public class Compression {

    public static void main(String[] args) {
        String encode = encodeString("The quick brown fox jumps over the lazy dog.");
        System.out.println(encode);
        String decode = decodeString(encode);
        System.out.println(decode);

    }

    private static String[] alphabet = new String[] {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
            "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "+", "/" };

    public static String encodeString(String input) {
        // Split string into chars
        char[] arr = input.toCharArray();
        ArrayList<String> list = new ArrayList<>();
        for (char c : arr) {
            // get list of ascii values
            list.add(Integer.valueOf(c).toString());
        }
        for (int i = 0; i < list.size(); i++) {
            // translate all values to binary
            String el = list.get(i);
            int bin = Integer.valueOf(toDigits(el, 2));
            el = String.format("%08d", bin);
            list.set(i, el);
        }
        StringBuilder sb = new StringBuilder();
        for (String string : list) {
            // concatenate binary values
            sb.append(string);
        }
        String concat = sb.toString();
        int padding = arr.length % 3;
        padding = padding == 3 ? 0 : padding == 0 ? 0 : 3 - padding;
        sb = new StringBuilder(concat);
        for (int i = 0; i < padding * 2; i++) {
            sb.append("0");
        }
        concat = sb.toString();
        sb = new StringBuilder();
        for (int i = 0; i < concat.length() - 1; i += 6) {
            // split concatenated binary into groups of 6, convert each group to B64, and
            // add to string
            sb.append(binaryToBase64(concat.substring(i, i + 6)));
        }
        for (int i = 0; i < padding; i++) {
            sb.append("=");
        }
        // return string encoded in base64
        return sb.toString();
    }

    public static String decodeString(String input) {
        // Split string into chars
        char[] arr = input.toCharArray();
        ArrayList<String> list = new ArrayList<>();
        int padding = 0;
        for (char ch : arr) {
            // translate all values to binary and compile in list
            if (String.valueOf(ch).equals("=")) {
                padding += 1;
            } else {
                String bin = base64ToBinary(String.valueOf(ch));
                bin = String.format("%06d", Integer.valueOf(bin));
                list.add(bin);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String string : list) {
            // concat all binaries
            sb.append(string);
        }
        String concat = sb.toString();
        concat = concat.substring(0, concat.length() - (padding * 2));
        sb = new StringBuilder();
        for (int i = 0; i < concat.length(); i += 8) {
            // split concatenated binary into groups of 8, convert each group to a ascii
            // char, and add to string
            int ascii = Integer.valueOf(fromDigits(concat.substring(i, i + 8), 2));
            char ch = (char) ascii;
            sb.append(ch);
        }
        // return string encoded in ascii
        return sb.toString();
    }

    private static String binaryToBase64(String input) {
        int index = Integer.valueOf(fromDigits(input, 2));
        return alphabet[index];
    }

    public static String base64ToBinary(String input) {
        int i;
        for (i = 0; i < alphabet.length; i++) {
            if (alphabet[i].equals(input)) {
                break;
            }
        }
        return toDigits(String.valueOf(i), 2);
    }

    private static String fromDigits(String input, int b) {
        char[] digits = input.toCharArray();
        double acc = 0.0;
        for (int i = digits.length - 1; i >= 0; i--) {
            acc += Integer.valueOf(String.valueOf(digits[i])) * Math.pow(b, digits.length - i - 1);
        }
        return String.valueOf((int) acc);
    }

    private static String toDigits(String inputString, int b) {
        int input = Integer.valueOf(inputString);
        StringBuilder sb = new StringBuilder();
        while (input > 0) {
            int val = input % b;
            input /= b;
            sb.append(val);
        }
        return sb.reverse().toString();
    }
}