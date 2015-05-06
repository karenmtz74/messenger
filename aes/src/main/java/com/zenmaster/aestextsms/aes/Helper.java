package com.zenmaster.aestextsms.aes;


public class Helper
{

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return data;
    }

    public static boolean GetByteBit(byte b, int bit) {
        return (b & (1 << bit)) != 0;
    }

    public static int ByteIntValue(byte b)
    {
     int toReturn=0;
        for(int i=0;i<8;i++)
        {
            if(GetBit(b,0)) {
                toReturn += Math.pow(2, i);
            }
        }
        return toReturn;
    }
    private static boolean GetBit(byte b, int bitNumber) //0 es el menos significativo 7 el mas significativo
    {
        return (b & (1 << bitNumber)) != 0;
    }
    public static byte[] LeftCircularShift(byte[] b) {
        byte first = b[1]; //guardar el primero
        for (int i = 1; i < b.length; i++) {
            b[i - 1] = b[i]; //recorre a la izquierda
        }
        b[b.length - 1] = first;
        return b;
    }

    public static byte[] RigthCircularShift(byte[] b) {
        byte last = b[b.length - 1]; //guardar el ultimo
        for (int i = b.length - 1; i > 0; i--) {
            b[i] = b[i - 1]; //recorre a la derecha
        }
        b[0] = last;
        return b;
    }

    public static byte[] LeftShift(byte[] b) {

        for (int i = 1; i < b.length; i++) {
            b[i - 1] = b[i]; //recorre a la izquierda
        }
        b[b.length - 1] = 0x00;
        return b;
    }

    public static byte[] RigthShift(byte[] b) {

        for (int i = b.length - 1; i > 0; i--) {
            b[i] = b[i - 1]; //recorre a la derecha
        }
        b[0] = 0x00;
        return b;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String bytesToString(byte[] bytes) {
        try {
            return new String(bytes, "ASCII");
        } catch (Exception ex) {
            return ex.toString();
        }
    }


}
