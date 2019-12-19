package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
        if (keys.containsKey(p) && !keysFound.contains(p)) {
            return null;
        }
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

class SearchState  implements Comparable<SearchState>{
    PosM node;
    Set<PosM> keysVisited;
    int distance;
    public SearchState(PosM node, Set<PosM> keysVisited, int distance) {
        this.node = node;
        this.keysVisited = keysVisited;
        this.distance = distance;
    }

    public String getKeys(State state) {
        String keys = "";
        for (PosM k : keysVisited) {
            keys += state.keys.get(k);
        }
        return keys;
    }

    @Override
    public int compareTo(SearchState o) {
        return distance - o.distance;
    }

}

public class Maze {

    public static int getMinDistance(State state, PosM start, Set<PosM> keysFound, Map<PosM, Map<String, Integer>> memory) {
        Map<PosM, Integer> distances = new HashMap<>();
        System.err.println("At " + start + " " + state.keys.get(start));
        if (keysFound.size() == state.keys.size()) {
            System.err.print("End: ");
            for (PosM k : keysFound) {
                System.err.print(state.keys.get(k));
            }
            System.err.println();
            return 0;
        }
        String keysToGo = "";
        for (PosM key : state.keys.keySet()) {
            if (!keysFound.contains(key)) {
                keysToGo += state.keys.get(key);
            }
        }
        // System.err.println(keysToGo);
        if (!memory.containsKey(start)) {
            memory.put(start, new HashMap<>());
        } else {
            Map<String, Integer> m = memory.get(start);
            if (m.containsKey(keysToGo)) {
                // System.err.println(m.get(keysToGo));
                return m.get(keysToGo);
            }
        }

        Set<PosM> visited = new HashSet<PosM>();
        visited.add(start);
        PosM p = start;
        //System.err.println("Starting at " + start + " with keys from " + keysFound);

        Deque<PosM> reverse = new ArrayDeque<>();
        // reverse.push(start);
        boolean done = false;
        String options = "";
        while (!done) {
            PosM next = state.next(p, visited, keysFound);
            if (next == null) {
                if (reverse.isEmpty()) {
                    done = true;
                } else {
                    p = reverse.pop();
                    //System.err.println("Back to " + p);
                }
            } else {
                //System.err.println("Forward to " + next);
                if (state.keys.containsKey(next) && !keysFound.contains(next)) {
                    int d = reverse.size() + 1;
                    if (!distances.containsKey(next) || distances.get(next) > d) {
                        distances.put(next, d);
                        options += state.keys.get(next);
                        // System.err.println("Key " + state.keys.get(next) + " at " + next + " distance " + (reverse.size() + 1));
                    }
                }
                visited.add(next);
                reverse.push(p);
                p = next;
            }
        }
        System.err.println("Options: " + options);

        int minDistance = Integer.MAX_VALUE;
        for (Entry<PosM, Integer> dist : distances.entrySet()) {
            PosM next = dist.getKey();
            int d = dist.getValue();
            Set<PosM> nextKeysFound = new LinkedHashSet<>(keysFound);
            nextKeysFound.add(next);
            int nextD = getMinDistance(state, next, nextKeysFound, memory) + d;
            System.err.println("Distance from " + start + " = " + nextD);
            minDistance = Math.min(minDistance, nextD);
        }

        //System.err.println("New from " + start + ": " + keysToGo + " " + minDistance);
        // System.err.println("New from " + start + ": " + minDistance);
        memory.get(start).put(keysToGo, minDistance);
        return minDistance;
    }

    public static Map<PosM, Integer> getDistances(State state, PosM start) {
        Map<PosM, Integer> distances = new HashMap<>();
        Set<PosM> visited = new HashSet<PosM>();
        visited.add(start);
        PosM p = start;
        Deque<PosM> reverse = new ArrayDeque<>();
        // reverse.push(start);
        boolean done = false;
        while (!done) {
            PosM next = state.next(p, visited, state.keys.keySet());
            if (next == null) {
                if (reverse.isEmpty()) {
                    done = true;
                } else {
                    p = reverse.pop();
                    //System.err.println("Back to " + p);
                }
            } else {
                //System.err.println("Forward to " + next);
                if (state.keys.containsKey(next) || state.doors.containsKey(next)) {
                    if (!distances.containsKey(next) || distances.get(next) > reverse.size() + 1) {
                        distances.put(next, reverse.size() + 1);

                        //System.err.println("Key " + state.keys.get(next) + " at " + next + " distance " + (reverse.size() + 1));
                    }
                }
                visited.add(next);
                reverse.push(p);
                p = next;
            }
        }
        return distances;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("maze"));
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

        Map<PosM, Map<String, Integer>> memory = new HashMap<>();
        int minDistance = getMinDistance(state, start, new HashSet<PosM>(), memory);
        System.err.println(minDistance);

        /*Map<PosM, Map<PosM, Integer>> distsPerNode = new HashMap<>();
        List<PosM> nodes = new ArrayList<>();
        nodes.add(start);
        nodes.addAll(state.keys.keySet());
        nodes.addAll(state.doors.keySet());
        Map<PosM, Map<String, Integer>> bfDist = new HashMap<>();

        for (PosM key : nodes) {
            System.err.println(key);
            Map<PosM, Integer> dists = getDistances(state, key);
            for (Entry<PosM, Integer> entry : dists.entrySet()) {
                System.err.println(entry.getKey() + " = " + entry.getValue());
            }
            distsPerNode.put(key, dists);
            bfDist.put(key, new HashMap<String, Integer>());
        }
        bfDist.get(start).put("", 0);
        PriorityQueue<SearchState> toDo = new PriorityQueue<>();

        toDo.add(new SearchState(start, new HashSet<PosM>(), 0));
        int minDist = Integer.MAX_VALUE;
        while (!toDo.isEmpty()) {
            SearchState s = toDo.poll();
            //System.err.println(s.node + " = " + s.distance);
            if (s.keysVisited.size() == state.keys.size()) {
                System.err.println(s.distance + " " + s.getKeys(state));
                minDist = Math.min(minDist, s.distance);
                break;
            } else {
                for (PosM nextNode : distsPerNode.get(s.node).keySet()) {
                    //System.err.println(nextNode);
                    if (state.doors.containsKey(nextNode) && !s.keysVisited.contains(state.keyPos.get(Character.toLowerCase(state.doors.get(nextNode))))) {
                        //System.err.println("No key in " + s.getKeys(state) + " for " + state.doors.get(nextNode));
                        continue;
                    }
                    int nextD = s.distance + distsPerNode.get(s.node).get(nextNode);
                    Set<PosM> nextKeysVisited = new LinkedHashSet<PosM>(s.keysVisited);
                    if (state.keys.containsKey(nextNode)) {
                        nextKeysVisited.add(nextNode);
                        //System.err.println("Found key " + nextNode);
                    }
                    SearchState nextS = new SearchState(nextNode, nextKeysVisited, nextD);
                    String kstr = nextS.getKeys(state);
                    if (!bfDist.get(nextNode).containsKey(kstr) || nextD < bfDist.get(nextNode).get(kstr)) {
                        bfDist.get(nextNode).put(kstr, nextD);
                        toDo.add(nextS);
                    }
                }
            }

        }
        System.err.println(minDist); */

    }

}
