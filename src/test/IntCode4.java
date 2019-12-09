package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Set;

class Instruction4 {
    private static final int ZERO = '0';
    public int op;
    public long mode[] = new long[3];

    public Instruction4(long value) {
        String s = String.valueOf(value);
        if (s.length() == 1) {
            op = (int) value;
            for (int i = 0; i < 3; i++) {
                mode[i] = 0;
            }
        } else {
            op = Integer.parseInt(s.substring(s.length() - 2, s.length()));
            for (int i = 0; i < 3; i++) {
                int pos = s.length() - 3 - i;
                if (pos >= 0) {
                    mode[i] = s.charAt(pos) - ZERO;
                } else {
                    mode[i] = 0;
                }
            }
        }
    }

    public long getValue(long[] prog, int pc, int arg, int relBase) {
        if (mode[arg] == 0) {
            return prog[(int) prog[pc + arg + 1]];
        } else if (mode[arg] == 1) {
            return prog[pc + arg + 1];
        } else if (mode[arg] == 2) {
        	return prog[(int) prog[pc + arg + 1] + relBase];
        }
        throw new RuntimeException(
                "Unknown mode " + mode[arg] + " at pc " + pc + " arg " + arg);
    }
}

class IntCodeMachine4 {

    private long[] prog;

    private boolean finished = false;

    private long lastOutput = -1;

    private int pc;
    
    private int relBase = 0;

    public IntCodeMachine4(long[] origProg, int size) {
        prog = new long[size];
        for (int i = 0; i < origProg.length; i++) {
        	prog[i] = origProg[i];
        }
        pc = 0;
    }

    public boolean isFinished() {
        return finished;
    }

    public long runUntilOutOfInputOrFinished(int[] inputs) {
        LinkedList<Integer> storedInputs = new LinkedList<>();
        for (int i = 0; i < inputs.length; i++) {
            storedInputs.add(inputs[i]);
        }
        ListIterator<Integer> inputIter = storedInputs.listIterator();
        boolean outOfInput = false;
        while (pc < prog.length && !finished && !outOfInput) {
            Instruction4 i = new Instruction4(prog[pc]);
            switch (i.op) {
                case 1:
                    int outsum = (int) prog[pc + 3];
                    long insum1 = i.getValue(prog, pc, 0, relBase);
                    long  insum2 = i.getValue(prog, pc, 1, relBase);
                    prog[outsum] = insum1 + insum2;
                    System.err.println("pc[" + outsum + "] = " + insum1 + " + " + insum2);
                    pc += 4;
                    break;

                case 2:
                    int outmul = (int) prog[pc + 3];
                    long inmul1 = i.getValue(prog, pc, 0, relBase);
                    long inmul2 = i.getValue(prog, pc, 1, relBase);
                    prog[outmul] = inmul1 * inmul2;
                    System.err.println("pc[" + outmul + "] = " + inmul1 + " * " + inmul2);
                    pc += 4;
                    break;

                case 3:
                    int strval = -1;
                    if (inputIter.hasNext()) {
                        strval = inputIter.next();
                        System.err.println("Using " + strval + " as input");
                    } else {
                        System.err.println("Out of input");
                        outOfInput = true;
                        break;
                    }
                    int outstr = (int) prog[pc + 1];
                    prog[outstr] = strval;
                    System.err.println("pc[" + outstr + "] = " + strval);
                    pc += 2;
                    break;

                case 4:
                    long ldr = i.getValue(prog, pc, 0, relBase);
                    System.err.println("Output: " + ldr);
                    lastOutput = ldr;
                    pc += 2;
                    break;

                case 5:
                    long valueTrue = i.getValue(prog, pc, 0, relBase);
                    int jumpTrue = (int) i.getValue(prog, pc, 1, relBase);
                    if (valueTrue > 0) {
                        pc = jumpTrue;
                    } else {
                        pc += 3;
                    }
                    break;

                case 6:
                    long valueFalse = i.getValue(prog, pc, 0, relBase);
                    int jumpFalse = (int) i.getValue(prog, pc, 1, relBase);
                    if (valueFalse == 0) {
                        pc = jumpFalse;
                    } else {
                        pc += 3;
                    }
                    break;

                case 7:
                    long ltv1 = i.getValue(prog, pc, 0, relBase);
                    long ltv2 = i.getValue(prog, pc, 1, relBase);
                    int ltout = (int) prog[pc + 3];
                    if (ltv1 < ltv2) {
                        prog[ltout] = 1;
                    } else {
                        prog[ltout] = 0;
                    }
                    pc += 4;
                    break;

                case 8:
                    long eqv1 = i.getValue(prog, pc, 0, relBase);
                    long eqv2 = i.getValue(prog, pc, 1, relBase);
                    int eqout = (int) prog[pc + 3];
                    if (eqv1 == eqv2) {
                        prog[eqout] = 1;
                    } else {
                        prog[eqout] = 0;
                    }
                    pc += 4;
                    break;
                    
                case 9:
                	int newBase = (int) i.getValue(prog, pc, 0, relBase);
                	relBase = newBase;
                	pc += 2;
                	break;

                case 99:
                    finished = true;
                    System.err.println("Finished!");
                    break;

                default:
                    throw new RuntimeException("Unknown operation " + i.op + " at " + pc);
            }
        }
        return lastOutput;
    }
}

public class IntCode4 {

    public static boolean nextInputs(int[] inputs, int minValue, int maxValue) {
        int i = inputs.length - 1;
        while (i >= 0) {
            inputs[i] += 1;
            if (inputs[i] <= maxValue) {
                return true;
            }
            inputs[i] = minValue;
            i--;
        }
        return false;
    }

    public static boolean isValid(int[] inputs) {
        Set<Integer> values = new HashSet<>();
        for (int i = 0; i < inputs.length; i++) {
            if (values.contains(inputs[i])) {
                return false;
            }
            values.add(inputs[i]);
        }
        return true;
    }

    public static boolean isRunning(IntCodeMachine4[] machines) {
        for (IntCodeMachine4 machine : machines) {
            if (!machine.isFinished()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("intcode4"));
        String data = reader.readLine();
        reader.close();
        String[] progString = data.split(",");
        long[] prog = new long[progString.length];
        for (int i = 0; i < prog.length; i++) {
            prog[i] = Long.parseLong(progString[i]);
        }

        int[] inputs = new int[]{1};
        IntCodeMachine4 m = new IntCodeMachine4(prog, 1000000);
        long output = m.runUntilOutOfInputOrFinished(inputs);
        System.err.println("Final result = " + output);
    }
}
