package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class CMCStatusInfo extends ASN1Object {
  private final CMCStatus cMCStatus;
  
  private final ASN1Sequence bodyList;
  
  private final DERUTF8String statusString;
  
  private final OtherInfo otherInfo;
  
  CMCStatusInfo(CMCStatus paramCMCStatus, ASN1Sequence paramASN1Sequence, DERUTF8String paramDERUTF8String, OtherInfo paramOtherInfo) {
    this.cMCStatus = paramCMCStatus;
    this.bodyList = paramASN1Sequence;
    this.statusString = paramDERUTF8String;
    this.otherInfo = paramOtherInfo;
  }
  
  private CMCStatusInfo(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() < 2 || paramASN1Sequence.size() > 4)
      throw new IllegalArgumentException("incorrect sequence size"); 
    this.cMCStatus = CMCStatus.getInstance(paramASN1Sequence.getObjectAt(0));
    this.bodyList = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(1));
    if (paramASN1Sequence.size() > 3) {
      this.statusString = DERUTF8String.getInstance(paramASN1Sequence.getObjectAt(2));
      this.otherInfo = OtherInfo.getInstance(paramASN1Sequence.getObjectAt(3));
    } else if (paramASN1Sequence.size() > 2) {
      if (paramASN1Sequence.getObjectAt(2) instanceof DERUTF8String) {
        this.statusString = DERUTF8String.getInstance(paramASN1Sequence.getObjectAt(2));
        this.otherInfo = null;
      } else {
        this.statusString = null;
        this.otherInfo = OtherInfo.getInstance(paramASN1Sequence.getObjectAt(2));
      } 
    } else {
      this.statusString = null;
      this.otherInfo = null;
    } 
  }
  
  public static CMCStatusInfo getInstance(Object paramObject) {
    return (paramObject instanceof CMCStatusInfo) ? (CMCStatusInfo)paramObject : ((paramObject != null) ? new CMCStatusInfo(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.cMCStatus);
    aSN1EncodableVector.add((ASN1Encodable)this.bodyList);
    if (this.statusString != null)
      aSN1EncodableVector.add((ASN1Encodable)this.statusString); 
    if (this.otherInfo != null)
      aSN1EncodableVector.add((ASN1Encodable)this.otherInfo); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public CMCStatus getCMCStatus() {
    return this.cMCStatus;
  }
  
  public BodyPartID[] getBodyList() {
    return Utils.toBodyPartIDArray(this.bodyList);
  }
  
  public DERUTF8String getStatusString() {
    return this.statusString;
  }
  
  public boolean hasOtherInfo() {
    return (this.otherInfo != null);
  }
  
  public OtherInfo getOtherInfo() {
    return this.otherInfo;
  }
  
  public static class OtherInfo extends ASN1Object implements ASN1Choice {
    private final CMCFailInfo failInfo;
    
    private final PendInfo pendInfo;
    
    private static OtherInfo getInstance(Object param1Object) {
      if (param1Object instanceof OtherInfo)
        return (OtherInfo)param1Object; 
      if (param1Object instanceof ASN1Encodable) {
        ASN1Primitive aSN1Primitive = ((ASN1Encodable)param1Object).toASN1Primitive();
        if (aSN1Primitive instanceof org.bouncycastle.asn1.ASN1Integer)
          return new OtherInfo(CMCFailInfo.getInstance(aSN1Primitive)); 
        if (aSN1Primitive instanceof ASN1Sequence)
          return new OtherInfo(PendInfo.getInstance(aSN1Primitive)); 
      } 
      throw new IllegalArgumentException("unknown object in getInstance(): " + param1Object.getClass().getName());
    }
    
    OtherInfo(CMCFailInfo param1CMCFailInfo) {
      this(param1CMCFailInfo, null);
    }
    
    OtherInfo(PendInfo param1PendInfo) {
      this(null, param1PendInfo);
    }
    
    private OtherInfo(CMCFailInfo param1CMCFailInfo, PendInfo param1PendInfo) {
      this.failInfo = param1CMCFailInfo;
      this.pendInfo = param1PendInfo;
    }
    
    public boolean isFailInfo() {
      return (this.failInfo != null);
    }
    
    public ASN1Primitive toASN1Primitive() {
      return (this.pendInfo != null) ? this.pendInfo.toASN1Primitive() : this.failInfo.toASN1Primitive();
    }
  }
}
