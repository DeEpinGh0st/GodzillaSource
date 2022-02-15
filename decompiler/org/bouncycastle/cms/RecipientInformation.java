package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.Streams;

public abstract class RecipientInformation {
  protected RecipientId rid;
  
  protected AlgorithmIdentifier keyEncAlg;
  
  protected AlgorithmIdentifier messageAlgorithm;
  
  protected CMSSecureReadable secureReadable;
  
  private AuthAttributesProvider additionalData;
  
  private byte[] resultMac;
  
  private RecipientOperator operator;
  
  RecipientInformation(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2, CMSSecureReadable paramCMSSecureReadable, AuthAttributesProvider paramAuthAttributesProvider) {
    this.keyEncAlg = paramAlgorithmIdentifier1;
    this.messageAlgorithm = paramAlgorithmIdentifier2;
    this.secureReadable = paramCMSSecureReadable;
    this.additionalData = paramAuthAttributesProvider;
  }
  
  public RecipientId getRID() {
    return this.rid;
  }
  
  private byte[] encodeObj(ASN1Encodable paramASN1Encodable) throws IOException {
    return (paramASN1Encodable != null) ? paramASN1Encodable.toASN1Primitive().getEncoded() : null;
  }
  
  public AlgorithmIdentifier getKeyEncryptionAlgorithm() {
    return this.keyEncAlg;
  }
  
  public String getKeyEncryptionAlgOID() {
    return this.keyEncAlg.getAlgorithm().getId();
  }
  
  public byte[] getKeyEncryptionAlgParams() {
    try {
      return encodeObj(this.keyEncAlg.getParameters());
    } catch (Exception exception) {
      throw new RuntimeException("exception getting encryption parameters " + exception);
    } 
  }
  
  public byte[] getContentDigest() {
    return (this.secureReadable instanceof CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable) ? ((CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable)this.secureReadable).getDigest() : null;
  }
  
  public byte[] getMac() {
    if (this.resultMac == null && this.operator.isMacBased()) {
      if (this.additionalData != null)
        try {
          Streams.drain(this.operator.getInputStream(new ByteArrayInputStream(this.additionalData.getAuthAttributes().getEncoded("DER"))));
        } catch (IOException iOException) {
          throw new IllegalStateException("unable to drain input: " + iOException.getMessage());
        }  
      this.resultMac = this.operator.getMac();
    } 
    return this.resultMac;
  }
  
  public byte[] getContent(Recipient paramRecipient) throws CMSException {
    try {
      return CMSUtils.streamToByteArray(getContentStream(paramRecipient).getContentStream());
    } catch (IOException iOException) {
      throw new CMSException("unable to parse internal stream: " + iOException.getMessage(), iOException);
    } 
  }
  
  public CMSTypedStream getContentStream(Recipient paramRecipient) throws CMSException, IOException {
    this.operator = getRecipientOperator(paramRecipient);
    return (this.additionalData != null) ? new CMSTypedStream(this.secureReadable.getInputStream()) : new CMSTypedStream(this.operator.getInputStream(this.secureReadable.getInputStream()));
  }
  
  protected abstract RecipientOperator getRecipientOperator(Recipient paramRecipient) throws CMSException, IOException;
}
