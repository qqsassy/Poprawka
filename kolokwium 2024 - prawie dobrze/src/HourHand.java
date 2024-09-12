import java.time.LocalTime;

public class HourHand extends ClockHand {
    private LocalTime time;

    @Override
    public void setTime(LocalTime time) {
        this.time = time;
    }

    @Override
    public String toSvg() {
        return "<line x1=\"0\" y1=\"0\" x2=\"0\" y2=\"-80\" stroke=\"black\" stroke-width=\"4\" />";
    }
}
