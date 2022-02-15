package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CRLBag;
import org.bouncycastle.asn1.pkcs.CertBag;
import org.bouncycastle.asn1.pkcs.EncryptedPrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;

public class PKCS12SafeBag {
  public static final ASN1ObjectIdentifier friendlyNameAttribute = PKCSObjectIdentifiers.pkcs_9_at_friendlyName;
  
  public static final ASN1ObjectIdentifier localKeyIdAttribute = PKCSObjectIdentifiers.pkcs_9_at_localKeyId;
  
  private SafeBag safeBag;
  
  public PKCS12SafeBag(SafeBag paramSafeBag) {
    this.safeBag = paramSafeBag;
  }
  
  public SafeBag toASN1Structure() {
    return this.safeBag;
  }
  
  public ASN1ObjectIdentifier getType() {
    return this.safeBag.getBagId();
  }
  
  public Attribute[] getAttributes() {
    ASN1Set aSN1Set = this.safeBag.getBagAttributes();
    if (aSN1Set == null)
      return null; 
    Attribute[] arrayOfAttribute = new Attribute[aSN1Set.size()];
    for (byte b = 0; b != aSN1Set.size(); b++)
      arrayOfAttribute[b] = Attribute.getInstance(aSN1Set.getObjectAt(b)); 
    return arrayOfAttribute;
  }
  
  public Object getBagValue() {
    if (getType().equals(PKCSObjectIdentifiers.pkcs8ShroudedKeyBag))
      return new PKCS8EncryptedPrivateKeyInfo(EncryptedPrivateKeyInfo.getInstance(this.safeBag.getBagValue())); 
    if (getType().equals(PKCSObjectIdentifiers.certBag)) {
      CertBag certBag = CertBag.getInstance(this.safeBag.getBagValue());
      return new X509CertificateHolder(Certificate.getInstance(ASN1OctetString.getInstance(certBag.getCertValue()).getOctets()));
    } 
    if (getType().equals(PKCSObjectIdentifiers.keyBag))
      return PrivateKeyInfo.getInstance(this.safeBag.getBagValue()); 
    if (getType().equals(PKCSObjectIdentifiers.crlBag)) {
      CRLBag cRLBag = CRLBag.getInstance(this.safeBag.getBagValue());
      return new X509CRLHolder(CertificateList.getInstance(ASN1OctetString.getInstance(cRLBag.getCrlValue()).getOctets()));
    } 
    return this.safeBag.getBagValue();
  }
}
