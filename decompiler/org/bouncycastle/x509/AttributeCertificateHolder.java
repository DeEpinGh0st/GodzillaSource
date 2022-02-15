package org.bouncycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.cert.CertSelector;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.Holder;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.x509.ObjectDigestInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class AttributeCertificateHolder implements CertSelector, Selector {
  final Holder holder;
  
  AttributeCertificateHolder(ASN1Sequence paramASN1Sequence) {
    this.holder = Holder.getInstance(paramASN1Sequence);
  }
  
  public AttributeCertificateHolder(X509Principal paramX509Principal, BigInteger paramBigInteger) {
    this.holder = new Holder(new IssuerSerial(GeneralNames.getInstance(new DERSequence((ASN1Encodable)new GeneralName((X509Name)paramX509Principal))), new ASN1Integer(paramBigInteger)));
  }
  
  public AttributeCertificateHolder(X500Principal paramX500Principal, BigInteger paramBigInteger) {
    this(X509Util.convertPrincipal(paramX500Principal), paramBigInteger);
  }
  
  public AttributeCertificateHolder(X509Certificate paramX509Certificate) throws CertificateParsingException {
    X509Principal x509Principal;
    try {
      x509Principal = PrincipalUtil.getIssuerX509Principal(paramX509Certificate);
    } catch (Exception exception) {
      throw new CertificateParsingException(exception.getMessage());
    } 
    this.holder = new Holder(new IssuerSerial(generateGeneralNames(x509Principal), new ASN1Integer(paramX509Certificate.getSerialNumber())));
  }
  
  public AttributeCertificateHolder(X509Principal paramX509Principal) {
    this.holder = new Holder(generateGeneralNames(paramX509Principal));
  }
  
  public AttributeCertificateHolder(X500Principal paramX500Principal) {
    this(X509Util.convertPrincipal(paramX500Principal));
  }
  
  public AttributeCertificateHolder(int paramInt, String paramString1, String paramString2, byte[] paramArrayOfbyte) {
    this.holder = new Holder(new ObjectDigestInfo(paramInt, new ASN1ObjectIdentifier(paramString2), new AlgorithmIdentifier(new ASN1ObjectIdentifier(paramString1)), Arrays.clone(paramArrayOfbyte)));
  }
  
  public int getDigestedObjectType() {
    return (this.holder.getObjectDigestInfo() != null) ? this.holder.getObjectDigestInfo().getDigestedObjectType().getValue().intValue() : -1;
  }
  
  public String getDigestAlgorithm() {
    return (this.holder.getObjectDigestInfo() != null) ? this.holder.getObjectDigestInfo().getDigestAlgorithm().getAlgorithm().getId() : null;
  }
  
  public byte[] getObjectDigest() {
    return (this.holder.getObjectDigestInfo() != null) ? this.holder.getObjectDigestInfo().getObjectDigest().getBytes() : null;
  }
  
  public String getOtherObjectTypeID() {
    if (this.holder.getObjectDigestInfo() != null)
      this.holder.getObjectDigestInfo().getOtherObjectTypeID().getId(); 
    return null;
  }
  
  private GeneralNames generateGeneralNames(X509Principal paramX509Principal) {
    return GeneralNames.getInstance(new DERSequence((ASN1Encodable)new GeneralName((X509Name)paramX509Principal)));
  }
  
  private boolean matchesDN(X509Principal paramX509Principal, GeneralNames paramGeneralNames) {
    GeneralName[] arrayOfGeneralName = paramGeneralNames.getNames();
    for (byte b = 0; b != arrayOfGeneralName.length; b++) {
      GeneralName generalName = arrayOfGeneralName[b];
      if (generalName.getTagNo() == 4)
        try {
          if ((new X509Principal(generalName.getName().toASN1Primitive().getEncoded())).equals(paramX509Principal))
            return true; 
        } catch (IOException iOException) {} 
    } 
    return false;
  }
  
  private Object[] getNames(GeneralName[] paramArrayOfGeneralName) {
    ArrayList<X500Principal> arrayList = new ArrayList(paramArrayOfGeneralName.length);
    for (byte b = 0; b != paramArrayOfGeneralName.length; b++) {
      if (paramArrayOfGeneralName[b].getTagNo() == 4)
        try {
          arrayList.add(new X500Principal(paramArrayOfGeneralName[b].getName().toASN1Primitive().getEncoded()));
        } catch (IOException iOException) {
          throw new RuntimeException("badly formed Name object");
        }  
    } 
    return arrayList.toArray(new Object[arrayList.size()]);
  }
  
  private Principal[] getPrincipals(GeneralNames paramGeneralNames) {
    Object[] arrayOfObject = getNames(paramGeneralNames.getNames());
    ArrayList<Object> arrayList = new ArrayList();
    for (byte b = 0; b != arrayOfObject.length; b++) {
      if (arrayOfObject[b] instanceof Principal)
        arrayList.add(arrayOfObject[b]); 
    } 
    return arrayList.<Principal>toArray(new Principal[arrayList.size()]);
  }
  
  public Principal[] getEntityNames() {
    return (this.holder.getEntityName() != null) ? getPrincipals(this.holder.getEntityName()) : null;
  }
  
  public Principal[] getIssuer() {
    return (this.holder.getBaseCertificateID() != null) ? getPrincipals(this.holder.getBaseCertificateID().getIssuer()) : null;
  }
  
  public BigInteger getSerialNumber() {
    return (this.holder.getBaseCertificateID() != null) ? this.holder.getBaseCertificateID().getSerial().getValue() : null;
  }
  
  public Object clone() {
    return new AttributeCertificateHolder((ASN1Sequence)this.holder.toASN1Primitive());
  }
  
  public boolean match(Certificate paramCertificate) {
    if (!(paramCertificate instanceof X509Certificate))
      return false; 
    X509Certificate x509Certificate = (X509Certificate)paramCertificate;
    try {
      if (this.holder.getBaseCertificateID() != null)
        return (this.holder.getBaseCertificateID().getSerial().getValue().equals(x509Certificate.getSerialNumber()) && matchesDN(PrincipalUtil.getIssuerX509Principal(x509Certificate), this.holder.getBaseCertificateID().getIssuer())); 
      if (this.holder.getEntityName() != null && matchesDN(PrincipalUtil.getSubjectX509Principal(x509Certificate), this.holder.getEntityName()))
        return true; 
      if (this.holder.getObjectDigestInfo() != null) {
        MessageDigest messageDigest = null;
        try {
          messageDigest = MessageDigest.getInstance(getDigestAlgorithm(), "BC");
        } catch (Exception exception) {
          return false;
        } 
        switch (getDigestedObjectType()) {
          case 0:
            messageDigest.update(paramCertificate.getPublicKey().getEncoded());
            break;
          case 1:
            messageDigest.update(paramCertificate.getEncoded());
            break;
        } 
        if (!Arrays.areEqual(messageDigest.digest(), getObjectDigest()))
          return false; 
      } 
    } catch (CertificateEncodingException certificateEncodingException) {
      return false;
    } 
    return false;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof AttributeCertificateHolder))
      return false; 
    AttributeCertificateHolder attributeCertificateHolder = (AttributeCertificateHolder)paramObject;
    return this.holder.equals(attributeCertificateHolder.holder);
  }
  
  public int hashCode() {
    return this.holder.hashCode();
  }
  
  public boolean match(Object paramObject) {
    return !(paramObject instanceof X509Certificate) ? false : match((Certificate)paramObject);
  }
}
