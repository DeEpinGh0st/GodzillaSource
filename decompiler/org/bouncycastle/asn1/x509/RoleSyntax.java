package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class RoleSyntax extends ASN1Object {
  private GeneralNames roleAuthority;
  
  private GeneralName roleName;
  
  public static RoleSyntax getInstance(Object paramObject) {
    return (paramObject instanceof RoleSyntax) ? (RoleSyntax)paramObject : ((paramObject != null) ? new RoleSyntax(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public RoleSyntax(GeneralNames paramGeneralNames, GeneralName paramGeneralName) {
    if (paramGeneralName == null || paramGeneralName.getTagNo() != 6 || ((ASN1String)paramGeneralName.getName()).getString().equals(""))
      throw new IllegalArgumentException("the role name MUST be non empty and MUST use the URI option of GeneralName"); 
    this.roleAuthority = paramGeneralNames;
    this.roleName = paramGeneralName;
  }
  
  public RoleSyntax(GeneralName paramGeneralName) {
    this(null, paramGeneralName);
  }
  
  public RoleSyntax(String paramString) {
    this(new GeneralName(6, (paramString == null) ? "" : paramString));
  }
  
  private RoleSyntax(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 1 || paramASN1Sequence.size() > 2)
      throw new IllegalArgumentException("Bad sequence size: " + paramASN1Sequence.size()); 
    for (byte b = 0; b != paramASN1Sequence.size(); b++) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(b));
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.roleAuthority = GeneralNames.getInstance(aSN1TaggedObject, false);
          break;
        case 1:
          this.roleName = GeneralName.getInstance(aSN1TaggedObject, true);
          break;
        default:
          throw new IllegalArgumentException("Unknown tag in RoleSyntax");
      } 
    } 
  }
  
  public GeneralNames getRoleAuthority() {
    return this.roleAuthority;
  }
  
  public GeneralName getRoleName() {
    return this.roleName;
  }
  
  public String getRoleNameAsString() {
    ASN1String aSN1String = (ASN1String)this.roleName.getName();
    return aSN1String.getString();
  }
  
  public String[] getRoleAuthorityAsString() {
    if (this.roleAuthority == null)
      return new String[0]; 
    GeneralName[] arrayOfGeneralName = this.roleAuthority.getNames();
    String[] arrayOfString = new String[arrayOfGeneralName.length];
    for (byte b = 0; b < arrayOfGeneralName.length; b++) {
      ASN1Encodable aSN1Encodable = arrayOfGeneralName[b].getName();
      if (aSN1Encodable instanceof ASN1String) {
        arrayOfString[b] = ((ASN1String)aSN1Encodable).getString();
      } else {
        arrayOfString[b] = aSN1Encodable.toString();
      } 
    } 
    return arrayOfString;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.roleAuthority != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.roleAuthority)); 
    aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 1, (ASN1Encodable)this.roleName));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer("Name: " + getRoleNameAsString() + " - Auth: ");
    if (this.roleAuthority == null || (this.roleAuthority.getNames()).length == 0) {
      stringBuffer.append("N/A");
    } else {
      String[] arrayOfString = getRoleAuthorityAsString();
      stringBuffer.append('[').append(arrayOfString[0]);
      for (byte b = 1; b < arrayOfString.length; b++)
        stringBuffer.append(", ").append(arrayOfString[b]); 
      stringBuffer.append(']');
    } 
    return stringBuffer.toString();
  }
}
