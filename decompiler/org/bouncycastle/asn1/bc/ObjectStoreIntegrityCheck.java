package org.bouncycastle.asn1.bc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class ObjectStoreIntegrityCheck extends ASN1Object implements ASN1Choice {
  public static final int PBKD_MAC_CHECK = 0;
  
  private final int type;
  
  private final ASN1Object integrityCheck;
  
  public ObjectStoreIntegrityCheck(PbkdMacIntegrityCheck paramPbkdMacIntegrityCheck) {
    this((ASN1Encodable)paramPbkdMacIntegrityCheck);
  }
  
  private ObjectStoreIntegrityCheck(ASN1Encodable paramASN1Encodable) {
    if (paramASN1Encodable instanceof org.bouncycastle.asn1.ASN1Sequence || paramASN1Encodable instanceof PbkdMacIntegrityCheck) {
      this.type = 0;
      this.integrityCheck = PbkdMacIntegrityCheck.getInstance(paramASN1Encodable);
    } else {
      throw new IllegalArgumentException("Unknown check object in integrity check.");
    } 
  }
  
  public static ObjectStoreIntegrityCheck getInstance(Object paramObject) {
    if (paramObject instanceof ObjectStoreIntegrityCheck)
      return (ObjectStoreIntegrityCheck)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return new ObjectStoreIntegrityCheck((ASN1Encodable)ASN1Primitive.fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("Unable to parse integrity check details.");
      }  
    return (paramObject != null) ? new ObjectStoreIntegrityCheck((ASN1Encodable)paramObject) : null;
  }
  
  public int getType() {
    return this.type;
  }
  
  public ASN1Object getIntegrityCheck() {
    return this.integrityCheck;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.integrityCheck.toASN1Primitive();
  }
}
