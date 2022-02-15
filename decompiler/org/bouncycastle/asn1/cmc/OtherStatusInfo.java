package org.bouncycastle.asn1.cmc;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;

public class OtherStatusInfo extends ASN1Object implements ASN1Choice {
  private final CMCFailInfo failInfo;
  
  private final PendInfo pendInfo;
  
  private final ExtendedFailInfo extendedFailInfo;
  
  public static OtherStatusInfo getInstance(Object paramObject) {
    if (paramObject instanceof OtherStatusInfo)
      return (OtherStatusInfo)paramObject; 
    if (paramObject instanceof ASN1Encodable) {
      ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
      if (aSN1Primitive instanceof org.bouncycastle.asn1.ASN1Integer)
        return new OtherStatusInfo(CMCFailInfo.getInstance(aSN1Primitive)); 
      if (aSN1Primitive instanceof ASN1Sequence)
        return (((ASN1Sequence)aSN1Primitive).getObjectAt(0) instanceof org.bouncycastle.asn1.ASN1ObjectIdentifier) ? new OtherStatusInfo(ExtendedFailInfo.getInstance(aSN1Primitive)) : new OtherStatusInfo(PendInfo.getInstance(aSN1Primitive)); 
    } else if (paramObject instanceof byte[]) {
      try {
        return getInstance(ASN1Primitive.fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("parsing error: " + iOException.getMessage());
      } 
    } 
    throw new IllegalArgumentException("unknown object in getInstance(): " + paramObject.getClass().getName());
  }
  
  OtherStatusInfo(CMCFailInfo paramCMCFailInfo) {
    this(paramCMCFailInfo, null, null);
  }
  
  OtherStatusInfo(PendInfo paramPendInfo) {
    this(null, paramPendInfo, null);
  }
  
  OtherStatusInfo(ExtendedFailInfo paramExtendedFailInfo) {
    this(null, null, paramExtendedFailInfo);
  }
  
  private OtherStatusInfo(CMCFailInfo paramCMCFailInfo, PendInfo paramPendInfo, ExtendedFailInfo paramExtendedFailInfo) {
    this.failInfo = paramCMCFailInfo;
    this.pendInfo = paramPendInfo;
    this.extendedFailInfo = paramExtendedFailInfo;
  }
  
  public boolean isPendingInfo() {
    return (this.pendInfo != null);
  }
  
  public boolean isFailInfo() {
    return (this.failInfo != null);
  }
  
  public boolean isExtendedFailInfo() {
    return (this.extendedFailInfo != null);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (this.pendInfo != null) ? this.pendInfo.toASN1Primitive() : ((this.failInfo != null) ? this.failInfo.toASN1Primitive() : this.extendedFailInfo.toASN1Primitive());
  }
}
