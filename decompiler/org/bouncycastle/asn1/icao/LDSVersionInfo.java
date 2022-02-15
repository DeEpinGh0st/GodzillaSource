package org.bouncycastle.asn1.icao;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;

public class LDSVersionInfo extends ASN1Object {
  private DERPrintableString ldsVersion;
  
  private DERPrintableString unicodeVersion;
  
  public LDSVersionInfo(String paramString1, String paramString2) {
    this.ldsVersion = new DERPrintableString(paramString1);
    this.unicodeVersion = new DERPrintableString(paramString2);
  }
  
  private LDSVersionInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() != 2)
      throw new IllegalArgumentException("sequence wrong size for LDSVersionInfo"); 
    this.ldsVersion = DERPrintableString.getInstance(paramASN1Sequence.getObjectAt(0));
    this.unicodeVersion = DERPrintableString.getInstance(paramASN1Sequence.getObjectAt(1));
  }
  
  public static LDSVersionInfo getInstance(Object paramObject) {
    return (paramObject instanceof LDSVersionInfo) ? (LDSVersionInfo)paramObject : ((paramObject != null) ? new LDSVersionInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public String getLdsVersion() {
    return this.ldsVersion.getString();
  }
  
  public String getUnicodeVersion() {
    return this.unicodeVersion.getString();
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.ldsVersion);
    aSN1EncodableVector.add((ASN1Encodable)this.unicodeVersion);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
