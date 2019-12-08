package test;

public class Password {

    private static final int ZERO = '0';

    public static void main(String[] args) {
        int min = 387638;
        int max = 919123;

        int count = 0;

        for (int value = min; value <= max; value++) {
            String vString = String.valueOf(value);
            int lastV = vString.charAt(0) - ZERO;
            boolean isDouble = false;
            boolean isValid = true;
            int nRepeats = 1;
            for (int i = 1; i < vString.length() && isValid; i++) {
                char c = vString.charAt(i);
                int v = c - ZERO;
                if (v < lastV) {
                    isValid = false;
                } else if (v == lastV) {
                    nRepeats += 1;
                } else {
                    if (nRepeats == 2) {
                        isDouble = true;
                    }
                    nRepeats = 1;
                }
                lastV = v;
            }
            if (nRepeats == 2) {
                isDouble = true;
            }

            if (isDouble && isValid) {
                System.err.println(value);
                count += 1;
            }
        }

        System.err.println(count);
    }

}
