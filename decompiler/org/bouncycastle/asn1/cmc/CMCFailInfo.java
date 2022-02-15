package org.bouncycastle.asn1.cmc;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class CMCFailInfo extends ASN1Object {
  public static final CMCFailInfo badAlg = new CMCFailInfo(new ASN1Integer(0L));
  
  public static final CMCFailInfo badMessageCheck = new CMCFailInfo(new ASN1Integer(1L));
  
  public static final CMCFailInfo badRequest = new CMCFailInfo(new ASN1Integer(2L));
  
  public static final CMCFailInfo badTime = new CMCFailInfo(new ASN1Integer(3L));
  
  public static final CMCFailInfo badCertId = new CMCFailInfo(new ASN1Integer(4L));
  
  public static final CMCFailInfo unsupportedExt = new CMCFailInfo(new ASN1Integer(5L));
  
  public static final CMCFailInfo mustArchiveKeys = new CMCFailInfo(new ASN1Integer(6L));
  
  public static final CMCFailInfo badIdentity = new CMCFailInfo(new ASN1Integer(7L));
  
  public static final CMCFailInfo popRequired = new CMCFailInfo(new ASN1Integer(8L));
  
  public static final CMCFailInfo popFailed = new CMCFailInfo(new ASN1Integer(9L));
  
  public static final CMCFailInfo noKeyReuse = new CMCFailInfo(new ASN1Integer(10L));
  
  public static final CMCFailInfo internalCAError = new CMCFailInfo(new ASN1Integer(11L));
  
  public static final CMCFailInfo tryLater = new CMCFailInfo(new ASN1Integer(12L));
  
  public static final CMCFailInfo authDataFail = new CMCFailInfo(new ASN1Integer(13L));
  
  private static Map range = new HashMap<Object, Object>();
  
  private final ASN1Integer value;
  
  private CMCFailInfo(ASN1Integer paramASN1Integer) {
    this.value = paramASN1Integer;
  }
  
  public static CMCFailInfo getInstance(Object paramObject) {
    if (paramObject instanceof CMCFailInfo)
      return (CMCFailInfo)paramObject; 
    if (paramObject != null) {
      CMCFailInfo cMCFailInfo = (CMCFailInfo)range.get(ASN1Integer.getInstance(paramObject));
      if (cMCFailInfo != null)
        return cMCFailInfo; 
      throw new IllegalArgumentException("unknown object in getInstance(): " + paramObject.getClass().getName());
    } 
    return null;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.value;
  }
  
  static {
    range.put(badAlg.value, badAlg);
    range.put(badMessageCheck.value, badMessageCheck);
    range.put(badRequest.value, badRequest);
    range.put(badTime.value, badTime);
    range.put(badCertId.value, badCertId);
    range.put(popRequired.value, popRequired);
    range.put(unsupportedExt.value, unsupportedExt);
    range.put(mustArchiveKeys.value, mustArchiveKeys);
    range.put(badIdentity.value, badIdentity);
    range.put(popRequired.value, popRequired);
    range.put(popFailed.value, popFailed);
    range.put(badCertId.value, badCertId);
    range.put(popRequired.value, popRequired);
    range.put(noKeyReuse.value, noKeyReuse);
    range.put(internalCAError.value, internalCAError);
    range.put(tryLater.value, tryLater);
    range.put(authDataFail.value, authDataFail);
  }
}
