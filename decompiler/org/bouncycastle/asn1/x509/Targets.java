package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class Targets extends ASN1Object {
  private ASN1Sequence targets;
  
  public static Targets getInstance(Object paramObject) {
    return (paramObject instanceof Targets) ? (Targets)paramObject : ((paramObject != null) ? new Targets(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private Targets(ASN1Sequence paramASN1Sequence) {
    this.targets = paramASN1Sequence;
  }
  
  public Targets(Target[] paramArrayOfTarget) {
    this.targets = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfTarget);
  }
  
  public Target[] getTargets() {
    Target[] arrayOfTarget = new Target[this.targets.size()];
    byte b = 0;
    Enumeration enumeration = this.targets.getObjects();
    while (enumeration.hasMoreElements())
      arrayOfTarget[b++] = Target.getInstance(enumeration.nextElement()); 
    return arrayOfTarget;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.targets;
  }
}
