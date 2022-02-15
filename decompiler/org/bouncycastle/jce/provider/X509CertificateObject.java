package org.bouncycastle.jce.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.misc.NetscapeCertType;
import org.bouncycastle.asn1.misc.NetscapeRevocationURL;
import org.bouncycastle.asn1.misc.VerisignCzagExtension;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class X509CertificateObject extends X509Certificate implements PKCS12BagAttributeCarrier {
  private Certificate c;
  
  private BasicConstraints basicConstraints;
  
  private boolean[] keyUsage;
  
  private boolean hashValueSet;
  
  private int hashValue;
  
  private PKCS12BagAttributeCarrier attrCarrier = (PKCS12BagAttributeCarrier)new PKCS12BagAttributeCarrierImpl();
  
  public X509CertificateObject(Certificate paramCertificate) throws CertificateParsingException {
    this.c = paramCertificate;
    try {
      byte[] arrayOfByte = getExtensionBytes("2.5.29.19");
      if (arrayOfByte != null)
        this.basicConstraints = BasicConstraints.getInstance(ASN1Primitive.fromByteArray(arrayOfByte)); 
    } catch (Exception exception) {
      throw new CertificateParsingException("cannot construct BasicConstraints: " + exception);
    } 
    try {
      byte[] arrayOfByte = getExtensionBytes("2.5.29.15");
      if (arrayOfByte != null) {
        DERBitString dERBitString = DERBitString.getInstance(ASN1Primitive.fromByteArray(arrayOfByte));
        arrayOfByte = dERBitString.getBytes();
        int i = arrayOfByte.length * 8 - dERBitString.getPadBits();
        this.keyUsage = new boolean[(i < 9) ? 9 : i];
        for (int j = 0; j != i; j++)
          this.keyUsage[j] = ((arrayOfByte[j / 8] & 128 >>> j % 8) != 0); 
      } else {
        this.keyUsage = null;
      } 
    } catch (Exception exception) {
      throw new CertificateParsingException("cannot construct KeyUsage: " + exception);
    } 
  }
  
  public void checkValidity() throws CertificateExpiredException, CertificateNotYetValidException {
    checkValidity(new Date());
  }
  
  public void checkValidity(Date paramDate) throws CertificateExpiredException, CertificateNotYetValidException {
    if (paramDate.getTime() > getNotAfter().getTime())
      throw new CertificateExpiredException("certificate expired on " + this.c.getEndDate().getTime()); 
    if (paramDate.getTime() < getNotBefore().getTime())
      throw new CertificateNotYetValidException("certificate not valid till " + this.c.getStartDate().getTime()); 
  }
  
  public int getVersion() {
    return this.c.getVersionNumber();
  }
  
  public BigInteger getSerialNumber() {
    return this.c.getSerialNumber().getValue();
  }
  
  public Principal getIssuerDN() {
    try {
      return (Principal)new X509Principal(X500Name.getInstance(this.c.getIssuer().getEncoded()));
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public X500Principal getIssuerX500Principal() {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ASN1OutputStream aSN1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
      aSN1OutputStream.writeObject((ASN1Encodable)this.c.getIssuer());
      return new X500Principal(byteArrayOutputStream.toByteArray());
    } catch (IOException iOException) {
      throw new IllegalStateException("can't encode issuer DN");
    } 
  }
  
  public Principal getSubjectDN() {
    return (Principal)new X509Principal(X500Name.getInstance(this.c.getSubject().toASN1Primitive()));
  }
  
  public X500Principal getSubjectX500Principal() {
    try {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      ASN1OutputStream aSN1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
      aSN1OutputStream.writeObject((ASN1Encodable)this.c.getSubject());
      return new X500Principal(byteArrayOutputStream.toByteArray());
    } catch (IOException iOException) {
      throw new IllegalStateException("can't encode issuer DN");
    } 
  }
  
  public Date getNotBefore() {
    return this.c.getStartDate().getDate();
  }
  
  public Date getNotAfter() {
    return this.c.getEndDate().getDate();
  }
  
  public byte[] getTBSCertificate() throws CertificateEncodingException {
    try {
      return this.c.getTBSCertificate().getEncoded("DER");
    } catch (IOException iOException) {
      throw new CertificateEncodingException(iOException.toString());
    } 
  }
  
  public byte[] getSignature() {
    return this.c.getSignature().getOctets();
  }
  
  public String getSigAlgName() {
    Provider provider = Security.getProvider("BC");
    if (provider != null) {
      String str = provider.getProperty("Alg.Alias.Signature." + getSigAlgOID());
      if (str != null)
        return str; 
    } 
    Provider[] arrayOfProvider = Security.getProviders();
    for (byte b = 0; b != arrayOfProvider.length; b++) {
      String str = arrayOfProvider[b].getProperty("Alg.Alias.Signature." + getSigAlgOID());
      if (str != null)
        return str; 
    } 
    return getSigAlgOID();
  }
  
  public String getSigAlgOID() {
    return this.c.getSignatureAlgorithm().getAlgorithm().getId();
  }
  
  public byte[] getSigAlgParams() {
    if (this.c.getSignatureAlgorithm().getParameters() != null)
      try {
        return this.c.getSignatureAlgorithm().getParameters().toASN1Primitive().getEncoded("DER");
      } catch (IOException iOException) {
        return null;
      }  
    return null;
  }
  
  public boolean[] getIssuerUniqueID() {
    DERBitString dERBitString = this.c.getTBSCertificate().getIssuerUniqueId();
    if (dERBitString != null) {
      byte[] arrayOfByte = dERBitString.getBytes();
      boolean[] arrayOfBoolean = new boolean[arrayOfByte.length * 8 - dERBitString.getPadBits()];
      for (byte b = 0; b != arrayOfBoolean.length; b++)
        arrayOfBoolean[b] = ((arrayOfByte[b / 8] & 128 >>> b % 8) != 0); 
      return arrayOfBoolean;
    } 
    return null;
  }
  
  public boolean[] getSubjectUniqueID() {
    DERBitString dERBitString = this.c.getTBSCertificate().getSubjectUniqueId();
    if (dERBitString != null) {
      byte[] arrayOfByte = dERBitString.getBytes();
      boolean[] arrayOfBoolean = new boolean[arrayOfByte.length * 8 - dERBitString.getPadBits()];
      for (byte b = 0; b != arrayOfBoolean.length; b++)
        arrayOfBoolean[b] = ((arrayOfByte[b / 8] & 128 >>> b % 8) != 0); 
      return arrayOfBoolean;
    } 
    return null;
  }
  
  public boolean[] getKeyUsage() {
    return this.keyUsage;
  }
  
  public List getExtendedKeyUsage() throws CertificateParsingException {
    byte[] arrayOfByte = getExtensionBytes("2.5.29.37");
    if (arrayOfByte != null)
      try {
        ASN1InputStream aSN1InputStream = new ASN1InputStream(arrayOfByte);
        ASN1Sequence aSN1Sequence = (ASN1Sequence)aSN1InputStream.readObject();
        ArrayList<String> arrayList = new ArrayList();
        for (byte b = 0; b != aSN1Sequence.size(); b++)
          arrayList.add(((ASN1ObjectIdentifier)aSN1Sequence.getObjectAt(b)).getId()); 
        return Collections.unmodifiableList(arrayList);
      } catch (Exception exception) {
        throw new CertificateParsingException("error processing extended key usage extension");
      }  
    return null;
  }
  
  public int getBasicConstraints() {
    return (this.basicConstraints != null) ? (this.basicConstraints.isCA() ? ((this.basicConstraints.getPathLenConstraint() == null) ? Integer.MAX_VALUE : this.basicConstraints.getPathLenConstraint().intValue()) : -1) : -1;
  }
  
  public Collection getSubjectAlternativeNames() throws CertificateParsingException {
    return getAlternativeNames(getExtensionBytes(Extension.subjectAlternativeName.getId()));
  }
  
  public Collection getIssuerAlternativeNames() throws CertificateParsingException {
    return getAlternativeNames(getExtensionBytes(Extension.issuerAlternativeName.getId()));
  }
  
  public Set getCriticalExtensionOIDs() {
    if (getVersion() == 3) {
      HashSet<String> hashSet = new HashSet();
      Extensions extensions = this.c.getTBSCertificate().getExtensions();
      if (extensions != null) {
        Enumeration<ASN1ObjectIdentifier> enumeration = extensions.oids();
        while (enumeration.hasMoreElements()) {
          ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
          Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
          if (extension.isCritical())
            hashSet.add(aSN1ObjectIdentifier.getId()); 
        } 
        return hashSet;
      } 
    } 
    return null;
  }
  
  private byte[] getExtensionBytes(String paramString) {
    Extensions extensions = this.c.getTBSCertificate().getExtensions();
    if (extensions != null) {
      Extension extension = extensions.getExtension(new ASN1ObjectIdentifier(paramString));
      if (extension != null)
        return extension.getExtnValue().getOctets(); 
    } 
    return null;
  }
  
  public byte[] getExtensionValue(String paramString) {
    Extensions extensions = this.c.getTBSCertificate().getExtensions();
    if (extensions != null) {
      Extension extension = extensions.getExtension(new ASN1ObjectIdentifier(paramString));
      if (extension != null)
        try {
          return extension.getExtnValue().getEncoded();
        } catch (Exception exception) {
          throw new IllegalStateException("error parsing " + exception.toString());
        }  
    } 
    return null;
  }
  
  public Set getNonCriticalExtensionOIDs() {
    if (getVersion() == 3) {
      HashSet<String> hashSet = new HashSet();
      Extensions extensions = this.c.getTBSCertificate().getExtensions();
      if (extensions != null) {
        Enumeration<ASN1ObjectIdentifier> enumeration = extensions.oids();
        while (enumeration.hasMoreElements()) {
          ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
          Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
          if (!extension.isCritical())
            hashSet.add(aSN1ObjectIdentifier.getId()); 
        } 
        return hashSet;
      } 
    } 
    return null;
  }
  
  public boolean hasUnsupportedCriticalExtension() {
    if (getVersion() == 3) {
      Extensions extensions = this.c.getTBSCertificate().getExtensions();
      if (extensions != null) {
        Enumeration<ASN1ObjectIdentifier> enumeration = extensions.oids();
        while (enumeration.hasMoreElements()) {
          ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
          String str = aSN1ObjectIdentifier.getId();
          if (str.equals(RFC3280CertPathUtilities.KEY_USAGE) || str.equals(RFC3280CertPathUtilities.CERTIFICATE_POLICIES) || str.equals(RFC3280CertPathUtilities.POLICY_MAPPINGS) || str.equals(RFC3280CertPathUtilities.INHIBIT_ANY_POLICY) || str.equals(RFC3280CertPathUtilities.CRL_DISTRIBUTION_POINTS) || str.equals(RFC3280CertPathUtilities.ISSUING_DISTRIBUTION_POINT) || str.equals(RFC3280CertPathUtilities.DELTA_CRL_INDICATOR) || str.equals(RFC3280CertPathUtilities.POLICY_CONSTRAINTS) || str.equals(RFC3280CertPathUtilities.BASIC_CONSTRAINTS) || str.equals(RFC3280CertPathUtilities.SUBJECT_ALTERNATIVE_NAME) || str.equals(RFC3280CertPathUtilities.NAME_CONSTRAINTS))
            continue; 
          Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
          if (extension.isCritical())
            return true; 
        } 
      } 
    } 
    return false;
  }
  
  public PublicKey getPublicKey() {
    try {
      return BouncyCastleProvider.getPublicKey(this.c.getSubjectPublicKeyInfo());
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public byte[] getEncoded() throws CertificateEncodingException {
    try {
      return this.c.getEncoded("DER");
    } catch (IOException iOException) {
      throw new CertificateEncodingException(iOException.toString());
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Certificate))
      return false; 
    Certificate certificate = (Certificate)paramObject;
    try {
      byte[] arrayOfByte1 = getEncoded();
      byte[] arrayOfByte2 = certificate.getEncoded();
      return Arrays.areEqual(arrayOfByte1, arrayOfByte2);
    } catch (CertificateEncodingException certificateEncodingException) {
      return false;
    } 
  }
  
  public synchronized int hashCode() {
    if (!this.hashValueSet) {
      this.hashValue = calculateHashCode();
      this.hashValueSet = true;
    } 
    return this.hashValue;
  }
  
  private int calculateHashCode() {
    try {
      int i = 0;
      byte[] arrayOfByte = getEncoded();
      for (byte b = 1; b < arrayOfByte.length; b++)
        i += arrayOfByte[b] * b; 
      return i;
    } catch (CertificateEncodingException certificateEncodingException) {
      return 0;
    } 
  }
  
  public void setBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.attrCarrier.setBagAttribute(paramASN1ObjectIdentifier, paramASN1Encodable);
  }
  
  public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return this.attrCarrier.getBagAttribute(paramASN1ObjectIdentifier);
  }
  
  public Enumeration getBagAttributeKeys() {
    return this.attrCarrier.getBagAttributeKeys();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    String str = Strings.lineSeparator();
    stringBuffer.append("  [0]         Version: ").append(getVersion()).append(str);
    stringBuffer.append("         SerialNumber: ").append(getSerialNumber()).append(str);
    stringBuffer.append("             IssuerDN: ").append(getIssuerDN()).append(str);
    stringBuffer.append("           Start Date: ").append(getNotBefore()).append(str);
    stringBuffer.append("           Final Date: ").append(getNotAfter()).append(str);
    stringBuffer.append("            SubjectDN: ").append(getSubjectDN()).append(str);
    stringBuffer.append("           Public Key: ").append(getPublicKey()).append(str);
    stringBuffer.append("  Signature Algorithm: ").append(getSigAlgName()).append(str);
    byte[] arrayOfByte = getSignature();
    stringBuffer.append("            Signature: ").append(new String(Hex.encode(arrayOfByte, 0, 20))).append(str);
    for (byte b = 20; b < arrayOfByte.length; b += 20) {
      if (b < arrayOfByte.length - 20) {
        stringBuffer.append("                       ").append(new String(Hex.encode(arrayOfByte, b, 20))).append(str);
      } else {
        stringBuffer.append("                       ").append(new String(Hex.encode(arrayOfByte, b, arrayOfByte.length - b))).append(str);
      } 
    } 
    Extensions extensions = this.c.getTBSCertificate().getExtensions();
    if (extensions != null) {
      Enumeration<ASN1ObjectIdentifier> enumeration = extensions.oids();
      if (enumeration.hasMoreElements())
        stringBuffer.append("       Extensions: \n"); 
      while (enumeration.hasMoreElements()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
        Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
        if (extension.getExtnValue() != null) {
          byte[] arrayOfByte1 = extension.getExtnValue().getOctets();
          ASN1InputStream aSN1InputStream = new ASN1InputStream(arrayOfByte1);
          stringBuffer.append("                       critical(").append(extension.isCritical()).append(") ");
          try {
            if (aSN1ObjectIdentifier.equals(Extension.basicConstraints)) {
              stringBuffer.append(BasicConstraints.getInstance(aSN1InputStream.readObject())).append(str);
              continue;
            } 
            if (aSN1ObjectIdentifier.equals(Extension.keyUsage)) {
              stringBuffer.append(KeyUsage.getInstance(aSN1InputStream.readObject())).append(str);
              continue;
            } 
            if (aSN1ObjectIdentifier.equals(MiscObjectIdentifiers.netscapeCertType)) {
              stringBuffer.append(new NetscapeCertType((DERBitString)aSN1InputStream.readObject())).append(str);
              continue;
            } 
            if (aSN1ObjectIdentifier.equals(MiscObjectIdentifiers.netscapeRevocationURL)) {
              stringBuffer.append(new NetscapeRevocationURL((DERIA5String)aSN1InputStream.readObject())).append(str);
              continue;
            } 
            if (aSN1ObjectIdentifier.equals(MiscObjectIdentifiers.verisignCzagExtension)) {
              stringBuffer.append(new VerisignCzagExtension((DERIA5String)aSN1InputStream.readObject())).append(str);
              continue;
            } 
            stringBuffer.append(aSN1ObjectIdentifier.getId());
            stringBuffer.append(" value = ").append(ASN1Dump.dumpAsString(aSN1InputStream.readObject())).append(str);
          } catch (Exception exception) {
            stringBuffer.append(aSN1ObjectIdentifier.getId());
            stringBuffer.append(" value = ").append("*****").append(str);
          } 
          continue;
        } 
        stringBuffer.append(str);
      } 
    } 
    return stringBuffer.toString();
  }
  
  public final void verify(PublicKey paramPublicKey) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    Signature signature;
    String str = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
    try {
      signature = Signature.getInstance(str, "BC");
    } catch (Exception exception) {
      signature = Signature.getInstance(str);
    } 
    checkSignature(paramPublicKey, signature);
  }
  
  public final void verify(PublicKey paramPublicKey, String paramString) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, SignatureException {
    Signature signature;
    String str = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
    if (paramString != null) {
      signature = Signature.getInstance(str, paramString);
    } else {
      signature = Signature.getInstance(str);
    } 
    checkSignature(paramPublicKey, signature);
  }
  
  public final void verify(PublicKey paramPublicKey, Provider paramProvider) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    Signature signature;
    String str = X509SignatureUtil.getSignatureName(this.c.getSignatureAlgorithm());
    if (paramProvider != null) {
      signature = Signature.getInstance(str, paramProvider);
    } else {
      signature = Signature.getInstance(str);
    } 
    checkSignature(paramPublicKey, signature);
  }
  
  private void checkSignature(PublicKey paramPublicKey, Signature paramSignature) throws CertificateException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
    if (!isAlgIdEqual(this.c.getSignatureAlgorithm(), this.c.getTBSCertificate().getSignature()))
      throw new CertificateException("signature algorithm in TBS cert not same as outer cert"); 
    ASN1Encodable aSN1Encodable = this.c.getSignatureAlgorithm().getParameters();
    X509SignatureUtil.setSignatureParameters(paramSignature, aSN1Encodable);
    paramSignature.initVerify(paramPublicKey);
    paramSignature.update(getTBSCertificate());
    if (!paramSignature.verify(getSignature()))
      throw new SignatureException("certificate does not verify with supplied key"); 
  }
  
  private boolean isAlgIdEqual(AlgorithmIdentifier paramAlgorithmIdentifier1, AlgorithmIdentifier paramAlgorithmIdentifier2) {
    return !paramAlgorithmIdentifier1.getAlgorithm().equals(paramAlgorithmIdentifier2.getAlgorithm()) ? false : ((paramAlgorithmIdentifier1.getParameters() == null) ? (!(paramAlgorithmIdentifier2.getParameters() != null && !paramAlgorithmIdentifier2.getParameters().equals(DERNull.INSTANCE))) : ((paramAlgorithmIdentifier2.getParameters() == null) ? (!(paramAlgorithmIdentifier1.getParameters() != null && !paramAlgorithmIdentifier1.getParameters().equals(DERNull.INSTANCE))) : paramAlgorithmIdentifier1.getParameters().equals(paramAlgorithmIdentifier2.getParameters())));
  }
  
  private static Collection getAlternativeNames(byte[] paramArrayOfbyte) throws CertificateParsingException {
    if (paramArrayOfbyte == null)
      return null; 
    try {
      ArrayList<?> arrayList = new ArrayList();
      Enumeration enumeration = ASN1Sequence.getInstance(paramArrayOfbyte).getObjects();
      while (enumeration.hasMoreElements()) {
        byte[] arrayOfByte;
        String str;
        GeneralName generalName = GeneralName.getInstance(enumeration.nextElement());
        ArrayList<Integer> arrayList1 = new ArrayList();
        arrayList1.add(Integers.valueOf(generalName.getTagNo()));
        switch (generalName.getTagNo()) {
          case 0:
          case 3:
          case 5:
            arrayList1.add(generalName.getEncoded());
            break;
          case 4:
            arrayList1.add(X500Name.getInstance(RFC4519Style.INSTANCE, generalName.getName()).toString());
            break;
          case 1:
          case 2:
          case 6:
            arrayList1.add(((ASN1String)generalName.getName()).getString());
            break;
          case 8:
            arrayList1.add(ASN1ObjectIdentifier.getInstance(generalName.getName()).getId());
            break;
          case 7:
            arrayOfByte = DEROctetString.getInstance(generalName.getName()).getOctets();
            try {
              str = InetAddress.getByAddress(arrayOfByte).getHostAddress();
            } catch (UnknownHostException unknownHostException) {
              continue;
            } 
            arrayList1.add(str);
            break;
          default:
            throw new IOException("Bad tag number: " + generalName.getTagNo());
        } 
        arrayList.add(Collections.unmodifiableList(arrayList1));
      } 
      return (arrayList.size() == 0) ? null : Collections.unmodifiableCollection(arrayList);
    } catch (Exception exception) {
      throw new CertificateParsingException(exception.getMessage());
    } 
  }
}
