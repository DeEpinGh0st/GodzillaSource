package org.bouncycastle.jcajce.spec;

import javax.crypto.spec.PBEKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PBKDF2KeySpec extends PBEKeySpec {
  private static final AlgorithmIdentifier defaultPRF = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE);
  
  private AlgorithmIdentifier prf;
  
  public PBKDF2KeySpec(char[] paramArrayOfchar, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, AlgorithmIdentifier paramAlgorithmIdentifier) {
    super(paramArrayOfchar, paramArrayOfbyte, paramInt1, paramInt2);
    this.prf = paramAlgorithmIdentifier;
  }
  
  public boolean isDefaultPrf() {
    return defaultPRF.equals(this.prf);
  }
  
  public AlgorithmIdentifier getPrf() {
    return this.prf;
  }
}
