package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.pkcs.AuthenticatedSafe;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.cms.CMSEncryptedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.operator.OutputEncryptor;

public class PKCS12PfxPduBuilder {
  private ASN1EncodableVector dataVector = new ASN1EncodableVector();
  
  public PKCS12PfxPduBuilder addData(PKCS12SafeBag paramPKCS12SafeBag) throws IOException {
    this.dataVector.add((ASN1Encodable)new ContentInfo(PKCSObjectIdentifiers.data, (ASN1Encodable)new DEROctetString((new DLSequence((ASN1Encodable)paramPKCS12SafeBag.toASN1Structure())).getEncoded())));
    return this;
  }
  
  public PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor paramOutputEncryptor, PKCS12SafeBag paramPKCS12SafeBag) throws IOException {
    return addEncryptedData(paramOutputEncryptor, (ASN1Sequence)new DERSequence((ASN1Encodable)paramPKCS12SafeBag.toASN1Structure()));
  }
  
  public PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor paramOutputEncryptor, PKCS12SafeBag[] paramArrayOfPKCS12SafeBag) throws IOException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (byte b = 0; b != paramArrayOfPKCS12SafeBag.length; b++)
      aSN1EncodableVector.add((ASN1Encodable)paramArrayOfPKCS12SafeBag[b].toASN1Structure()); 
    return addEncryptedData(paramOutputEncryptor, (ASN1Sequence)new DLSequence(aSN1EncodableVector));
  }
  
  private PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor paramOutputEncryptor, ASN1Sequence paramASN1Sequence) throws IOException {
    CMSEncryptedDataGenerator cMSEncryptedDataGenerator = new CMSEncryptedDataGenerator();
    try {
      this.dataVector.add((ASN1Encodable)cMSEncryptedDataGenerator.generate((CMSTypedData)new CMSProcessableByteArray(paramASN1Sequence.getEncoded()), paramOutputEncryptor).toASN1Structure());
    } catch (CMSException cMSException) {
      throw new PKCSIOException(cMSException.getMessage(), cMSException.getCause());
    } 
    return this;
  }
  
  public PKCS12PfxPdu build(PKCS12MacCalculatorBuilder paramPKCS12MacCalculatorBuilder, char[] paramArrayOfchar) throws PKCSException {
    byte[] arrayOfByte;
    AuthenticatedSafe authenticatedSafe = AuthenticatedSafe.getInstance(new DLSequence(this.dataVector));
    try {
      arrayOfByte = authenticatedSafe.getEncoded();
    } catch (IOException iOException) {
      throw new PKCSException("unable to encode AuthenticatedSafe: " + iOException.getMessage(), iOException);
    } 
    ContentInfo contentInfo = new ContentInfo(PKCSObjectIdentifiers.data, (ASN1Encodable)new DEROctetString(arrayOfByte));
    MacData macData = null;
    if (paramPKCS12MacCalculatorBuilder != null) {
      MacDataGenerator macDataGenerator = new MacDataGenerator(paramPKCS12MacCalculatorBuilder);
      macData = macDataGenerator.build(paramArrayOfchar, arrayOfByte);
    } 
    Pfx pfx = new Pfx(contentInfo, macData);
    return new PKCS12PfxPdu(pfx);
  }
}
