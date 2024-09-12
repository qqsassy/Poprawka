package example;

public class House {
    enum Color {RED, GREEN, BLUE};
    private Color insideWallColor, outsideWallColor;
    private Double squareFootage;
    private Boolean onSale, hasGarage;

    public static class Builder {
        private Double squareFootage = 0.0;
        private Color wallColor;
        private Boolean onSale = false;
        private Boolean hasGarage = false;

        public Builder addSquareFootage(final Double squareFootage) {
            this.squareFootage = squareFootage;
            return this;
        }

        public Builder addOnSale(final Boolean onSale) {
            this.onSale = onSale;
            return this;
        }

        public Builder addHasGarage(final Boolean hasGarage) {
            this.hasGarage = hasGarage;
            return this;
        }

        public Builder wallColor(final Color wallColor) {
            this.wallColor = wallColor;
            return this;
        }

        public House build() {
            House house = new House();
            house.squareFootage = this.squareFootage;
            house.insideWallColor = this.wallColor;
            house.outsideWallColor = this.wallColor;
            house.onSale = this.onSale;
            house.hasGarage = this.hasGarage;
            return house;
        }
    }
}
