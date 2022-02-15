package org.bouncycastle.asn1.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class PKIStatus extends ASN1Object {
  public static final int GRANTED = 0;
  
  public static final int GRANTED_WITH_MODS = 1;
  
  public static final int REJECTION = 2;
  
  public static final int WAITING = 3;
  
  public static final int REVOCATION_WARNING = 4;
  
  public static final int REVOCATION_NOTIFICATION = 5;
  
  public static final int KEY_UPDATE_WARNING = 6;
  
  public static final PKIStatus granted = new PKIStatus(0);
  
  public static final PKIStatus grantedWithMods = new PKIStatus(1);
  
  public static final PKIStatus rejection = new PKIStatus(2);
  
  public static final PKIStatus waiting = new PKIStatus(3);
  
  public static final PKIStatus revocationWarning = new PKIStatus(4);
  
  public static final PKIStatus revocationNotification = new PKIStatus(5);
  
  public static final PKIStatus keyUpdateWaiting = new PKIStatus(6);
  
  private ASN1Integer value;
  
  private PKIStatus(int paramInt) {
    this(new ASN1Integer(paramInt));
  }
  
  private PKIStatus(ASN1Integer paramASN1Integer) {
    this.value = paramASN1Integer;
  }
  
  public static PKIStatus getInstance(Object paramObject) {
    return (paramObject instanceof PKIStatus) ? (PKIStatus)paramObject : ((paramObject != null) ? new PKIStatus(ASN1Integer.getInstance(paramObject)) : null);
  }
  
  public BigInteger getValue() {
    return this.value.getValue();
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.value;
  }
}
