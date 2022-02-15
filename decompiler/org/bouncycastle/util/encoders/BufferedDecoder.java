package org.bouncycastle.util.encoders;

public class BufferedDecoder {
  protected byte[] buf;
  
  protected int bufOff;
  
  protected Translator translator;
  
  public BufferedDecoder(Translator paramTranslator, int paramInt) {
    this.translator = paramTranslator;
    if (paramInt % paramTranslator.getEncodedBlockSize() != 0)
      throw new IllegalArgumentException("buffer size not multiple of input block size"); 
    this.buf = new byte[paramInt];
    this.bufOff = 0;
  }
  
  public int processByte(byte paramByte, byte[] paramArrayOfbyte, int paramInt) {
    int i = 0;
    this.buf[this.bufOff++] = paramByte;
    if (this.bufOff == this.buf.length) {
      i = this.translator.decode(this.buf, 0, this.buf.length, paramArrayOfbyte, paramInt);
      this.bufOff = 0;
    } 
    return i;
  }
  
  public int processBytes(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    if (paramInt2 < 0)
      throw new IllegalArgumentException("Can't have a negative input length!"); 
    int i = 0;
    int j = this.buf.length - this.bufOff;
    if (paramInt2 > j) {
      System.arraycopy(paramArrayOfbyte1, paramInt1, this.buf, this.bufOff, j);
      i += this.translator.decode(this.buf, 0, this.buf.length, paramArrayOfbyte2, paramInt3);
      this.bufOff = 0;
      paramInt2 -= j;
      paramInt1 += j;
      paramInt3 += i;
      int k = paramInt2 - paramInt2 % this.buf.length;
      i += this.translator.decode(paramArrayOfbyte1, paramInt1, k, paramArrayOfbyte2, paramInt3);
      paramInt2 -= k;
      paramInt1 += k;
    } 
    if (paramInt2 != 0) {
      System.arraycopy(paramArrayOfbyte1, paramInt1, this.buf, this.bufOff, paramInt2);
      this.bufOff += paramInt2;
    } 
    return i;
  }
}
