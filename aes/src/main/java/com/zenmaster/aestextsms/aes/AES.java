package com.zenmaster.aestextsms.aes;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;


public class AES
{
    byte[] key;
    byte[] plainBytes;
    byte[] cipherBytes;
    Map<Byte, Byte> SBoxDic;
    Map<Byte, Byte> InvSBoxDic;
    BitTransformer tables;

    DH dh= new DH();
    String llaveCompartida=dh.getLlaveCompartida();




    public AES() {
        key = Helper.hexStringToByteArray(llaveCompartida);
        SBoxDic= new HashMap<Byte, Byte>() ;
        InvSBoxDic= new HashMap<Byte, Byte>() ;
        tables=new BitTransformer();
    }

    public AES(byte[] key) {
        this.key = key;
    }

    public String Cipher(String toCipher) {
        Log.d("CIPHER", "Inicio de cifrado");
        String send;
        String works = new String();
        byte[] toSend;
        for (int i = 0; i < toCipher.length(); i += 16) {
            if (toCipher.length() - i > 16) {
                send = toCipher.substring(i, 16);
            } else {
                send = toCipher.substring(i, toCipher.length() - i);
            }
            if (send.length() != 16) {
                int missing = 16 - send.length();
                for (int j = 0; j < missing; j++) {
                    send += "0";
                }
            }
            try {
                toSend = send.getBytes("ASCII");
            } catch (Exception ex) {
                toSend = new byte[16];
            }
            works += Helper.bytesToHex(Cipher(toSend));
        }
        return works;
    }

    public String Decipher(String toDecipher) {
        Log.d("DECIPHER", "Inicio descifrado");
        String send;
        byte[] toSend;
        String works = new String();
        for (int i = 0; i < toDecipher.length(); i += 32) {
            if (toDecipher.length() - i > 32) {
                send = toDecipher.substring(i, 32);
            } else {
                send = toDecipher.substring(i, toDecipher.length() - i);
            }
            if (send.length() != 32) {
                int missing = 32 - send.length();
                for (int j = 0; j < missing; j++) {
                    send += "A";
                }
            }

            toSend = Helper.hexStringToByteArray(send);

            works += Helper.bytesToString(Decipher(toSend));
        }
        return works;
    }

    private byte[] Cipher(byte[] toCipher) //deben ser 16 bytes
    {
        Log.d("CIPHER[BYTE]", "Cifrado por bytes");
        SMS[] keyExpanded = KeyExpansion(key);
        byte[] roundKey = WordToByteKey(keyExpanded, 0);
        toCipher = AddRoundKey(toCipher, roundKey);
        for (int i = 1; i < 10; i++) {
            roundKey = WordToByteKey(keyExpanded, i);
            toCipher = SubByte(toCipher);
            toCipher = ShiftRowTransformation(toCipher);
            toCipher = MixColumns(toCipher);
            toCipher = AddRoundKey(toCipher, roundKey);
        }

        roundKey = WordToByteKey(keyExpanded, 10);
        toCipher = SubByte(toCipher);
        toCipher = ShiftRowTransformation(toCipher);
        toCipher = AddRoundKey(toCipher, roundKey);

        return toCipher;

    }

    private byte[] Decipher(byte[] toDecipher) {
        Log.d("DECIPHER[] byte", "DESCIFRADO POR BYTES");
        SMS[] keyExpanded = KeyExpansion(key);
        byte[] roundKey = WordToByteKey(keyExpanded, 10);
        toDecipher = AddRoundKey(toDecipher, roundKey);
        toDecipher = InverseShiftRowTransformation(toDecipher);
        toDecipher = InverseSubByte(toDecipher);

        for (int i = 9; i > 0; i--) {
            roundKey = WordToByteKey(keyExpanded, i);
            toDecipher = AddRoundKey(toDecipher, roundKey);
            toDecipher = InverseMixColumns(toDecipher);
            toDecipher = InverseShiftRowTransformation(toDecipher);
            toDecipher = InverseSubByte(toDecipher);

        }
        roundKey = WordToByteKey(keyExpanded, 0);
        toDecipher = AddRoundKey(toDecipher, roundKey);

        return toDecipher;
    }

