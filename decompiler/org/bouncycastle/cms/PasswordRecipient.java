package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface PasswordRecipient extends Recipient {
  public static final int PKCS5_SCHEME2 = 0;
  
  public static final int PKCS5_SCHEME2_UTF8 = 1;
  
  byte[] calculateDerivedKey(int paramInt1, AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt2) throws CMSException;
  
  RecipientOperator getRecipientOperator(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws CMSException;
  
  int getPasswordConversionScheme();
  
  char[] getPassword();
  
  public static final class PRF {
    public static final PRF HMacSHA1 = new PRF("HMacSHA1", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE));
    
    public static final PRF HMacSHA224 = new PRF("HMacSHA224", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, (ASN1Encodable)DERNull.INSTANCE));
    
    public static final PRF HMacSHA256 = new PRF("HMacSHA256", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, (ASN1Encodable)DERNull.INSTANCE));
    
    public static final PRF HMacSHA384 = new PRF("HMacSHA384", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, (ASN1Encodable)DERNull.INSTANCE));
    
    public static final PRF HMacSHA512 = new PRF("HMacSHA512", new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, (ASN1Encodable)DERNull.INSTANCE));
    
    private final String hmac;
    
    final AlgorithmIdentifier prfAlgID;
    
    private PRF(String param1String, AlgorithmIdentifier param1AlgorithmIdentifier) {
      this.hmac = param1String;
      this.prfAlgID = param1AlgorithmIdentifier;
    }
    
    public String getName() {
      return this.hmac;
    }
    
    public AlgorithmIdentifier getAlgorithmID() {
      return this.prfAlgID;
    }
  }
}
