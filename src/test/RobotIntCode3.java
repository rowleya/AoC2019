package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class InstructionR3 {
    private static final int ZERO = '0';
    public int op;
    public long mode[] = new long[3];

    public InstructionR3(long value) {
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

    public int getPos(long[] prog, int pc, int arg, int relBase) {
        if (mode[arg] == 0) {
            return (int) prog[pc + arg + 1];
        } else if (mode[arg] == 1) {
            return pc + arg + 1;
        } else if (mode[arg] == 2) {
            return (int) prog[pc + arg + 1] + relBase;
        }
        throw new RuntimeException(
            "Unknown mode " + mode[arg] + " at pc " + pc + " arg " + arg);
    }

    public long getValue(long[] prog, int pc, int arg, int relBase) {
        int pos = getPos(prog, pc, arg, relBase);
        //System.err.println("Reading from " + pos);
        return prog[pos];
    }
}

class IntCodeMachineR3 {

    private static final boolean DEBUG = false;

    private long[] prog;

    private boolean finished = false;

    private long lastOutput = -1;

    private int pc;

    private int relBase = 0;

    public IntCodeMachineR3(long[] origProg, int size) {
        prog = new long[size];
        for (int i = 0; i < origProg.length; i++) {
            prog[i] = origProg[i];
        }
        pc = 0;
    }

    private void println(String s) {
        if (DEBUG) {
            System.err.println(s);
        }
    }

    private void printInstruction(int nArgs) {
        if (DEBUG) {
            System.err.print(pc + ": " + prog[pc]);
            for (int i = 0; i < nArgs; i++) {
                System.err.print(" " + prog[pc + i + 1]);
            }
        System.err.println();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public List<Long> runUntilOutOfInputOrFinished(int[] inputs) {
        LinkedList<Integer> storedInputs = new LinkedList<>();
        for (int i = 0; i < inputs.length; i++) {
            storedInputs.add(inputs[i]);
        }
        ListIterator<Integer> inputIter = storedInputs.listIterator();
        boolean outOfInput = false;
        List<Long> outputs = new ArrayList<Long>();
        while (pc < prog.length && !finished && !outOfInput) {
            InstructionR3 i = new InstructionR3(prog[pc]);
            switch (i.op) {
                case 1:
                    printInstruction(3);
                    int outsum = i.getPos(prog, pc, 2, relBase);
                    long insum1 = i.getValue(prog, pc, 0, relBase);
                    long  insum2 = i.getValue(prog, pc, 1, relBase);
                    prog[outsum] = insum1 + insum2;
                    println("pc[" + outsum + "] = " + insum1 + " + " + insum2);
                    pc += 4;
                    break;

                case 2:
                    printInstruction(3);
                    int outmul = i.getPos(prog, pc, 2, relBase);
                    long inmul1 = i.getValue(prog, pc, 0, relBase);
                    long inmul2 = i.getValue(prog, pc, 1, relBase);
                    prog[outmul] = inmul1 * inmul2;
                    println("pc[" + outmul + "] = " + inmul1 + " * " + inmul2);
                    pc += 4;
                    break;

                case 3:
                    printInstruction(1);
                    long strval = -1;
                    if (inputIter.hasNext()) {
                        strval = inputIter.next();
                        System.err.println("Using " + strval + " as input");
                    } else {
                        System.err.println("Out of input");
                        outOfInput = true;
                        break;
                    }
                    int outstr = i.getPos(prog, pc, 0, relBase);
                    prog[outstr] = strval;
                    println("pc[" + outstr + "] = " + strval);
                    pc += 2;
                    break;

                case 4:
                    printInstruction(1);
                    long ldr = i.getValue(prog, pc, 0, relBase);
                    System.err.println("Output: " + ldr);
                    lastOutput = ldr;
                    outputs.add(ldr);
                    pc += 2;
                    break;

                case 5:
                    printInstruction(2);
                    long valueTrue = i.getValue(prog, pc, 0, relBase);
                    int jumpTrue = (int) i.getValue(prog, pc, 1, relBase);
                    if (valueTrue > 0) {
                        println("Jump to " + jumpTrue + " because " + valueTrue + " > 0");
                        pc = jumpTrue;
                    } else {
                        println("Don't Jump to " + jumpTrue + " because " + valueTrue + " <= 0");
                        pc += 3;
                    }
                    break;

                case 6:
                    printInstruction(2);
                    long valueFalse = i.getValue(prog, pc, 0, relBase);
                    int jumpFalse = (int) i.getValue(prog, pc, 1, relBase);
                    println("Jump to " + jumpFalse + " because " + valueFalse + " == 0");
                    if (valueFalse == 0) {
                        pc = jumpFalse;
                    } else {
                        println("Don't Jump to " + jumpFalse + " because " + valueFalse + " != 0");
                        pc += 3;
                    }
                    break;

                case 7:
                    printInstruction(3);
                    long ltv1 = i.getValue(prog, pc, 0, relBase);
                    long ltv2 = i.getValue(prog, pc, 1, relBase);
                    int ltout = i.getPos(prog, pc, 2, relBase);
                    if (ltv1 < ltv2) {
                        println("prog[" + ltout + "] = 1 (" + ltv1 + " < " + ltv2 + ")");
                        prog[ltout] = 1;
                    } else {
                        println("prog[" + ltout + "] = 0 (" + ltv1 + " >= " + ltv2 + ")");
                        prog[ltout] = 0;
                    }
                    pc += 4;
                    break;

                case 8:
                    printInstruction(3);
                    long eqv1 = i.getValue(prog, pc, 0, relBase);
                    long eqv2 = i.getValue(prog, pc, 1, relBase);
                    int eqout = i.getPos(prog, pc, 2, relBase);
                    if (eqv1 == eqv2) {
                        println("prog[" + eqout + "] = 1 (" + eqv1 + " == " + eqv2 + ")");
                        prog[eqout] = 1;
                    } else {
                        println("prog[" + eqout + "] = 0 (" + eqv1 + " != " + eqv2 + ")");
                        prog[eqout] = 0;
                    }
                    pc += 4;
                    break;

                case 9:
                    printInstruction(1);
                    int newBase = (int) i.getValue(prog, pc, 0, relBase);
                    relBase += newBase;
                    println("relBase += " + newBase + " = " + relBase);
                    pc += 2;
                    break;

                case 99:
                    printInstruction(0);
                    finished = true;
                    println("Finished!");
                    break;

                default:
                    throw new RuntimeException("Unknown operation " + i.op + " at " + pc);
            }
        }
        return outputs;
    }
}

class CoordR3 {
    public int x;
    public int y;

    public CoordR3(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return (x + "," + y).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CoordR3) {
            CoordR3 c = (CoordR3) obj;
            return c.x == x && c.y == y;
        }
        return false;
    }
}

class RobotR3 {
    public int minX;
    public int minY;
    public int maxX;
    public int maxY;
    public CoordR3 location;
    public Map<CoordR3, Integer> state = new HashMap<CoordR3, Integer>();
    public Deque<Integer> trace = new ArrayDeque<Integer>();
    public RobotR3(int x, int y) {
        location = new CoordR3(x, y);
        minX = x;
        maxX = x;
        minY = y;
        maxY = y;
    }

    public CoordR3 getMove(int move, CoordR3 l) {

        switch (move) {
        case 1:
            return new CoordR3(l.x, l.y - 1);

        case 4:
            return new CoordR3(l.x + 1, l.y);

        case 2:
            return new CoordR3(l.x, l.y + 1);

        case 3:
            return new CoordR3(l.x - 1, l.y);

        default:
            throw new RuntimeException("Unknown coomand " + move);
        }
    }

    public void move(int move, int result, boolean save) {

        CoordR3 next = getMove(move, location);
        maxX = Math.max(next.x, maxX);
        maxY = Math.max(next.y, maxY);
        minX = Math.min(next.x, minX);
        minY = Math.min(next.y, minY);

        System.err.println(minX + "," + minY + " - " + maxX + "," + maxY);

        state.put(next, result);

        if (result != 0) {
            location = next;
            if (save) {
                int rev = -1;
                switch (move) {
                case 1:
                    rev = 2;
                    break;
                case 2:
                    rev = 1;
                    break;
                case 3:
                    rev = 4;
                    break;
                case 4:
                    rev = 3;
                    break;
                }
                trace.push(rev);
            }
            System.err.println("Robot at " + location.x +"," + location.y);
        }
    }

    public int getState() {
        if (state.containsKey(location)) {
            return state.get(location);
        }
        return 0;
    }

    public int getState(int x, int y) {
        CoordR3 l = new CoordR3(x, y);
        if (state.containsKey(l)) {
            return state.get(l);
        }
        return -1;
    }

    public int getStateMove(int move) {
        CoordR3 next = getMove(move, location);
        return getState(next.x, next.y);
    }

    public void setState(int c) {
        state.put(location, c);
    }

    public boolean isHere(int x, int y) {
        return location.x == x && location.y == y;
    }

    public void draw() {
        draw(null);
    }

    public void draw(Set<CoordR3> oxygen) {
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (oxygen != null) {
                    if (oxygen.contains(new CoordR3(x, y))) {
                        System.err.print('@');
                        continue;
                    }
                }
                int s = getState(x, y);
                if (s == 0) {
                    System.err.print('#');
                } else if (s == -1) {
                    System.err.print('?');
                } else if (s == 2 && isHere(x, y)) {
                    System.err.print('O');
                } else if (s == 2 && !isHere(x, y)) {
                    System.err.print('o');
                } else if (isHere(x, y)) {
                    System.err.print('D');
                } else {
                    System.err.print(' ');
                }
            }
            System.err.println();
        }
    }

    public boolean isUnknown() {
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                if (getState(x, y) == -1) {
                    return true;
                }
            }
        }
        for (int y = minY; y <= maxY; y++) {
            if (getState(minX, y) != 0 || getState(maxX, y) != 0) {
                return true;
            }
        }
        for (int x = minX; x <= maxX; x++) {
            if (getState(x, minY) != 0 || getState(x, maxY) != 0) {
                return true;
            }
        }
        return false;
    }
}