    private byte[] WordToByteKey(SMS[] key, int round) {
        Log.d("WORD TO BYTE", "INICIO WORD TO BYTE");
        byte[] toReturn = new byte[16];
        SMS a = key[round * 4];
        SMS b = key[(round * 4) + 1];
        SMS c = key[(round * 4) + 2];
        SMS d = key[(round * 4) + 3];
        for (int i = 0; i < 4; i++) {
            toReturn[i * 4] = a.Get(i);
            toReturn[(i * 4) + 1] = b.Get(i);
            toReturn[(i * 4) + 2] = c.Get(i);
            toReturn[(i * 4) + 3] = d.Get(i);
        }

        return toReturn;
    }

    private byte[] SubByte(byte[] b) {
        Log.d("sub byte[]", "INICIO");
        for (int i = 0; i < b.length; i++) {
            b[i] = SubByte(b[i]);
        }
        return b;
    }

    private byte SubByte(byte b) {
        Log.d("SUB BYTE", "INICIO");
        return tables.S[unsignedByteToInt(b)];
        /*if(SBoxDic.containsKey(b)){
            return SBoxDic.get(b);
        }
        byte toReturn = 0x00;
        byte c = 0x63;
        b = Inverse(b);
        boolean value;
        for (int i = 0; i < 8; i++) {
            value = GetBit(b, i) ^ GetBit(b, (i + 4) % 8) ^ GetBit(b, (i + 5) % 8) ^ GetBit(b, (i + 6) % 8) ^ GetBit(b, (i + 7) % 8) ^ GetBit(c, i);
            toReturn = SetBit(toReturn, i, value);
        }
        SBoxDic.put(b,toReturn);
        return toReturn;*/
    }

    private byte[] InverseSubByte(byte[] b) {
        Log.d("INVERSE SUB BYTE []", "INICIO");
        for (int i = 0; i < b.length; i++) {
            b[i] = InverseSubByte(b[i]);
        }
        return b;
    }

    private byte InverseSubByte(byte b) {
        Log.d("INVERSESUBBYTE[]", "INICIO");
        return tables.invS[unsignedByteToInt(b)];
        /*if(InvSBoxDic.containsKey(b)){
            return InvSBoxDic.get(b);
        }
        byte toReturn = 0x00;
        byte d = 0x05;
        b = Inverse(b);
        boolean value;
        for (int i = 0; i < 8; i++) {
            value = GetBit(b, (i + 2) % 8) ^ GetBit(b, (i + 5) % 8) ^ GetBit(b, (i + 7) % 8) ^ GetBit(d, i);
            toReturn = SetBit(toReturn, i, value);
        }
        InvSBoxDic.put(b,toReturn);
        return toReturn;*/
    }

    private byte[] ShiftRowTransformation(byte[] a) {
        Log.d("SHIFT ROQW", "INICIO");
        byte[] toReturn = new byte[16];
        toReturn[0] = a[0];
        toReturn[1] = a[1];
        toReturn[2] = a[2];
        toReturn[3] = a[3];

        toReturn[4] = a[5];
        toReturn[5] = a[6];
        toReturn[6] = a[7];
        toReturn[7] = a[4];

        toReturn[8] = a[10];
        toReturn[9] = a[11];
        toReturn[10] = a[8];
        toReturn[11] = a[9];

        toReturn[12] = a[15];
        toReturn[13] = a[12];
        toReturn[14] = a[13];
        toReturn[15] = a[14];
        return toReturn;
    }

    private byte[] InverseShiftRowTransformation(byte[] a) {
        Log.d("INVERSE SHIFT ROW[]", "INICIO");
        byte[] toReturn = new byte[16];
        toReturn[0] = a[0];
        toReturn[1] = a[1];
        toReturn[2] = a[2];
        toReturn[3] = a[3];

        toReturn[4] = a[7];
        toReturn[5] = a[4];
        toReturn[6] = a[5];
        toReturn[7] = a[6];

        toReturn[8] = a[10];
        toReturn[9] = a[11];
        toReturn[10] = a[8];
        toReturn[11] = a[9];

        toReturn[12] = a[13];
        toReturn[13] = a[14];
        toReturn[14] = a[15];
        toReturn[15] = a[12];
        return toReturn;
    }

