package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Strings;

public class DistributionPointName extends ASN1Object implements ASN1Choice {
  ASN1Encodable name;
  
  int type;
  
  public static final int FULL_NAME = 0;
  
  public static final int NAME_RELATIVE_TO_CRL_ISSUER = 1;
  
  public static DistributionPointName getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1TaggedObject.getInstance(paramASN1TaggedObject, true));
  }
  
  public static DistributionPointName getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DistributionPointName)
      return (DistributionPointName)paramObject; 
    if (paramObject instanceof ASN1TaggedObject)
      return new DistributionPointName((ASN1TaggedObject)paramObject); 
    throw new IllegalArgumentException("unknown object in factory: " + paramObject.getClass().getName());
  }
  
  public DistributionPointName(int paramInt, ASN1Encodable paramASN1Encodable) {
    this.type = paramInt;
    this.name = paramASN1Encodable;
  }
  
  public DistributionPointName(GeneralNames paramGeneralNames) {
    this(0, (ASN1Encodable)paramGeneralNames);
  }
  
  public int getType() {
    return this.type;
  }
  
  public ASN1Encodable getName() {
    return this.name;
  }
  
  public DistributionPointName(ASN1TaggedObject paramASN1TaggedObject) {
    this.type = paramASN1TaggedObject.getTagNo();
    if (this.type == 0) {
      this.name = (ASN1Encodable)GeneralNames.getInstance(paramASN1TaggedObject, false);
    } else {
      this.name = (ASN1Encodable)ASN1Set.getInstance(paramASN1TaggedObject, false);
    } 
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERTaggedObject(false, this.type, this.name);
  }
  
  public String toString() {
    String str = Strings.lineSeparator();
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("DistributionPointName: [");
    stringBuffer.append(str);
    if (this.type == 0) {
      appendObject(stringBuffer, str, "fullName", this.name.toString());
    } else {
      appendObject(stringBuffer, str, "nameRelativeToCRLIssuer", this.name.toString());
    } 
    stringBuffer.append("]");
    stringBuffer.append(str);
    return stringBuffer.toString();
  }
  
  private void appendObject(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3) {
    String str = "    ";
    paramStringBuffer.append(str);
    paramStringBuffer.append(paramString2);
    paramStringBuffer.append(":");
    paramStringBuffer.append(paramString1);
    paramStringBuffer.append(str);
    paramStringBuffer.append(str);
    paramStringBuffer.append(paramString3);
    paramStringBuffer.append(paramString1);
  }
}
