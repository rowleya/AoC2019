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


class InstructionN {
    private static final int ZERO = '0';
    public int op;
    public long mode[] = new long[3];

    public InstructionN(long value) {
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

class IntCodeMachineN {

    private static final boolean DEBUG = false;

    private long[] prog;

    private boolean finished = false;

    private long lastOutput = -1;

    private int pc;

    private int relBase = 0;

    public IntCodeMachineN(long[] origProg, int size) {
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

    public List<Long> runUntilOutOfInputOrFinished(long[] inputs) {
        LinkedList<Long> storedInputs = new LinkedList<>();
        for (int i = 0; i < inputs.length; i++) {
            storedInputs.add(inputs[i]);
        }
        ListIterator<Long> inputIter = storedInputs.listIterator();
        boolean outOfInput = false;
        List<Long> outputs = new ArrayList<Long>();
        while (pc < prog.length && !finished && !outOfInput) {
            InstructionN i = new InstructionN(prog[pc]);
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
                        //System.err.println("Using " + strval + " as input");
                    } else {
                        //System.err.println("Out of input");
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
                    //System.err.println("Output: " + ldr);
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
                    //println("Finished!");
                    break;

                default:
                    throw new RuntimeException("Unknown operation " + i.op + " at " + pc);
            }
        }
        return outputs;
    }
}

class Packet {
	long x;
	long y;
	
	@Override
	public String toString() {
		return x + "," + y;
	}
}

class Send {
	Packet p;
	long target;
}

class Net {
	IntCodeMachineN m = null;
	Deque<Packet> input = new ArrayDeque<Packet>();
	Deque<Send> output = new ArrayDeque<Send>();
	
	public Net(long[] prog, int i) {
		this.m = new IntCodeMachineN(prog, 1000000);
		processOutput(m.runUntilOutOfInputOrFinished(new long[] {i}));
	}
	
	public void run() {
		long[] i = null;
		if (input.isEmpty()) {
			i = new long[] {-1};
		} else {
			i = new long[input.size() * 2];
			int j = 0;
			while (!input.isEmpty()) {
				Packet p = input.poll();
				i[j++] = p.x;
				i[j++] = p.y;
			}
		}
		List<Long> o = m.runUntilOutOfInputOrFinished(i);
		processOutput(o);
	}
	
	public void processOutput(List<Long> o) {
		//System.err.println(o.size() + " outputs");
		for (int j = 0; j < o.size(); j += 3) {
			Send s = new Send();
			s.p = new Packet();
			s.target = o.get(j);
			s.p.x = o.get(j+1);
			s.p.y = o.get(j+2);
			
			output.push(s);
		}
		
	}
}


public class NetIntCode {
	
	

    public static void main(String[] args) throws Exception {

        BufferedReader reader = new BufferedReader(new FileReader("netintcode"));
        String data = reader.readLine();
        reader.close();
        String[] progString = data.split(",");
        long[] prog = new long[progString.length];
        for (int i = 0; i < prog.length; i++) {
            prog[i] = Long.parseLong(progString[i]);
        }
        
        Net nets[] = new Net[50];
        for (int i = 0; i < nets.length; i++) {
        	nets[i] = new Net(prog, i);
        }
        boolean done = false;
        Packet lastNat = null;
        Packet lastSent = null;
        int nCountZero = 0;
        while (!done) {
        	int count = 0;
        	int countInputs = 0;
        	for (int i = 0; i < nets.length; i++) {
        		nets[i].run();
        		while (!nets[i].output.isEmpty()) {
        			count++;
        			Send s = nets[i].output.poll();
        			if (s.target < nets.length) {
        				//System.err.println("Sending to " + s.target + " " + s.p);
        				nets[(int) s.target].input.push(s.p);
        			} else {
        				//System.err.println(i + ": Packet to " + s.target + " " + s.p);
        				if (s.target == 255) {
        					if (lastNat == null) {
        						System.err.println(s.p.y);
        					}
        					lastNat = s.p;
        				}
        			}
        		}
        		countInputs += nets[i].input.size();
        	}
        	/*for (int i = 0; i < nets.length; i++) {
        		nets[i].run();
        	}*/
        	
        	if (count == 0 && countInputs == 0) {
        		nCountZero++;
        	} else {
        		nCountZero = 0;
        	}
        	if (nCountZero > 10 && lastNat != null) {
        		System.err.println("Injecting " + lastNat);
        		nets[0].input.push(lastNat);
        		if (lastSent != null) {
        			if (lastSent.y == lastNat.y) {
        				done = true;
        				System.err.println(lastNat.y);
        			}
        		}
        		lastSent = lastNat;
        		nCountZero = 0;
        	}
        }
    }
}
