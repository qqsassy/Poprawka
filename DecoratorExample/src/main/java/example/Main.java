package example;

public class Main {
    public static void main(String[] args) {
        Tea herbata = new Tea();
        Tea herbataZCukrem = new TeaWithSugar(herbata);
        Tea herbataZCukremIMlekiem = new TeaWithMilk((herbataZCukrem));
        System.out.println(herbataZCukremIMlekiem.getKcal());
    }
}