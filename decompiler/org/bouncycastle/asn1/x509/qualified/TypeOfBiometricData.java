package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;

public class TypeOfBiometricData extends ASN1Object implements ASN1Choice {
  public static final int PICTURE = 0;
  
  public static final int HANDWRITTEN_SIGNATURE = 1;
  
  ASN1Encodable obj;
  
  public static TypeOfBiometricData getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof TypeOfBiometricData)
      return (TypeOfBiometricData)paramObject; 
    if (paramObject instanceof ASN1Integer) {
      ASN1Integer aSN1Integer = ASN1Integer.getInstance(paramObject);
      int i = aSN1Integer.getValue().intValue();
      return new TypeOfBiometricData(i);
    } 
    if (paramObject instanceof ASN1ObjectIdentifier) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(paramObject);
      return new TypeOfBiometricData(aSN1ObjectIdentifier);
    } 
    throw new IllegalArgumentException("unknown object in getInstance");
  }
  
  public TypeOfBiometricData(int paramInt) {
    if (paramInt == 0 || paramInt == 1) {
      this.obj = (ASN1Encodable)new ASN1Integer(paramInt);
    } else {
      throw new IllegalArgumentException("unknow PredefinedBiometricType : " + paramInt);
    } 
  }
  
  public TypeOfBiometricData(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.obj = (ASN1Encodable)paramASN1ObjectIdentifier;
  }
  
  public boolean isPredefined() {
    return this.obj instanceof ASN1Integer;
  }
  
  public int getPredefinedBiometricType() {
    return ((ASN1Integer)this.obj).getValue().intValue();
  }
  
  public ASN1ObjectIdentifier getBiometricDataOid() {
    return (ASN1ObjectIdentifier)this.obj;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.obj.toASN1Primitive();
  }
}
