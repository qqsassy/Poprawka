package example;

public class TeaWithMilk extends Tea {
    private static final int kcal = 30;
    private Tea tea;

    public TeaWithMilk(Tea tea) {
        this.tea = tea;
    }

    @Override
    public int getKcal() {
        return tea.getKcal() + kcal;
    }
}
