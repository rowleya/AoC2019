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

class StateM {
	PosM pos;
	Set<PosM> visited;
	int distance;
	int level;
	
	public StateM(PosM pos) {
		this.pos = pos;
		this.visited = new HashSet<PosM>();
		this.visited.add(pos);
		this.distance = 0;
		this.level = 0;
	}
	
	public StateM(StateM s, PosM pos, int level, int distance) {
		this.pos = pos;
		this.visited = new HashSet<PosM>();
		if (s.level == level) {
			this.visited.addAll(s.visited);
		}
		this.visited.add(pos);
		this.distance = s.distance + distance;
		this.level = level;
		//System.err.println(pos + " = " + distance);
	}
	
	
	void add(PosM pos, List<StateM> n, Map<PosM, Character> map, int level, int distance) {
		if (pos != null && map.containsKey(pos) && map.get(pos) == '.' && !visited.contains(pos)) {
			n.add(new StateM(this, pos, level, distance));
		}
	}
	
	public List<StateM> nexts(Map<PosM, Character> map) {
		List<StateM> n = new ArrayList<>();
		//System.err.println(pos);
		add(pos.right(), n, map, level, 1);
		add(pos.left(), n, map, level, 1);
		add(pos.up(), n, map, level, 1);
		add(pos.down(), n, map, level, 1);
		return n;
	}
	
	public List<StateM> nexts(Map<PosM, Character> map, Map<PosM, PosM> portalsUp, Map<PosM, PosM> portalsDown) {
		List<StateM> n = new ArrayList<>();
		//System.err.println(pos);
		add(pos.right(), n, map, level, 1);
		add(pos.left(), n, map, level, 1);
		add(pos.up(), n, map, level, 1);
		add(pos.down(), n, map, level, 1);
		if (level > 0) {
		    add(portalsUp.get(pos), n, map, level - 1, 1);
		} 
		add(portalsDown.get(pos), n, map, level + 1, 1);
		return n;
	}
	
	public List<StateM> nexts(Map<PosM, Character> map, Map<PosM, PosM> portalsUp, Map<PosM, PosM> portalsDown, PosM target, Map<PosM, Map<PosM, Integer>> distances) {
		List<StateM> n = new ArrayList<>();
		Map<PosM, Integer> d = distances.get(pos);
		for (Entry<PosM, Integer> e : d.entrySet()) {
			if (level > 0) {
			    add(portalsUp.get(e.getKey()), n, map, level - 1, e.getValue() + 1);
			} 
			if (level < 30) {
			    add(portalsDown.get(e.getKey()), n, map, level + 1, e.getValue() + 1);
			}
			if (e.getKey().equals(target) && level == 0) {
				add(target, n, map, level, e.getValue());
			}
		}
		return n;
	}
}

public class Maze2 {
	
	public static void update(String key, PosM end, Map<PosM, PosM> portalsUp, Map<PosM, PosM> portalsDown, Map<String, PosM> ends, boolean inner) {
		if (ends.containsKey(key)) {
			PosM other = ends.remove(key);
			if (inner) {
			    portalsDown.put(end, other);
			    portalsUp.put(other, end);
			} else {
				portalsUp.put(end, other);
				portalsDown.put(other, end);
			}
		} else {
			ends.put(key, end);
		}
	}
	
	public static Map<PosM, Integer> distances(PosM start, Map<PosM, Character> map, Set<PosM> targets) {
		Map<PosM, Integer> d = new HashMap<PosM, Integer>();
		Deque<StateM> q = new ArrayDeque<>();
		q.push(new StateM(start));
		while (!q.isEmpty()) {
			StateM s = q.pop();
			if (targets.contains(s.pos) && !s.pos.equals(start)) {
				d.put(s.pos, s.distance);
			} else {
				q.addAll(s.nexts(map));
			}
		}
		return d;
	}

	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("mazeportal"));
        String line = null;
        int y = 0;
        Map<PosM, Character> map = new HashMap<PosM, Character>();
        Set<PosM> caps = new HashSet<PosM>();
        while ((line = reader.readLine()) != null) {
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                PosM p = new PosM(x, y);
                map.put(p, c);
                if (c >= 'A' && c <= 'Z') {
                    caps.add(p);
                }
            }
            y++;
        }
        reader.close();
        
        Map<PosM, PosM> portalsUp = new HashMap<PosM, PosM>();
        Map<PosM, PosM> portalsDown = new HashMap<PosM, PosM>();
        
        Map<String, PosM> ends = new HashMap<String, PosM>();
        for (PosM cap : caps) {
        	if (caps.contains(cap.right()) && map.containsKey(cap.right().right()) && map.get(cap.right().right()) == '.') {
        		String key = "" + map.get(cap) + map.get(cap.right());
        		update(key, cap.right().right(), portalsUp, portalsDown, ends, map.containsKey(cap.left()));
        	} else if (caps.contains(cap.left()) && map.containsKey(cap.left().left()) && map.get(cap.left().left()) == '.') {
        		String key = "" + map.get(cap.left()) + map.get(cap);
        		update(key, cap.left().left(), portalsUp, portalsDown, ends, map.containsKey(cap.right()));
        	} else if (caps.contains(cap.up()) && map.containsKey(cap.up().up()) && map.get(cap.up().up()) == '.') {
        		String key = "" + map.get(cap.up()) + map.get(cap);
        		update(key, cap.up().up(), portalsUp, portalsDown, ends, map.containsKey(cap.down()));
        	} else if (caps.contains(cap.down()) && map.containsKey(cap.down().down()) && map.get(cap.down().down()) == '.') {
        		String key = "" + map.get(cap) + map.get(cap.down());
        		update(key, cap.down().down(), portalsUp, portalsDown, ends, map.containsKey(cap.up()));
        	}
        }
        
        PosM start = ends.get("AA");
        PosM end = ends.get("ZZ");
        
        System.err.println(portalsDown);
        System.err.println(portalsUp);
        System.err.println(ends);
        
        Map<PosM, Map<PosM, Integer>> distances = new HashMap<>();
        Set<PosM> targets = new HashSet<PosM>(portalsUp.keySet());
        targets.addAll(portalsDown.keySet());
        targets.add(end);
        distances.put(start, distances(start, map, targets));
        for (PosM target : targets) {
        	if (target != end) {
        		distances.put(target, distances(target, map, targets));
        	}
        }
        
        for (Entry<PosM, Map<PosM, Integer>> e : distances.entrySet()) {
        	System.err.println(e.getKey() + ": " + e.getValue());
        }
        
        Deque<StateM> q = new ArrayDeque<>();
        q.push(new StateM(start));
        int min = Integer.MAX_VALUE;
        while (!q.isEmpty()) {
        	StateM s = q.pop();
        	//System.err.println(s.pos + " " + s.level);
        	if (s.pos.equals(end) && s.level == 0) {
        		min = Math.min(min, s.distance);
        		System.err.println(min);
        	} else {
        		q.addAll(s.nexts(map, portalsUp, portalsDown, end, distances));
        	}
        }
        System.err.println(min);
	}

}
