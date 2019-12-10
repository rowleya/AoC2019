package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

class Coords {
	public int x;
	public int y;
	
	public Coords(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

public class Asteroids {
	
	public List<Coords> getBlockers(int width, int height, int x, int y) {
		for (int i = x + 1; i < width; i++) {
			
		}
	}
	
	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("asteroids"));
		String line;
		List<Coords> coords = new ArrayList<Coords>();
		int x = 0;
		int width = 0;
		while ((line = reader.readLine()) != null) {
			width = line.length();
			for (int y = 0; y < width; y++) {
				if (line.charAt(y) == '#') {
					coords.add(new Coords(x, y));
				}
			}
		}
		reader.close();
		int height = x;
		
		boolean[][] isAsteroid = new boolean[width][height];
		for (Coords c : coords) {
			isAsteroid[c.x][c.y] = true;
		}
		
		int [][] n = new int[width][height];
		for (int i = 0; i < width; i++) {
			
		}
	}

}
