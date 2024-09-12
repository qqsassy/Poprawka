import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class Clock {
    protected LocalTime time;
    private City city;

    public Clock(City city) {
        this.city = city;
    }

    void setCurrentTime() {
        time = LocalTime.now();
    }

    void setTime(int hour, int minute, int second) {
        if (hour < 0 || hour > 23)
            throw new IllegalArgumentException("Hour " + hour + " has to be between 0 and 23.");
        else if (minute < 0 || minute > 59)
            throw new IllegalArgumentException("Minute " + minute + " has to be between 0 and 59.");
        else if (second < 0 || second > 59)
            throw new IllegalArgumentException("Second " + second + " has to be between 0 and 59.");

        time = LocalTime.of(hour, minute, second);
    }

    @Override
    public String toString() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            return time.format(formatter);
    }

    public void setCity(City city) {
        Double currentTimeZone = this.city.getSummerTimeZone();
        if (currentTimeZone == null)
            currentTimeZone = 0.0;

        Double newTimeZone = city.getSummerTimeZone();
        if (newTimeZone == null)
            newTimeZone = 0.0;

        Double timeDifference = newTimeZone - currentTimeZone;

        time = time.plusHours(timeDifference.longValue()).plusMinutes((int) ((timeDifference % 1) * 60));

        this.city = city;
    }
}