    private byte[] MixColumns(byte[] a) {
        Log.d("MIX COLUMNS", "INICIO");
        byte[] toReturn = new byte[16];

        for (int i = 0; i < 4; i++) {
            byte s0 = a[i];
            byte s1 = a[i + 4];
            byte s2 = a[i + 8];
            byte s3 = a[i + 12];
            toReturn[i] = (byte) (MultiplyBy2(s0) ^ MultiplyBy3(s1) ^ s2 ^ s3);
            toReturn[i + 4] = (byte) (MultiplyBy2(s1) ^ MultiplyBy3(s2) ^ s3 ^ s0);
            toReturn[i + 8] = (byte) (MultiplyBy2(s2) ^ MultiplyBy3(s3) ^ s0 ^ s1);
            toReturn[i + 12] = (byte) (MultiplyBy2(s3) ^ MultiplyBy3(s0) ^ s1 ^ s2);
        }

        return toReturn;
    }

    private byte[] InverseMixColumns(byte[] a) {
        Log.d("INVERSE MIX COLUMNS", "INICIO");
        byte[] toReturn = new byte[16];

        for (int i = 0; i < 4; i++) {
            byte s0 = a[i];
            byte s1 = a[i + 4];
            byte s2 = a[i + 8];
            byte s3 = a[i + 12];

            byte e = 0x0e;
            byte b = 0x0b;
            byte d = 0x0d;
            byte n = 0x09;

            toReturn[i] = (byte) (Multiply(e, s0) ^ Multiply(b, s1) ^ Multiply(d, s2) ^ Multiply(n, s3));
            toReturn[i + 4] = (byte) (Multiply(n, s0) ^ Multiply(e, s1) ^ Multiply(b, s2) ^ Multiply(d, s3));
            toReturn[i + 8] = (byte) (Multiply(d, s0) ^ Multiply(n, s1) ^ Multiply(e, s2) ^ Multiply(b, s3));
            toReturn[i + 12] = (byte) (Multiply(b, s0) ^ Multiply(d, s1) ^ Multiply(n, s2) ^ Multiply(e, s3));
        }

        return toReturn;
    }

    private byte[] AddRoundKey(byte[] a, byte[] key) {
        Log.d("ADD ROUND KEY", "INICIO");
        for (int i = 0; i < 16; i++) {
            a[i] = (byte) (a[i] ^ key[i]);
        }
        return a;
    }

    private SMS[] KeyExpansion(byte[] key) {
        Log.d("KEY EXPANSION", "INICIO");

        SMS[] toReturn = new SMS[44];
        byte[] Rcon = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80, 0x1b, 0x36}; //por alguna razon 0x80 se queja de no ser byte
        for (int i = 0; i < 4; i++) {
            toReturn[i] = new SMS(key[4 * i], key[(4 * i) + 1], key[(4 * i) + 2], key[(4 * i) + 3]);
        }

        for (int i = 4; i < 44; i++) {
            SMS temp = toReturn[i - 1];
            if (i % 4 == 0) {
                temp.RotWord();
                for (int j = 0; j < 4; j++) //hace el SubWord
                {
                    byte toReplace = temp.Get(j);
                    toReplace = SubByte(toReplace);
                    temp.Set(toReplace, j);
                }
                temp.Rcon(Rcon[(i / 4)-1]);
            }
            toReturn[i] = SMS.XOR(toReturn[i - 4], temp);
        }

