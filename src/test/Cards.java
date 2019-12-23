package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;

class Coeffs {
	BigInteger a;
	BigInteger b;
	
	public Coeffs(BigInteger a, BigInteger b) {
		this.a = a;
		this.b = b;
	}
}

public class Cards {
	
	public static int[] newStack(int[] cards) {
		int[] newCards = new int[cards.length];
		for (int i = 0; i < cards.length; i++) {
			newCards[cards.length - i - 1] = cards[i];
		}
		return newCards;
	}
	
	public static Coeffs cardAtStackCoeffs(BigInteger nCards, Coeffs i) {
		return new Coeffs(BigInteger.valueOf(-1).multiply(i.a).mod(nCards), BigInteger.valueOf(-1).multiply(i.b).subtract(BigInteger.ONE).mod(nCards));
	}
	
	public static long nextStack(long card, long nCards) {
		return nCards - card - 1;
	}
	
	public static int[] cut(int[] cards, int n) {
		int[] newCards  = new int[cards.length];
		if (n >= 0) {
			for (int i = 0; i < cards.length; i++) {
				newCards[i] = cards[(i + n) % cards.length];
			}
			return newCards;
		}
        for (int i = 0; i < cards.length; i++) {
			newCards[(i - n) % cards.length] = cards[i];
		}
        return newCards;
	}
	
	public static long nextCut(long card, long nCards, long n) {
		if (n >= 0) {
			long next = card - n;
			if (next < 0) {
				return nCards + next;
			}
			return next;
		}
		return (card - n) % nCards;
	}
	
	public static Coeffs cardAtCut(BigInteger nCards, Coeffs i, BigInteger c) {
		return new Coeffs(i.a, i.b.add(c).mod(nCards);
	}
	
	public static int[] increment(int[] cards, int n) {
		int[] newCards = new int[cards.length];
		for (int i = 0; i < cards.length; i++) {
			newCards[(i * n) % cards.length] = cards[i];
		}
		return newCards;
	}
	
	public static long nextInc(long card, long nCards, long n) {
		return (card * n) % nCards;
 	}
	
	public static Coeffs cardAtInc(BigInteger nCards, Coeffs i, BigInteger n) {
		BigInteger inv = nCards.modInverse(n);
		return new Coeffs(i.a.multiply(inv).mod(nCards), i.b.multiply(inv).mod(nCards));
	}
	
	public static final String INC = "deal with increment ";
	
	public static final String CUT = "cut ";
	
	public static final String STACK = "deal into new stack";
	
	public static int getValue(String line, String pre) {
		return Integer.parseInt(line.substring(pre.length()));
	}
	
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
    
    public static long run(long card, long nCards) throws Exception{
    	BufferedReader reader = new BufferedReader(new FileReader("cards"));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(INC)) {
				int n = getValue(line, INC);
				card = nextInc(card, nCards, n);
			} else if (line.startsWith(CUT)) {
				int n = getValue(line, CUT);
				card = nextCut(card, nCards, n);
			} else if (line.startsWith(STACK)) {
				card = nextStack(card, nCards);
			}
		}
		reader.close();
		return card;
    }
	
    public static long runInv(long card, long nCards, ) throws Exception{
    	BufferedReader reader = new BufferedReader(new FileReader("cards"));
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith(INC)) {
				int n = getValue(line, INC);
				card = nextInc(card, nCards, n);
			} else if (line.startsWith(CUT)) {
				int n = getValue(line, CUT);
				card = nextCut(card, nCards, n);
			} else if (line.startsWith(STACK)) {
				card = nextStack(card, nCards);
			}
		}
		reader.close();
		return card;
    }
	
    
	public static void main(String[] args) throws Exception {
	
		long card = run(2019, 10007);
		System.err.println(card);
		
		
		long nBigCards = 119315717514047L;
		long nShuffles = 101741582076661L;
		
		
		
	}

}
