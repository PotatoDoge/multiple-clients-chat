public class Test {
    public static void main(String[] args) {
        String key = "埼䚃档媩\u0C8D㮸勠䣲";
        //String text = "The quick brown fox jumps over 13 lazy dogs.";
        char x = (char) 65535;
        System.out.println(x);
        String text = "The 1 (one) quick brown fox; jumps, over the lazy dog!!13098r3-490wionsfsf54/sef´``+``çf!11ñЛ";
        //String text = "asljdnawjdn84y98qdaad.w-d adwwadawdawdlmwadaw+d``ad0w9ea d.a-d.wadj ";
        System.out.println("Original: " + text);
        String e = Encryption.encrypt(text,key);
        System.out.println("Encriptado: " + e);
        String d = Encryption.decrypt(e,key);
        System.out.println("Desencriptado: " + d);
        System.out.println(text.equals(d));
    }
}
