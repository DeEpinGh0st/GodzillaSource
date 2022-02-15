package org.bouncycastle.openssl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.util.io.pem.PemHeader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

public class MiscPEMGenerator implements PemObjectGenerator {
  private static final ASN1ObjectIdentifier[] dsaOids = new ASN1ObjectIdentifier[] { X9ObjectIdentifiers.id_dsa, OIWObjectIdentifiers.dsaWithSHA1 };
  
  private static final byte[] hexEncodingTable = new byte[] { 
      48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 
      65, 66, 67, 68, 69, 70 };
  
  private final Object obj;
  
  private final PEMEncryptor encryptor;
  
  public MiscPEMGenerator(Object paramObject) {
    this.obj = paramObject;
    this.encryptor = null;
  }
  
  public MiscPEMGenerator(Object paramObject, PEMEncryptor paramPEMEncryptor) {
    this.obj = paramObject;
    this.encryptor = paramPEMEncryptor;
  }
  
  private PemObject createPemObject(Object paramObject) throws IOException {
    String str;
    byte[] arrayOfByte;
    if (paramObject instanceof PemObject)
      return (PemObject)paramObject; 
    if (paramObject instanceof PemObjectGenerator)
      return ((PemObjectGenerator)paramObject).generate(); 
    if (paramObject instanceof X509CertificateHolder) {
      str = "CERTIFICATE";
      arrayOfByte = ((X509CertificateHolder)paramObject).getEncoded();
    } else if (paramObject instanceof X509CRLHolder) {
      str = "X509 CRL";
      arrayOfByte = ((X509CRLHolder)paramObject).getEncoded();
    } else if (paramObject instanceof X509TrustedCertificateBlock) {
      str = "TRUSTED CERTIFICATE";
      arrayOfByte = ((X509TrustedCertificateBlock)paramObject).getEncoded();
    } else if (paramObject instanceof PrivateKeyInfo) {
      PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo)paramObject;
      ASN1ObjectIdentifier aSN1ObjectIdentifier = privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm();
      if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.rsaEncryption)) {
        str = "RSA PRIVATE KEY";
        arrayOfByte = privateKeyInfo.parsePrivateKey().toASN1Primitive().getEncoded();
      } else if (aSN1ObjectIdentifier.equals(dsaOids[0]) || aSN1ObjectIdentifier.equals(dsaOids[1])) {
        str = "DSA PRIVATE KEY";
        DSAParameter dSAParameter = DSAParameter.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(0L));
        aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(dSAParameter.getP()));
        aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(dSAParameter.getQ()));
        aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(dSAParameter.getG()));
        BigInteger bigInteger1 = ASN1Integer.getInstance(privateKeyInfo.parsePrivateKey()).getValue();
        BigInteger bigInteger2 = dSAParameter.getG().modPow(bigInteger1, dSAParameter.getP());
        aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(bigInteger2));
        aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(bigInteger1));
        arrayOfByte = (new DERSequence(aSN1EncodableVector)).getEncoded();
      } else if (aSN1ObjectIdentifier.equals(X9ObjectIdentifiers.id_ecPublicKey)) {
        str = "EC PRIVATE KEY";
        arrayOfByte = privateKeyInfo.parsePrivateKey().toASN1Primitive().getEncoded();
      } else {
        throw new IOException("Cannot identify private key");
      } 
    } else if (paramObject instanceof SubjectPublicKeyInfo) {
      str = "PUBLIC KEY";
      arrayOfByte = ((SubjectPublicKeyInfo)paramObject).getEncoded();
    } else if (paramObject instanceof X509AttributeCertificateHolder) {
      str = "ATTRIBUTE CERTIFICATE";
      arrayOfByte = ((X509AttributeCertificateHolder)paramObject).getEncoded();
    } else if (paramObject instanceof PKCS10CertificationRequest) {
      str = "CERTIFICATE REQUEST";
      arrayOfByte = ((PKCS10CertificationRequest)paramObject).getEncoded();
    } else if (paramObject instanceof PKCS8EncryptedPrivateKeyInfo) {
      str = "ENCRYPTED PRIVATE KEY";
      arrayOfByte = ((PKCS8EncryptedPrivateKeyInfo)paramObject).getEncoded();
    } else if (paramObject instanceof ContentInfo) {
      str = "PKCS7";
      arrayOfByte = ((ContentInfo)paramObject).getEncoded();
    } else {
      throw new PemGenerationException("unknown object passed - can't encode.");
    } 
    if (this.encryptor != null) {
      String str1 = Strings.toUpperCase(this.encryptor.getAlgorithm());
      if (str1.equals("DESEDE"))
        str1 = "DES-EDE3-CBC"; 
      byte[] arrayOfByte1 = this.encryptor.getIV();
      byte[] arrayOfByte2 = this.encryptor.encrypt(arrayOfByte);
      ArrayList<PemHeader> arrayList = new ArrayList(2);
      arrayList.add(new PemHeader("Proc-Type", "4,ENCRYPTED"));
      arrayList.add(new PemHeader("DEK-Info", str1 + "," + getHexEncoded(arrayOfByte1)));
      return new PemObject(str, arrayList, arrayOfByte2);
    } 
    return new PemObject(str, arrayOfByte);
  }
  
  private String getHexEncoded(byte[] paramArrayOfbyte) throws IOException {
    char[] arrayOfChar = new char[paramArrayOfbyte.length * 2];
    for (byte b = 0; b != paramArrayOfbyte.length; b++) {
      int i = paramArrayOfbyte[b] & 0xFF;
      arrayOfChar[2 * b] = (char)hexEncodingTable[i >>> 4];
      arrayOfChar[2 * b + 1] = (char)hexEncodingTable[i & 0xF];
    } 
    return new String(arrayOfChar);
  }
  
  public PemObject generate() throws PemGenerationException {
    try {
      return createPemObject(this.obj);
    } catch (IOException iOException) {
      throw new PemGenerationException("encoding exception: " + iOException.getMessage(), iOException);
    } 
  }
}
