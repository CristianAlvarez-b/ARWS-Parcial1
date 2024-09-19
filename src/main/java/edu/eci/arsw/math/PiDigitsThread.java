package edu.eci.arsw.math;

import static edu.eci.arsw.math.PiDigits.DigitsPerSum;
import static edu.eci.arsw.math.PiDigits.sum;

public class PiDigitsThread extends Thread{
    private int start;
    private int count;
    private byte[] digits;

    public PiDigitsThread(int start, int count) {
        this.start = start;
        this.count = count;
        digits = new byte[count];
    }

    @Override
    public void run(){
        double sum = 0;
        for (int i = 0; i < count; i++) {
            if (i % DigitsPerSum == 0) {
                sum = 4 * sum(1, start)
                        - 2 * sum(4, start)
                        - sum(5, start)
                        - sum(6, start);

                start += DigitsPerSum;
            }
            sum = 16 * (sum - Math.floor(sum));
            digits[i] = (byte) sum;
        }
    }

    public byte[] getDigits() {
        return digits;
    }
}
