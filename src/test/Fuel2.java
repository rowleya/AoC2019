package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

class Chem {
    String id;
    long quant;

    public Chem(String id, long quant) {
        this.id = id;
        this.quant = quant;
    }

    public Chem(String s) {
        String[] bits = s.trim().split(" ");
        quant = Integer.parseInt(bits[0]);
        id = bits[1];
    }

    public String asString(int ratio) {
        return (quant * ratio) + " " + id;
    }
}

class Reaction {
    Chem[] input;
    Chem output;

    public Reaction(Chem []input, Chem output) {
        this.input = input;
        this.output = output;
    }

    public String asString(int ratio) {
        String s = "";
        for (Chem i : input) {
            s += i.asString(ratio) + "; ";

        }
        s += "=> " + output.asString(ratio);
        return s;
    }

    public boolean isInput(Chem c) {
        for (Chem i : input) {
            if (i.id == c.id) {
                return true;
            }
        }
        return false;
    }
}

class RNode {
    Reaction r;
    int quant;
    List<RNode> parents = new ArrayList<RNode>();
    List<RNode> children = new ArrayList<RNode>();

    public RNode(Reaction r) {
        this.r = r;
    }

    public void addChild(RNode c) {
        children.add(c);
    }

    public void addParent(RNode p) {
        parents.add(p);
    }

    public boolean dependsOn(Chem c) {
        if (r.isInput(c)) {
            return true;
        }
        for (RNode child : children) {
            if (child.dependsOn(c)) {
                return true;
            }
        }
        return false;
    }

}

public class Fuel2 {

    public static long test(Map<String, Reaction> reactions, long wanted) {
        Deque<Chem> needed = new ArrayDeque<Chem>();
        needed.addLast(new Chem("FUEL", wanted));
        long ore = 0;
        Map<String, Chem> extra = new HashMap<String, Chem>();
        while (!needed.isEmpty()) {
            Chem out = needed.removeLast();

            Chem e = extra.get(out.id);
            long quantNeeded = out.quant;
            long quantNeededNext = quantNeeded;
            if (e != null) {
                if (quantNeeded >= e.quant) {
                    System.err.println("Using up all " + e.quant + " of " + out.id);
                    quantNeededNext = quantNeeded - e.quant;
                    e.quant = 0;
                } else {
                    System.err.println("Using up " + quantNeeded + " of " + e.quant + " of " + out.id);
                    e.quant -= quantNeeded;
                    quantNeededNext = 0;
                    continue;
                }
            }

            Reaction r = reactions.get(out.id);

            int ratio = (int) Math.ceil((double) out.quant / (double) r.output.quant);
            long extraProduced = (ratio * r.output.quant) - out.quant;
            System.err.println("Produced " + extraProduced + " extra " + r.output.id);
            if (extraProduced > 0) {
                Chem ex = extra.get(r.output.id);
                if (ex == null) {
                    ex = new Chem(r.output.id, extraProduced);
                    extra.put(r.output.id, ex);
                } else {
                    System.err.println("Now an extra " + extraProduced + " " + r.output.id);
                    ex.quant += extraProduced;
                }
            }

            System.err.println(r.asString(ratio));
            for (Chem input : r.input) {
                long quantNeeded = input.quant * ratio;
                long quantNeededNext = quantNeeded;
                Chem e = extra.get(input.id);
                if (e != null) {
                    if (quantNeeded >= e.quant) {
                        System.err.println("Using up all " + e.quant + " of " + input.id);
                        quantNeededNext = quantNeeded - e.quant;
                        e.quant = 0;
                    } else {
                        System.err.println("Using up " + quantNeeded + " of " + e.quant + " of " + input.id);
                        e.quant -= quantNeeded;
                        quantNeededNext = 0;
                    }
                    if (e.quant == 0) {
                        extra.remove(input.id);
                    }
                }
                Chem u = used.get(input.id);
                System.err.println("Used " + quantNeeded + " of " + input.id);
                if (u == null) {
                    u = new Chem(input.id, quantNeeded);
                    used.put(input.id, u);
                } else {
                    u.quant += quantNeeded;
                }
                if (!input.id.equals("ORE") && quantNeededNext > 0) {
                    needed.addLast(new Chem(input.id, quantNeededNext));
                }
            }


        }
        return used.get("ORE").quant;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("fuel2_test2"));
        String line;
        Map<String, Reaction> reactions = new HashMap<String, Reaction>();
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" => ", 2);
            String[] iParts = parts[0].trim().split(",");
            Chem[] inputs = new Chem[iParts.length];
            for (int i = 0; i< iParts.length; i++) {
                inputs[i] = new Chem(iParts[i]);
            }
            Chem output = new Chem(parts[1]);
            Reaction r = new Reaction(inputs, output);
            reactions.put(output.id, r);
        }

        System.err.println(test(reactions, 460664));
        //           781925695372
        /*long max = 1000000000000L;

        long imin = max / test(reactions, 1);
        long imax = 2 * imin;

        while (imin < imax) {
            long imid = (imax + imin) / 2;
            System.err.print(imid + ": ");
            long ore = test(reactions, imid);
            System.err.println(ore);
            if (ore == max) {
                break;
            }
            if (ore < max) {
                imin = imid + 1;
            } else {
                imax = imid;
            }
        } */
    }


}
