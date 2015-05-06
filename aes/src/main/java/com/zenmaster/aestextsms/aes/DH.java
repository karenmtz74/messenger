package com.zenmaster.aestextsms.aes;

import java.math.*;
import java.util.*;
import java.security.MessageDigest;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.*;

public class DH {

    int tamanoBit = 54;
    int certainty = 20;

    String AliceLlaveCompartidas="";
    private static final Random numeroRandom = new Random();

    public DH() {
        Random randomGenerator = new Random();
        BigInteger numeroGenerado, numeroPrimo, AlicePublica, BobPublica, AlicePrivada, BobPrivada, AliceLlaveCompartida, BobLlaveCompartida;

        numeroPrimo = encuentraNumeroPrimo();
        //System.out.println("El numero primo es: " + numeroPrimo);
        numeroGenerado = encuentraRaizPrima(numeroPrimo);//
        //System.out.println("El numero primo generado es " + numeroGenerado);

        // on machine 1
        AlicePrivada = new BigInteger(tamanoBit - 2, randomGenerator);
        // on machine 2
        BobPrivada = new BigInteger(tamanoBit - 2, randomGenerator);

        // to be published:
        AlicePublica = numeroGenerado.modPow(AlicePrivada, numeroPrimo);
        BobPublica = numeroGenerado.modPow(BobPrivada, numeroPrimo);
        AliceLlaveCompartida = BobPublica.modPow(AlicePrivada, numeroPrimo);
        BobLlaveCompartida = AlicePublica.modPow(BobPrivada, numeroPrimo);
/*
        System.out.println("Llave publida de Alice " + AlicePublica);
        System.out.println("Llave publida de  Bob " + BobPublica);
        System.out.println("Llave compartida de Alice " + AliceLlaveCompartida);
        System.out.println("Llave compartida de Bob " + BobLlaveCompartida);
        System.out.println("LLave privada de Alice " + AlicePrivada);
        System.out.println("LLave privada de Bob " + BobPrivada);*/

    }
    public void setLlaveCompartida(String AliceLlaveCompartida){
        AliceLlaveCompartidas=AliceLlaveCompartida;
    }

    public String getLlaveCompartida(){
        return this.AliceLlaveCompartidas;
    }

    private static boolean cifrado(BigInteger numeroA, BigInteger numeroN) {

        BigInteger n_minus_one = numeroN.subtract(BigInteger.ONE);

        BigInteger numeroD = n_minus_one;

        int s = numeroD.getLowestSetBit();

        numeroD = numeroD.shiftRight(s);

        BigInteger a_to_power = numeroA.modPow(numeroD, numeroN);

        if (a_to_power.equals(BigInteger.ONE)) {
            return true;
        }
        for (int i = 0; i < s - 1; i++) {
            if (a_to_power.equals(n_minus_one)) {
                return true;
            }
            a_to_power = a_to_power.multiply(a_to_power).mod(numeroN);
        }
        if (a_to_power.equals(n_minus_one)) {
            return true;
        }
        return false;
    }

    public static boolean cifrado2(BigInteger numeroN) {
        for (int repeat = 0; repeat < 20; repeat++) {
            BigInteger numeroA;
            do {
                numeroA = new BigInteger(numeroN.bitLength(), numeroRandom);
            } while (numeroA.equals(BigInteger.ZERO));
            if (!cifrado(numeroA, numeroN)) {
                return false;
            }
        }
        return true;
    }

    boolean esPrimo(BigInteger r) {
        return cifrado2(r);

    }

    public List<BigInteger> FactorPrimos(BigInteger numero) {
        BigInteger numeros = numero;
        BigInteger i = BigInteger.valueOf(2);

        BigInteger limite = BigInteger.valueOf(10000);

        List<BigInteger> factores = new ArrayList<BigInteger>();

        while (!numeros.equals(BigInteger.ONE)) {
            while (numeros.mod(i).equals(BigInteger.ZERO)) {
                factores.add(i);
                numeros = numeros.divide(i);
                // System.out.println(i);
                // System.out.println(n);
                if (esPrimo(numeros)) {
                    factores.add(numeros);
                    return factores;
                }
            }
            i = i.add(BigInteger.ONE);
            if (i.equals(limite)) {
                return factores;
            }
        }
        System.out.println(factores);
        return factores;
    }

    boolean esRaizPrima(BigInteger numeroG, BigInteger numeroP) {
        BigInteger totient = numeroP.subtract(BigInteger.ONE);
        List<BigInteger> factor = FactorPrimos(totient);
        int i = 0;
        int j = factor.size();
        for (; i < j; i++) {
            BigInteger factors = factor.get(i);
            BigInteger t = totient.divide(factors);
            if (numeroG.modPow(t, numeroP).equals(BigInteger.ONE)) {
                return false;
            }
        }
        return true;
    }

    BigInteger encuentraRaizPrima(BigInteger numeroP) {
        int start = 10;

        for (int i = start; i < 100000000; i++) {
            if (esRaizPrima(BigInteger.valueOf(i), numeroP)) {
                return BigInteger.valueOf(i);
            }
        }

        return BigInteger.valueOf(0);
    }

    BigInteger encuentraNumeroPrimo() {
        Random random = new Random();
        BigInteger numeroP = BigInteger.ZERO;
        numeroP = new BigInteger(tamanoBit, certainty, random);
        return numeroP;

    }

}

