package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public abstract class AbstractTlsSigner implements TlsSigner {
  protected TlsContext context;
  
  public void init(TlsContext paramTlsContext) {
    this.context = paramTlsContext;
  }
  
  public byte[] generateRawSignature(AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte) throws CryptoException {
    return generateRawSignature(null, paramAsymmetricKeyParameter, paramArrayOfbyte);
  }
  
  public boolean verifyRawSignature(byte[] paramArrayOfbyte1, AsymmetricKeyParameter paramAsymmetricKeyParameter, byte[] paramArrayOfbyte2) throws CryptoException {
    return verifyRawSignature(null, paramArrayOfbyte1, paramAsymmetricKeyParameter, paramArrayOfbyte2);
  }
  
  public Signer createSigner(AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return createSigner(null, paramAsymmetricKeyParameter);
  }
  
  public Signer createVerifyer(AsymmetricKeyParameter paramAsymmetricKeyParameter) {
    return createVerifyer(null, paramAsymmetricKeyParameter);
  }
}
