package org.bouncycastle.asn1.cmc;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class CMCStatus extends ASN1Object {
  public static final CMCStatus success = new CMCStatus(new ASN1Integer(0L));
  
  public static final CMCStatus failed = new CMCStatus(new ASN1Integer(2L));
  
  public static final CMCStatus pending = new CMCStatus(new ASN1Integer(3L));
  
  public static final CMCStatus noSupport = new CMCStatus(new ASN1Integer(4L));
  
  public static final CMCStatus confirmRequired = new CMCStatus(new ASN1Integer(5L));
  
  public static final CMCStatus popRequired = new CMCStatus(new ASN1Integer(6L));
  
  public static final CMCStatus partial = new CMCStatus(new ASN1Integer(7L));
  
  private static Map range = new HashMap<Object, Object>();
  
  private final ASN1Integer value;
  
  private CMCStatus(ASN1Integer paramASN1Integer) {
    this.value = paramASN1Integer;
  }
  
  public static CMCStatus getInstance(Object paramObject) {
    if (paramObject instanceof CMCStatus)
      return (CMCStatus)paramObject; 
    if (paramObject != null) {
      CMCStatus cMCStatus = (CMCStatus)range.get(ASN1Integer.getInstance(paramObject));
      if (cMCStatus != null)
        return cMCStatus; 
      throw new IllegalArgumentException("unknown object in getInstance(): " + paramObject.getClass().getName());
    } 
    return null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.value;
  }
  
  static {
    range.put(success.value, success);
    range.put(failed.value, failed);
    range.put(pending.value, pending);
    range.put(noSupport.value, noSupport);
    range.put(confirmRequired.value, confirmRequired);
    range.put(popRequired.value, popRequired);
    range.put(partial.value, partial);
  }
}
