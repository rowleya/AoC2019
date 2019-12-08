package test;

import java.io.BufferedReader;
import java.io.FileReader;

class Move {
    public char direction;
    public int distance;

    public Move(String move) {
        direction = move.charAt(0);
        distance = Integer.parseInt(move.substring(1));
    }

    public String toString() {
        return String.valueOf(direction) + distance;
    }
}

class Coordinate {
    public int x;
    public int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int distance(Coordinate c2) {
        return Math.abs(c2.x - x) + Math.abs(c2.y - y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean equals(Coordinate c2) {
        return x == c2.x && y == c2.y;
    }
}

class Line {
    public Coordinate c1;
    public Coordinate c2;
    public Coordinate start;
    public Coordinate end;
    public int length;

    public Line(Coordinate start, Move move) {
        this.c1 = new Coordinate(start.x, start.y);
        this.start = c1;
        int x2;
        int y2;
        switch (move.direction) {
            case 'R':
                y2 = c1.y;
                x2 = c1.x + move.distance;
                break;
            case 'L':
                y2 = c1.y;
                x2 = c1.x - move.distance;
                break;
            case 'U':
                x2 = c1.x;
                y2 = c1.y + move.distance;
                break;
            case 'D':
                x2 = c1.x;
                y2 = c1.y - move.distance;
                break;
            default:
                throw new RuntimeException("Unknown direction " + move.direction);
        }
        end = new Coordinate(x2, y2);
        c2 = end;
        length = c1.distance(c2);
        if ((this.isVertical() && c1.y > c2.y) || (this.isHorizontal() && c1.x > c2.x)) {
            Coordinate temp = c1;
            c1 = c2;
            c2 = temp;
        }
    }

    public boolean isVertical() {
        return c1.x == c2.x;
    }

    public boolean isHorizontal() {
        return c1.y == c2.y;
    }

    public Coordinate intersection(Line line) {
        if (this.isVertical() && line.isVertical()) {
            return null;
        }

        if (this.isHorizontal() && line.isHorizontal()) {
            return null;
        }
        int x1 = c1.x;
        int y1 = c1.y;
        int x2 = c2.x;
        int y2 = c2.y;
        int x3 = line.c1.x;
        int y3 = line.c1.y;
        int x4 = line.c2.x;
        int y4 = line.c2.y;
        int xT = (((x2 * y1) - (y2 * x1)) * (x4 - x3)) - (((x4 * y3) - (x3 * y4)) * (x2 - x1));
        int yT = (((x2 * y1) - (y2 * x1)) * (y4 - y3)) - (((x4 * y3) - (x3 * y4)) * (y2 - y1));
        int b = ((x2 - x1) * (y4 - y3)) - ((x4 - x3) * (y2 - y1));
        int x = xT / b;
        int y = yT / b;
        if (x < x1 || x > x2 || x < x3 || x > x4 || y < y1 || y > y2 || y < y3 || y > y4) {
            return null;
        }
        return new Coordinate(x, y);
    }

    public String toString() {
        return "[" + c1 + "->" + c2 + "]";
    }
}

public class Intersection {

    public static Move[] getMoves(String line) {
        String[] parts = line.split(",");
        Move[] moves = new Move[parts.length];
        for (int i = 0; i < parts.length; i++) {
            moves[i] = new Move(parts[i]);
        }
        return moves;
    }

    public static Line[] getLines(Coordinate start, Move[] moves) {
        Line[] lines = new Line[moves.length];
        Coordinate next = start;
        for (int i = 0; i < moves.length; i++) {
            lines[i] = new Line(next, moves[i]);
            next = lines[i].end;
        }
        return lines;
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("intersection"));
        String line1 = reader.readLine();
        String line2 = reader.readLine();
        reader.close();
        Coordinate start = new Coordinate(0, 0);
        Move[] moves1 = getMoves(line1);
        Move[] moves2 = getMoves(line2);
        Line[] lines1 = getLines(start, moves1);
        Line[] lines2 = getLines(start, moves2);

        for (int i = 0; i < moves1.length; i++) {
            System.err.println(moves1[i] + " = " + lines1[i]);
        }
        for (int i = 0; i < moves2.length; i++) {
            System.err.println(moves2[i] + " = " + lines2[i]);
        }

        int minDistance = Integer.MAX_VALUE;
        int l1Distance = 0;
        for (Line l1 : lines1) {
            int l2Distance = 0;
            for (Line l2 : lines2) {
                Coordinate intersection = l1.intersection(l2);
                if (intersection != null && !intersection.equals(start)) {
                    int d1 = l1Distance + l1.start.distance(intersection);
                    int d2 = l2Distance + l2.start.distance(intersection);
                    int distance = d1 + d2;
                    if (distance != 0 && distance < minDistance) {
                        minDistance = distance;
                    }
                }
                l2Distance += l2.length;
            }
            l1Distance += l1.length;
        }

        System.err.println(minDistance);
    }
}
