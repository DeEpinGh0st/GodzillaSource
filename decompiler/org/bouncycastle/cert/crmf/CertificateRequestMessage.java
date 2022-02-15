package org.bouncycastle.cert.crmf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.Controls;
import org.bouncycastle.asn1.crmf.PKIArchiveOptions;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Encodable;

public class CertificateRequestMessage implements Encodable {
  public static final int popRaVerified = 0;
  
  public static final int popSigningKey = 1;
  
  public static final int popKeyEncipherment = 2;
  
  public static final int popKeyAgreement = 3;
  
  private final CertReqMsg certReqMsg;
  
  private final Controls controls;
  
  private static CertReqMsg parseBytes(byte[] paramArrayOfbyte) throws IOException {
    try {
      return CertReqMsg.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte));
    } catch (ClassCastException classCastException) {
      throw new CertIOException("malformed data: " + classCastException.getMessage(), classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CertIOException("malformed data: " + illegalArgumentException.getMessage(), illegalArgumentException);
    } 
  }
  
  public CertificateRequestMessage(byte[] paramArrayOfbyte) throws IOException {
    this(parseBytes(paramArrayOfbyte));
  }
  
  public CertificateRequestMessage(CertReqMsg paramCertReqMsg) {
    this.certReqMsg = paramCertReqMsg;
    this.controls = paramCertReqMsg.getCertReq().getControls();
  }
  
  public CertReqMsg toASN1Structure() {
    return this.certReqMsg;
  }
  
  public CertTemplate getCertTemplate() {
    return this.certReqMsg.getCertReq().getCertTemplate();
  }
  
  public boolean hasControls() {
    return (this.controls != null);
  }
  
  public boolean hasControl(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (findControl(paramASN1ObjectIdentifier) != null);
  }
  
  public Control getControl(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    AttributeTypeAndValue attributeTypeAndValue = findControl(paramASN1ObjectIdentifier);
    if (attributeTypeAndValue != null) {
      if (attributeTypeAndValue.getType().equals(CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions))
        return new PKIArchiveControl(PKIArchiveOptions.getInstance(attributeTypeAndValue.getValue())); 
      if (attributeTypeAndValue.getType().equals(CRMFObjectIdentifiers.id_regCtrl_regToken))
        return new RegTokenControl(DERUTF8String.getInstance(attributeTypeAndValue.getValue())); 
      if (attributeTypeAndValue.getType().equals(CRMFObjectIdentifiers.id_regCtrl_authenticator))
        return new AuthenticatorControl(DERUTF8String.getInstance(attributeTypeAndValue.getValue())); 
    } 
    return null;
  }
  
  private AttributeTypeAndValue findControl(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    if (this.controls == null)
      return null; 
    AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = this.controls.toAttributeTypeAndValueArray();
    AttributeTypeAndValue attributeTypeAndValue = null;
    for (byte b = 0; b != arrayOfAttributeTypeAndValue.length; b++) {
      if (arrayOfAttributeTypeAndValue[b].getType().equals(paramASN1ObjectIdentifier)) {
        attributeTypeAndValue = arrayOfAttributeTypeAndValue[b];
        break;
      } 
    } 
    return attributeTypeAndValue;
  }
  
  public boolean hasProofOfPossession() {
    return (this.certReqMsg.getPopo() != null);
  }
  
  public int getProofOfPossessionType() {
    return this.certReqMsg.getPopo().getType();
  }
  
  public boolean hasSigningKeyProofOfPossessionWithPKMAC() {
    ProofOfPossession proofOfPossession = this.certReqMsg.getPopo();
    if (proofOfPossession.getType() == 1) {
      POPOSigningKey pOPOSigningKey = POPOSigningKey.getInstance(proofOfPossession.getObject());
      return (pOPOSigningKey.getPoposkInput().getPublicKeyMAC() != null);
    } 
    return false;
  }
  
  public boolean isValidSigningKeyPOP(ContentVerifierProvider paramContentVerifierProvider) throws CRMFException, IllegalStateException {
    ProofOfPossession proofOfPossession = this.certReqMsg.getPopo();
    if (proofOfPossession.getType() == 1) {
      POPOSigningKey pOPOSigningKey = POPOSigningKey.getInstance(proofOfPossession.getObject());
      if (pOPOSigningKey.getPoposkInput() != null && pOPOSigningKey.getPoposkInput().getPublicKeyMAC() != null)
        throw new IllegalStateException("verification requires password check"); 
      return verifySignature(paramContentVerifierProvider, pOPOSigningKey);
    } 
    throw new IllegalStateException("not Signing Key type of proof of possession");
  }
  
  public boolean isValidSigningKeyPOP(ContentVerifierProvider paramContentVerifierProvider, PKMACBuilder paramPKMACBuilder, char[] paramArrayOfchar) throws CRMFException, IllegalStateException {
    ProofOfPossession proofOfPossession = this.certReqMsg.getPopo();
    if (proofOfPossession.getType() == 1) {
      POPOSigningKey pOPOSigningKey = POPOSigningKey.getInstance(proofOfPossession.getObject());
      if (pOPOSigningKey.getPoposkInput() == null || pOPOSigningKey.getPoposkInput().getSender() != null)
        throw new IllegalStateException("no PKMAC present in proof of possession"); 
      PKMACValue pKMACValue = pOPOSigningKey.getPoposkInput().getPublicKeyMAC();
      PKMACValueVerifier pKMACValueVerifier = new PKMACValueVerifier(paramPKMACBuilder);
      return pKMACValueVerifier.isValid(pKMACValue, paramArrayOfchar, getCertTemplate().getPublicKey()) ? verifySignature(paramContentVerifierProvider, pOPOSigningKey) : false;
    } 
    throw new IllegalStateException("not Signing Key type of proof of possession");
  }
  
  private boolean verifySignature(ContentVerifierProvider paramContentVerifierProvider, POPOSigningKey paramPOPOSigningKey) throws CRMFException {
    ContentVerifier contentVerifier;
    try {
      contentVerifier = paramContentVerifierProvider.get(paramPOPOSigningKey.getAlgorithmIdentifier());
    } catch (OperatorCreationException operatorCreationException) {
      throw new CRMFException("unable to create verifier: " + operatorCreationException.getMessage(), operatorCreationException);
    } 
    if (paramPOPOSigningKey.getPoposkInput() != null) {
      CRMFUtil.derEncodeToStream((ASN1Encodable)paramPOPOSigningKey.getPoposkInput(), contentVerifier.getOutputStream());
    } else {
      CRMFUtil.derEncodeToStream((ASN1Encodable)this.certReqMsg.getCertReq(), contentVerifier.getOutputStream());
    } 
    return contentVerifier.verify(paramPOPOSigningKey.getSignature().getOctets());
  }
  
  public byte[] getEncoded() throws IOException {
    return this.certReqMsg.getEncoded();
  }
}
