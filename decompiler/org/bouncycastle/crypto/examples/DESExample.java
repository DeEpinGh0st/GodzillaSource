package org.bouncycastle.crypto.examples;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

public class DESExample {
  private boolean encrypt = true;
  
  private PaddedBufferedBlockCipher cipher = null;
  
  private BufferedInputStream in = null;
  
  private BufferedOutputStream out = null;
  
  private byte[] key = null;
  
  public static void main(String[] paramArrayOfString) {
    boolean bool = true;
    String str1 = null;
    String str2 = null;
    String str3 = null;
    if (paramArrayOfString.length < 2) {
      DESExample dESExample1 = new DESExample();
      System.err.println("Usage: java " + dESExample1.getClass().getName() + " infile outfile [keyfile]");
      System.exit(1);
    } 
    str3 = "deskey.dat";
    str1 = paramArrayOfString[0];
    str2 = paramArrayOfString[1];
    if (paramArrayOfString.length > 2) {
      bool = false;
      str3 = paramArrayOfString[2];
    } 
    DESExample dESExample = new DESExample(str1, str2, str3, bool);
    dESExample.process();
  }
  
  public DESExample() {}
  
  public DESExample(String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    this.encrypt = paramBoolean;
    try {
      this.in = new BufferedInputStream(new FileInputStream(paramString1));
    } catch (FileNotFoundException fileNotFoundException) {
      System.err.println("Input file not found [" + paramString1 + "]");
      System.exit(1);
    } 
    try {
      this.out = new BufferedOutputStream(new FileOutputStream(paramString2));
    } catch (IOException iOException) {
      System.err.println("Output file not created [" + paramString2 + "]");
      System.exit(1);
    } 
    if (paramBoolean) {
      try {
        SecureRandom secureRandom = null;
        try {
          secureRandom = new SecureRandom();
          secureRandom.setSeed("www.bouncycastle.org".getBytes());
        } catch (Exception exception) {
          System.err.println("Hmmm, no SHA1PRNG, you need the Sun implementation");
          System.exit(1);
        } 
        KeyGenerationParameters keyGenerationParameters = new KeyGenerationParameters(secureRandom, 192);
        DESedeKeyGenerator dESedeKeyGenerator = new DESedeKeyGenerator();
        dESedeKeyGenerator.init(keyGenerationParameters);
        this.key = dESedeKeyGenerator.generateKey();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(paramString3));
        byte[] arrayOfByte = Hex.encode(this.key);
        bufferedOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
      } catch (IOException iOException) {
        System.err.println("Could not decryption create key file [" + paramString3 + "]");
        System.exit(1);
      } 
    } else {
      try {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(paramString3));
        int i = bufferedInputStream.available();
        byte[] arrayOfByte = new byte[i];
        bufferedInputStream.read(arrayOfByte, 0, i);
        this.key = Hex.decode(arrayOfByte);
      } catch (IOException iOException) {
        System.err.println("Decryption key file not found, or not valid [" + paramString3 + "]");
        System.exit(1);
      } 
    } 
  }
  
  private void process() {
    this.cipher = new PaddedBufferedBlockCipher((BlockCipher)new CBCBlockCipher((BlockCipher)new DESedeEngine()));
    if (this.encrypt) {
      performEncrypt(this.key);
    } else {
      performDecrypt(this.key);
    } 
    try {
      this.in.close();
      this.out.flush();
      this.out.close();
    } catch (IOException iOException) {
      System.err.println("exception closing resources: " + iOException.getMessage());
    } 
  }
  
  private void performEncrypt(byte[] paramArrayOfbyte) {
    this.cipher.init(true, (CipherParameters)new KeyParameter(paramArrayOfbyte));
    byte b = 47;
    int i = this.cipher.getOutputSize(b);
    byte[] arrayOfByte1 = new byte[b];
    byte[] arrayOfByte2 = new byte[i];
    try {
      byte[] arrayOfByte = null;
      int j;
      while ((j = this.in.read(arrayOfByte1, 0, b)) > 0) {
        int k = this.cipher.processBytes(arrayOfByte1, 0, j, arrayOfByte2, 0);
        if (k > 0) {
          arrayOfByte = Hex.encode(arrayOfByte2, 0, k);
          this.out.write(arrayOfByte, 0, arrayOfByte.length);
          this.out.write(10);
        } 
      } 
      try {
        int k = this.cipher.doFinal(arrayOfByte2, 0);
        if (k > 0) {
          arrayOfByte = Hex.encode(arrayOfByte2, 0, k);
          this.out.write(arrayOfByte, 0, arrayOfByte.length);
          this.out.write(10);
        } 
      } catch (CryptoException cryptoException) {}
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  private void performDecrypt(byte[] paramArrayOfbyte) {
    this.cipher.init(false, (CipherParameters)new KeyParameter(paramArrayOfbyte));
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.in));
    try {
      byte[] arrayOfByte1 = null;
      byte[] arrayOfByte2 = null;
      String str = null;
      while ((str = bufferedReader.readLine()) != null) {
        arrayOfByte1 = Hex.decode(str);
        arrayOfByte2 = new byte[this.cipher.getOutputSize(arrayOfByte1.length)];
        int i = this.cipher.processBytes(arrayOfByte1, 0, arrayOfByte1.length, arrayOfByte2, 0);
        if (i > 0)
          this.out.write(arrayOfByte2, 0, i); 
      } 
      try {
        int i = this.cipher.doFinal(arrayOfByte2, 0);
        if (i > 0)
          this.out.write(arrayOfByte2, 0, i); 
      } catch (CryptoException cryptoException) {}
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
}
