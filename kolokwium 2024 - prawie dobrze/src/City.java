import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class City {
    private String capital;
    private Double summerTimeZone;
    private String latitude, longitude;

    private static City parseLine(String line) {
        List<String> tokens = Arrays.stream(line.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        City city = new City();

        city.capital = tokens.get(0);
        city.summerTimeZone = Double.parseDouble(tokens.get(1));
        city.latitude = tokens.get(2);
        city.longitude = tokens.get(3);

        return city;
    }

    public static Map<String, City> parseFile(String filePath) throws IOException {
        Map<String, City> map = new HashMap<>();

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.skip(1)
                    .map(City::parseLine)
                    .forEach(city -> map.put(city.capital, city));
        }

        return map;
    }

    public String getCapital() {
        return capital;
    }

    public Double getSummerTimeZone() {
        return summerTimeZone;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public LocalTime localMeanTime(LocalTime time) {
        LocalTime newTime = LocalTime.from(time);

        boolean isLongitudeNegative = longitude.charAt(longitude.length() - 1) == 'W';

        double longitudeValue = Double.parseDouble(longitude.substring(0, longitude.length() - 2));
        if (isLongitudeNegative)
            longitudeValue = -longitudeValue;

        double hourOffset = 12 * (longitudeValue / 180);

        int hours = (int)(hourOffset);
        int seconds = (int)((hourOffset - hours) * 3600);
        int minutes = seconds / 60;
        seconds = seconds - minutes * 60;

        newTime = newTime
                .minusHours(summerTimeZone.longValue())
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);

        return newTime;
    }

    public static int worstTimezoneFit(City a, City b) {
        LocalTime time = LocalTime.of(0, 0, 0);
        LocalTime mean_a = a.localMeanTime(time);
        LocalTime mean_b = b.localMeanTime(time);

        Duration diff_a = Duration.between(mean_a, mean_b).abs();
        Duration diff_b = Duration.between(mean_a, mean_b).abs();

        return diff_b.compareTo(diff_a);
    }

    public static void generateAnalogClocksSvg(ArrayList<City> cities, AnalogClock clock) throws IOException {
        Path path = Paths.get("./" + clock.toString());
        Files.createDirectory(path);
        for (City city : cities) {
            Path filePath = Files.createFile(path.resolve(city.getCapital()));
            clock.toSvg(filePath.toString());
        }
    }
}
