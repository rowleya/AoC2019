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
import java.util.Scanner;
import java.util.Set;

class Instruction6 {
    private static final int ZERO = '0';
    public int op;
    public long mode[] = new long[3];

    public Instruction6(long value) {
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

class IntCodeMachine6 {

    private long[] prog;

    private boolean finished = false;

    private long lastOutput = -1;

    private int pc;

    private int relBase = 0;

    public IntCodeMachine6(long[] origProg, int size) {
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
            Instruction6 i = new Instruction6(prog[pc]);
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

class Pos {
	int x;
	int y;
	
	public Pos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int hashCode() {
		return (x + "," + y).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pos) {
			Pos c = (Pos) obj;
			return c.x == x && c.y == y;
		}
		return false;
	}
}

class Tile {
	public int x;
	public int y;
	public int type;
	
	public Tile(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		return (x + "," + y).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tile) {
			Tile c = (Tile) obj;
			return c.x == x && c.y == y;
		}
		return false;
	}
}

public class ArcadeIntCode {
	
	public static void draw(Map<Pos, Tile> tiles, int minX, int minY, int maxX, int maxY) {
		for (int i = minY; i <= maxY; i++) {
			for (int j = minX; j <= maxX; j++) {
				Pos p = new Pos(j, i);
				if (!tiles.containsKey(p)) {
					System.err.print(' ');
				} else {
					Tile t = tiles.get(p);
					char c = ' ';
					switch (t.type) {
					case 0:
						c = ' ';
						break;
					case 1:
						c = '#';
						break;
					case 2:
						c = '*';
						break;
					case 3:
						c = '-';
						break;
					case 4:
						c = '0';
						break;
					}
					System.err.print(c);;
				}
			}
			System.err.println();
		}
	}

    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("arcadeintcode"));
        String data = reader.readLine();
        reader.close();
        String[] progString = data.split(",");
        long[] prog = new long[progString.length];
        for (int i = 0; i < prog.length; i++) {
            prog[i] = Long.parseLong(progString[i]);
        }
        prog[0] = 2;
        
        Map<Pos, Tile> tiles = new HashMap<Pos, Tile>();
        
        IntCodeMachine6 m = new IntCodeMachine6(prog, 1000000);
        
        int score = 0;
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        int[] inputs = new int[]{};
        Scanner in = new Scanner(System.in);
        while (!m.isFinished()) {
            List<Long> outputs = m.runUntilOutOfInputOrFinished(inputs);
            for (int i = 0; i < outputs.size(); i += 3) {
            	int x = outputs.get(i).intValue();
            	int y = outputs.get(i + 1).intValue();
            	int type = outputs.get(i + 2).intValue();
            	if (x == -1 && y == 0) {
            		score = type;
            	} else {
            		maxX = Math.max(x, maxX);
            		maxY = Math.max(y, maxY);
            		minX = Math.min(x, minX);
            		minY = Math.min(y, minY);
		        	
		        	Tile t = new Tile(x, y, type);
		        	Pos p = new Pos(x, y);
		        	tiles.put(p, t);
            	}
            }
            System.err.println();
            draw(tiles, minX, minY, maxX, maxY);
            System.err.println(score);
            System.err.print("Joystick: ");
            inputs = new int[] {in.nextInt()};
        }
        in.close();
        System.err.println("Final result = " + score);
    }
}
