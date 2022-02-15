package org.bouncycastle.asn1.pkcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BERSequence;

public class Pfx extends ASN1Object implements PKCSObjectIdentifiers {
  private ContentInfo contentInfo;
  
  private MacData macData = null;
  
  private Pfx(ASN1Sequence paramASN1Sequence) {
    BigInteger bigInteger = ((ASN1Integer)paramASN1Sequence.getObjectAt(0)).getValue();
    if (bigInteger.intValue() != 3)
      throw new IllegalArgumentException("wrong version for PFX PDU"); 
    this.contentInfo = ContentInfo.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() == 3)
      this.macData = MacData.getInstance(paramASN1Sequence.getObjectAt(2)); 
  }
  
  public static Pfx getInstance(Object paramObject) {
    return (paramObject instanceof Pfx) ? (Pfx)paramObject : ((paramObject != null) ? new Pfx(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public Pfx(ContentInfo paramContentInfo, MacData paramMacData) {
    this.contentInfo = paramContentInfo;
    this.macData = paramMacData;
  }
  
  public ContentInfo getAuthSafe() {
    return this.contentInfo;
  }
  
  public MacData getMacData() {
    return this.macData;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(3L));
    aSN1EncodableVector.add((ASN1Encodable)this.contentInfo);
    if (this.macData != null)
      aSN1EncodableVector.add((ASN1Encodable)this.macData); 
    return (ASN1Primitive)new BERSequence(aSN1EncodableVector);
  }
}
