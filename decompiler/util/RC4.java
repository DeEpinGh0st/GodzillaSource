package util;

import java.util.Arrays;










public class RC4
{
  private static final int SBOX_LENGTH = 256;
  private static final int KEY_MIN_LENGTH = 5;
  private byte[] key = new byte[255];


  
  private int[] sbox = new int[256];
  
  public RC4() {
    reset();
  }
  
  public RC4(String key) throws InvalidKeyException {
    this();
    setKey(key);
  }
  
  private void reset() {
    Arrays.fill(this.key, (byte)0);
    Arrays.fill(this.sbox, 0);
  }


  
  public byte[] encryptMessage(byte[] data, String key) throws InvalidKeyException {
    reset();
    setKey(key);
    byte[] crypt = crypt(data);
    reset();
    return crypt;
  }
  public byte[] decryptMessage(byte[] message, String key) {
    reset();
    setKey(key);
    byte[] msg = crypt(message);
    reset();
    return msg;
  }
  
  public byte[] crypt(byte[] msg) {
    this.sbox = initSBox(this.key);
    byte[] code = new byte[msg.length];
    int i = 0;
    int j = 0;
    for (int n = 0; n < msg.length; n++) {
      i = (i + 1) % 256;
      j = (j + this.sbox[i]) % 256;
      swap(i, j, this.sbox);
      int rand = this.sbox[(this.sbox[i] + this.sbox[j]) % 256];
      code[n] = (byte)(rand ^ msg[n]);
    } 
    return code;
  }
  
  private int[] initSBox(byte[] key) {
    int[] sbox = new int[256];
    int j = 0;
    int i;
    for (i = 0; i < 256; i++) {
      sbox[i] = i;
    }
    
    for (i = 0; i < 256; i++) {
      j = (j + sbox[i] + key[i % key.length] & 0xFF) % 256;
      swap(i, j, sbox);
    } 
    return sbox;
  }
  
  private void swap(int i, int j, int[] sbox) {
    int temp = sbox[i];
    sbox[i] = sbox[j];
    sbox[j] = temp;
  }
  
  public void setKey(String key) throws InvalidKeyException {
    if (key.length() < 5 || key.length() >= 256) {
      throw new InvalidKeyException("Key length has to be between 5 and 255");
    }

    
    this.key = key.getBytes();
  }
}
