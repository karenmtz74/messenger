package com.zenmaster.aestextsms.aes;


public class SMS
{
    private byte[] bytes;

    public SMS() {
        bytes = new byte[4];
    }

    public SMS(byte[] a) {
        bytes = new byte[4];
        Set(a);
    }

    public SMS(byte a, byte b, byte c, byte d) {
        bytes = new byte[4];
        bytes[0] = a;
        bytes[1] = b;
        bytes[2] = c;
        bytes[3] = d;
    }

    public byte Get(int pos) {
        return bytes[pos];
    }

    public void Set(byte[] a) {
        for (int i = 0; i < 4; i++) {
            bytes[i] = a[i];
        }
    }

    public void Set(byte a, int pos) {
        bytes[pos] = a;
    }

    public void RotWord() {
        byte first = bytes[0];

        bytes[0] = bytes[1];
        bytes[1] = bytes[2];
        bytes[2] = bytes[3];
        bytes[3] = first;
    }

    public void Rcon(byte rcon){
        bytes[0]=(byte)(bytes[0]^rcon);
    }

    public static SMS XOR(SMS a,SMS b)
    {
        byte[] newBytes=new byte[4];

        for(int i=0;i<4;i++)
        {
            newBytes[i]=(byte)(a.Get(i)^b.Get(i));
        }

        return new SMS(newBytes);
    }

}
