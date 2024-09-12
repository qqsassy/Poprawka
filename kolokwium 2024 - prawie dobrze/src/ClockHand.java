import java.time.LocalTime;

public abstract class ClockHand {
    private LocalTime time;

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String toSvg() {
        return "<line x1=\"0\" y1=\"0\" x2=\"0\" y2=\"-80\" stroke=\"red\" stroke-width=\"1\" />";
    }
}
