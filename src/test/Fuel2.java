package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

class Chem {
	String id;
	int quant;
	
	public Chem(String id, int quant) {
		this.id = id;
		this.quant = quant;
	}
	
	public Chem(String s) {
		String[] bits = s.strip().split(" ");
		quant = Integer.parseInt(bits[0]);
		id = bits[1];
	}
	
	public String asString(int ratio) {
		return (quant * ratio) + " " + id;
	}
}

class Reaction {
	Chem[] input;
	Chem output;
	
	public Reaction(Chem []input, Chem output) {
		this.input = input;
		this.output = output;
	}
	
	public String asString(int ratio) {
		String s = "";
		for (Chem i : input) {
			s += i.asString(ratio) + "; ";
			
		}
		s += "=> " + output.asString(ratio);
		return s;
	}
	
	public boolean isInput(Chem c) {
		for (Chem i : input) {
			if (i.id == c.id) {
				return true;
			}
		}
		return false;
	}
}

class RNode {
	Reaction r;
	int quant;
	List<RNode> children = new ArrayList<RNode>();
	
	public RNode(Reaction r, int quant) {
		this.r = r;
		this.quant = quant;
	}
	
	public void addChild(RNode c) {
		children.add(c);
	}
	
	public boolean dependsOn(Chem c) {
		if (r.isInput(c)) {
			return true;
		}
		for (RNode child : children) {
			if (child.dependsOn(c)) {
				return true;
			}
		}
		return false;
	}
	
	public int count() {
		if (r == null) {
			return quant;
		}
		int count = 0;
		for (RNode child : children) {
			count += child.count();
		}
		return count;
	}
	
	public void traverse(Map<String, Chem> quants) {
		String id = null;
		if (r == null) {
			id = "ORE";
		} else {
			id = r.output.id;
		}
		Chem c = quants.get(id);
		if (c == null) {
			quants.put(id, new Chem(id, quant));
		} else {
			c.quant += quant;
		}
	}
	
}

public class Fuel2 {

	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("fuel2_test"));
		String line;
		Map<String, Reaction> reactions = new HashMap<String, Reaction>();
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split(" => ", 2);
			String[] iParts = parts[0].strip().split(",");
			Chem[] inputs = new Chem[iParts.length];
			for (int i = 0; i< iParts.length; i++) {
				inputs[i] = new Chem(iParts[i]);
			}
		    Chem output = new Chem(parts[1]);
		    Reaction r = new Reaction(inputs, output);
		    reactions.put(output.id, r);
		}
		
		RNode fuel = new RNode(reactions.get("FUEL"), 1);
		Deque<RNode> needed = new ArrayDeque<RNode>();
		needed.addLast(fuel);
		Map<String, RNode> nodes = new HashMap<String, RNode>();
		nodes.put("FUEL", fuel);
		while (!needed.isEmpty()) {
			RNode r = needed.removeFirst();
			int ratio = (int) Math.ceil((double) r.quant / (double) r.r.output.quant);
			for (Chem input : r.r.input) {
				Reaction re = reactions.get(input.id);
				RNode child = new RNode(re, ratio * input.quant);
				r.addChild(child);
				if (re != null) {
					needed.add(child);
				}
			}
		}
		System.err.println(fuel.count());
	}
	

}