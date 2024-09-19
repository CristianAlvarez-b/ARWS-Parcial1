package edu.eci.arsw.math;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

///  <summary>
///  An implementation of the Bailey-Borwein-Plouffe formula for calculating hexadecimal
///  digits of pi.
///  https://en.wikipedia.org/wiki/Bailey%E2%80%93Borwein%E2%80%93Plouffe_formula
///  *** Translated from C# code: https://github.com/mmoroney/DigitsOfPi ***
///  </summary>
public class PiDigits {

    /**
     * Returns a range of hexadecimal digits of pi.
     * @param start The starting location of the range.
     * @param count The number of digits to return
     * @return An array containing the hexadecimal digits.
     */
    public static byte[] getDigits(int start, int count, int N) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        AtomicBoolean flag = new AtomicBoolean(false);
        if (start < 0) {
            throw new RuntimeException("Invalid Interval");
        }

        if (count < 0) {
            throw new RuntimeException("Invalid Interval");
        }
        byte[] digits = new byte[count];
        ArrayList<PiDigitsThread> hilos = new ArrayList<>();
        int newCount = count / N;
        int remaining = count % N;
        for(int i=0; i < N; i++){
            PiDigitsThread hilo;
            if(i+1==N){
                //Si es el ultimo, el conteo debe ir incluyendo el remaining
                hilo = new PiDigitsThread(start + (i * newCount), newCount + remaining, flag);
            }else {
                hilo = new PiDigitsThread(start + (i * newCount), newCount, flag);
            }
            hilo.start();
            hilos.add(hilo);
        }

        while(true){
            //Esta espera es por si los hilos resulven el problema rapido, para que el usuario no espere los 5 segundos
            Thread.sleep(100);
            boolean isAnyThreadAlive = true;
            int initialCount = 0;
            for(PiDigitsThread p: hilos){
                initialCount += p.getCountDigits();
                if(p.isAlive()){
                    isAnyThreadAlive = true;
                    break;
                }else{
                    isAnyThreadAlive = false;
                }
            }
            if(!isAnyThreadAlive || initialCount == count){
                break;
            }
            //Si preciso un hilo acabo su ejecucion despues de haber consultado si seguia vivo, entonces se ejecutara la espera.
            //Despues de dar enter al scanner se mostrara el resultado en ese caso.
            Thread.sleep(5000);
            flag.set(true);
            int countCurrentDigits = 0;
            for(PiDigitsThread h: hilos){
                countCurrentDigits += h.getCountDigits();
            }
            System.out.println("Digitos procesados: " + countCurrentDigits);
            System.out.println("Precione enter para continuar: ");
            scanner.nextLine();
            flag.set(false);
            synchronized (flag){
                flag.notifyAll();
            }
        }



        for(PiDigitsThread h: hilos){
            h.join();
        }

        int currentPosition = 0;
        for (PiDigitsThread p : hilos) {
            byte[] hiloResult = p.getDigits();
            System.arraycopy(hiloResult, 0, digits, currentPosition, hiloResult.length);
           currentPosition += hiloResult.length;
        }
        return digits;
    }



}
