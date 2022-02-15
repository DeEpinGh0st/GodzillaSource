package org.bouncycastle.asn1.isismtt.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.DirectoryString;

public class Restriction extends ASN1Object {
  private DirectoryString restriction;
  
  public static Restriction getInstance(Object paramObject) {
    return (paramObject instanceof Restriction) ? (Restriction)paramObject : ((paramObject != null) ? new Restriction(DirectoryString.getInstance(paramObject)) : null);
  }
  
  private Restriction(DirectoryString paramDirectoryString) {
    this.restriction = paramDirectoryString;
  }
  
  public Restriction(String paramString) {
    this.restriction = new DirectoryString(paramString);
  }
  
  public DirectoryString getRestriction() {
    return this.restriction;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.restriction.toASN1Primitive();
  }
}