        return toReturn;
    }


    private byte Inverse(byte b) {

        if (b == 0) {
            return (byte) 0x00;
        }
        if(b==1){
            return (byte) 0x01;
        }

        byte a1 = 0x01;
        byte a2 = 0x02;
        byte a3 = 0x1b;
        byte b1 = 0x00;
        byte b2 = 0x01;
        byte b3 = b;
        byte q = DivisionByIrredusable(b);
        byte t1, t2, t3;

        t1 = (byte) (a1 ^ Multiply(q, b1));
        t2 = (byte) (a2 ^ Multiply(q, b2));
        t3 = (byte) (a3 ^ Multiply(q, b3));

        a1 = b1;
        a2 = b2;
        a3 = b3;

        b1 = t1;
        b2 = t2;
        b3 = t3;

        while (Helper.ByteIntValue(b3) != 1) {
            Log.d("UN WHILE SOSPECHOSO", String.valueOf(b3));
            q = Division(a3, b3);
            t1 = (byte) (a1 ^ Multiply(q, b1));
            t2 = (byte) (a2 ^ Multiply(q, b2));
            t3 = (byte) (a3 ^ Multiply(q, b3));

            a1 = b1;
            a2 = b2;
            a3 = b3;

            b1 = t1;
            b2 = t2;
            b3 = t3;
        }

        return b2;
    }

    private byte SetBit(byte b, int pos, boolean value) {
        Log.d("SET BIT", "inicio");
        if (value) {
            b = (byte) (b | (1 << pos));
        } else {
            b = (byte) (b & ~(1 << pos));
        }
        return b;
    }


    private boolean GetBit(byte b, int bitNumber) //0 es el menos significativo 7 el mas significativo
    {
        return (b & (1 << bitNumber)) != 0;
    }

    private byte Multiply(byte a, byte b) {
        Log.d("MULTIPLY", "INICIO");
        byte result = 0x00;
        byte aa = a;
        byte bb = b;
        if (a == 0 || b == 0) {
            return result;
        }
        for (int i = 0; i < 8; i++) {
            if (GetBit(bb, 0)) {
                result = (byte) (result ^ aa);
            }
            aa = MultiplyBy2(aa);
            bb = (byte) (bb >>> 1); //>>> es unsigned shift y agrega 0
        }
        return result;
    }

    private byte Division(byte a, byte b) // esto da el cosiente de  a entre b
    {
        Log.d("DIVISION", "INICIO");
        byte result = 0x00;
        int moving;
        boolean end = false;
        byte aa = a;
        byte bb;
        while (Helper.ByteIntValue(aa) >= Helper.ByteIntValue(b)) {
            Log.d("WHILE DE DIVISION", String.valueOf(aa)+", "+String.valueOf(b));
            moving = HighestSetBit(aa) - HighestSetBit(b);
            bb = (byte) (b << moving);
            aa = (byte) (aa ^ bb);
            result = SetBit(result, moving, true);

        }
        return result;
    }

    private byte DivisionByIrredusable(byte b) {
        Log.d("DIVISION BY IRREDUSABLE", "INICIO");
        byte result = 0x00;
        int moving;
        boolean end = false;
        byte aa = 0x1b;
        byte bb;

        moving = 8 - HighestSetBit(b);
        bb = (byte) (b << moving);
        aa = (byte) (aa ^ bb);
        result = SetBit(result, moving, true);

        while (Helper.ByteIntValue(aa) >= Helper.ByteIntValue(b)) {
            Log.d("WHILE DE DIVISION BY IRREDUSABLE", String.valueOf(aa));
            moving = HighestSetBit(aa) - HighestSetBit(b);
            bb = (byte) (b << moving);
            aa = (byte) (aa ^ bb);
            result = SetBit(result, moving, true);
        }
        return result;
    }


    private int HighestSetBit(byte a) {
        for (int i = 7; i >= 0; i--) {
            if (GetBit(a, i)) {
                return i;
            }
        }
        return -1;
    }

    private byte MultiplyBy2(byte a) {
        if (GetBit(a, 7)) {
            a = (byte) (a << 1);
            return (byte) (a ^ 0x1b);
        } else {
            return (byte) (a << 1);
        }
    }

    private byte MultiplyBy3(byte a) {
        return (byte) (a ^ MultiplyBy2(a));
    }

    private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }



}
