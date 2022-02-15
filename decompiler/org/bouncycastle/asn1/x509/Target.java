package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class Target extends ASN1Object implements ASN1Choice {
  public static final int targetName = 0;
  
  public static final int targetGroup = 1;
  
  private GeneralName targName;
  
  private GeneralName targGroup;
  
  public static Target getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof Target)
      return (Target)paramObject; 
    if (paramObject instanceof ASN1TaggedObject)
      return new Target((ASN1TaggedObject)paramObject); 
    throw new IllegalArgumentException("unknown object in factory: " + paramObject.getClass());
  }
  
  private Target(ASN1TaggedObject paramASN1TaggedObject) {
    switch (paramASN1TaggedObject.getTagNo()) {
      case 0:
        this.targName = GeneralName.getInstance(paramASN1TaggedObject, true);
        return;
      case 1:
        this.targGroup = GeneralName.getInstance(paramASN1TaggedObject, true);
        return;
    } 
    throw new IllegalArgumentException("unknown tag: " + paramASN1TaggedObject.getTagNo());
  }
  
  public Target(int paramInt, GeneralName paramGeneralName) {
    this((ASN1TaggedObject)new DERTaggedObject(paramInt, (ASN1Encodable)paramGeneralName));
  }
  
  public GeneralName getTargetGroup() {
    return this.targGroup;
  }
  
  public GeneralName getTargetName() {
    return this.targName;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.targName != null) ? new DERTaggedObject(true, 0, (ASN1Encodable)this.targName) : new DERTaggedObject(true, 1, (ASN1Encodable)this.targGroup));
  }
}
