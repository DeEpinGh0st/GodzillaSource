package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

public class PBKDF2Params extends ASN1Object {
  private static final AlgorithmIdentifier algid_hmacWithSHA1 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA1, (ASN1Encodable)DERNull.INSTANCE);
  
  private final ASN1OctetString octStr;
  
  private final ASN1Integer iterationCount;
  
  private final ASN1Integer keyLength;
  
  private final AlgorithmIdentifier prf;
  
  public static PBKDF2Params getInstance(Object paramObject) {
    return (paramObject instanceof PBKDF2Params) ? (PBKDF2Params)paramObject : ((paramObject != null) ? new PBKDF2Params(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public PBKDF2Params(byte[] paramArrayOfbyte, int paramInt) {
    this(paramArrayOfbyte, paramInt, 0);
  }
  
  public PBKDF2Params(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this(paramArrayOfbyte, paramInt1, paramInt2, null);
  }
  
  public PBKDF2Params(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.octStr = (ASN1OctetString)new DEROctetString(Arrays.clone(paramArrayOfbyte));
    this.iterationCount = new ASN1Integer(paramInt1);
    if (paramInt2 > 0) {
      this.keyLength = new ASN1Integer(paramInt2);
    } else {
      this.keyLength = null;
    } 
    this.prf = paramAlgorithmIdentifier;
  }
  
  public PBKDF2Params(byte[] paramArrayOfbyte, int paramInt, AlgorithmIdentifier paramAlgorithmIdentifier) {
    this(paramArrayOfbyte, paramInt, 0, paramAlgorithmIdentifier);
  }
  
  private PBKDF2Params(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1OctetString> enumeration = paramASN1Sequence.getObjects();
    this.octStr = enumeration.nextElement();
    this.iterationCount = (ASN1Integer)enumeration.nextElement();
    if (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      if (object instanceof ASN1Integer) {
        this.keyLength = ASN1Integer.getInstance(object);
        if (enumeration.hasMoreElements()) {
          object = enumeration.nextElement();
        } else {
          object = null;
        } 
      } else {
        this.keyLength = null;
      } 
      if (object != null) {
        this.prf = AlgorithmIdentifier.getInstance(object);
      } else {
        this.prf = null;
      } 
    } else {
      this.keyLength = null;
      this.prf = null;
    } 
  }
  
  public byte[] getSalt() {
    return this.octStr.getOctets();
  }
  
  public BigInteger getIterationCount() {
    return this.iterationCount.getValue();
  }
  
  public BigInteger getKeyLength() {
    return (this.keyLength != null) ? this.keyLength.getValue() : null;
  }
  
  public boolean isDefaultPrf() {
    return (this.prf == null || this.prf.equals(algid_hmacWithSHA1));
  }
  
  public AlgorithmIdentifier getPrf() {
    return (this.prf != null) ? this.prf : algid_hmacWithSHA1;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.octStr);
    aSN1EncodableVector.add((ASN1Encodable)this.iterationCount);
    if (this.keyLength != null)
      aSN1EncodableVector.add((ASN1Encodable)this.keyLength); 
    if (this.prf != null && !this.prf.equals(algid_hmacWithSHA1))
      aSN1EncodableVector.add((ASN1Encodable)this.prf); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
