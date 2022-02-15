package org.bouncycastle.asn1.dvcs;

import java.util.Arrays;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class PathProcInput extends ASN1Object {
  private PolicyInformation[] acceptablePolicySet;
  
  private boolean inhibitPolicyMapping = false;
  
  private boolean explicitPolicyReqd = false;
  
  private boolean inhibitAnyPolicy = false;
  
  public PathProcInput(PolicyInformation[] paramArrayOfPolicyInformation) {
    this.acceptablePolicySet = paramArrayOfPolicyInformation;
  }
  
  public PathProcInput(PolicyInformation[] paramArrayOfPolicyInformation, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    this.acceptablePolicySet = paramArrayOfPolicyInformation;
    this.inhibitPolicyMapping = paramBoolean1;
    this.explicitPolicyReqd = paramBoolean2;
    this.inhibitAnyPolicy = paramBoolean3;
  }
  
  private static PolicyInformation[] fromSequence(ASN1Sequence paramASN1Sequence) {
    PolicyInformation[] arrayOfPolicyInformation = new PolicyInformation[paramASN1Sequence.size()];
    for (byte b = 0; b != arrayOfPolicyInformation.length; b++)
      arrayOfPolicyInformation[b] = PolicyInformation.getInstance(paramASN1Sequence.getObjectAt(b)); 
    return arrayOfPolicyInformation;
  }
  
  public static PathProcInput getInstance(Object paramObject) {
    if (paramObject instanceof PathProcInput)
      return (PathProcInput)paramObject; 
    if (paramObject != null) {
      ASN1Sequence aSN1Sequence1 = ASN1Sequence.getInstance(paramObject);
      ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(aSN1Sequence1.getObjectAt(0));
      PathProcInput pathProcInput = new PathProcInput(fromSequence(aSN1Sequence2));
      for (byte b = 1; b < aSN1Sequence1.size(); b++) {
        ASN1Encodable aSN1Encodable = aSN1Sequence1.getObjectAt(b);
        if (aSN1Encodable instanceof ASN1Boolean) {
          ASN1Boolean aSN1Boolean = ASN1Boolean.getInstance(aSN1Encodable);
          pathProcInput.setInhibitPolicyMapping(aSN1Boolean.isTrue());
        } else if (aSN1Encodable instanceof ASN1TaggedObject) {
          ASN1Boolean aSN1Boolean;
          ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(aSN1Encodable);
          switch (aSN1TaggedObject.getTagNo()) {
            case 0:
              aSN1Boolean = ASN1Boolean.getInstance(aSN1TaggedObject, false);
              pathProcInput.setExplicitPolicyReqd(aSN1Boolean.isTrue());
              break;
            case 1:
              aSN1Boolean = ASN1Boolean.getInstance(aSN1TaggedObject, false);
              pathProcInput.setInhibitAnyPolicy(aSN1Boolean.isTrue());
              break;
            default:
              throw new IllegalArgumentException("Unknown tag encountered: " + aSN1TaggedObject.getTagNo());
          } 
        } 
      } 
      return pathProcInput;
    } 
    return null;
  }
  
  public static PathProcInput getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    for (byte b = 0; b != this.acceptablePolicySet.length; b++)
      aSN1EncodableVector2.add((ASN1Encodable)this.acceptablePolicySet[b]); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
    if (this.inhibitPolicyMapping)
      aSN1EncodableVector1.add((ASN1Encodable)ASN1Boolean.getInstance(this.inhibitPolicyMapping)); 
    if (this.explicitPolicyReqd)
      aSN1EncodableVector1.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)ASN1Boolean.getInstance(this.explicitPolicyReqd))); 
    if (this.inhibitAnyPolicy)
      aSN1EncodableVector1.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)ASN1Boolean.getInstance(this.inhibitAnyPolicy))); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector1);
  }
  
  public String toString() {
    return "PathProcInput: {\nacceptablePolicySet: " + Arrays.<PolicyInformation>asList(this.acceptablePolicySet) + "\n" + "inhibitPolicyMapping: " + this.inhibitPolicyMapping + "\n" + "explicitPolicyReqd: " + this.explicitPolicyReqd + "\n" + "inhibitAnyPolicy: " + this.inhibitAnyPolicy + "\n" + "}\n";
  }
  
  public PolicyInformation[] getAcceptablePolicySet() {
    return this.acceptablePolicySet;
  }
  
  public boolean isInhibitPolicyMapping() {
    return this.inhibitPolicyMapping;
  }
  
  private void setInhibitPolicyMapping(boolean paramBoolean) {
    this.inhibitPolicyMapping = paramBoolean;
  }
  
  public boolean isExplicitPolicyReqd() {
    return this.explicitPolicyReqd;
  }
  
  private void setExplicitPolicyReqd(boolean paramBoolean) {
    this.explicitPolicyReqd = paramBoolean;
  }
  
  public boolean isInhibitAnyPolicy() {
    return this.inhibitAnyPolicy;
  }
  
  private void setInhibitAnyPolicy(boolean paramBoolean) {
    this.inhibitAnyPolicy = paramBoolean;
  }
}
