package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSequenceGenerator;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.SignerInfo;

public class CMSSignedDataStreamGenerator extends CMSSignedGenerator {
  private int _bufferSize;
  
  public void setBufferSize(int paramInt) {
    this._bufferSize = paramInt;
  }
  
  public OutputStream open(OutputStream paramOutputStream) throws IOException {
    return open(paramOutputStream, false);
  }
  
  public OutputStream open(OutputStream paramOutputStream, boolean paramBoolean) throws IOException {
    return open(CMSObjectIdentifiers.data, paramOutputStream, paramBoolean);
  }
  
  public OutputStream open(OutputStream paramOutputStream1, boolean paramBoolean, OutputStream paramOutputStream2) throws IOException {
    return open(CMSObjectIdentifiers.data, paramOutputStream1, paramBoolean, paramOutputStream2);
  }
  
  public OutputStream open(ASN1ObjectIdentifier paramASN1ObjectIdentifier, OutputStream paramOutputStream, boolean paramBoolean) throws IOException {
    return open(paramASN1ObjectIdentifier, paramOutputStream, paramBoolean, (OutputStream)null);
  }
  
  public OutputStream open(ASN1ObjectIdentifier paramASN1ObjectIdentifier, OutputStream paramOutputStream1, boolean paramBoolean, OutputStream paramOutputStream2) throws IOException {
    BERSequenceGenerator bERSequenceGenerator1 = new BERSequenceGenerator(paramOutputStream1);
    bERSequenceGenerator1.addObject((ASN1Encodable)CMSObjectIdentifiers.signedData);
    BERSequenceGenerator bERSequenceGenerator2 = new BERSequenceGenerator(bERSequenceGenerator1.getRawOutputStream(), 0, true);
    bERSequenceGenerator2.addObject((ASN1Encodable)calculateVersion(paramASN1ObjectIdentifier));
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (SignerInformation signerInformation : this._signers)
      aSN1EncodableVector.add((ASN1Encodable)CMSSignedHelper.INSTANCE.fixAlgID(signerInformation.getDigestAlgorithmID())); 
    for (SignerInfoGenerator signerInfoGenerator : this.signerGens)
      aSN1EncodableVector.add((ASN1Encodable)signerInfoGenerator.getDigestAlgorithm()); 
    bERSequenceGenerator2.getRawOutputStream().write((new DERSet(aSN1EncodableVector)).getEncoded());
    BERSequenceGenerator bERSequenceGenerator3 = new BERSequenceGenerator(bERSequenceGenerator2.getRawOutputStream());
    bERSequenceGenerator3.addObject((ASN1Encodable)paramASN1ObjectIdentifier);
    OutputStream outputStream1 = paramBoolean ? CMSUtils.createBEROctetOutputStream(bERSequenceGenerator3.getRawOutputStream(), 0, true, this._bufferSize) : null;
    OutputStream outputStream2 = CMSUtils.getSafeTeeOutputStream(paramOutputStream2, outputStream1);
    OutputStream outputStream3 = CMSUtils.attachSignersToOutputStream(this.signerGens, outputStream2);
    return new CmsSignedDataOutputStream(outputStream3, paramASN1ObjectIdentifier, bERSequenceGenerator1, bERSequenceGenerator2, bERSequenceGenerator3);
  }
  
