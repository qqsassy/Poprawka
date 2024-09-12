package example;

public class Main {
    public static void main(String[] args) {
        House house = new House.Builder()
                .addHasGarage(true)
                .addOnSale(false)
                .addSquareFootage(20.0)
                .wallColor(House.Color.RED)
                .build();
    }
}