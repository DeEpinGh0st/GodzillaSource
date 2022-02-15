package org.bouncycastle.tsp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;

public class TSPUtil {
  private static List EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
  
  private static final Map digestLengths = new HashMap<Object, Object>();
  
  private static final Map digestNames = new HashMap<Object, Object>();
  
  public static Collection getSignatureTimestamps(SignerInformation paramSignerInformation, DigestCalculatorProvider paramDigestCalculatorProvider) throws TSPValidationException {
    ArrayList<TimeStampToken> arrayList = new ArrayList();
    AttributeTable attributeTable = paramSignerInformation.getUnsignedAttributes();
    if (attributeTable != null) {
      ASN1EncodableVector aSN1EncodableVector = attributeTable.getAll(PKCSObjectIdentifiers.id_aa_signatureTimeStampToken);
      for (byte b = 0; b < aSN1EncodableVector.size(); b++) {
        Attribute attribute = (Attribute)aSN1EncodableVector.get(b);
        ASN1Set aSN1Set = attribute.getAttrValues();
        for (byte b1 = 0; b1 < aSN1Set.size(); b1++) {
          try {
            ContentInfo contentInfo = ContentInfo.getInstance(aSN1Set.getObjectAt(b1));
            TimeStampToken timeStampToken = new TimeStampToken(contentInfo);
            TimeStampTokenInfo timeStampTokenInfo = timeStampToken.getTimeStampInfo();
            DigestCalculator digestCalculator = paramDigestCalculatorProvider.get(timeStampTokenInfo.getHashAlgorithm());
            OutputStream outputStream = digestCalculator.getOutputStream();
            outputStream.write(paramSignerInformation.getSignature());
            outputStream.close();
            byte[] arrayOfByte = digestCalculator.getDigest();
            if (!Arrays.constantTimeAreEqual(arrayOfByte, timeStampTokenInfo.getMessageImprintDigest()))
              throw new TSPValidationException("Incorrect digest in message imprint"); 
            arrayList.add(timeStampToken);
          } catch (OperatorCreationException operatorCreationException) {
            throw new TSPValidationException("Unknown hash algorithm specified in timestamp");
          } catch (Exception exception) {
            throw new TSPValidationException("Timestamp could not be parsed");
          } 
        } 
      } 
    } 
    return arrayList;
  }
  
  public static void validateCertificate(X509CertificateHolder paramX509CertificateHolder) throws TSPValidationException {
    if (paramX509CertificateHolder.toASN1Structure().getVersionNumber() != 3)
      throw new IllegalArgumentException("Certificate must have an ExtendedKeyUsage extension."); 
    Extension extension = paramX509CertificateHolder.getExtension(Extension.extendedKeyUsage);
    if (extension == null)
      throw new TSPValidationException("Certificate must have an ExtendedKeyUsage extension."); 
    if (!extension.isCritical())
      throw new TSPValidationException("Certificate must have an ExtendedKeyUsage extension marked as critical."); 
    ExtendedKeyUsage extendedKeyUsage = ExtendedKeyUsage.getInstance(extension.getParsedValue());
    if (!extendedKeyUsage.hasKeyPurposeId(KeyPurposeId.id_kp_timeStamping) || extendedKeyUsage.size() != 1)
      throw new TSPValidationException("ExtendedKeyUsage not solely time stamping."); 
  }
  
  static int getDigestLength(String paramString) throws TSPException {
    Integer integer = (Integer)digestLengths.get(paramString);
    if (integer != null)
      return integer.intValue(); 
    throw new TSPException("digest algorithm cannot be found.");
  }
  
  static List getExtensionOIDs(Extensions paramExtensions) {
    return (paramExtensions == null) ? EMPTY_LIST : Collections.unmodifiableList(Arrays.asList((Object[])paramExtensions.getExtensionOIDs()));
  }
  
  static void addExtension(ExtensionsGenerator paramExtensionsGenerator, ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, ASN1Encodable paramASN1Encodable) throws TSPIOException {
    try {
      paramExtensionsGenerator.addExtension(paramASN1ObjectIdentifier, paramBoolean, paramASN1Encodable);
    } catch (IOException iOException) {
      throw new TSPIOException("cannot encode extension: " + iOException.getMessage(), iOException);
    } 
  }
  
  static {
    digestLengths.put(PKCSObjectIdentifiers.md5.getId(), Integers.valueOf(16));
    digestLengths.put(OIWObjectIdentifiers.idSHA1.getId(), Integers.valueOf(20));
    digestLengths.put(NISTObjectIdentifiers.id_sha224.getId(), Integers.valueOf(28));
    digestLengths.put(NISTObjectIdentifiers.id_sha256.getId(), Integers.valueOf(32));
    digestLengths.put(NISTObjectIdentifiers.id_sha384.getId(), Integers.valueOf(48));
    digestLengths.put(NISTObjectIdentifiers.id_sha512.getId(), Integers.valueOf(64));
    digestLengths.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), Integers.valueOf(16));
    digestLengths.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), Integers.valueOf(20));
    digestLengths.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), Integers.valueOf(32));
    digestLengths.put(CryptoProObjectIdentifiers.gostR3411.getId(), Integers.valueOf(32));
    digestLengths.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256.getId(), Integers.valueOf(32));
    digestLengths.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512.getId(), Integers.valueOf(64));
    digestNames.put(PKCSObjectIdentifiers.md5.getId(), "MD5");
    digestNames.put(OIWObjectIdentifiers.idSHA1.getId(), "SHA1");
    digestNames.put(NISTObjectIdentifiers.id_sha224.getId(), "SHA224");
    digestNames.put(NISTObjectIdentifiers.id_sha256.getId(), "SHA256");
    digestNames.put(NISTObjectIdentifiers.id_sha384.getId(), "SHA384");
    digestNames.put(NISTObjectIdentifiers.id_sha512.getId(), "SHA512");
    digestNames.put(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId(), "SHA1");
    digestNames.put(PKCSObjectIdentifiers.sha224WithRSAEncryption.getId(), "SHA224");
    digestNames.put(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId(), "SHA256");
    digestNames.put(PKCSObjectIdentifiers.sha384WithRSAEncryption.getId(), "SHA384");
    digestNames.put(PKCSObjectIdentifiers.sha512WithRSAEncryption.getId(), "SHA512");
    digestNames.put(TeleTrusTObjectIdentifiers.ripemd128.getId(), "RIPEMD128");
    digestNames.put(TeleTrusTObjectIdentifiers.ripemd160.getId(), "RIPEMD160");
    digestNames.put(TeleTrusTObjectIdentifiers.ripemd256.getId(), "RIPEMD256");
    digestNames.put(CryptoProObjectIdentifiers.gostR3411.getId(), "GOST3411");
    digestNames.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256.getId(), "GOST3411-2012-256");
    digestNames.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512.getId(), "GOST3411-2012-512");
  }
}
