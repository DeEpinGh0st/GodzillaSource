package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Strings;

public class GeneralNames extends ASN1Object {
  private final GeneralName[] names;
  
  public static GeneralNames getInstance(Object paramObject) {
    return (paramObject instanceof GeneralNames) ? (GeneralNames)paramObject : ((paramObject != null) ? new GeneralNames(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static GeneralNames getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public static GeneralNames fromExtensions(Extensions paramExtensions, ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return getInstance(paramExtensions.getExtensionParsedValue(paramASN1ObjectIdentifier));
  }
  
  public GeneralNames(GeneralName paramGeneralName) {
    this.names = new GeneralName[] { paramGeneralName };
  }
  
  public GeneralNames(GeneralName[] paramArrayOfGeneralName) {
    this.names = paramArrayOfGeneralName;
  }
  
  private GeneralNames(ASN1Sequence paramASN1Sequence) {
    this.names = new GeneralName[paramASN1Sequence.size()];
    for (byte b = 0; b != paramASN1Sequence.size(); b++)
      this.names[b] = GeneralName.getInstance(paramASN1Sequence.getObjectAt(b)); 
  }
  
  public GeneralName[] getNames() {
    GeneralName[] arrayOfGeneralName = new GeneralName[this.names.length];
    System.arraycopy(this.names, 0, arrayOfGeneralName, 0, this.names.length);
    return arrayOfGeneralName;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence((ASN1Encodable[])this.names);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("GeneralNames:");
    stringBuffer.append(str);
    for (byte b = 0; b != this.names.length; b++) {
      stringBuffer.append("    ");
      stringBuffer.append(this.names[b]);
      stringBuffer.append(str);
    } 
    return stringBuffer.toString();
  }
}
