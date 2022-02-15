package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIHeaderBuilder;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.MacCalculator;

public class ProtectedPKIMessageBuilder {
  private PKIHeaderBuilder hdrBuilder;
  
  private PKIBody body;
  
  private List generalInfos = new ArrayList();
  
  private List extraCerts = new ArrayList();
  
  public ProtectedPKIMessageBuilder(GeneralName paramGeneralName1, GeneralName paramGeneralName2) {
    this(2, paramGeneralName1, paramGeneralName2);
  }
  
  public ProtectedPKIMessageBuilder(int paramInt, GeneralName paramGeneralName1, GeneralName paramGeneralName2) {
    this.hdrBuilder = new PKIHeaderBuilder(paramInt, paramGeneralName1, paramGeneralName2);
  }
  
  public ProtectedPKIMessageBuilder setTransactionID(byte[] paramArrayOfbyte) {
    this.hdrBuilder.setTransactionID(paramArrayOfbyte);
    return this;
  }
  
  public ProtectedPKIMessageBuilder setFreeText(PKIFreeText paramPKIFreeText) {
    this.hdrBuilder.setFreeText(paramPKIFreeText);
    return this;
  }
  
  public ProtectedPKIMessageBuilder addGeneralInfo(InfoTypeAndValue paramInfoTypeAndValue) {
    this.generalInfos.add(paramInfoTypeAndValue);
    return this;
  }
  
  public ProtectedPKIMessageBuilder setMessageTime(Date paramDate) {
    this.hdrBuilder.setMessageTime(new ASN1GeneralizedTime(paramDate));
    return this;
  }
  
  public ProtectedPKIMessageBuilder setRecipKID(byte[] paramArrayOfbyte) {
    this.hdrBuilder.setRecipKID(paramArrayOfbyte);
    return this;
  }
  
  public ProtectedPKIMessageBuilder setRecipNonce(byte[] paramArrayOfbyte) {
    this.hdrBuilder.setRecipNonce(paramArrayOfbyte);
    return this;
  }
  
  public ProtectedPKIMessageBuilder setSenderKID(byte[] paramArrayOfbyte) {
    this.hdrBuilder.setSenderKID(paramArrayOfbyte);
    return this;
  }
  
  public ProtectedPKIMessageBuilder setSenderNonce(byte[] paramArrayOfbyte) {
    this.hdrBuilder.setSenderNonce(paramArrayOfbyte);
    return this;
  }
  
  public ProtectedPKIMessageBuilder setBody(PKIBody paramPKIBody) {
    this.body = paramPKIBody;
    return this;
  }
  
  public ProtectedPKIMessageBuilder addCMPCertificate(X509CertificateHolder paramX509CertificateHolder) {
    this.extraCerts.add(paramX509CertificateHolder);
    return this;
  }
  
  public ProtectedPKIMessage build(MacCalculator paramMacCalculator) throws CMPException {
    finaliseHeader(paramMacCalculator.getAlgorithmIdentifier());
    PKIHeader pKIHeader = this.hdrBuilder.build();
    try {
      DERBitString dERBitString = new DERBitString(calculateMac(paramMacCalculator, pKIHeader, this.body));
      return finaliseMessage(pKIHeader, dERBitString);
    } catch (IOException iOException) {
      throw new CMPException("unable to encode MAC input: " + iOException.getMessage(), iOException);
    } 
  }
  
  public ProtectedPKIMessage build(ContentSigner paramContentSigner) throws CMPException {
    finaliseHeader(paramContentSigner.getAlgorithmIdentifier());
    PKIHeader pKIHeader = this.hdrBuilder.build();
    try {
      DERBitString dERBitString = new DERBitString(calculateSignature(paramContentSigner, pKIHeader, this.body));
      return finaliseMessage(pKIHeader, dERBitString);
    } catch (IOException iOException) {
      throw new CMPException("unable to encode signature input: " + iOException.getMessage(), iOException);
    } 
  }
  
  private void finaliseHeader(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.hdrBuilder.setProtectionAlg(paramAlgorithmIdentifier);
    if (!this.generalInfos.isEmpty()) {
      InfoTypeAndValue[] arrayOfInfoTypeAndValue = new InfoTypeAndValue[this.generalInfos.size()];
      this.hdrBuilder.setGeneralInfo((InfoTypeAndValue[])this.generalInfos.toArray((Object[])arrayOfInfoTypeAndValue));
    } 
  }
  
  private ProtectedPKIMessage finaliseMessage(PKIHeader paramPKIHeader, DERBitString paramDERBitString) {
    if (!this.extraCerts.isEmpty()) {
      CMPCertificate[] arrayOfCMPCertificate = new CMPCertificate[this.extraCerts.size()];
      for (byte b = 0; b != arrayOfCMPCertificate.length; b++)
        arrayOfCMPCertificate[b] = new CMPCertificate(((X509CertificateHolder)this.extraCerts.get(b)).toASN1Structure()); 
      return new ProtectedPKIMessage(new PKIMessage(paramPKIHeader, this.body, paramDERBitString, arrayOfCMPCertificate));
    } 
    return new ProtectedPKIMessage(new PKIMessage(paramPKIHeader, this.body, paramDERBitString));
  }
  
  private byte[] calculateSignature(ContentSigner paramContentSigner, PKIHeader paramPKIHeader, PKIBody paramPKIBody) throws IOException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)paramPKIHeader);
    aSN1EncodableVector.add((ASN1Encodable)paramPKIBody);
    OutputStream outputStream = paramContentSigner.getOutputStream();
    outputStream.write((new DERSequence(aSN1EncodableVector)).getEncoded("DER"));
    outputStream.close();
    return paramContentSigner.getSignature();
  }
  
  private byte[] calculateMac(MacCalculator paramMacCalculator, PKIHeader paramPKIHeader, PKIBody paramPKIBody) throws IOException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)paramPKIHeader);
    aSN1EncodableVector.add((ASN1Encodable)paramPKIBody);
    OutputStream outputStream = paramMacCalculator.getOutputStream();
    outputStream.write((new DERSequence(aSN1EncodableVector)).getEncoded("DER"));
    outputStream.close();
    return paramMacCalculator.getMac();
  }
}
