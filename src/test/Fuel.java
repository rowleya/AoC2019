package test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Fuel {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("input"));
        String line;
        long sum = 0;
        while ((line = reader.readLine()) != null) {
            long mass = Long.parseLong(line);
            long fuel = ((mass / 3) - 2);
            sum += fuel;
            long fuelOfFuel = (fuel / 3) - 2;
            while (fuelOfFuel > 0) {
                sum += fuelOfFuel;
                fuelOfFuel = (fuelOfFuel / 3) - 2;
            }
        }
        System.err.println(sum);
        reader.close();
    }
}
