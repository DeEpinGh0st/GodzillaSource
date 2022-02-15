package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.util.Pack;

public class CMac implements Mac {
  private byte[] poly;
  
  private byte[] ZEROES;
  
  private byte[] mac;
  
  private byte[] buf;
  
  private int bufOff;
  
  private BlockCipher cipher;
  
  private int macSize;
  
  private byte[] Lu;
  
  private byte[] Lu2;
  
  public CMac(BlockCipher paramBlockCipher) {
    this(paramBlockCipher, paramBlockCipher.getBlockSize() * 8);
  }
  
  public CMac(BlockCipher paramBlockCipher, int paramInt) {
    if (paramInt % 8 != 0)
      throw new IllegalArgumentException("MAC size must be multiple of 8"); 
    if (paramInt > paramBlockCipher.getBlockSize() * 8)
      throw new IllegalArgumentException("MAC size must be less or equal to " + (paramBlockCipher.getBlockSize() * 8)); 
    this.cipher = (BlockCipher)new CBCBlockCipher(paramBlockCipher);
    this.macSize = paramInt / 8;
    this.poly = lookupPoly(paramBlockCipher.getBlockSize());
    this.mac = new byte[paramBlockCipher.getBlockSize()];
    this.buf = new byte[paramBlockCipher.getBlockSize()];
    this.ZEROES = new byte[paramBlockCipher.getBlockSize()];
    this.bufOff = 0;
  }
  
  public String getAlgorithmName() {
    return this.cipher.getAlgorithmName();
  }
  
  private static int shiftLeft(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int i = paramArrayOfbyte1.length;
    int j;
    for (j = 0; --i >= 0; j = k >>> 7 & 0x1) {
      int k = paramArrayOfbyte1[i] & 0xFF;
      paramArrayOfbyte2[i] = (byte)(k << 1 | j);
    } 
    return j;
  }
  
  private byte[] doubleLu(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length];
    int i = shiftLeft(paramArrayOfbyte, arrayOfByte);
    int j = -i & 0xFF;
    arrayOfByte[paramArrayOfbyte.length - 3] = (byte)(arrayOfByte[paramArrayOfbyte.length - 3] ^ this.poly[1] & j);
    arrayOfByte[paramArrayOfbyte.length - 2] = (byte)(arrayOfByte[paramArrayOfbyte.length - 2] ^ this.poly[2] & j);
    arrayOfByte[paramArrayOfbyte.length - 1] = (byte)(arrayOfByte[paramArrayOfbyte.length - 1] ^ this.poly[3] & j);
    return arrayOfByte;
  }
  
  private static byte[] lookupPoly(int paramInt) {
    int i;
    switch (paramInt * 8) {
      case 64:
        i = 27;
        return Pack.intToBigEndian(i);
      case 128:
        i = 135;
        return Pack.intToBigEndian(i);
      case 160:
        i = 45;
        return Pack.intToBigEndian(i);
      case 192:
        i = 135;
        return Pack.intToBigEndian(i);
      case 224:
        i = 777;
        return Pack.intToBigEndian(i);
      case 256:
        i = 1061;
        return Pack.intToBigEndian(i);
      case 320:
        i = 27;
        return Pack.intToBigEndian(i);
      case 384:
        i = 4109;
        return Pack.intToBigEndian(i);
      case 448:
        i = 2129;
        return Pack.intToBigEndian(i);
      case 512:
        i = 293;
        return Pack.intToBigEndian(i);
      case 768:
        i = 655377;
        return Pack.intToBigEndian(i);
      case 1024:
        i = 524355;
        return Pack.intToBigEndian(i);
      case 2048:
        i = 548865;
        return Pack.intToBigEndian(i);
    } 
    throw new IllegalArgumentException("Unknown block size for CMAC: " + (paramInt * 8));
  }
  
  public void init(CipherParameters paramCipherParameters) {
    validate(paramCipherParameters);
    this.cipher.init(true, paramCipherParameters);
    byte[] arrayOfByte = new byte[this.ZEROES.length];
    this.cipher.processBlock(this.ZEROES, 0, arrayOfByte, 0);
    this.Lu = doubleLu(arrayOfByte);
    this.Lu2 = doubleLu(this.Lu);
    reset();
  }
  
  void validate(CipherParameters paramCipherParameters) {
    if (paramCipherParameters != null && !(paramCipherParameters instanceof org.bouncycastle.crypto.params.KeyParameter))
      throw new IllegalArgumentException("CMac mode only permits key to be set."); 
  }
  
  public int getMacSize() {
    return this.macSize;
  }
  
  public void update(byte paramByte) {
    if (this.bufOff == this.buf.length) {
      this.cipher.processBlock(this.buf, 0, this.mac, 0);
      this.bufOff = 0;
    } 
    this.buf[this.bufOff++] = paramByte;
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("Can't have a negative input length!"); 
    int i = this.cipher.getBlockSize();
    int j = i - this.bufOff;
    if (paramInt2 > j) {
      System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, j);
      this.cipher.processBlock(this.buf, 0, this.mac, 0);
      this.bufOff = 0;
      paramInt2 -= j;
      for (paramInt1 += j; paramInt2 > i; paramInt1 += i) {
        this.cipher.processBlock(paramArrayOfbyte, paramInt1, this.mac, 0);
        paramInt2 -= i;
      } 
    } 
    System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, paramInt2);
    this.bufOff += paramInt2;
  }
  
  public int doFinal(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte;
    int i = this.cipher.getBlockSize();
    if (this.bufOff == i) {
      arrayOfByte = this.Lu;
    } else {
      (new ISO7816d4Padding()).addPadding(this.buf, this.bufOff);
      arrayOfByte = this.Lu2;
    } 
    for (byte b = 0; b < this.mac.length; b++)
      this.buf[b] = (byte)(this.buf[b] ^ arrayOfByte[b]); 
    this.cipher.processBlock(this.buf, 0, this.mac, 0);
    System.arraycopy(this.mac, 0, paramArrayOfbyte, paramInt, this.macSize);
    reset();
    return this.macSize;
  }
  
  public void reset() {
    for (byte b = 0; b < this.buf.length; b++)
      this.buf[b] = 0; 
    this.bufOff = 0;
    this.cipher.reset();
  }
}
