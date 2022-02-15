package org.bouncycastle.asn1.dvcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.util.BigIntegers;

public class DVCSRequestInformationBuilder {
  private int version = 1;
  
  private final ServiceType service;
  
  private DVCSRequestInformation initialInfo;
  
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
  
  public DVCSRequestInformationBuilder(ServiceType paramServiceType) {
    this.service = paramServiceType;
  }
  
  public DVCSRequestInformationBuilder(DVCSRequestInformation paramDVCSRequestInformation) {
    this.initialInfo = paramDVCSRequestInformation;
    this.service = paramDVCSRequestInformation.getService();
    this.version = paramDVCSRequestInformation.getVersion();
    this.nonce = paramDVCSRequestInformation.getNonce();
    this.requestTime = paramDVCSRequestInformation.getRequestTime();
    this.requestPolicy = paramDVCSRequestInformation.getRequestPolicy();
    this.dvcs = paramDVCSRequestInformation.getDVCS();
    this.dataLocations = paramDVCSRequestInformation.getDataLocations();
  }
  
  public DVCSRequestInformation build() {
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
    return DVCSRequestInformation.getInstance(new DERSequence(aSN1EncodableVector));
  }
  
  public void setVersion(int paramInt) {
    if (this.initialInfo != null)
      throw new IllegalStateException("cannot change version in existing DVCSRequestInformation"); 
    this.version = paramInt;
  }
  
  public void setNonce(BigInteger paramBigInteger) {
    if (this.initialInfo != null)
      if (this.initialInfo.getNonce() == null) {
        this.nonce = paramBigInteger;
      } else {
        byte[] arrayOfByte1 = this.initialInfo.getNonce().toByteArray();
        byte[] arrayOfByte2 = BigIntegers.asUnsignedByteArray(paramBigInteger);
        byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
        System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
        this.nonce = new BigInteger(arrayOfByte3);
      }  
    this.nonce = paramBigInteger;
  }
  
  public void setRequestTime(DVCSTime paramDVCSTime) {
    if (this.initialInfo != null)
      throw new IllegalStateException("cannot change request time in existing DVCSRequestInformation"); 
    this.requestTime = paramDVCSTime;
  }
  
  public void setRequester(GeneralName paramGeneralName) {
    setRequester(new GeneralNames(paramGeneralName));
  }
  
  public void setRequester(GeneralNames paramGeneralNames) {
    this.requester = paramGeneralNames;
  }
  
  public void setRequestPolicy(PolicyInformation paramPolicyInformation) {
    if (this.initialInfo != null)
      throw new IllegalStateException("cannot change request policy in existing DVCSRequestInformation"); 
    this.requestPolicy = paramPolicyInformation;
  }
  
  public void setDVCS(GeneralName paramGeneralName) {
    setDVCS(new GeneralNames(paramGeneralName));
  }
  
  public void setDVCS(GeneralNames paramGeneralNames) {
    this.dvcs = paramGeneralNames;
  }
  
  public void setDataLocations(GeneralName paramGeneralName) {
    setDataLocations(new GeneralNames(paramGeneralName));
  }
  
  public void setDataLocations(GeneralNames paramGeneralNames) {
    this.dataLocations = paramGeneralNames;
  }
  
  public void setExtensions(Extensions paramExtensions) {
    if (this.initialInfo != null)
      throw new IllegalStateException("cannot change extensions in existing DVCSRequestInformation"); 
    this.extensions = paramExtensions;
  }
}
