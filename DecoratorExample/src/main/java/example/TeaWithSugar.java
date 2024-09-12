package example;

public class TeaWithSugar extends Tea {
    private static final int kcal = 20;
    private Tea tea;

    public TeaWithSugar(Tea tea) {
        this.tea = tea;
    }

    @Override
    public int getKcal() {
        return tea.getKcal() + kcal;
    }
}
