package org.bouncycastle.crypto;

public class BufferedAsymmetricBlockCipher {
  protected byte[] buf;
  
  protected int bufOff;
  
  private final AsymmetricBlockCipher cipher;
  
  public BufferedAsymmetricBlockCipher(AsymmetricBlockCipher paramAsymmetricBlockCipher) {
    this.cipher = paramAsymmetricBlockCipher;
  }
  
  public AsymmetricBlockCipher getUnderlyingCipher() {
    return this.cipher;
  }
  
  public int getBufferPosition() {
    return this.bufOff;
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    reset();
    this.cipher.init(paramBoolean, paramCipherParameters);
    this.buf = new byte[this.cipher.getInputBlockSize() + (paramBoolean ? 1 : 0)];
    this.bufOff = 0;
  }
  
  public int getInputBlockSize() {
    return this.cipher.getInputBlockSize();
  }
  
  public int getOutputBlockSize() {
    return this.cipher.getOutputBlockSize();
  }
  
  public void processByte(byte paramByte) {
    if (this.bufOff >= this.buf.length)
      throw new DataLengthException("attempt to process message too long for cipher"); 
    this.buf[this.bufOff++] = paramByte;
  }
  
  public void processBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return; 
    if (paramInt2 < 0)
      throw new IllegalArgumentException("Can't have a negative input length!"); 
    if (this.bufOff + paramInt2 > this.buf.length)
      throw new DataLengthException("attempt to process message too long for cipher"); 
    System.arraycopy(paramArrayOfbyte, paramInt1, this.buf, this.bufOff, paramInt2);
    this.bufOff += paramInt2;
  }
  
  public byte[] doFinal() throws InvalidCipherTextException {
    byte[] arrayOfByte = this.cipher.processBlock(this.buf, 0, this.bufOff);
    reset();
    return arrayOfByte;
  }
  
  public void reset() {
    if (this.buf != null)
      for (byte b = 0; b < this.buf.length; b++)
        this.buf[b] = 0;  
    this.bufOff = 0;
  }
}
