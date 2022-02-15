package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.cms.CMSEncryptedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.InputDecryptorProvider;

public class PKCS12SafeBagFactory {
  private ASN1Sequence safeBagSeq;
  
  public PKCS12SafeBagFactory(ContentInfo paramContentInfo) {
    if (paramContentInfo.getContentType().equals(PKCSObjectIdentifiers.encryptedData))
      throw new IllegalArgumentException("encryptedData requires constructor with decryptor."); 
    this.safeBagSeq = ASN1Sequence.getInstance(ASN1OctetString.getInstance(paramContentInfo.getContent()).getOctets());
  }
  
  public PKCS12SafeBagFactory(ContentInfo paramContentInfo, InputDecryptorProvider paramInputDecryptorProvider) throws PKCSException {
    if (paramContentInfo.getContentType().equals(PKCSObjectIdentifiers.encryptedData)) {
      CMSEncryptedData cMSEncryptedData = new CMSEncryptedData(ContentInfo.getInstance(paramContentInfo));
      try {
        this.safeBagSeq = ASN1Sequence.getInstance(cMSEncryptedData.getContent(paramInputDecryptorProvider));
      } catch (CMSException cMSException) {
        throw new PKCSException("unable to extract data: " + cMSException.getMessage(), cMSException);
      } 
      return;
    } 
    throw new IllegalArgumentException("encryptedData requires constructor with decryptor.");
  }
  
  public PKCS12SafeBag[] getSafeBags() {
    PKCS12SafeBag[] arrayOfPKCS12SafeBag = new PKCS12SafeBag[this.safeBagSeq.size()];
    for (byte b = 0; b != this.safeBagSeq.size(); b++)
      arrayOfPKCS12SafeBag[b] = new PKCS12SafeBag(SafeBag.getInstance(this.safeBagSeq.getObjectAt(b))); 
    return arrayOfPKCS12SafeBag;
  }
}
