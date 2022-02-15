package org.bouncycastle.asn1.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.ASN1TaggedObjectParser;

public class SignedDataParser {
  private ASN1SequenceParser _seq;
  
  private ASN1Integer _version;
  
  private Object _nextObject;
  
  private boolean _certsCalled;
  
  private boolean _crlsCalled;
  
  public static SignedDataParser getInstance(Object paramObject) throws IOException {
    if (paramObject instanceof ASN1Sequence)
      return new SignedDataParser(((ASN1Sequence)paramObject).parser()); 
    if (paramObject instanceof ASN1SequenceParser)
      return new SignedDataParser((ASN1SequenceParser)paramObject); 
    throw new IOException("unknown object encountered: " + paramObject.getClass().getName());
  }
  
  private SignedDataParser(ASN1SequenceParser paramASN1SequenceParser) throws IOException {
    this._seq = paramASN1SequenceParser;
    this._version = (ASN1Integer)paramASN1SequenceParser.readObject();
  }
  
  public ASN1Integer getVersion() {
    return this._version;
  }
  
  public ASN1SetParser getDigestAlgorithms() throws IOException {
    ASN1Encodable aSN1Encodable = this._seq.readObject();
    return (aSN1Encodable instanceof ASN1Set) ? ((ASN1Set)aSN1Encodable).parser() : (ASN1SetParser)aSN1Encodable;
  }
  
  public ContentInfoParser getEncapContentInfo() throws IOException {
    return new ContentInfoParser((ASN1SequenceParser)this._seq.readObject());
  }
  
  public ASN1SetParser getCertificates() throws IOException {
    this._certsCalled = true;
    this._nextObject = this._seq.readObject();
    if (this._nextObject instanceof ASN1TaggedObjectParser && ((ASN1TaggedObjectParser)this._nextObject).getTagNo() == 0) {
      ASN1SetParser aSN1SetParser = (ASN1SetParser)((ASN1TaggedObjectParser)this._nextObject).getObjectParser(17, false);
      this._nextObject = null;
      return aSN1SetParser;
    } 
    return null;
  }
  
  public ASN1SetParser getCrls() throws IOException {
    if (!this._certsCalled)
      throw new IOException("getCerts() has not been called."); 
    this._crlsCalled = true;
    if (this._nextObject == null)
      this._nextObject = this._seq.readObject(); 
    if (this._nextObject instanceof ASN1TaggedObjectParser && ((ASN1TaggedObjectParser)this._nextObject).getTagNo() == 1) {
      ASN1SetParser aSN1SetParser = (ASN1SetParser)((ASN1TaggedObjectParser)this._nextObject).getObjectParser(17, false);
      this._nextObject = null;
      return aSN1SetParser;
    } 
    return null;
  }
  
  public ASN1SetParser getSignerInfos() throws IOException {
    if (!this._certsCalled || !this._crlsCalled)
      throw new IOException("getCerts() and/or getCrls() has not been called."); 
    if (this._nextObject == null)
      this._nextObject = this._seq.readObject(); 
    return (ASN1SetParser)this._nextObject;
  }
}
