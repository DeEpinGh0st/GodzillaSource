package org.bouncycastle.asn1.x509.qualified;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class QCStatement extends ASN1Object implements ETSIQCObjectIdentifiers, RFC3739QCObjectIdentifiers {
  ASN1ObjectIdentifier qcStatementId;
  
  ASN1Encodable qcStatementInfo;
  
  public static QCStatement getInstance(Object paramObject) {
    return (paramObject instanceof QCStatement) ? (QCStatement)paramObject : ((paramObject != null) ? new QCStatement(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private QCStatement(ASN1Sequence paramASN1Sequence) {
    Enumeration<ASN1Encodable> enumeration = paramASN1Sequence.getObjects();
    this.qcStatementId = ASN1ObjectIdentifier.getInstance(enumeration.nextElement());
    if (enumeration.hasMoreElements())
      this.qcStatementInfo = enumeration.nextElement(); 
  }
  
  public QCStatement(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.qcStatementId = paramASN1ObjectIdentifier;
    this.qcStatementInfo = null;
  }
  
  public QCStatement(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.qcStatementId = paramASN1ObjectIdentifier;
    this.qcStatementInfo = paramASN1Encodable;
  }
  
  public ASN1ObjectIdentifier getStatementId() {
    return this.qcStatementId;
  }
  
  public ASN1Encodable getStatementInfo() {
    return this.qcStatementInfo;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.qcStatementId);
    if (this.qcStatementInfo != null)
      aSN1EncodableVector.add(this.qcStatementInfo); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
