package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class FFT {

    private static final int ZERO = '0';

    private static long findGCD(long number1, long number2) {
        //base case
        if (number2 == 0) {
            return number1;
        }
        return findGCD(number2, number1 % number2);
    }

    private static long findLCM(long number1, long number2) {
        return (number1 * number2) / findGCD(number1, number2);
    }

    public static void main(String[] args)  throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("fft"));
        String data = reader.readLine();
        reader.close();
        // data = "80871224585914546619083218645595";
        // data = "03036732577212944063491565474664";
        // data = "12345678";

        int repeat = 10000;
        int [] input = new int[data.length() * repeat];
        for (int i = 0; i < data.length(); i++) {
            input[i] = data.charAt(i) - ZERO;
            for (int j = 0; j < repeat; j++) {
                input[(j * data.length()) + i] = input[i];
            }
        }
        //System.err.println(Arrays.toString(input));
        String off = "";
        for (int i = 0; i < 7; i++) {
            off += String.valueOf(input[i]);
        }
        int offset = Integer.parseInt(off);
        System.err.println(offset);

        int sum = 0;
        int[] output = new int[input.length];
        System.err.println("Start 1s at " + offset + " End 1s at " + (offset + 1) * 4 + " Input length " + input.length);
        for (int phase = 0; phase < 100; phase++) {
            sum = 0;
            for (int i = input.length - 1; i >= offset; i--) {
                sum += input[i];
                output[i] = sum % 10;
            }
            input = output;
            output = new int[input.length];
        }

//        int[] output = new int[input.length];
//        for (int phase = 0; phase < 100; phase++) {
//            for (int i = 0; i < output.length; i++) {
//                output[i] = 0;
//                for (int j = i; j < input.length; j += (i + 1) * 4) {
//                    for (int k = 0; k <= i && (j + k) < input.length; k++) {
//                        output[i] += input[j + k];
//                    }
//                }
//                for (int j = i + ((i + 1) * 2); j < input.length; j += (i + 1) * 4) {
//                    for (int k = 0; k <= i && (j + k) < input.length; k++) {
//                        output[i] -= input[j + k];
//                    }
//                }
//                /*int p = 0;
//                int c = 0;
//                output[i] = 0;
//                for (int j = 0; j < input.length; j++) {
//                    if (c == i) {
//                        c = 0;
//                        p++;
//                        if (p >= pattern.length) {
//                            p = 0;
//                        }
//                    } else {
//                        c++;
//                    }
//                    //System.err.print(input[j] + "*" + pattern[p] + " + ");
//                    output[i] += input[j] * pattern[p];
//
//                }*/
//                // System.err.print(" = " + output[i]);
//                output[i] = output[i] % 10;
//                if (output[i] < 0) {
//                    output[i] *= -1;
//                }
//                //System.err.println(i);
//            }
//            System.err.println(phase);
//            input = output;
//            output = new int[input.length];
//        }

        for (int i = 0; i < 8; i++) {
            System.err.print(input[i + offset]);
        }
        System.err.println();
    }

}
