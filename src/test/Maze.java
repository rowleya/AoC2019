package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class PosM {
    int x;
    int y;

    public PosM(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return (x * 100000) + y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PosM) {
            PosM p = (PosM) obj;
            return p.x == x && p.y == y;
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

class State {
    Map<PosM, Character> map = new HashMap<PosM, Character>();
    Map<PosM, Character> doors = new HashMap<PosM, Character>();
    Map<PosM, Character> keys = new HashMap<PosM, Character>();
    Map<Character, PosM> keyPos = new HashMap<Character, PosM>();

    PosM move(PosM p, int dir) {
        switch (dir) {
        case 0:
            return new PosM(p.x + 1, p.y);
        case 1:
            return new PosM(p.x - 1, p.y);
        case 2:
            return new PosM(p.x, p.y + 1);
        case 3:
            return new PosM(p.x, p.y - 1);

        }
        throw new RuntimeException();
    }

    PosM getKey(char door) {
        return keyPos.get(Character.toLowerCase(door));
    }

    boolean isReachable(PosM p, Set<PosM> keysFound) {
        if (map.containsKey(p)) {
            return false;
        }
        if (doors.containsKey(p) && !keysFound.contains(getKey(doors.get(p)))) {
            return false;
        }
        return true;
    }

    PosM next(PosM p, Set<PosM> visited, Set<PosM> keysFound) {
        for (int i = 0; i < 4; i++) {
            PosM next = move(p, i);
            if (!visited.contains(next) && isReachable(next, keysFound)) {
                return next;
            }
        }
        return null;
    }

    List<PosM> nexts(PosM p, Set<PosM> visited, Set<PosM> keysFound) {
        List<PosM> result = new ArrayList<PosM>();
        for (int i = 0; i < 4; i++) {
            PosM next = move(p, i);
            if (!visited.contains(next) && isReachable(next, keysFound)) {
                result.add(next);
            }
        }
        return result;
    }

}

class KeyDist {
    PosM pos;
    int distance;

    public KeyDist(PosM pos, int distance) {
        this.pos = pos;
        this.distance = distance;
    }
}

public class Maze {

    public static Map<PosM, Integer> getMinKeys(State state, PosM start, Set<PosM> keysFound, Set<PosM> visited) {
        Set<PosM> keysFoundHere = new HashSet<>(keysFound);
        Map<PosM, Integer> distances = new HashMap<>();
        Set<PosM> visitedHere = new HashSet<PosM>(visited);
        visitedHere.add(start);
        PosM p = start;
        List<PosM> nexts = state.nexts(p, visitedHere, keysFoundHere);
        //System.err.println("Starting at " + start + " with keys from " + keysFoundHere + " next = " + nexts);

        int distance = 0;
        Deque<PosM> reverse = new ArrayDeque<>();
        while (nexts.size() == 1 && (distance == 0 || !state.keys.containsKey(p) || keysFoundHere.contains(p))) {
            PosM next = nexts.get(0);
            visitedHere.add(next);
            distance++;
            p = next;
            nexts = state.nexts(p, visitedHere, keysFoundHere);
        }

        if (distance > 0 && state.keys.containsKey(p) && !keysFoundHere.contains(p)) {
            distances.put(p, distance);
            keysFoundHere.add(p);
            //System.err.println("Found key " + state.keys.get(p) + " at " + p + " distance = " + distance + " from " + start);
            Set<PosM> visitedKey = new HashSet<>();
            Map<PosM, Integer> nextDistances = getMinKeys(state, p, keysFoundHere, visitedKey);
            for (Entry<PosM, Integer> d : nextDistances.entrySet()) {
                PosM key = d.getKey();
                int newD = d.getValue() + distance;
                if (distances.containsKey(key)) {
                    int currentD = distances.get(key);
                    if (newD < currentD) {
                        distances.put(key, newD);
                    }
                } else {
                    distances.put(key, newD);
                }
            }
            //System.err.println("Return distances from key " + state.keys.get(p) + " = " + distances);
            return distances;
        }

        if (nexts.size() == 0) {
            return distances;
        }

        distance++;
        //System.err.println("Options at " + p + " distance " + distance);
        for (PosM next : nexts) {
            Map<PosM, Integer> nextDistances = getMinKeys(state, next, keysFoundHere, visitedHere);
            for (Entry<PosM, Integer> d : nextDistances.entrySet()) {
                PosM key = d.getKey();
                int newD = d.getValue() + distance;
                if (distances.containsKey(key)) {
                    int currentD = distances.get(key);
                    if (newD < currentD) {
                        distances.put(key, newD);
                    }
                } else {
                    distances.put(key, newD);
                }
            }
        }
        //System.err.println("Return distances from pos " + p + " = " + distances);
        return distances;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("maze4"));
        String line = null;
        int y = 0;
        State state = new State();
        PosM start = null;
        while ((line = reader.readLine()) != null) {
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                PosM p = new PosM(x, y);
                if (c == '#') {
                    state.map.put(p, c);
                }
                if (c >= 'a' && c <= 'z') {
                    state.keys.put(p, c);
                    state.keyPos.put(c, p);
                }
                if (c >= 'A' && c <= 'Z') {
                    state.doors.put(p, c);
                }
                if (c == '@') {
                    start = p;
                }

            }
            y++;
        }
        reader.close();

        System.err.println(start);

        Set<PosM> keysFound = new HashSet<>();
        Set<PosM> visited = new HashSet<>();
        Map<PosM, Integer> dists = getMinKeys(state, start, keysFound, visited);
        for (Entry<PosM, Integer> entry : dists.entrySet()) {
            System.err.println(state.keys.get(entry.getKey()) + " = " + entry.getValue());
        }
    }

}
