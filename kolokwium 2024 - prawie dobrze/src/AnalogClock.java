import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnalogClock extends Clock {
    private final List<ClockHand> hands;

    public AnalogClock(City city) {
        super(city);

        ClockHand hour = new HourHand();
        hour.setTime(this.time);
        ClockHand minute = new MinuteHand();
        minute.setTime(this.time);
        ClockHand second = new SecondHand();
        second.setTime(this.time);

        hands = new ArrayList<>();
        hands.add(hour);
        hands.add(minute);
        hands.add(second);
    }

    @Override
    void setTime(int hour, int minute, int second) {
        super.setTime(hour, minute, second);
        for (ClockHand h : hands) {
            h.setTime(this.time);
        }
    }

    @Override
    void setCurrentTime() {
        super.setCurrentTime();
        for (ClockHand h : hands) {
            h.setTime(this.time);
        }
    }

    public void toSvg(String filePath) throws IOException {

        String svg;
        try (Stream<String> lines = Files.lines(Paths.get("zegar.svg"))) {
            ArrayList<String> linesStr = new ArrayList<>();

            lines.map(String::trim)
                    .filter(line -> !line.startsWith("<line"))
                    .forEach(linesStr::add);

            linesStr.remove(linesStr.size() -1 );

            for (ClockHand o : hands) {
                linesStr.add(o.toSvg());
            }

            linesStr.add("</svg>");

            svg = String.join("", linesStr);
        }

        Files.writeString(Paths.get(filePath), svg);
    }
}
