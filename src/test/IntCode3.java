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

class Instruction3 {
    private static final int ZERO = '0';
    public int op;
    public int mode[] = new int[3];

    public Instruction3(int value) {
        String s = String.valueOf(value);
        if (s.length() == 1) {
            op = value;
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

    public int getValue(int[] prog, int pc, int arg) {
        if (mode[arg] == 0) {
            return prog[prog[pc + arg + 1]];
        } else if (mode[arg] == 1) {
            return prog[pc + arg + 1];
        }
        throw new RuntimeException(
                "Unknown mode " + mode[arg] + " at pc " + pc + " arg " + arg);
    }
}

class IntCodeMachine {

    private int[] prog;

    private boolean finished = false;

    private int lastOutput = -1;

    private int pc;

    public IntCodeMachine(int[] origProg) {
        prog = origProg.clone();
        pc = 0;
    }

    public boolean isFinished() {
        return finished;
    }

    public int runUntilOutOfInputOrFinished(int[] inputs) {
        LinkedList<Integer> storedInputs = new LinkedList<>();
        for (int i = 0; i < inputs.length; i++) {
            storedInputs.add(inputs[i]);
        }
        ListIterator<Integer> inputIter = storedInputs.listIterator();
        boolean outOfInput = false;
        while (pc < prog.length && !finished && !outOfInput) {
            Instruction3 i = new Instruction3(prog[pc]);
            switch (i.op) {
                case 1:
                    int outsum = prog[pc + 3];
                    int insum1 = i.getValue(prog, pc, 0);
                    int insum2 = i.getValue(prog, pc, 1);
                    prog[outsum] = insum1 + insum2;
                    System.err.println("pc[" + outsum + "] = " + insum1 + " + " + insum2);
                    pc += 4;
                    break;

                case 2:
                    int outmul = prog[pc + 3];
                    int inmul1 = i.getValue(prog, pc, 0);
                    int inmul2 = i.getValue(prog, pc, 1);
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
                    int outstr = prog[pc + 1];
                    prog[outstr] = strval;
                    System.err.println("pc[" + outstr + "] = " + strval);
                    pc += 2;
                    break;

                case 4:
                    int ldr = i.getValue(prog, pc, 0);
                    System.err.println("Output: " + ldr);
                    lastOutput = ldr;
                    pc += 2;
                    break;

                case 5:
                    int valueTrue = i.getValue(prog, pc, 0);
                    int jumpTrue = i.getValue(prog, pc, 1);
                    if (valueTrue > 0) {
                        pc = jumpTrue;
                    } else {
                        pc += 3;
                    }
                    break;

                case 6:
                    int valueFalse = i.getValue(prog, pc, 0);
                    int jumpFalse = i.getValue(prog, pc, 1);
                    if (valueFalse == 0) {
                        pc = jumpFalse;
                    } else {
                        pc += 3;
                    }
                    break;

                case 7:
                    int ltv1 = i.getValue(prog, pc, 0);
                    int ltv2 = i.getValue(prog, pc, 1);
                    int ltout = prog[pc + 3];
                    if (ltv1 < ltv2) {
                        prog[ltout] = 1;
                    } else {
                        prog[ltout] = 0;
                    }
                    pc += 4;
                    break;

                case 8:
                    int eqv1 = i.getValue(prog, pc, 0);
                    int eqv2 = i.getValue(prog, pc, 1);
                    int eqout = prog[pc + 3];
                    if (eqv1 == eqv2) {
                        prog[eqout] = 1;
                    } else {
                        prog[eqout] = 0;
                    }
                    pc += 4;
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

public class IntCode3 {

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

    public static boolean isRunning(IntCodeMachine[] machines) {
        for (IntCodeMachine machine : machines) {
            if (!machine.isFinished()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("intcode3"));
        String data = reader.readLine();
        reader.close();
        String[] progString = data.split(",");
        int[] prog = new int[progString.length];
        for (int i = 0; i < prog.length; i++) {
            prog[i] = Integer.parseInt(progString[i]);
        }

        int[] inputs = new int[]{5, 5, 5, 5, 5};
        boolean isNext = true;
        int maxOutput = 0;
        while (isNext) {
            if (isValid(inputs)) {
                IntCodeMachine[] machines = new IntCodeMachine[inputs.length];
                for (int i = 0; i < inputs.length; i++) {
                    machines[i] = new IntCodeMachine(prog);
                    int[] initInput = new int[]{inputs[i]};
                    machines[i].runUntilOutOfInputOrFinished(initInput);
                }
                System.err.println("Trying " + Arrays.toString(inputs));
                int lastOutput = 0;
                while (isRunning(machines)) {
                    for (int i = 0; i < inputs.length; i++) {
                        int[] thisInputs = new int[]{lastOutput};
                        lastOutput = machines[i].runUntilOutOfInputOrFinished(thisInputs);
                    }
                }
                if (lastOutput > maxOutput) {
                    maxOutput = lastOutput;
                }
            }
            isNext = nextInputs(inputs, 5, 9);
        }
        System.err.println("Final result = " + maxOutput);
    }
}
