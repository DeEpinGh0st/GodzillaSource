package org.bouncycastle.asn1.dvcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSRequestInformation extends ASN1Object {
  private int version = 1;
  
  private ServiceType service;
  
  private BigInteger nonce;
  
  private DVCSTime requestTime;
  
  private GeneralNames requester;
  
  private PolicyInformation requestPolicy;
  
  private GeneralNames dvcs;
  
  private GeneralNames dataLocations;
  
  private Extensions extensions;
  
  private static final int DEFAULT_VERSION = 1;
  
  private static final int TAG_REQUESTER = 0;
  
  private static final int TAG_REQUEST_POLICY = 1;
  
  private static final int TAG_DVCS = 2;
  
  private static final int TAG_DATA_LOCATIONS = 3;
  
  private static final int TAG_EXTENSIONS = 4;
  
  private DVCSRequestInformation(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
      ASN1Integer aSN1Integer = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(b++));
      this.version = aSN1Integer.getValue().intValue();
    } else {
      this.version = 1;
    } 
    this.service = ServiceType.getInstance(paramASN1Sequence.getObjectAt(b++));
    while (b < paramASN1Sequence.size()) {
      ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(b);
      if (aSN1Encodable instanceof ASN1Integer) {
        this.nonce = ASN1Integer.getInstance(aSN1Encodable).getValue();
      } else if (aSN1Encodable instanceof org.bouncycastle.asn1.ASN1GeneralizedTime) {
        this.requestTime = DVCSTime.getInstance(aSN1Encodable);
      } else if (aSN1Encodable instanceof ASN1TaggedObject) {
        ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(aSN1Encodable);
        int i = aSN1TaggedObject.getTagNo();
        switch (i) {
          case 0:
            this.requester = GeneralNames.getInstance(aSN1TaggedObject, false);
            break;
          case 1:
            this.requestPolicy = PolicyInformation.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, false));
            break;
          case 2:
            this.dvcs = GeneralNames.getInstance(aSN1TaggedObject, false);
            break;
          case 3:
            this.dataLocations = GeneralNames.getInstance(aSN1TaggedObject, false);
            break;
          case 4:
            this.extensions = Extensions.getInstance(aSN1TaggedObject, false);
            break;
          default:
            throw new IllegalArgumentException("unknown tag number encountered: " + i);
        } 
      } else {
        this.requestTime = DVCSTime.getInstance(aSN1Encodable);
      } 
      b++;
    } 
  }
  
  public static DVCSRequestInformation getInstance(Object paramObject) {
    return (paramObject instanceof DVCSRequestInformation) ? (DVCSRequestInformation)paramObject : ((paramObject != null) ? new DVCSRequestInformation(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static DVCSRequestInformation getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.version != 1)
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.version)); 
    aSN1EncodableVector.add((ASN1Encodable)this.service);
    if (this.nonce != null)
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.nonce)); 
    if (this.requestTime != null)
      aSN1EncodableVector.add((ASN1Encodable)this.requestTime); 
    int[] arrayOfInt = { 0, 1, 2, 3, 4 };
    ASN1Encodable[] arrayOfASN1Encodable = { (ASN1Encodable)this.requester, (ASN1Encodable)this.requestPolicy, (ASN1Encodable)this.dvcs, (ASN1Encodable)this.dataLocations, (ASN1Encodable)this.extensions };
    for (byte b = 0; b < arrayOfInt.length; b++) {
      int i = arrayOfInt[b];
      ASN1Encodable aSN1Encodable = arrayOfASN1Encodable[b];
      if (aSN1Encodable != null)
        aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, i, aSN1Encodable)); 
    } 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("DVCSRequestInformation {\n");
    if (this.version != 1)
      stringBuffer.append("version: " + this.version + "\n"); 
    stringBuffer.append("service: " + this.service + "\n");
    if (this.nonce != null)
      stringBuffer.append("nonce: " + this.nonce + "\n"); 
    if (this.requestTime != null)
      stringBuffer.append("requestTime: " + this.requestTime + "\n"); 
    if (this.requester != null)
      stringBuffer.append("requester: " + this.requester + "\n"); 
    if (this.requestPolicy != null)
      stringBuffer.append("requestPolicy: " + this.requestPolicy + "\n"); 
    if (this.dvcs != null)
      stringBuffer.append("dvcs: " + this.dvcs + "\n"); 
    if (this.dataLocations != null)
      stringBuffer.append("dataLocations: " + this.dataLocations + "\n"); 
    if (this.extensions != null)
      stringBuffer.append("extensions: " + this.extensions + "\n"); 
    stringBuffer.append("}\n");
    return stringBuffer.toString();
  }
  
  public int getVersion() {
    return this.version;
  }
  
  public ServiceType getService() {
    return this.service;
  }
  
  public BigInteger getNonce() {
    return this.nonce;
  }
  
  public DVCSTime getRequestTime() {
    return this.requestTime;
  }
  
  public GeneralNames getRequester() {
    return this.requester;
  }
  
  public PolicyInformation getRequestPolicy() {
    return this.requestPolicy;
  }
  
  public GeneralNames getDVCS() {
    return this.dvcs;
  }
  
  public GeneralNames getDataLocations() {
    return this.dataLocations;
  }
  
  public Extensions getExtensions() {
    return this.extensions;
  }
}
