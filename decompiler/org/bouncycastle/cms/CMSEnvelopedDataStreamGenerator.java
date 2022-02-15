package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OutputEncryptor;

public class CMSEnvelopedDataStreamGenerator extends CMSEnvelopedGenerator {
  private ASN1Set _unprotectedAttributes = null;
  
  private int _bufferSize;
  
  private boolean _berEncodeRecipientSet;
  
  public void setBufferSize(int paramInt) {
    this._bufferSize = paramInt;
  }
  
  public void setBEREncodeRecipients(boolean paramBoolean) {
    this._berEncodeRecipientSet = paramBoolean;
  }
  
  private ASN1Integer getVersion() {
    return (this.originatorInfo != null || this._unprotectedAttributes != null) ? new ASN1Integer(2L) : new ASN1Integer(0L);
  }
  
  private OutputStream doOpen(ASN1ObjectIdentifier paramASN1ObjectIdentifier, OutputStream paramOutputStream, OutputEncryptor paramOutputEncryptor) throws IOException, CMSException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    GenericKey genericKey = paramOutputEncryptor.getKey();
    for (RecipientInfoGenerator recipientInfoGenerator : this.recipientInfoGenerators)
      aSN1EncodableVector.add((ASN1Encodable)recipientInfoGenerator.generate(genericKey)); 
    return open(paramASN1ObjectIdentifier, paramOutputStream, aSN1EncodableVector, paramOutputEncryptor);
  }
  
  protected OutputStream open(ASN1ObjectIdentifier paramASN1ObjectIdentifier, OutputStream paramOutputStream, ASN1EncodableVector paramASN1EncodableVector, OutputEncryptor paramOutputEncryptor) throws IOException {
    BERSequenceGenerator bERSequenceGenerator1 = new BERSequenceGenerator(paramOutputStream);
    bERSequenceGenerator1.addObject((ASN1Encodable)CMSObjectIdentifiers.envelopedData);
    BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator1.getRawOutputStream(), 0, true);
    bERSequenceGenerator2.addObject((ASN1Encodable)getVersion());
    if (this.originatorInfo != null)
      bERSequenceGenerator2.addObject((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo)); 
    if (this._berEncodeRecipientSet) {
      bERSequenceGenerator2.getRawOutputStream().write((new BERSet(paramASN1EncodableVector)).getEncoded());
    } else {
      bERSequenceGenerator2.getRawOutputStream().write((new DERSet(paramASN1EncodableVector)).getEncoded());
    } 
    BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
    bERSequenceGenerator3.addObject((ASN1Encodable)paramASN1ObjectIdentifier);
    AlgorithmIdentifier algorithmIdentifier = paramOutputEncryptor.getAlgorithmIdentifier();
    bERSequenceGenerator3.getRawOutputStream().write(algorithmIdentifier.getEncoded());
    OutputStream outputStream1 = CMSUtils.createBEROctetOutputStream(bERSequenceGenerator3.getRawOutputStream(), 0, false, this._bufferSize);
    OutputStream outputStream2 = paramOutputEncryptor.getOutputStream(outputStream1);
    return new CmsEnvelopedDataOutputStream(outputStream2, bERSequenceGenerator1, bERSequenceGenerator2, bERSequenceGenerator3);
  }
  
  protected OutputStream open(OutputStream paramOutputStream, ASN1EncodableVector paramASN1EncodableVector, OutputEncryptor paramOutputEncryptor) throws CMSException {
    try {
      DERSet dERSet;
      BERSequenceGenerator bERSequenceGenerator1 = new BERSequenceGenerator(paramOutputStream);
      bERSequenceGenerator1.addObject((ASN1Encodable)CMSObjectIdentifiers.envelopedData);
      BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator1.getRawOutputStream(), 0, true);
      if (this._berEncodeRecipientSet) {
        BERSet bERSet = new BERSet(paramASN1EncodableVector);
      } else {
        dERSet = new DERSet(paramASN1EncodableVector);
      } 
      bERSequenceGenerator2.addObject((ASN1Encodable)new ASN1Integer(EnvelopedData.calculateVersion(this.originatorInfo, (ASN1Set)dERSet, this._unprotectedAttributes)));
      if (this.originatorInfo != null)
        bERSequenceGenerator2.addObject((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.originatorInfo)); 
      bERSequenceGenerator2.getRawOutputStream().write(dERSet.getEncoded());
      BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
      bERSequenceGenerator3.addObject((ASN1Encodable)CMSObjectIdentifiers.data);
      AlgorithmIdentifier algorithmIdentifier = paramOutputEncryptor.getAlgorithmIdentifier();
      bERSequenceGenerator3.getRawOutputStream().write(algorithmIdentifier.getEncoded());
      OutputStream outputStream = CMSUtils.createBEROctetOutputStream(bERSequenceGenerator3.getRawOutputStream(), 0, false, this._bufferSize);
      return new CmsEnvelopedDataOutputStream(paramOutputEncryptor.getOutputStream(outputStream), bERSequenceGenerator1, bERSequenceGenerator2, bERSequenceGenerator3);
    } catch (IOException iOException) {
      throw new CMSException("exception decoding algorithm parameters.", iOException);
    } 
  }
  
  public OutputStream open(OutputStream paramOutputStream, OutputEncryptor paramOutputEncryptor) throws CMSException, IOException {
    return doOpen(new ASN1ObjectIdentifier(CMSObjectIdentifiers.data.getId()), paramOutputStream, paramOutputEncryptor);
  }
  
  public OutputStream open(ASN1ObjectIdentifier paramASN1ObjectIdentifier, OutputStream paramOutputStream, OutputEncryptor paramOutputEncryptor) throws CMSException, IOException {
    return doOpen(paramASN1ObjectIdentifier, paramOutputStream, paramOutputEncryptor);
  }
  
  private class CmsEnvelopedDataOutputStream extends OutputStream {
    private OutputStream _out;
    
    private BERSequenceGenerator _cGen;
    
    private BERSequenceGenerator _envGen;
    
    private BERSequenceGenerator _eiGen;
    
    public CmsEnvelopedDataOutputStream(OutputStream param1OutputStream, BERSequenceGenerator param1BERSequenceGenerator1, BERSequenceGenerator param1BERSequenceGenerator2, BERSequenceGenerator param1BERSequenceGenerator3) {
      this._out = param1OutputStream;
      this._cGen = param1BERSequenceGenerator1;
      this._envGen = param1BERSequenceGenerator2;
      this._eiGen = param1BERSequenceGenerator3;
    }
    
    public void write(int param1Int) throws IOException {
      this._out.write(param1Int);
    }
    
    public void write(byte[] param1ArrayOfbyte, int param1Int1, int param1Int2) throws IOException {
      this._out.write(param1ArrayOfbyte, param1Int1, param1Int2);
    }
    
    public void write(byte[] param1ArrayOfbyte) throws IOException {
      this._out.write(param1ArrayOfbyte);
    }
    
    public void close() throws IOException {
      this._out.close();
      this._eiGen.close();
      if (CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator != null) {
        AttributeTable attributeTable = CMSEnvelopedDataStreamGenerator.this.unprotectedAttributeGenerator.getAttributes(new HashMap<Object, Object>());
        BERSet bERSet = new BERSet(attributeTable.toASN1EncodableVector());
        this._envGen.addObject((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)bERSet));
      } 
      this._envGen.close();
      this._cGen.close();
    }
  }
}
