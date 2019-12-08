package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

class Instruction {
    private static final int ZERO = '0';
    public int op;
    public int mode[] = new int[3];

    public Instruction(int value) {
        String s = String.valueOf(value);
        System.err.println("Instruction " + value);
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
                System.err.println("Mode[" + i + "] = " + mode[i] + " (" + pos + ")");
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

public class IntCode2 {

    public static int run(int[] origProg) throws Exception {
        int[] prog = origProg.clone();
        Scanner in = new Scanner(System.in);
        boolean finished = false;
        for (int pc = 0; pc < prog.length && !finished; ) {
            Instruction i = new Instruction(prog[pc]);
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
                    System.err.print("Input: ");
                    int strval = in.nextInt();
                    int outstr = prog[pc + 1];
                    prog[outstr] = strval;
                    System.err.println("pc[" + outstr + "] = " + strval);
                    pc += 2;
                    break;

                case 4:
                    int ldr = i.getValue(prog, pc, 0);
                    System.err.println("Output pc[" + ldr + "]: " + ldr);
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
                    break;

                default:
                    throw new Exception("Unknown operation " + i.op + " at " + pc);
            }
        }
        in.close();
        return prog[0];
    }

    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("intcode2"));
        String data = reader.readLine();
        reader.close();
        String[] progString = data.split(",");
        int[] prog = new int[progString.length];
        for (int i = 0; i < prog.length; i++) {
            prog[i] = Integer.parseInt(progString[i]);
        }

        int result = run(prog);
        System.err.println("Final result = " + result);
    }
}
