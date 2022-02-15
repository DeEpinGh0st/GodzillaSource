package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.util.Arrays;

public class MacData extends ASN1Object {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  DigestInfo digInfo;
  
  byte[] salt;
  
  BigInteger iterationCount;
  
  public static MacData getInstance(Object paramObject) {
    return (paramObject instanceof MacData) ? (MacData)paramObject : ((paramObject != null) ? new MacData(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private MacData(ASN1Sequence paramASN1Sequence) {
    this.digInfo = DigestInfo.getInstance(paramASN1Sequence.getObjectAt(0));
    this.salt = Arrays.clone(((ASN1OctetString)paramASN1Sequence.getObjectAt(1)).getOctets());
    if (paramASN1Sequence.size() == 3) {
      this.iterationCount = ((ASN1Integer)paramASN1Sequence.getObjectAt(2)).getValue();
    } else {
      this.iterationCount = ONE;
    } 
  }
  
  public MacData(DigestInfo paramDigestInfo, byte[] paramArrayOfbyte, int paramInt) {
    this.digInfo = paramDigestInfo;
    this.salt = Arrays.clone(paramArrayOfbyte);
    this.iterationCount = BigInteger.valueOf(paramInt);
  }
  
  public DigestInfo getMac() {
    return this.digInfo;
  }
  
  public byte[] getSalt() {
    return Arrays.clone(this.salt);
  }
  
  public BigInteger getIterationCount() {
    return this.iterationCount;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.digInfo);
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.salt));
    if (!this.iterationCount.equals(ONE))
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.iterationCount)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
