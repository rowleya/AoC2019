package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Coords {
    public int x;
    public int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class AsteroidFromPoint implements Comparable<AsteroidFromPoint> {
    public Coords point;
    public Coords asteroid;
    public double angle;
    public Set<Coords> blockedBy = new HashSet<>();

    public AsteroidFromPoint(Coords point, Coords asteroid) {
        this.point = point;
        this.asteroid = asteroid;
        int x1 = 0;
        int y1 = 10;
        int x2 = asteroid.x - point.x;
        int y2 = asteroid.y - point.y;
        this.angle = Math.atan2(x1 * y2 - y1 * x2, x1 * x2 + y1 * y2);
        if (point.x == asteroid.x && point.y > asteroid.y) {
            this.angle = -Math.PI;
        }
    }

    public void addBlocker(Coords c) {
        blockedBy.add(c);
    }

    @Override
    public int compareTo(AsteroidFromPoint other) {
        if (blockedBy.size() == other.blockedBy.size()) {
            if (other.angle == angle) {
                return 0;
            }
            return angle > other.angle? 1: -1;
        }
        return blockedBy.size() - other.blockedBy.size();
    }


}

public class Asteroids {

    private static int findGCD(int number1, int number2) {
        //base case
        if (number2 == 0) {
            return number1;
        }
        return findGCD(number2, number1 % number2);
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("asteroids"));
        String line;
        List<Coords> coords = new ArrayList<Coords>();
        int y = 0;
        int width = 0;
        while ((line = reader.readLine()) != null) {
            width = line.length();
            for (int x = 0; x < width; x++) {
                if (line.charAt(x) != '.') {
                    coords.add(new Coords(x, y));
                }
            }
            y++;
        }
        reader.close();
        int height = y;

        boolean[][] isAsteroid = new boolean[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                isAsteroid[j][i] = false;
            }
        }
        for (Coords c : coords) {
            isAsteroid[c.x][c.y] = true;
        }

        int maxAsteroids = 0;
        Coords maxCoords = null;
        Map<Coords, List<AsteroidFromPoint>> all = new HashMap<>();
        for (Coords pos : coords) {
            int i = pos.x;
            int j = pos.y;
            int n = 0;
            List<AsteroidFromPoint> allFromMe = new ArrayList<>();
            for (Coords c : coords) {
                if (c.x == i && c.y == j) {
                    continue;
                }
                AsteroidFromPoint thisFromMe = new AsteroidFromPoint(pos, c);
                allFromMe.add(thisFromMe);
                int xdist = Math.abs(i - c.x);
                int ydist = Math.abs(j - c.y);
                boolean isBlocked = false;
                if (ydist == 0) {
                    for (int k = Math.min(i, c.x) + 1; k < Math.max(i, c.x); k++) {
                        System.err.println("    Checking " + k + ", " + j);
                        if (isAsteroid[k][j]) {
                            System.err.println("        Blocked!");
                            isBlocked = true;
                            thisFromMe.addBlocker(new Coords(k, j));
                        }
                    }
                } else if (xdist == 0) {
                    for (int k = Math.min(j, c.y) + 1; k < Math.max(j, c.y); k++) {
                        System.err.println("    Checking " + i + ", " + k);
                        if (isAsteroid[i][k]) {
                            System.err.println("        Blocked!");
                            isBlocked = true;
                            thisFromMe.addBlocker(new Coords(i, k));
                        }
                    }
                } else {
                    int gcd = findGCD(Math.max(xdist, ydist), Math.min(xdist, ydist));
                    int xmove = xdist / gcd;
                    int ymove = ydist / gcd;
                    int nmoves = xdist / xmove;
                    if (i > c.x) {
                        xmove = -xmove;
                    }
                    if (j > c.y) {
                        ymove = -ymove;
                    }
                    int k = i + xmove;
                    int l = j + ymove;
                    for (int move = 0; move < nmoves && k != c.x && l != c.y; move++) {
                        System.err.println("    Checking " + k + ", " + l);
                        if (isAsteroid[k][l]) {
                            System.err.println("        Blocked!");
                            isBlocked = true;
                            thisFromMe.addBlocker(new Coords(k, l));
                        }
                        k += xmove;
                        l += ymove;
                    }
                }
                if (!isBlocked) {
                    System.err.println(c.x + "," + c.y + " not blocked from " + i + "," + j);
                    n++;
                }
            }
            System.err.println(n + " asteroids can be seen from (" + i + "," + j + ")");
            if (n > maxAsteroids) {
                maxAsteroids = n;
                maxCoords = pos;
            }
            all.put(pos, allFromMe);
        }
        System.err.println("(" + maxCoords.x + "," + maxCoords.y + ") " + maxAsteroids);

        List<AsteroidFromPoint> allFromBest = all.get(maxCoords);
        allFromBest.sort(null);
        int i = 1;
        for (AsteroidFromPoint a : allFromBest) {
            System.err.println(i + ": " + a.asteroid.x + ", " + a.asteroid.y + " - " + a.angle + ":" + a.blockedBy.size());
            i++;
        }
        AsteroidFromPoint chosen = allFromBest.get(199);
        System.err.println("(" + chosen.asteroid.x + "," + chosen.asteroid.y + ") " + ((chosen.asteroid.x * 100) + chosen.asteroid.y));
    }
}
