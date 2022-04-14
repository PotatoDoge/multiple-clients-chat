public class Test {
    public static void main(String[] args) {
        String key = "qlk";
        //String text = "The quick brown fox jumps over 13 lazy dogs.";
        char x = (char) 65535;
        System.out.println(x);
        String text = "The 1 (one) quick brown fox; jumps, over the lazy dog!!!11ñЛ";
        System.out.println("Original: " + text);
        String e = Encryption.encrypt(text,key);
        System.out.println("Encriptado: " + e);
        String d = Encryption.decrypt(e,key);
        System.out.println("Desencriptado: " + d);
    }
}
