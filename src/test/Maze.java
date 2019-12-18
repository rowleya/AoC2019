package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
	
}

public class Maze {

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
        
        Set<PosM> visited = new HashSet<PosM>();
        Deque<PosM> reverse = new ArrayDeque<PosM>();
        visited.add(start);
        reverse.push(start);
        PosM p = start;
        Set<PosM> keysFound = new HashSet<PosM>();
        int distanceToKey = 0;
        int distance = 0;
        while (keysFound.size() < state.keys.size()) {
        	PosM next = state.next(p, visited, keysFound);
        	if (next != null) {
        		System.err.println("Move to " + next);
        		p = next;
        		visited.add(next);
        		reverse.push(next);
        		distanceToKey++;
        		if (state.keys.containsKey(p)) {
        			System.err.println("Found key " + state.keys.get(p) + "; " + (state.keys.size() - keysFound.size()) + " to go");
        			keysFound.add(p);
        			distance += distanceToKey;
        			distanceToKey = 0;
        		}
        	} else {
        		
        		p = reverse.pop();
        		distanceToKey--;
        		System.err.println("Go back to " + p);
        	}
        }
        System.err.println(distance);
	}

}
