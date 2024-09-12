import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
//        Clock digitalClock12 = new DigitalClock(DigitalClock.Type.H12);
//        Clock digitalClock24 = new DigitalClock(DigitalClock.Type.H24);
//
//        digitalClock12.setTime(0, 0, 0);
//        digitalClock24.setTime(0, 0, 0);
//        System.out.println(digitalClock12 + " - " + digitalClock24);
//
//        digitalClock12.setTime(0, 1, 1);
//        digitalClock24.setTime(0, 1, 1);
//        System.out.println(digitalClock12 + " - " + digitalClock24);
//
//        digitalClock12.setTime(1, 0, 0);
//        digitalClock24.setTime(1, 0, 0);
//        System.out.println(digitalClock12 + " - " + digitalClock24);
//
//        digitalClock12.setTime(11, 59, 0);
//        digitalClock24.setTime(11, 59, 0);
//        System.out.println(digitalClock12 + " - " + digitalClock24);
//
//        digitalClock12.setTime(12, 0, 0);
//        digitalClock24.setTime(12, 0, 0);
//        System.out.println(digitalClock12 + " - " + digitalClock24);
//
//        digitalClock12.setTime(13, 0, 0);
//        digitalClock24.setTime(13, 0, 0);
//        System.out.println(digitalClock12 + " - " + digitalClock24);
//
//        digitalClock12.setTime(23, 59, 0);
//        digitalClock24.setTime(23, 59, 0);
//        System.out.println(digitalClock12 + " - " + digitalClock24);

        Map<String, City> cities = City.parseFile("strefy.csv");

//        DigitalClock c24 = new DigitalClock(DigitalClock.Type.H24, cities.get("Warszawa"));
//        c24.setTime(12, 0, 0);
//        c24.setCity(cities.get("Kijów"));
//        System.out.println(c24);

//        City lublin = cities.get("Lublin");
//        LocalTime time = LocalTime.of(12, 0, 0);
//        System.out.println(lublin.localMeanTime(time));

//        ArrayList<City> citiesList = new ArrayList<>(cities.values());
//        citiesList.sort(City::worstTimezoneFit);
//        for (City c : citiesList) {
//            System.out.println(c.getCapital());
//        }

            AnalogClock ac = new AnalogClock(cities.get("Warszawa"));
            ac.setTime(12, 0, 0);

            ArrayList<City> citiesList = new ArrayList<>(cities.values());

            City.generateAnalogClocksSvg(citiesList, ac);
    }
}