package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipient;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public abstract class BcPasswordRecipient implements PasswordRecipient {
  private final char[] password;
  
  private int schemeID = 1;
  
  BcPasswordRecipient(char[] paramArrayOfchar) {
    this.password = paramArrayOfchar;
  }
  
  public BcPasswordRecipient setPasswordConversionScheme(int paramInt) {
    this.schemeID = paramInt;
    return this;
  }
  
  protected KeyParameter extractSecretKey(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws CMSException {
    Wrapper wrapper = EnvelopedDataHelper.createRFC3211Wrapper(paramAlgorithmIdentifier1.getAlgorithm());
    wrapper.init(false, (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(paramArrayOfbyte1), ASN1OctetString.getInstance(paramAlgorithmIdentifier1.getParameters()).getOctets()));
    try {
      return new KeyParameter(wrapper.unwrap(paramArrayOfbyte2, 0, paramArrayOfbyte2.length));
    } catch (InvalidCipherTextException invalidCipherTextException) {
      throw new CMSException("unable to unwrap key: " + invalidCipherTextException.getMessage(), invalidCipherTextException);
    } 
  }
  
  public byte[] calculateDerivedKey(int paramInt1, AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt2) throws CMSException {
    PBKDF2Params pBKDF2Params = PBKDF2Params.getInstance(paramAlgorithmIdentifier.getParameters());
    byte[] arrayOfByte = (paramInt1 == 0) ? PBEParametersGenerator.PKCS5PasswordToBytes(this.password) : PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.password);
    try {
      PKCS5S2ParametersGenerator pKCS5S2ParametersGenerator = new PKCS5S2ParametersGenerator((Digest)EnvelopedDataHelper.getPRF(pBKDF2Params.getPrf()));
      pKCS5S2ParametersGenerator.init(arrayOfByte, pBKDF2Params.getSalt(), pBKDF2Params.getIterationCount().intValue());
      return ((KeyParameter)pKCS5S2ParametersGenerator.generateDerivedParameters(paramInt2)).getKey();
    } catch (Exception exception) {
      throw new CMSException("exception creating derived key: " + exception.getMessage(), exception);
    } 
  }
  
  public int getPasswordConversionScheme() {
    return this.schemeID;
  }
  
  public char[] getPassword() {
    return this.password;
  }
}
