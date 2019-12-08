package test;

import java.io.BufferedReader;
import java.io.FileReader;

public class IntCode {

    public static int run(int[] origProg, int noun, int verb) throws Exception {
        int[] prog = origProg.clone();
        prog[1] = noun;
        prog[2] = verb;
        boolean finished = false;
        for (int pc = 0; pc < prog.length && !finished; pc += 4) {
            switch (prog[pc]) {
                case 1:
                    prog[prog[pc + 3]] = prog[prog[pc + 1]] + prog[prog[pc + 2]];
                    break;

                case 2:
                    prog[prog[pc + 3]] = prog[prog[pc + 1]] * prog[prog[pc + 2]];
                    break;

                case 99:
                    finished = true;
                    break;

                default:
                    throw new Exception("Error at " + pc);
            }
        }
        return prog[0];
    }

    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("input2"));
        String data = reader.readLine();
        reader.close();
        String[] progString = data.split(",");
        int[] prog = new int[progString.length];
        for (int i = 0; i < prog.length; i++) {
            prog[i] = Integer.parseInt(progString[i]);
        }

        int result = 0;
        for (int noun = 0; noun < prog.length; noun++) {
            for (int verb = 0; verb < prog.length; verb++) {
                result = run(prog, noun, verb);
                if (result == 19690720) {
                    System.err.println(100 * noun + verb);
                    System.exit(0);
                }
            }
        }
    }
}