  private ASN1Integer calculateVersion(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    if (this.certs != null)
      for (ASN1TaggedObject aSN1TaggedObject : this.certs) {
        if (aSN1TaggedObject instanceof ASN1TaggedObject) {
          ASN1TaggedObject aSN1TaggedObject1 = aSN1TaggedObject;
          if (aSN1TaggedObject1.getTagNo() == 1) {
            bool3 = true;
            continue;
          } 
          if (aSN1TaggedObject1.getTagNo() == 2) {
            bool4 = true;
            continue;
          } 
          if (aSN1TaggedObject1.getTagNo() == 3)
            bool1 = true; 
        } 
      }  
    if (bool1)
      return new ASN1Integer(5L); 
    if (this.crls != null)
      for (Object object : this.crls) {
        if (object instanceof ASN1TaggedObject)
          bool2 = true; 
      }  
    return bool2 ? new ASN1Integer(5L) : (bool4 ? new ASN1Integer(4L) : (bool3 ? new ASN1Integer(3L) : (checkForVersion3(this._signers, this.signerGens) ? new ASN1Integer(3L) : (!CMSObjectIdentifiers.data.equals(paramASN1ObjectIdentifier) ? new ASN1Integer(3L) : new ASN1Integer(1L)))));
  }
  
  private boolean checkForVersion3(List paramList1, List<SignerInformation> paramList2) {
    null = paramList1.iterator();
    while (null.hasNext()) {
      SignerInfo signerInfo = SignerInfo.getInstance(((SignerInformation)null.next()).toASN1Structure());
      if (signerInfo.getVersion().getValue().intValue() == 3)
        return true; 
    } 
    for (SignerInfoGenerator signerInfoGenerator : paramList2) {
      if (signerInfoGenerator.getGeneratedVersion() == 3)
        return true; 
    } 
    return false;
  }
  
  private class CmsSignedDataOutputStream extends OutputStream {
    private OutputStream _out;
    
    private ASN1ObjectIdentifier _contentOID;
    
    private BERSequenceGenerator _sGen;
    
    private BERSequenceGenerator _sigGen;
    
    private BERSequenceGenerator _eiGen;
    
    public CmsSignedDataOutputStream(OutputStream param1OutputStream, ASN1ObjectIdentifier param1ASN1ObjectIdentifier, BERSequenceGenerator param1BERSequenceGenerator1, BERSequenceGenerator param1BERSequenceGenerator2, BERSequenceGenerator param1BERSequenceGenerator3) {
      this._out = param1OutputStream;
      this._contentOID = param1ASN1ObjectIdentifier;
      this._sGen = param1BERSequenceGenerator1;
      this._sigGen = param1BERSequenceGenerator2;
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
      CMSSignedDataStreamGenerator.this.digests.clear();
      if (CMSSignedDataStreamGenerator.this.certs.size() != 0) {
        ASN1Set aSN1Set = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.certs);
        this._sigGen.getRawOutputStream().write((new BERTaggedObject(false, 0, (ASN1Encodable)aSN1Set)).getEncoded());
      } 
      if (CMSSignedDataStreamGenerator.this.crls.size() != 0) {
        ASN1Set aSN1Set = CMSUtils.createBerSetFromList(CMSSignedDataStreamGenerator.this.crls);
        this._sigGen.getRawOutputStream().write((new BERTaggedObject(false, 1, (ASN1Encodable)aSN1Set)).getEncoded());
      } 
      ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
      for (SignerInfoGenerator signerInfoGenerator : CMSSignedDataStreamGenerator.this.signerGens) {
        try {
          aSN1EncodableVector.add((ASN1Encodable)signerInfoGenerator.generate(this._contentOID));
          byte[] arrayOfByte = signerInfoGenerator.getCalculatedDigest();
          CMSSignedDataStreamGenerator.this.digests.put(signerInfoGenerator.getDigestAlgorithm().getAlgorithm().getId(), arrayOfByte);
        } catch (CMSException cMSException) {
          throw new CMSStreamException("exception generating signers: " + cMSException.getMessage(), cMSException);
        } 
      } 
      for (SignerInformation signerInformation : CMSSignedDataStreamGenerator.this._signers)
        aSN1EncodableVector.add((ASN1Encodable)signerInformation.toASN1Structure()); 
      this._sigGen.getRawOutputStream().write((new DERSet(aSN1EncodableVector)).getEncoded());
      this._sigGen.close();
      this._sGen.close();
    }
  }
}
