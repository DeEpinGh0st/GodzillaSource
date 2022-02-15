package org.bouncycastle.cms;

import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.util.Arrays;

public abstract class PasswordRecipientInfoGenerator implements RecipientInfoGenerator {
  protected char[] password;
  
  private AlgorithmIdentifier keyDerivationAlgorithm;
  
  private ASN1ObjectIdentifier kekAlgorithm;
  
  private SecureRandom random;
  
  private int schemeID;
  
  private int keySize;
  
  private int blockSize;
  
  private PasswordRecipient.PRF prf;
  
  private byte[] salt;
  
  private int iterationCount;
  
  protected PasswordRecipientInfoGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier, char[] paramArrayOfchar) {
    this(paramASN1ObjectIdentifier, paramArrayOfchar, getKeySize(paramASN1ObjectIdentifier), ((Integer)PasswordRecipientInformation.BLOCKSIZES.get(paramASN1ObjectIdentifier)).intValue());
  }
  
  protected PasswordRecipientInfoGenerator(ASN1ObjectIdentifier paramASN1ObjectIdentifier, char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    this.password = paramArrayOfchar;
    this.schemeID = 1;
    this.kekAlgorithm = paramASN1ObjectIdentifier;
    this.keySize = paramInt1;
    this.blockSize = paramInt2;
    this.prf = PasswordRecipient.PRF.HMacSHA1;
    this.iterationCount = 1024;
  }
  
  private static int getKeySize(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    Integer integer = (Integer)PasswordRecipientInformation.KEYSIZES.get(paramASN1ObjectIdentifier);
    if (integer == null)
      throw new IllegalArgumentException("cannot find key size for algorithm: " + paramASN1ObjectIdentifier); 
    return integer.intValue();
  }
  
  public PasswordRecipientInfoGenerator setPasswordConversionScheme(int paramInt) {
    this.schemeID = paramInt;
    return this;
  }
  
  public PasswordRecipientInfoGenerator setPRF(PasswordRecipient.PRF paramPRF) {
    this.prf = paramPRF;
    return this;
  }
  
  public PasswordRecipientInfoGenerator setSaltAndIterationCount(byte[] paramArrayOfbyte, int paramInt) {
    this.salt = Arrays.clone(paramArrayOfbyte);
    this.iterationCount = paramInt;
    return this;
  }
  
  public PasswordRecipientInfoGenerator setSecureRandom(SecureRandom paramSecureRandom) {
    this.random = paramSecureRandom;
    return this;
  }
  
  public RecipientInfo generate(GenericKey paramGenericKey) throws CMSException {
    byte[] arrayOfByte1 = new byte[this.blockSize];
    if (this.random == null)
      this.random = new SecureRandom(); 
    this.random.nextBytes(arrayOfByte1);
    if (this.salt == null) {
      this.salt = new byte[20];
      this.random.nextBytes(this.salt);
    } 
    this.keyDerivationAlgorithm = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBKDF2, (ASN1Encodable)new PBKDF2Params(this.salt, this.iterationCount, this.prf.prfAlgID));
    byte[] arrayOfByte2 = calculateDerivedKey(this.schemeID, this.keyDerivationAlgorithm, this.keySize);
    AlgorithmIdentifier algorithmIdentifier1 = new AlgorithmIdentifier(this.kekAlgorithm, (ASN1Encodable)new DEROctetString(arrayOfByte1));
    byte[] arrayOfByte3 = generateEncryptedBytes(algorithmIdentifier1, arrayOfByte2, paramGenericKey);
    DEROctetString dEROctetString = new DEROctetString(arrayOfByte3);
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.kekAlgorithm);
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(arrayOfByte1));
    AlgorithmIdentifier algorithmIdentifier2 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_PWRI_KEK, (ASN1Encodable)new DERSequence(aSN1EncodableVector));
    return new RecipientInfo(new PasswordRecipientInfo(this.keyDerivationAlgorithm, algorithmIdentifier2, (ASN1OctetString)dEROctetString));
  }
  
  protected abstract byte[] calculateDerivedKey(int paramInt1, AlgorithmIdentifier paramAlgorithmIdentifier, int paramInt2) throws CMSException;
  
  protected abstract byte[] generateEncryptedBytes(AlgorithmIdentifier paramAlgorithmIdentifier, byte[] paramArrayOfbyte, GenericKey paramGenericKey) throws CMSException;
}
