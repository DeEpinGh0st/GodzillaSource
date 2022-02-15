package org.bouncycastle.crypto.tls;

import java.io.IOException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;

public class TlsNullCipher implements TlsCipher {
  protected TlsContext context;
  
  protected TlsMac writeMac;
  
  protected TlsMac readMac;
  
  public TlsNullCipher(TlsContext paramTlsContext) {
    this.context = paramTlsContext;
    this.writeMac = null;
    this.readMac = null;
  }
  
  public TlsNullCipher(TlsContext paramTlsContext, Digest paramDigest1, Digest paramDigest2) throws IOException {
    if (((paramDigest1 == null) ? true : false) != ((paramDigest2 == null) ? true : false))
      throw new TlsFatalAlert((short)80); 
    this.context = paramTlsContext;
    TlsMac tlsMac1 = null;
    TlsMac tlsMac2 = null;
    if (paramDigest1 != null) {
      int i = paramDigest1.getDigestSize() + paramDigest2.getDigestSize();
      byte[] arrayOfByte = TlsUtils.calculateKeyBlock(paramTlsContext, i);
      int j = 0;
      tlsMac1 = new TlsMac(paramTlsContext, paramDigest1, arrayOfByte, j, paramDigest1.getDigestSize());
      j += paramDigest1.getDigestSize();
      tlsMac2 = new TlsMac(paramTlsContext, paramDigest2, arrayOfByte, j, paramDigest2.getDigestSize());
      j += paramDigest2.getDigestSize();
      if (j != i)
        throw new TlsFatalAlert((short)80); 
    } 
    if (paramTlsContext.isServer()) {
      this.writeMac = tlsMac2;
      this.readMac = tlsMac1;
    } else {
      this.writeMac = tlsMac1;
      this.readMac = tlsMac2;
    } 
  }
  
  public int getPlaintextLimit(int paramInt) {
    int i = paramInt;
    if (this.writeMac != null)
      i -= this.writeMac.getSize(); 
    return i;
  }
  
  public byte[] encodePlaintext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this.writeMac == null)
      return Arrays.copyOfRange(paramArrayOfbyte, paramInt1, paramInt1 + paramInt2); 
    byte[] arrayOfByte1 = this.writeMac.calculateMac(paramLong, paramShort, paramArrayOfbyte, paramInt1, paramInt2);
    byte[] arrayOfByte2 = new byte[paramInt2 + arrayOfByte1.length];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte2, 0, paramInt2);
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, paramInt2, arrayOfByte1.length);
    return arrayOfByte2;
  }
  
  public byte[] decodeCiphertext(long paramLong, short paramShort, byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException {
    if (this.readMac == null)
      return Arrays.copyOfRange(paramArrayOfbyte, paramInt1, paramInt1 + paramInt2); 
    int i = this.readMac.getSize();
    if (paramInt2 < i)
      throw new TlsFatalAlert((short)50); 
    int j = paramInt2 - i;
    byte[] arrayOfByte1 = Arrays.copyOfRange(paramArrayOfbyte, paramInt1 + j, paramInt1 + paramInt2);
    byte[] arrayOfByte2 = this.readMac.calculateMac(paramLong, paramShort, paramArrayOfbyte, paramInt1, j);
    if (!Arrays.constantTimeAreEqual(arrayOfByte1, arrayOfByte2))
      throw new TlsFatalAlert((short)20); 
    return Arrays.copyOfRange(paramArrayOfbyte, paramInt1, paramInt1 + j);
  }
}
