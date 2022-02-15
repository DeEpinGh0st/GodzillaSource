package org.bouncycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.asn1.x509.Extensions;

public class TimeStampResponseGenerator {
  int status;
  
  ASN1EncodableVector statusStrings;
  
  int failInfo;
  
  private TimeStampTokenGenerator tokenGenerator;
  
  private Set acceptedAlgorithms;
  
  private Set acceptedPolicies;
  
  private Set acceptedExtensions;
  
  public TimeStampResponseGenerator(TimeStampTokenGenerator paramTimeStampTokenGenerator, Set paramSet) {
    this(paramTimeStampTokenGenerator, paramSet, null, null);
  }
  
  public TimeStampResponseGenerator(TimeStampTokenGenerator paramTimeStampTokenGenerator, Set paramSet1, Set paramSet2) {
    this(paramTimeStampTokenGenerator, paramSet1, paramSet2, null);
  }
  
  public TimeStampResponseGenerator(TimeStampTokenGenerator paramTimeStampTokenGenerator, Set paramSet1, Set paramSet2, Set paramSet3) {
    this.tokenGenerator = paramTimeStampTokenGenerator;
    this.acceptedAlgorithms = convert(paramSet1);
    this.acceptedPolicies = convert(paramSet2);
    this.acceptedExtensions = convert(paramSet3);
    this.statusStrings = new ASN1EncodableVector();
  }
  
  private void addStatusString(String paramString) {
    this.statusStrings.add((ASN1Encodable)new DERUTF8String(paramString));
  }
  
  private void setFailInfoField(int paramInt) {
    this.failInfo |= paramInt;
  }
  
  private PKIStatusInfo getPKIStatusInfo() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.status));
    if (this.statusStrings.size() > 0)
      aSN1EncodableVector.add((ASN1Encodable)PKIFreeText.getInstance(new DERSequence(this.statusStrings))); 
    if (this.failInfo != 0) {
      FailInfo failInfo = new FailInfo(this.failInfo);
      aSN1EncodableVector.add((ASN1Encodable)failInfo);
    } 
    return PKIStatusInfo.getInstance(new DERSequence(aSN1EncodableVector));
  }
  
  public TimeStampResponse generate(TimeStampRequest paramTimeStampRequest, BigInteger paramBigInteger, Date paramDate) throws TSPException {
    try {
      return generateGrantedResponse(paramTimeStampRequest, paramBigInteger, paramDate, "Operation Okay");
    } catch (Exception exception) {
      return generateRejectedResponse(exception);
    } 
  }
  
  public TimeStampResponse generateGrantedResponse(TimeStampRequest paramTimeStampRequest, BigInteger paramBigInteger, Date paramDate) throws TSPException {
    return generateGrantedResponse(paramTimeStampRequest, paramBigInteger, paramDate, null);
  }
  
  public TimeStampResponse generateGrantedResponse(TimeStampRequest paramTimeStampRequest, BigInteger paramBigInteger, Date paramDate, String paramString) throws TSPException {
    return generateGrantedResponse(paramTimeStampRequest, paramBigInteger, paramDate, paramString, null);
  }
  
  public TimeStampResponse generateGrantedResponse(TimeStampRequest paramTimeStampRequest, BigInteger paramBigInteger, Date paramDate, String paramString, Extensions paramExtensions) throws TSPException {
    ContentInfo contentInfo;
    if (paramDate == null)
      throw new TSPValidationException("The time source is not available.", 512); 
    paramTimeStampRequest.validate(this.acceptedAlgorithms, this.acceptedPolicies, this.acceptedExtensions);
    this.status = 0;
    this.statusStrings = new ASN1EncodableVector();
    if (paramString != null)
      addStatusString(paramString); 
    PKIStatusInfo pKIStatusInfo = getPKIStatusInfo();
    try {
      contentInfo = this.tokenGenerator.generate(paramTimeStampRequest, paramBigInteger, paramDate, paramExtensions).toCMSSignedData().toASN1Structure();
    } catch (TSPException tSPException) {
      throw tSPException;
    } catch (Exception exception) {
      throw new TSPException("Timestamp token received cannot be converted to ContentInfo", exception);
    } 
    try {
      return new TimeStampResponse(new DLSequence(new ASN1Encodable[] { (ASN1Encodable)pKIStatusInfo.toASN1Primitive(), (ASN1Encodable)contentInfo.toASN1Primitive() }));
    } catch (IOException iOException) {
      throw new TSPException("created badly formatted response!");
    } 
  }
  
  public TimeStampResponse generateRejectedResponse(Exception paramException) throws TSPException {
    return (paramException instanceof TSPValidationException) ? generateFailResponse(2, ((TSPValidationException)paramException).getFailureCode(), paramException.getMessage()) : generateFailResponse(2, 1073741824, paramException.getMessage());
  }
  
  public TimeStampResponse generateFailResponse(int paramInt1, int paramInt2, String paramString) throws TSPException {
    this.status = paramInt1;
    this.statusStrings = new ASN1EncodableVector();
    setFailInfoField(paramInt2);
    if (paramString != null)
      addStatusString(paramString); 
    PKIStatusInfo pKIStatusInfo = getPKIStatusInfo();
    TimeStampResp timeStampResp = new TimeStampResp(pKIStatusInfo, null);
    try {
      return new TimeStampResponse(timeStampResp);
    } catch (IOException iOException) {
      throw new TSPException("created badly formatted response!");
    } 
  }
  
  private Set convert(Set paramSet) {
    if (paramSet == null)
      return paramSet; 
    HashSet<ASN1ObjectIdentifier> hashSet = new HashSet(paramSet.size());
    for (String str : paramSet) {
      if (str instanceof String) {
        hashSet.add(new ASN1ObjectIdentifier(str));
        continue;
      } 
      hashSet.add(str);
    } 
    return hashSet;
  }
  
  class FailInfo extends DERBitString {
    FailInfo(int param1Int) {
      super(getBytes(param1Int), getPadBits(param1Int));
    }
  }
}
