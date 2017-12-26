package client;

public class test {
    public static void main(String[] args) {
        String s = "\\\"";
        System.out.println(s);
        if (s == "\\\"") {
            s = "\"";
        }
        System.out.println(s);
    }
}
