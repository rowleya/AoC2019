package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Orbiter {
    public String id;
    public Orbiter parent;
    public List<Orbiter> orbiters = new ArrayList<Orbiter>();

    public Orbiter(String id) {
        this.id = id;
    }

    public void setParent(Orbiter parent) {
        if (this.parent != null) {
            throw new RuntimeException("Non null parent on " + id);
        }
        this.parent = parent;
    }

    public void addOrbiter(Orbiter orbiter) {
        orbiters.add(orbiter);
        System.err.println("Adding " + orbiter.id + " to " + id);
    }

    public int lengthOfOrbits() {
        int len = 0;
        for (Orbiter orbiter : orbiters) {
            int oLen = orbiter.lengthOfOrbits();
            len += oLen + 1;
        }
        return len;
    }

    public int distanceTo(String other, Orbiter start) {
        if (id.equals(other)) {
            System.err.println("This is " + other);
            return 0;
        }
        int minLength = Integer.MAX_VALUE - 1;
        for (Orbiter orbiter : orbiters) {
            if (orbiter != start) {
                int distance = orbiter.distanceTo(other, this) + 1;
                System.err.println("Distance from " + id + " to " + other + " via " + orbiter.id + " = " + distance);
                if (distance < minLength) {
                    minLength = distance;
                }
            }
        }
        if (parent != start && parent != null) {
            int distance = parent.distanceTo(other, this) + 1;
            System.err.println("Distance from " + id + " to " + other + " via " + parent.id + " = " + distance);
            if (distance < minLength) {
                minLength = distance;
            }
        }
        return minLength;
    }
}

public class Orbit {

    public static Orbiter getOrbiter(Map<String, Orbiter> orbits, Set<String> roots, String id) {
        if (orbits.containsKey(id)) {
            return orbits.get(id);
        }
        Orbiter o = new Orbiter(id);
        orbits.put(id, o);
        roots.add(id);
        System.err.println("Root " + id);
        return o;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("orbits"));
        String line;
        Map<String, Orbiter> orbits = new HashMap<String, Orbiter>();
        Set<String> roots = new HashSet<>();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\\)", 2);
            String id = parts[0];
            String orbiter = parts[1];
            Orbiter a = getOrbiter(orbits, roots, id);
            Orbiter o = getOrbiter(orbits, roots, orbiter);
            a.addOrbiter(o);
            o.setParent(a);
            System.err.println("Not root " + orbiter);
            roots.remove(orbiter);
        }
        reader.close();

        int sum = 0;
        for (String id : orbits.keySet()) {
            System.err.println("Using root " + id);
            sum += orbits.get(id).lengthOfOrbits();
        }
        System.err.println(sum);

        Orbiter you = orbits.get("YOU");
        System.err.println(you.distanceTo("SAN", null) - 2);
    }

}
