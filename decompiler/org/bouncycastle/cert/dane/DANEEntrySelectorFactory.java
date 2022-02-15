package org.bouncycastle.cert.dane;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class DANEEntrySelectorFactory {
  private final DigestCalculator digestCalculator;
  
  public DANEEntrySelectorFactory(DigestCalculator paramDigestCalculator) {
    this.digestCalculator = paramDigestCalculator;
  }
  
  public DANEEntrySelector createSelector(String paramString) throws DANEException {
    byte[] arrayOfByte1 = Strings.toUTF8ByteArray(paramString.substring(0, paramString.indexOf('@')));
    try {
      OutputStream outputStream = this.digestCalculator.getOutputStream();
      outputStream.write(arrayOfByte1);
      outputStream.close();
    } catch (IOException iOException) {
      throw new DANEException("Unable to calculate digest string: " + iOException.getMessage(), iOException);
    } 
    byte[] arrayOfByte2 = this.digestCalculator.getDigest();
    String str = Strings.fromByteArray(Hex.encode(arrayOfByte2)) + "._smimecert." + paramString.substring(paramString.indexOf('@') + 1);
    return new DANEEntrySelector(str);
  }
}
