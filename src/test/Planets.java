package test;

import java.util.Arrays;
import java.util.Set;

class Planet {
    int [] pos = new int[3];
    int [] vel = new int[3];

    public Planet(int x, int y, int z) {
        this.pos[0] = x;
        this.pos[1] = y;
        this.pos[2] = z;
    }

    public Planet(Planet p) {
        this.pos = p.pos.clone();
    }

    public int energy() {
        int ep = 0;
        for (int i = 0; i < pos.length; i++) {
            ep += Math.abs(pos[i]);
        }
        int ek = 0;
        for (int i = 0; i < vel.length; i++) {
            ek += Math.abs(vel[i]);
        }
        return ep * ek;
    }

    @Override
    public String toString() {
        return "pos=<" + Arrays.toString(pos) +"> vel=<" + Arrays.toString(vel) + ">";
    }
}

public class Planets {

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

    public static int getV(int p1, int p2) {
        return p1 < p2? 1 : p1 > p2? -1: 0;
    }

    public static int ts(Planet[] planets) {
        for (Planet p1 : planets) {
            for (Planet p2 : planets) {
                if (p1 != p2) {
                    for (int i = 0; i < p1.pos.length; i++) {
                        p1.vel[i] += getV(p1.pos[i], p2.pos[i]);
                    }
                }
            }
        }

        int energy = 0;
        for (Planet p : planets) {
            for (int i = 0; i < p.pos.length; i++) {
                p.pos[i] += p.vel[i];
            }
            energy += p.energy();
        }
        return energy;
    }

    public static void main(String[] args) {
        Planet planets[] = new Planet[] {
            new Planet(17, -12, 13),
            new Planet(2, 1, 1),
            new Planet(-1, -17, 7),
            new Planet(12, -14, 18)
        };

//        Planet planets[] = new Planet[] {
//            new Planet(-8, -10, 0),
//            new Planet(5, 5, 10),
//            new Planet(2, -7, 3),
//            new Planet(9, -8, -3)
//        };
//        Planet planets[] = new Planet[] {
//            new Planet(-1, 0, 2),
//            new Planet(2, -10, -7),
//            new Planet(4, -8, 8),
//            new Planet(3, 5, -1)
//        };

//        int e = 0;
//        for (int i = 0; i < 1000; i++) {
//            //if (i % 10 == 0) {
//                for (Planet p : planets) {
//                    System.err.println(p);
//                }
//            //}
//            e = ts(planets);
//        }
//        System.err.println(e);

        Planet[] init = new Planet[planets.length];
        for (int i = 0; i < planets.length; i++) {
            init[i] = new Planet(planets[i]);
        }


        long c = 0;
        long[] foundCoord = new long[3];
        for (int i = 0; i < 3; i++) {
            foundCoord[i] = -1;
        }
        while (foundCoord[0] == -1 || foundCoord[1] == -1 || foundCoord[2] == -1) {
            ts(planets);
            c++;
//            System.err.println("Run " + c);
//            for (int i = 0; i < planets.length; i++) {
//                System.err.println(planets[i]);
//            }
            boolean[] equal = new boolean[]{true, true, true};
            for (int j = 0; j < planets.length; j++) {
                for (int k = 0; k < 3; k++) {
                    if (planets[j].pos[k] != init[j].pos[k] || planets[j].vel[k] != 0) {
                        equal[k] = false;
                    }
                }
            }
            for (int k = 0; k < 3; k++) {
                if (equal[k] && foundCoord[k] == -1) {
                    foundCoord[k] = c;
                    System.err.println(k + ", " + c);
                }
            }
        }

        System.err.println(findLCM(findLCM(foundCoord[2], foundCoord[1]), foundCoord[0]));
    }

}
