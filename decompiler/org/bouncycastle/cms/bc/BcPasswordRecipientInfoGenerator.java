package org.bouncycastle.cms.bc;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.PasswordRecipientInfoGenerator;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.Wrapper;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.operator.GenericKey;

public class BcPasswordRecipientInfoGenerator extends PasswordRecipientInfoGenerator {
  public BcPasswordRecipientInfoGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier, char[] paramArrayOfchar) {
    super(paramASN1ObjectIdentifier, paramArrayOfchar);
  }
  
  protected byte[] calculateDerivedKey(int paramInt1, AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt2) throws CMSException {
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
  
  public byte[] generateEncryptedBytes(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte, GenericKey paramGenericKey) throws CMSException {
    byte[] arrayOfByte = ((KeyParameter)CMSUtils.getBcKey(paramGenericKey)).getKey();
    Wrapper wrapper = EnvelopedDataHelper.createRFC3211Wrapper(paramAlgorithmIdentifier.getAlgorithm());
    wrapper.init(true, (CipherParameters)new ParametersWithIV((CipherParameters)new KeyParameter(paramArrayOfbyte), ASN1OctetString.getInstance(paramAlgorithmIdentifier.getParameters()).getOctets()));
    return wrapper.wrap(arrayOfByte, 0, arrayOfByte.length);
  }
}
