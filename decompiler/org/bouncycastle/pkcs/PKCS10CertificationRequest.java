package org.bouncycastle.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;

public class PKCS10CertificationRequest {
  private static Attribute[] EMPTY_ARRAY = new Attribute[0];
  
  private CertificationRequest certificationRequest;
  
  private static CertificationRequest parseBytes(byte[] paramArrayOfbyte) throws IOException {
    try {
      return CertificationRequest.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte));
    } catch (ClassCastException classCastException) {
      throw new PKCSIOException("malformed data: " + classCastException.getMessage(), classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new PKCSIOException("malformed data: " + illegalArgumentException.getMessage(), illegalArgumentException);
    } 
  }
  
  public PKCS10CertificationRequest(CertificationRequest paramCertificationRequest) {
    this.certificationRequest = paramCertificationRequest;
  }
  
  public PKCS10CertificationRequest(byte[] paramArrayOfbyte) throws IOException {
    this(parseBytes(paramArrayOfbyte));
  }
  
  public CertificationRequest toASN1Structure() {
    return this.certificationRequest;
  }
  
  public X500Name getSubject() {
    return X500Name.getInstance(this.certificationRequest.getCertificationRequestInfo().getSubject());
  }
  
  public AlgorithmIdentifier getSignatureAlgorithm() {
    return this.certificationRequest.getSignatureAlgorithm();
  }
  
  public byte[] getSignature() {
    return this.certificationRequest.getSignature().getOctets();
  }
  
  public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
    return this.certificationRequest.getCertificationRequestInfo().getSubjectPublicKeyInfo();
  }
  
  public Attribute[] getAttributes() {
    ASN1Set aSN1Set = this.certificationRequest.getCertificationRequestInfo().getAttributes();
    if (aSN1Set == null)
      return EMPTY_ARRAY; 
    Attribute[] arrayOfAttribute = new Attribute[aSN1Set.size()];
    for (byte b = 0; b != aSN1Set.size(); b++)
      arrayOfAttribute[b] = Attribute.getInstance(aSN1Set.getObjectAt(b)); 
    return arrayOfAttribute;
  }
  
  public Attribute[] getAttributes(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    ASN1Set aSN1Set = this.certificationRequest.getCertificationRequestInfo().getAttributes();
    if (aSN1Set == null)
      return EMPTY_ARRAY; 
    ArrayList<Attribute> arrayList = new ArrayList();
    for (byte b = 0; b != aSN1Set.size(); b++) {
      Attribute attribute = Attribute.getInstance(aSN1Set.getObjectAt(b));
      if (attribute.getAttrType().equals(paramASN1ObjectIdentifier))
        arrayList.add(attribute); 
    } 
    return (arrayList.size() == 0) ? EMPTY_ARRAY : arrayList.<Attribute>toArray(new Attribute[arrayList.size()]);
  }
  
  public byte[] getEncoded() throws IOException {
    return this.certificationRequest.getEncoded();
  }
  
  public boolean isSignatureValid(ContentVerifierProvider paramContentVerifierProvider) throws PKCSException {
    ContentVerifier contentVerifier;
    CertificationRequestInfo certificationRequestInfo = this.certificationRequest.getCertificationRequestInfo();
    try {
      contentVerifier = paramContentVerifierProvider.get(this.certificationRequest.getSignatureAlgorithm());
      OutputStream outputStream = contentVerifier.getOutputStream();
      outputStream.write(certificationRequestInfo.getEncoded("DER"));
      outputStream.close();
    } catch (Exception exception) {
      throw new PKCSException("unable to process signature: " + exception.getMessage(), exception);
    } 
    return contentVerifier.verify(getSignature());
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof PKCS10CertificationRequest))
      return false; 
    PKCS10CertificationRequest pKCS10CertificationRequest = (PKCS10CertificationRequest)paramObject;
    return toASN1Structure().equals(pKCS10CertificationRequest.toASN1Structure());
  }
  
  public int hashCode() {
    return toASN1Structure().hashCode();
  }
}
