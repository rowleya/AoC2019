package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Bugs {
	
	static void print(boolean[][] in) {
		for (int j = 0; j < in.length; j++) {
			for (int i = 0; i < in[j].length; i++) {
				if (in[i][j]) {
					System.err.print('#');
				} else {
					System.err.print('.');
				}
			}
			System.err.println();
		}
	}
	
	static int check(boolean[][] in, int i, int j) {
		if (i >= 0 && i < in.length) {
			if (j >= 0 && j < in[i].length) {
				if (in[i][j]) {
					return 1;
				}
			}
		}
		return 0;
	}
	
	static boolean[][] evolve(boolean[][] in) {
		boolean[][] out = new boolean[in.length][];
		for (int i = 0; i < in.length; i++) {
			out[i] = new boolean[in[i].length];
			for (int j = 0; j < in[i].length; j++) {
				int count = 0;
				count += check(in, i + 1, j);
				count += check(in, i - 1, j);
				count += check(in, i, j + 1);
				count += check(in, i, j - 1);
				
				if (in[i][j] && count != 1) {
					out[i][j] = false;
				} else if (!in[i][j] && (count == 1) || (count == 2)) {
					out[i][j] = true;
				} else {
					out[i][j] = in[i][j];
				}
			}
		}
		return out;
	}
	
	static long score(boolean[][] in) {
		long score = 0;
		long pow = 0;
		for (int j = 0; j < in.length; j++) {
			for (int i = 0; i < in[j].length; i++) {
				if (in[i][j]) {
					score += (1 << pow);
				}
				pow++;
			}
		}
		return score;
	}

	public static void main(String[] args) throws Exception {
		boolean[][] in = new boolean[5][5];
		BufferedReader reader = new BufferedReader(new FileReader("bugs"));
		for (int j = 0; j < 5; j++) {
			String line = reader.readLine();
			for (int i = 0; i < 5; i++) {
				in[i][j] = line.charAt(i) == '#';
			}
		}
		reader.close();
		
		Set<Long> scores = new HashSet<Long>();
		//scores.add(score(in));
		boolean done = false;
		long last = 0;
		while (!done) {
			boolean[][] out = evolve(in);
			in = out;
			last = score(out);
			print(out);
			System.err.println();
			if (!scores.add(last)) {
				done = true;
			}
		}
		System.err.println(last);
	}

}
