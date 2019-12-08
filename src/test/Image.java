package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;

public class Image {
	
	private static final int ZERO = '0';

	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader("image"));
		String input = reader.readLine();
		reader.close();
		StringReader s = new StringReader(input);
		
		int width = 25;
		int height = 6;
		int nLayers = input.length() / (width * height);
		
		int[][][] image = new int[nLayers][height][width];
		int[][] visible = new int[height][width];
		
		for (int j = 0; j < height; j++) {
			for (int k = 0; k < width; k++) {
				visible[j][k] = 2;
			}
		}
		
		int minZeroSum = Integer.MAX_VALUE;
		int minZeros = Integer.MAX_VALUE;
		for (int i = 0; i < nLayers; i++) {
			int[] nVals = new int[10];
			for (int j = 0; j < height; j++) {
				for (int k = 0; k < width; k++) {
					image[i][j][k] = s.read() - ZERO;
					nVals[image[i][j][k]]++;
					if (visible[j][k] == 2) {
						visible[j][k] = image[i][j][k];
					}
				}
			}
			if (nVals[0] < minZeros) {
				minZeros = nVals[0];
				minZeroSum = nVals[1] * nVals[2];
			}
		}
		s.close();
		
		System.err.println(minZeroSum);
		
		for (int j = 0; j < height; j++) {
			for (int k = 0; k < width; k++) {
				if (visible[j][k] == 1) {
				    System.err.print(visible[j][k]);
				} else {
					System.err.print(' ');
				}
			}
			System.err.println();
		}
	}

}
