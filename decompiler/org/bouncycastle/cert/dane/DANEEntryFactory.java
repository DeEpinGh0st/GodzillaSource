package org.bouncycastle.cert.dane;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;

public class DANEEntryFactory {
  private final DANEEntrySelectorFactory selectorFactory;
  
  public DANEEntryFactory(DigestCalculator paramDigestCalculator) {
    this.selectorFactory = new DANEEntrySelectorFactory(paramDigestCalculator);
  }
  
  public DANEEntry createEntry(String paramString, X509CertificateHolder paramX509CertificateHolder) throws DANEException {
    return createEntry(paramString, 3, paramX509CertificateHolder);
  }
  
  public DANEEntry createEntry(String paramString, int paramInt, X509CertificateHolder paramX509CertificateHolder) throws DANEException {
    if (paramInt < 0 || paramInt > 3)
      throw new DANEException("unknown certificate usage: " + paramInt); 
    DANEEntrySelector dANEEntrySelector = this.selectorFactory.createSelector(paramString);
    byte[] arrayOfByte = new byte[3];
    arrayOfByte[0] = (byte)paramInt;
    arrayOfByte[1] = 0;
    arrayOfByte[2] = 0;
    return new DANEEntry(dANEEntrySelector.getDomainName(), arrayOfByte, paramX509CertificateHolder);
  }
}
