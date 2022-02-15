package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.Arrays;

public class ProtectedPKIMessage {
  private PKIMessage pkiMessage;
  
  public ProtectedPKIMessage(GeneralPKIMessage paramGeneralPKIMessage) {
    if (!paramGeneralPKIMessage.hasProtection())
      throw new IllegalArgumentException("PKIMessage not protected"); 
    this.pkiMessage = paramGeneralPKIMessage.toASN1Structure();
  }
  
  ProtectedPKIMessage(PKIMessage paramPKIMessage) {
    if (paramPKIMessage.getHeader().getProtectionAlg() == null)
      throw new IllegalArgumentException("PKIMessage not protected"); 
    this.pkiMessage = paramPKIMessage;
  }
  
  public PKIHeader getHeader() {
    return this.pkiMessage.getHeader();
  }
  
  public PKIBody getBody() {
    return this.pkiMessage.getBody();
  }
  
  public PKIMessage toASN1Structure() {
    return this.pkiMessage;
  }
  
  public boolean hasPasswordBasedMacProtection() {
    return this.pkiMessage.getHeader().getProtectionAlg().getAlgorithm().equals(CMPObjectIdentifiers.passwordBasedMac);
  }
  
  public X509CertificateHolder[] getCertificates() {
    CMPCertificate[] arrayOfCMPCertificate = this.pkiMessage.getExtraCerts();
    if (arrayOfCMPCertificate == null)
      return new X509CertificateHolder[0]; 
    X509CertificateHolder[] arrayOfX509CertificateHolder = new X509CertificateHolder[arrayOfCMPCertificate.length];
    for (byte b = 0; b != arrayOfCMPCertificate.length; b++)
      arrayOfX509CertificateHolder[b] = new X509CertificateHolder(arrayOfCMPCertificate[b].getX509v3PKCert()); 
    return arrayOfX509CertificateHolder;
  }
  
  public boolean verify(ContentVerifierProvider paramContentVerifierProvider) throws CMPException {
    try {
      ContentVerifier contentVerifier = paramContentVerifierProvider.get(this.pkiMessage.getHeader().getProtectionAlg());
      return verifySignature(this.pkiMessage.getProtection().getBytes(), contentVerifier);
    } catch (Exception exception) {
      throw new CMPException("unable to verify signature: " + exception.getMessage(), exception);
    } 
  }
  
  public boolean verify(PKMACBuilder paramPKMACBuilder, char[] paramArrayOfchar) throws CMPException {
    if (!CMPObjectIdentifiers.passwordBasedMac.equals(this.pkiMessage.getHeader().getProtectionAlg().getAlgorithm()))
      throw new CMPException("protection algorithm not mac based"); 
    try {
      paramPKMACBuilder.setParameters(PBMParameter.getInstance(this.pkiMessage.getHeader().getProtectionAlg().getParameters()));
      MacCalculator macCalculator = paramPKMACBuilder.build(paramArrayOfchar);
      OutputStream outputStream = macCalculator.getOutputStream();
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      aSN1EncodableVector.add((ASN1Encodable)this.pkiMessage.getHeader());
      aSN1EncodableVector.add((ASN1Encodable)this.pkiMessage.getBody());
      outputStream.write((new DERSequence(aSN1EncodableVector)).getEncoded("DER"));
      outputStream.close();
      return Arrays.areEqual(macCalculator.getMac(), this.pkiMessage.getProtection().getBytes());
    } catch (Exception exception) {
      throw new CMPException("unable to verify MAC: " + exception.getMessage(), exception);
    } 
  }
  
  private boolean verifySignature(byte[] paramArrayOfbyte, ContentVerifier paramContentVerifier) throws IOException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.pkiMessage.getHeader());
    aSN1EncodableVector.add((ASN1Encodable)this.pkiMessage.getBody());
    OutputStream outputStream = paramContentVerifier.getOutputStream();
    outputStream.write((new DERSequence(aSN1EncodableVector)).getEncoded("DER"));
    outputStream.close();
    return paramContentVerifier.verify(paramArrayOfbyte);
  }
}
