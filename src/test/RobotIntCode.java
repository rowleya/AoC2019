package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

class Instruction5 {
    private static final int ZERO = '0';
    public int op;
    public long mode[] = new long[3];

    public Instruction5(long value) {
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
        System.err.println("Reading from " + pos);
        return prog[pos];
    }
}

class IntCodeMachine5 {

    private long[] prog;

    private boolean finished = false;

    private long lastOutput = -1;

    private int pc;

    private int relBase = 0;

    public IntCodeMachine5(long[] origProg, int size) {
        prog = new long[size];
        for (int i = 0; i < origProg.length; i++) {
            prog[i] = origProg[i];
        }
        pc = 0;
    }

    private void printInstruction(int nArgs) {
        System.err.print(pc + ": " + prog[pc]);
        for (int i = 0; i < nArgs; i++) {
            System.err.print(" " + prog[pc + i + 1]);
        }
        System.err.println();
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
            Instruction5 i = new Instruction5(prog[pc]);
            switch (i.op) {
                case 1:
                    printInstruction(3);
                    int outsum = i.getPos(prog, pc, 2, relBase);
                    long insum1 = i.getValue(prog, pc, 0, relBase);
                    long  insum2 = i.getValue(prog, pc, 1, relBase);
                    prog[outsum] = insum1 + insum2;
                    System.err.println("pc[" + outsum + "] = " + insum1 + " + " + insum2);
                    pc += 4;
                    break;

                case 2:
                    printInstruction(3);
                    int outmul = i.getPos(prog, pc, 2, relBase);
                    long inmul1 = i.getValue(prog, pc, 0, relBase);
                    long inmul2 = i.getValue(prog, pc, 1, relBase);
                    prog[outmul] = inmul1 * inmul2;
                    System.err.println("pc[" + outmul + "] = " + inmul1 + " * " + inmul2);
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
                    System.err.println("pc[" + outstr + "] = " + strval);
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
                        System.err.println("Jump to " + jumpTrue + " because " + valueTrue + " > 0");
                        pc = jumpTrue;
                    } else {
                        System.err.println("Don't Jump to " + jumpTrue + " because " + valueTrue + " <= 0");
                        pc += 3;
                    }
                    break;

                case 6:
                    printInstruction(2);
                    long valueFalse = i.getValue(prog, pc, 0, relBase);
                    int jumpFalse = (int) i.getValue(prog, pc, 1, relBase);
                    System.err.println("Jump to " + jumpFalse + " because " + valueFalse + " == 0");
                    if (valueFalse == 0) {
                        pc = jumpFalse;
                    } else {
                        System.err.println("Don't Jump to " + jumpFalse + " because " + valueFalse + " != 0");
                        pc += 3;
                    }
                    break;

                case 7:
                    printInstruction(3);
                    long ltv1 = i.getValue(prog, pc, 0, relBase);
                    long ltv2 = i.getValue(prog, pc, 1, relBase);
                    int ltout = i.getPos(prog, pc, 2, relBase);
                    if (ltv1 < ltv2) {
                        System.err.println("prog[" + ltout + "] = 1 (" + ltv1 + " < " + ltv2 + ")");
                        prog[ltout] = 1;
                    } else {
                        System.err.println("prog[" + ltout + "] = 0 (" + ltv1 + " >= " + ltv2 + ")");
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
                        System.err.println("prog[" + eqout + "] = 1 (" + eqv1 + " == " + eqv2 + ")");
                        prog[eqout] = 1;
                    } else {
                        System.err.println("prog[" + eqout + "] = 0 (" + eqv1 + " != " + eqv2 + ")");
                        prog[eqout] = 0;
                    }
                    pc += 4;
                    break;

                case 9:
                    printInstruction(1);
                    int newBase = (int) i.getValue(prog, pc, 0, relBase);
                    relBase += newBase;
                    System.err.println("relBase += " + newBase + " = " + relBase);
                    pc += 2;
                    break;

                case 99:
                    printInstruction(0);
                    finished = true;
                    System.err.println("Finished!");
                    break;

                default:
                    throw new RuntimeException("Unknown operation " + i.op + " at " + pc);
            }
        }
        return outputs;
    }
}

class Coord {
	public int x;
	public int y;
	
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return (x + "," + y).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coord) {
			Coord c = (Coord) obj;
			return c.x == x && c.y == y;
		}
		return false;
	}
}

class Robot {
	public int minX;
	public int minY;
	public int maxX;
	public int maxY;
	public Coord location;
	public int direction;
	public Map<Coord, Integer> colour = new HashMap<Coord, Integer>();
	
	public Robot(int x, int y) {
		location = new Coord(x, y);
		minX = x;
		maxX = x;
		minY = y;
		maxY = y;
	}
	
	public void processOutput(int paint, int move) {
		colour.put(location, paint);
		if (move == 0) {
			direction -= 1;
			if (direction < 0) {
				direction = 3;
			}
		} else if (move == 1) {
			direction += 1;
			if (direction > 3) {
				direction = 0;
			}
		} else { 
			throw new RuntimeException("Unknown move " + move);
		}
		
		switch (direction) {
		case 0:
			location = new Coord(location.x, location.y - 1);
			break;
		case 1:
			location = new Coord(location.x + 1, location.y);
			break;
		case 2:
			location = new Coord(location.x, location.y + 1);
			break;
		case 3:
			location = new Coord(location.x - 1, location.y);
			break;
		default:
			throw new RuntimeException("Unknown direction " + direction);
		}
		
		maxX = Math.max(location.x, maxX);
		maxY = Math.max(location.y, maxY);
		minX = Math.min(location.x, minX);
		minY = Math.min(location.y, minY);
	}
	
	public int getColour() {
		if (colour.containsKey(location)) {
			return colour.get(location);
		}
		return 0;
	}
	
	public int getColour(int x, int y) {
		Coord l = new Coord(x, y);
		if (colour.containsKey(l)) {
			return colour.get(l);
		}
		return 0;
	}
	
	public void setColour(int x, int y, int c) {
		colour.put(new Coord(x, y), c);
	}
	
	public void draw() {
		for (int y = minY; y <= maxY; y++) {
			for (int x = 0; x <= maxX; x++) {
				if (getColour(x, y) == 1) {
					System.err.print('#');
				} else {
					System.err.print(' ');
				}
			}
			System.err.println();
		}
	}
}

public class RobotIntCode {

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

    public static boolean isRunning(IntCodeMachine5[] machines) {
        for (IntCodeMachine5 machine : machines) {
            if (!machine.isFinished()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("robotint"));
        String data = reader.readLine();
        reader.close();
        String[] progString = data.split(",");
        long[] prog = new long[progString.length];
        for (int i = 0; i < prog.length; i++) {
            prog[i] = Long.parseLong(progString[i]);
        }
        
        Robot r = new Robot(0, 0);
        r.setColour(0, 0, 1);
        IntCodeMachine5 m = new IntCodeMachine5(prog, 1000000);
        while (!m.isFinished()) {
            int[] inputs = new int[]{r.getColour()};
            List<Long> outputs = m.runUntilOutOfInputOrFinished(inputs);
            r.processOutput(outputs.get(0).intValue(), outputs.get(1).intValue());
        }
        r.draw();
        System.err.println("Final result = " + r.colour.size());
    }
}
