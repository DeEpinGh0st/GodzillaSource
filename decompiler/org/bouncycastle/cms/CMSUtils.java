package org.bouncycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetStringGenerator;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.OtherRevocationInfoFormat;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificateList;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

class CMSUtils {
  private static final Set<String> des = new HashSet<String>();
  
  private static final Set mqvAlgs = new HashSet();
  
  private static final Set ecAlgs = new HashSet();
  
  private static final Set gostAlgs = new HashSet();
  
  static boolean isMQV(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return mqvAlgs.contains(paramASN1ObjectIdentifier);
  }
  
  static boolean isEC(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return ecAlgs.contains(paramASN1ObjectIdentifier);
  }
  
  static boolean isGOST(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return gostAlgs.contains(paramASN1ObjectIdentifier);
  }
  
  static boolean isRFC2631(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_ESDH) || paramASN1ObjectIdentifier.equals(PKCSObjectIdentifiers.id_alg_SSDH));
  }
  
  static boolean isDES(String paramString) {
    String str = Strings.toUpperCase(paramString);
    return des.contains(str);
  }
  
  static boolean isEquivalent(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) {
    if (paramAlgorithmIdentifier1 == null || paramAlgorithmIdentifier2 == null)
      return false; 
    if (!paramAlgorithmIdentifier1.getAlgorithm().equals(paramAlgorithmIdentifier2.getAlgorithm()))
      return false; 
    ASN1Encodable aSN1Encodable1 = paramAlgorithmIdentifier1.getParameters();
    ASN1Encodable aSN1Encodable2 = paramAlgorithmIdentifier2.getParameters();
    return (aSN1Encodable1 != null) ? ((aSN1Encodable1.equals(aSN1Encodable2) || (aSN1Encodable1.equals(DERNull.INSTANCE) && aSN1Encodable2 == null))) : ((aSN1Encodable2 == null || aSN1Encodable2.equals(DERNull.INSTANCE)));
  }
  
  static ContentInfo readContentInfo(byte[] paramArrayOfbyte) throws CMSException {
    return readContentInfo(new ASN1InputStream(paramArrayOfbyte));
  }
  
  static ContentInfo readContentInfo(InputStream paramInputStream) throws CMSException {
    return readContentInfo(new ASN1InputStream(paramInputStream));
  }
  
  static List getCertificatesFromStore(Store paramStore) throws CMSException {
    ArrayList<Certificate> arrayList = new ArrayList();
    try {
      for (X509CertificateHolder x509CertificateHolder : paramStore.getMatches(null))
        arrayList.add(x509CertificateHolder.toASN1Structure()); 
      return arrayList;
    } catch (ClassCastException classCastException) {
      throw new CMSException("error processing certs", classCastException);
    } 
  }
  
  static List getAttributeCertificatesFromStore(Store paramStore) throws CMSException {
    ArrayList<DERTaggedObject> arrayList = new ArrayList();
    try {
      for (X509AttributeCertificateHolder x509AttributeCertificateHolder : paramStore.getMatches(null))
        arrayList.add(new DERTaggedObject(false, 2, (ASN1Encodable)x509AttributeCertificateHolder.toASN1Structure())); 
      return arrayList;
    } catch (ClassCastException classCastException) {
      throw new CMSException("error processing certs", classCastException);
    } 
  }
  
  static List getCRLsFromStore(Store paramStore) throws CMSException {
    ArrayList<CertificateList> arrayList = new ArrayList();
    try {
      for (X509CRLHolder x509CRLHolder : paramStore.getMatches(null)) {
        if (x509CRLHolder instanceof X509CRLHolder) {
          X509CRLHolder x509CRLHolder1 = x509CRLHolder;
          arrayList.add(x509CRLHolder1.toASN1Structure());
          continue;
        } 
        if (x509CRLHolder instanceof OtherRevocationInfoFormat) {
          OtherRevocationInfoFormat otherRevocationInfoFormat = OtherRevocationInfoFormat.getInstance(x509CRLHolder);
          validateInfoFormat(otherRevocationInfoFormat);
          arrayList.add(new DERTaggedObject(false, 1, (ASN1Encodable)otherRevocationInfoFormat));
          continue;
        } 
        if (x509CRLHolder instanceof org.bouncycastle.asn1.ASN1TaggedObject)
          arrayList.add(x509CRLHolder); 
      } 
      return arrayList;
    } catch (ClassCastException classCastException) {
      throw new CMSException("error processing certs", classCastException);
    } 
  }
  
  private static void validateInfoFormat(OtherRevocationInfoFormat paramOtherRevocationInfoFormat) {
    if (CMSObjectIdentifiers.id_ri_ocsp_response.equals(paramOtherRevocationInfoFormat.getInfoFormat())) {
      OCSPResponse oCSPResponse = OCSPResponse.getInstance(paramOtherRevocationInfoFormat.getInfo());
      if (oCSPResponse.getResponseStatus().getValue().intValue() != 0)
        throw new IllegalArgumentException("cannot add unsuccessful OCSP response to CMS SignedData"); 
    } 
  }
  
  static Collection getOthersFromStore(ASN1ObjectIdentifier paramASN1ObjectIdentifier, Store paramStore) {
    ArrayList<DERTaggedObject> arrayList = new ArrayList();
    for (ASN1Encodable aSN1Encodable : paramStore.getMatches(null)) {
      OtherRevocationInfoFormat otherRevocationInfoFormat = new OtherRevocationInfoFormat(paramASN1ObjectIdentifier, aSN1Encodable);
      validateInfoFormat(otherRevocationInfoFormat);
      arrayList.add(new DERTaggedObject(false, 1, (ASN1Encodable)otherRevocationInfoFormat));
    } 
    return arrayList;
  }
  
  static ASN1Set createBerSetFromList(List paramList) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Iterator<ASN1Encodable> iterator = paramList.iterator();
    while (iterator.hasNext())
      aSN1EncodableVector.add(iterator.next()); 
    return (ASN1Set)new BERSet(aSN1EncodableVector);
  }
  
  static ASN1Set createDerSetFromList(List paramList) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    Iterator<ASN1Encodable> iterator = paramList.iterator();
    while (iterator.hasNext())
      aSN1EncodableVector.add(iterator.next()); 
    return (ASN1Set)new DERSet(aSN1EncodableVector);
  }
  
  static OutputStream createBEROctetOutputStream(OutputStream paramOutputStream, int paramInt1, boolean paramBoolean, int paramInt2) throws IOException {
    BEROctetStringGenerator bEROctetStringGenerator = new BEROctetStringGenerator(paramOutputStream, paramInt1, paramBoolean);
    return (paramInt2 != 0) ? bEROctetStringGenerator.getOctetOutputStream(new byte[paramInt2]) : bEROctetStringGenerator.getOctetOutputStream();
  }
  
  private static ContentInfo readContentInfo(ASN1InputStream paramASN1InputStream) throws CMSException {
    try {
      ContentInfo contentInfo = ContentInfo.getInstance(paramASN1InputStream.readObject());
      if (contentInfo == null)
        throw new CMSException("No content found."); 
      return contentInfo;
    } catch (IOException iOException) {
      throw new CMSException("IOException reading content.", iOException);
    } catch (ClassCastException classCastException) {
      throw new CMSException("Malformed content.", classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new CMSException("Malformed content.", illegalArgumentException);
    } 
  }
  
  public static byte[] streamToByteArray(InputStream paramInputStream) throws IOException {
    return Streams.readAll(paramInputStream);
  }
  
  public static byte[] streamToByteArray(InputStream paramInputStream, int paramInt) throws IOException {
    return Streams.readAllLimited(paramInputStream, paramInt);
  }
  
  static InputStream attachDigestsToInputStream(Collection paramCollection, InputStream paramInputStream) {
    TeeInputStream teeInputStream;
    InputStream inputStream = paramInputStream;
    for (DigestCalculator digestCalculator : paramCollection)
      teeInputStream = new TeeInputStream(inputStream, digestCalculator.getOutputStream()); 
    return (InputStream)teeInputStream;
  }
  
  static OutputStream attachSignersToOutputStream(Collection paramCollection, OutputStream paramOutputStream) {
    OutputStream outputStream = paramOutputStream;
    for (SignerInfoGenerator signerInfoGenerator : paramCollection)
      outputStream = getSafeTeeOutputStream(outputStream, signerInfoGenerator.getCalculatingOutputStream()); 
    return outputStream;
  }
  
  static OutputStream getSafeOutputStream(OutputStream paramOutputStream) {
    return (paramOutputStream == null) ? new NullOutputStream() : paramOutputStream;
  }
  
  static OutputStream getSafeTeeOutputStream(OutputStream paramOutputStream1, OutputStream paramOutputStream2) {
    return (paramOutputStream1 == null) ? getSafeOutputStream(paramOutputStream2) : ((paramOutputStream2 == null) ? getSafeOutputStream(paramOutputStream1) : (OutputStream)new TeeOutputStream(paramOutputStream1, paramOutputStream2));
  }
  
  static {
    des.add("DES");
    des.add("DESEDE");
    des.add(OIWObjectIdentifiers.desCBC.getId());
    des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
    des.add(PKCSObjectIdentifiers.des_EDE3_CBC.getId());
    des.add(PKCSObjectIdentifiers.id_alg_CMS3DESwrap.getId());
    mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
    mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
    mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
    mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
    mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
    ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
    ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
    ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
    gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
    gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
    gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
  }
}
