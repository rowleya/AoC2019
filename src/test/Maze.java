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
    
    public PosM right() {
    	return new PosM(x + 1, y);
    }
    
    public PosM left() {
    	return new PosM(x - 1, y);
    }
    
    public PosM up() {
    	return new PosM(x, y - 1);
    }
    
    public PosM down() {
    	return new PosM(x, y + 1);
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
        case 3:
            return new PosM(p.x - 1, p.y);
        case 2:
            return new PosM(p.x, p.y + 1);
        case 1:
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
        for (int i = 0; i <= 3; i++) {
            PosM next = move(p, i);
            if (!visited.contains(next) && isReachable(next, keysFound)) {
                return next;
            }
        }
        return null;
    }

    List<PosM> nexts(PosM p, Set<PosM> visited, Set<PosM> keysFound) {
        List<PosM> result = new ArrayList<PosM>();
        if (keys.containsKey(p) && !keysFound.contains(p)) {
            return result;
        }
        for (int i = 0; i < 4; i++) {
            PosM next = move(p, i);
            if (!visited.contains(next) && isReachable(next, keysFound)) {
                result.add(next);
            }
        }
        return result;
    }
    
    boolean isReachable(PosM p) {
        if (map.containsKey(p)) {
            return false;
        }
        return true;
    }
    
    List<PosM> nexts(PosM p, Set<PosM> visited) {
    	List<PosM> result = new ArrayList<PosM>();
    	if (!visited.contains(p) && (keys.containsKey(p) || doors.containsKey(p))) {
            return result;
        }
        for (int i = 0; i < 4; i++) {
            PosM next = move(p, i);
            if (!visited.contains(next) && isReachable(next)) {
                result.add(next);
            }
        }
        return result;
    }
    
    public char getChar(PosM p) {
    	if (keys.containsKey(p)) {
    		return keys.get(p);
    	}
    	if (doors.containsKey(p)) {
    		return doors.get(p);
    	}
    	return '@';
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

class SearchState {
    PosM node;
    Set<PosM> visited;
    int distance;
    public SearchState(PosM node, Set<PosM> visited, int distance) {
        this.node = node;
        this.visited = visited;
        this.distance = distance;
    }

}

public class Maze {
	
	public static boolean addKey(State state, SearchState s, Set<PosM> keysFound, Map<PosM, Integer> distances) {
		if (state.keys.containsKey(s.node) && !keysFound.contains(s.node)) {
			if (!distances.containsKey(s.node) || distances.get(s.node) > s.distance) {
				distances.put(s.node, s.distance);
				//System.err.println("Key " + state.keys.get(s.node) + " dist " + s.distance);
			}
			return true;
		} 
		return false;
	}
	
	public static void getKeys(State state, PosM start, Set<PosM> keysFound, Map<PosM, Integer> distances) {
		Deque<SearchState> q = new ArrayDeque<SearchState>();
		q.push(new SearchState(start, new HashSet<PosM>(), 0));
		while (!q.isEmpty()) {
			SearchState s = q.pop();
			List<PosM> nexts = state.nexts(s.node, s.visited, keysFound);
			
			if (!addKey(state, s, keysFound, distances)) {
				Set<PosM> nextVisit = new HashSet<>(s.visited);
				nextVisit.add(s.node);
				for (PosM next : nexts) {
					q.push(new SearchState(next, nextVisit, s.distance + 1));
				}
			}
		}
	}

    public static int getMinDistance(State state, PosM start, Set<PosM> keysFound, Map<PosM, Map<String, Integer>> memory, Map<PosM, Map<PosM, Integer>> distances, Set<PosM> visited) {
        System.err.println("At " + start + " " + state.getChar(start));
        if (keysFound.size() == state.keys.size()) {
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
                System.err.println("Using " + state.keys.get(start) + ": " + keysToGo + " = " + m.get(keysToGo));
                return m.get(keysToGo);
            }
        }

        Map<PosM, Integer> dists = distances.get(start);

        int minDistance = Integer.MAX_VALUE;
        for (Entry<PosM, Integer> dist : dists.entrySet()) {
            PosM next = dist.getKey();
            int d = dist.getValue();
            if (keysFound.contains(next)) {
            	continue;
            }
            if (state.doors.containsKey(next) && !keysFound.contains(state.getKey(state.doors.get(next)))) {
            	continue;
            }
            Set<PosM> nextKeysFound = new LinkedHashSet<>(keysFound);
            if (state.keys.containsKey(next)) {
                nextKeysFound.add(next);
            }
            Set<PosM> nextVisit = new HashSet<PosM>(visited);
            nextVisit.add(next);
            int nextD = getMinDistance(state, next, nextKeysFound, memory, distances, nextVisit) + d;
            if (keysFound.isEmpty()) {
                System.err.println(start + ": Distance from " + keysToGo + " = " + nextD);
            }
            minDistance = Math.min(minDistance, nextD);
        }

        //System.err.println("New from " + start + ": " + keysToGo + " " + minDistance);
        System.err.println("New from " + start + ": " + minDistance);
        memory.get(start).put(keysToGo, minDistance);
        return minDistance;
    }

    public static Map<PosM, Integer> getDistances(State state, PosM start) {
        Map<PosM, Integer> distances = new HashMap<>();
        Deque<SearchState> q = new ArrayDeque<SearchState>();
		q.push(new SearchState(start, new HashSet<PosM>(), 0));
		while (!q.isEmpty()) {
			SearchState s = q.pop();
			if (s.node != start && (state.keys.containsKey(s.node) || state.doors.containsKey(s.node))) {
				if (!distances.containsKey(s.node) || distances.get(s.node) > s.distance) {
				    distances.put(s.node, s.distance);
				    
				}
			} else {
				Set<PosM> nextVisit = new HashSet<>(s.visited);
				nextVisit.add(s.node);
				List<PosM> nexts = state.nexts(s.node, nextVisit);
				for (PosM next : nexts) {
					q.push(new SearchState(next, nextVisit, s.distance + 1));
				}
			}
		}
        return distances;
    }
    
    public static List<SearchState> nextKeys(State state, SearchState start, Map<PosM, Map<PosM, Integer>> dists) {
    	Map<PosM, Integer> nDists = new HashMap<PosM, Integer>();
    	
    	Deque<SearchState> q = new ArrayDeque<SearchState>();
    	HashSet<PosM> v = new HashSet<PosM>();
    	v.add(start.node);
    	q.push(new SearchState(start.node, v, 0));
    	while (!q.isEmpty()) {
    		SearchState s = q.pop();
    		if (state.keys.containsKey(s.node) && !start.visited.contains(s.node)) {
    			if (!nDists.containsKey(s.node) || nDists.get(s.node) > s.distance) {
    				nDists.put(s.node, s.distance);
    			}
    		} else {
    			for (Entry<PosM, Integer> e : dists.get(s.node).entrySet()) {
    				PosM p = e.getKey();
    				int d = e.getValue();
    				if (state.doors.containsKey(p) && !start.visited.contains(state.getKey(state.doors.get(p)))) {
    					continue;
    				}
    				if (s.visited.contains(p)) {
    					continue;
    				}
    				HashSet<PosM> visited = new HashSet<PosM>(s.visited);
    				visited.add(s.node);
    			    q.push(new SearchState(p, visited, s.distance + d));
    			}
    		}
    	}
    	
    	List<SearchState> nexts = new ArrayList<SearchState>();
    	for (Entry<PosM, Integer> e : nDists.entrySet()) {
    		PosM k = e.getKey();
    		int d = e.getValue() + start.distance;
			HashSet<PosM> visited = new HashSet<PosM>(start.visited);
			visited.add(k);
			nexts.add(new SearchState(k, visited, d));
			//System.err.println(state.getChar(k) + " " + d);
    	}
    	return nexts;
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

        Map<PosM, Map<PosM, Integer>> distsPerNode = new HashMap<>();
        List<PosM> nodes = new ArrayList<>();
        nodes.add(start);
        nodes.addAll(state.keys.keySet());
        nodes.addAll(state.doors.keySet());
        for (PosM key : nodes) {
            System.err.println(state.getChar(key));
            Map<PosM, Integer> dists = getDistances(state, key);
            for (Entry<PosM, Integer> entry : dists.entrySet()) {
                System.err.println(state.getChar(entry.getKey()) + " = " + entry.getValue());
            }
            distsPerNode.put(key, dists);
        }
        
        Map<PosM, Map<String, Integer>> memory = new HashMap<>();
        /*int minDistance = getMinDistance(state, start, new HashSet<PosM>(), memory, distsPerNode, new HashSet<PosM>());
        System.err.println(minDistance);*/
        
        Deque<SearchState> q = new ArrayDeque<>();
        HashSet<PosM> sv = new HashSet<PosM>();
        q.push(new SearchState(start, sv, 0));
        int min = Integer.MAX_VALUE;
        System.err.println(state.keys.size());
        while (!q.isEmpty()) {
        	SearchState s = q.pop();
        	String keysToGo = "";
            for (PosM key : state.keys.keySet()) {
                if (!s.visited.contains(key)) {
                    keysToGo += state.keys.get(key);
                }
            }
        	if (!memory.containsKey(s.node)) {
        		memory.put(s.node, new HashMap<String, Integer>());
        	} else {
        		if (memory.get(s.node).containsKey(keysToGo)) {
        			
        		}
        	}
        	//System.err.println(s.node + " " + state.getChar(s.node) + " " + s.distance + " " + s.visited.size());
        	if (s.visited.size() == state.keys.size()) {
        		min = Math.min(min, s.distance);
        		System.err.println(s.distance + " " + min);
        	} else {
        		for (SearchState st : nextKeys(state, s, distsPerNode)) {
        			q.push(st);
        		}
        	}
        }
        
        System.err.println(min);

        
        //Map<PosM, Map<String, Integer>> bfDist = new HashMap<>();

        
        /*bfDist.get(start).put("", 0);
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
