import java.time.format.DateTimeFormatter;

public class DigitalClock extends Clock {
    public enum Type { H12, H24 }
    Type clockType;

    public DigitalClock(Type clockType, City city) {
        super(city);
        this.clockType = clockType;
    }

    @Override
    public String toString() {
        if (clockType == Type.H24)
            return super.toString();
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm:ss a");
            return time.format(formatter);
        }
    }
}