public class RobotIntCode3 {

    public static void printOut(List<Long> out) {
        for (Long o : out) {
            char c = (char) o.intValue();
            System.err.print(c);
        }
    }

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

    public static boolean isRunning(IntCodeMachineR3[] machines) {
        for (IntCodeMachineR3 machine : machines) {
            if (!machine.isFinished()) {
                return true;
            }
        }
        return false;
    }

    public static int nextMove(List<Integer> moves, RobotR3 r, Random rand) {
        Collections.shuffle(moves, rand);
        int next = 0;
        for (int move : moves) {
            int s = r.getStateMove(move);
            if (s == -1) {
                return move;
            } else if (s != 0) {
                next = move;
            }
        }
        return -1;
    }

    public static int[] getInput(String prog) {
        int[] input = new int[prog.length() + 1];
        for (int i = 0; i < prog.length(); i++) {
            char c = prog.charAt(i);
            input[i] = (int) c;
        }
        input[prog.length()] = 10;
        return input;
    }

    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("robotint3"));
        String data = reader.readLine();
        reader.close();
        String[] progString = data.split(",");
        long[] prog = new long[progString.length];
        for (int i = 0; i < prog.length; i++) {
            prog[i] = Long.parseLong(progString[i]);
        }

        RobotR3 r = new RobotR3(0, 0);
        IntCodeMachineR3 m = new IntCodeMachineR3(prog, 1000000);

        List<Long> out = m.runUntilOutOfInputOrFinished(new int[0]);
        int y = 0;
        int x = 0;
        Set<CoordR3> scaf = new HashSet<CoordR3>();
        for (Long o : out) {
            char c = (char) o.intValue();
            System.err.print(c);
            if (c == '\n' || c == '\r') {
                y++;
                x = 0;
            } else {
                if (c == '#') {
                    scaf.add(new CoordR3(x, y));
                }
                x++;
            }
        }

        int v = 0;
        for (CoordR3 c : scaf) {
            boolean intersect = true;
            for (int i = 1; i <= 4; i++) {
                CoordR3 n = r.getMove(i, c);
                if (!scaf.contains(n)) {
                    intersect = false;
                    break;
                }
            }
            if (intersect) {
                System.err.println(c.x + ", " + c.y);
                v += c.x * c.y;
            }
        }
        System.err.println(v);

        prog[0] = 2;
        IntCodeMachineR3 m2 = new IntCodeMachineR3(prog, 1000000);
        String A = "R,8,L,4,R,4,R,10,R,8";
        String B = "L,12,L,12,R,8,R,8";
        String C = "R,10,R,4,R,4\nn";
        String PROG = "A,A,B,C,B,C,B,C,C,A";

        int[] p = getInput(PROG);
        int[] pA = getInput(A);
        int[] pB = getInput(B);
        int[] pC = getInput(C);
        printOut(m2.runUntilOutOfInputOrFinished(new int[]{}));
        System.err.print("Prog: ");
        printOut(m2.runUntilOutOfInputOrFinished(p));
        System.err.print("A: ");
        printOut(m2.runUntilOutOfInputOrFinished(pA));
        System.err.print("B: ");
        printOut(m2.runUntilOutOfInputOrFinished(pB));
        System.err.print("C: ");
        printOut(m2.runUntilOutOfInputOrFinished(pC));
        List<Long> output = m.runUntilOutOfInputOrFinished(new int[]{});
        System.err.print("END: ");
        printOut(output);
        System.err.println(output.get(0));
    }
}
