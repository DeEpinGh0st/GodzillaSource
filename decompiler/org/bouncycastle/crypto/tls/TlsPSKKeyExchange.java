package org.bouncycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public class TlsPSKKeyExchange extends AbstractTlsKeyExchange {
  protected TlsPSKIdentity pskIdentity;
  
  protected TlsPSKIdentityManager pskIdentityManager;
  
  protected DHParameters dhParameters;
  
  protected int[] namedCurves;
  
  protected short[] clientECPointFormats;
  
  protected short[] serverECPointFormats;
  
  protected byte[] psk_identity_hint = null;
  
  protected byte[] psk = null;
  
  protected DHPrivateKeyParameters dhAgreePrivateKey = null;
  
  protected DHPublicKeyParameters dhAgreePublicKey = null;
  
  protected ECPrivateKeyParameters ecAgreePrivateKey = null;
  
  protected ECPublicKeyParameters ecAgreePublicKey = null;
  
  protected AsymmetricKeyParameter serverPublicKey = null;
  
  protected RSAKeyParameters rsaServerPublicKey = null;
  
  protected TlsEncryptionCredentials serverCredentials = null;
  
  protected byte[] premasterSecret;
  
  public TlsPSKKeyExchange(int paramInt, Vector paramVector, TlsPSKIdentity paramTlsPSKIdentity, TlsPSKIdentityManager paramTlsPSKIdentityManager, DHParameters paramDHParameters, int[] paramArrayOfint, short[] paramArrayOfshort1, short[] paramArrayOfshort2) {
    super(paramInt, paramVector);
    switch (paramInt) {
      case 13:
      case 14:
      case 15:
      case 24:
        break;
      default:
        throw new IllegalArgumentException("unsupported key exchange algorithm");
    } 
    this.pskIdentity = paramTlsPSKIdentity;
    this.pskIdentityManager = paramTlsPSKIdentityManager;
    this.dhParameters = paramDHParameters;
    this.namedCurves = paramArrayOfint;
    this.clientECPointFormats = paramArrayOfshort1;
    this.serverECPointFormats = paramArrayOfshort2;
  }
  
  public void skipServerCredentials() throws IOException {
    if (this.keyExchange == 15)
      throw new TlsFatalAlert((short)10); 
  }
  
  public void processServerCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    if (!(paramTlsCredentials instanceof TlsEncryptionCredentials))
      throw new TlsFatalAlert((short)80); 
    processServerCertificate(paramTlsCredentials.getCertificate());
    this.serverCredentials = (TlsEncryptionCredentials)paramTlsCredentials;
  }
  
  public byte[] generateServerKeyExchange() throws IOException {
    this.psk_identity_hint = this.pskIdentityManager.getHint();
    if (this.psk_identity_hint == null && !requiresServerKeyExchange())
      return null; 
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    if (this.psk_identity_hint == null) {
      TlsUtils.writeOpaque16(TlsUtils.EMPTY_BYTES, byteArrayOutputStream);
    } else {
      TlsUtils.writeOpaque16(this.psk_identity_hint, byteArrayOutputStream);
    } 
    if (this.keyExchange == 14) {
      if (this.dhParameters == null)
        throw new TlsFatalAlert((short)80); 
      this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.dhParameters, byteArrayOutputStream);
    } else if (this.keyExchange == 24) {
      this.ecAgreePrivateKey = TlsECCUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.namedCurves, this.clientECPointFormats, byteArrayOutputStream);
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public void processServerCertificate(Certificate paramCertificate) throws IOException {
    if (this.keyExchange != 15)
      throw new TlsFatalAlert((short)10); 
    if (paramCertificate.isEmpty())
      throw new TlsFatalAlert((short)42); 
    Certificate certificate = paramCertificate.getCertificateAt(0);
    SubjectPublicKeyInfo subjectPublicKeyInfo = certificate.getSubjectPublicKeyInfo();
    try {
      this.serverPublicKey = PublicKeyFactory.createKey(subjectPublicKeyInfo);
    } catch (RuntimeException runtimeException) {
      throw new TlsFatalAlert((short)43, runtimeException);
    } 
    if (this.serverPublicKey.isPrivate())
      throw new TlsFatalAlert((short)80); 
    this.rsaServerPublicKey = validateRSAPublicKey((RSAKeyParameters)this.serverPublicKey);
    TlsUtils.validateKeyUsage(certificate, 32);
    super.processServerCertificate(paramCertificate);
  }
  
  public boolean requiresServerKeyExchange() {
    switch (this.keyExchange) {
      case 14:
      case 24:
        return true;
    } 
    return false;
  }
  
  public void processServerKeyExchange(InputStream paramInputStream) throws IOException {
    this.psk_identity_hint = TlsUtils.readOpaque16(paramInputStream);
    if (this.keyExchange == 14) {
      ServerDHParams serverDHParams = ServerDHParams.parse(paramInputStream);
      this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(serverDHParams.getPublicKey());
      this.dhParameters = this.dhAgreePublicKey.getParameters();
    } else if (this.keyExchange == 24) {
      ECDomainParameters eCDomainParameters = TlsECCUtils.readECParameters(this.namedCurves, this.clientECPointFormats, paramInputStream);
      byte[] arrayOfByte = TlsUtils.readOpaque8(paramInputStream);
      this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey(TlsECCUtils.deserializeECPublicKey(this.clientECPointFormats, eCDomainParameters, arrayOfByte));
    } 
  }
  
  public void validateCertificateRequest(CertificateRequest paramCertificateRequest) throws IOException {
    throw new TlsFatalAlert((short)10);
  }
  
  public void processClientCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  public void generateClientKeyExchange(OutputStream paramOutputStream) throws IOException {
    if (this.psk_identity_hint == null) {
      this.pskIdentity.skipIdentityHint();
    } else {
      this.pskIdentity.notifyIdentityHint(this.psk_identity_hint);
    } 
    byte[] arrayOfByte = this.pskIdentity.getPSKIdentity();
    if (arrayOfByte == null)
      throw new TlsFatalAlert((short)80); 
    this.psk = this.pskIdentity.getPSK();
    if (this.psk == null)
      throw new TlsFatalAlert((short)80); 
    TlsUtils.writeOpaque16(arrayOfByte, paramOutputStream);
    (this.context.getSecurityParameters()).pskIdentity = Arrays.clone(arrayOfByte);
    if (this.keyExchange == 14) {
      this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.dhParameters, paramOutputStream);
    } else if (this.keyExchange == 24) {
      this.ecAgreePrivateKey = TlsECCUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.serverECPointFormats, this.ecAgreePublicKey.getParameters(), paramOutputStream);
    } else if (this.keyExchange == 15) {
      this.premasterSecret = TlsRSAUtils.generateEncryptedPreMasterSecret(this.context, this.rsaServerPublicKey, paramOutputStream);
    } 
  }
  
  public void processClientKeyExchange(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = TlsUtils.readOpaque16(paramInputStream);
    this.psk = this.pskIdentityManager.getPSK(arrayOfByte);
    if (this.psk == null)
      throw new TlsFatalAlert((short)115); 
    (this.context.getSecurityParameters()).pskIdentity = arrayOfByte;
    if (this.keyExchange == 14) {
      BigInteger bigInteger = TlsDHUtils.readDHParameter(paramInputStream);
      this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(new DHPublicKeyParameters(bigInteger, this.dhParameters));
    } else if (this.keyExchange == 24) {
      byte[] arrayOfByte1 = TlsUtils.readOpaque8(paramInputStream);
      ECDomainParameters eCDomainParameters = this.ecAgreePrivateKey.getParameters();
      this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey(TlsECCUtils.deserializeECPublicKey(this.serverECPointFormats, eCDomainParameters, arrayOfByte1));
    } else if (this.keyExchange == 15) {
      byte[] arrayOfByte1;
      if (TlsUtils.isSSL(this.context)) {
        arrayOfByte1 = Streams.readAll(paramInputStream);
      } else {
        arrayOfByte1 = TlsUtils.readOpaque16(paramInputStream);
      } 
      this.premasterSecret = this.serverCredentials.decryptPreMasterSecret(arrayOfByte1);
    } 
  }
  
  public byte[] generatePremasterSecret() throws IOException {
    byte[] arrayOfByte = generateOtherSecret(this.psk.length);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4 + arrayOfByte.length + this.psk.length);
    TlsUtils.writeOpaque16(arrayOfByte, byteArrayOutputStream);
    TlsUtils.writeOpaque16(this.psk, byteArrayOutputStream);
    Arrays.fill(this.psk, (byte)0);
    this.psk = null;
    return byteArrayOutputStream.toByteArray();
  }
  
  protected byte[] generateOtherSecret(int paramInt) throws IOException {
    if (this.keyExchange == 14) {
      if (this.dhAgreePrivateKey != null)
        return TlsDHUtils.calculateDHBasicAgreement(this.dhAgreePublicKey, this.dhAgreePrivateKey); 
      throw new TlsFatalAlert((short)80);
    } 
    if (this.keyExchange == 24) {
      if (this.ecAgreePrivateKey != null)
        return TlsECCUtils.calculateECDHBasicAgreement(this.ecAgreePublicKey, this.ecAgreePrivateKey); 
      throw new TlsFatalAlert((short)80);
    } 
    return (this.keyExchange == 15) ? this.premasterSecret : new byte[paramInt];
  }
  
  protected RSAKeyParameters validateRSAPublicKey(RSAKeyParameters paramRSAKeyParameters) throws IOException {
    if (!paramRSAKeyParameters.getExponent().isProbablePrime(2))
      throw new TlsFatalAlert((short)47); 
    return paramRSAKeyParameters;
  }
}
