package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class TargetInformation extends ASN1Object {
  private ASN1Sequence targets;
  
  public static TargetInformation getInstance(Object paramObject) {
    return (paramObject instanceof TargetInformation) ? (TargetInformation)paramObject : ((paramObject != null) ? new TargetInformation(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private TargetInformation(ASN1Sequence paramASN1Sequence) {
    this.targets = paramASN1Sequence;
  }
  
  public Targets[] getTargetsObjects() {
    Targets[] arrayOfTargets = new Targets[this.targets.size()];
    byte b = 0;
    Enumeration enumeration = this.targets.getObjects();
    while (enumeration.hasMoreElements())
      arrayOfTargets[b++] = Targets.getInstance(enumeration.nextElement()); 
    return arrayOfTargets;
  }
  
  public TargetInformation(Targets paramTargets) {
    this.targets = (ASN1Sequence)new DERSequence((ASN1Encodable)paramTargets);
  }
  
  public TargetInformation(Target[] paramArrayOfTarget) {
    this(new Targets(paramArrayOfTarget));
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.targets;
  }
}
