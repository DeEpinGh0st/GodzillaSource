package org.bouncycastle.crypto.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;

public class TlsECDHKeyExchange extends AbstractTlsKeyExchange {
  protected TlsSigner tlsSigner;
  
  protected int[] namedCurves;
  
  protected short[] clientECPointFormats;
  
  protected short[] serverECPointFormats;
  
  protected AsymmetricKeyParameter serverPublicKey;
  
  protected TlsAgreementCredentials agreementCredentials;
  
  protected ECPrivateKeyParameters ecAgreePrivateKey;
  
  protected ECPublicKeyParameters ecAgreePublicKey;
  
  public TlsECDHKeyExchange(int paramInt, Vector paramVector, int[] paramArrayOfint, short[] paramArrayOfshort1, short[] paramArrayOfshort2) {
    super(paramInt, paramVector);
    switch (paramInt) {
      case 19:
        this.tlsSigner = new TlsRSASigner();
        break;
      case 17:
        this.tlsSigner = new TlsECDSASigner();
        break;
      case 16:
      case 18:
      case 20:
        this.tlsSigner = null;
        break;
      default:
        throw new IllegalArgumentException("unsupported key exchange algorithm");
    } 
    this.namedCurves = paramArrayOfint;
    this.clientECPointFormats = paramArrayOfshort1;
    this.serverECPointFormats = paramArrayOfshort2;
  }
  
  public void init(TlsContext paramTlsContext) {
    super.init(paramTlsContext);
    if (this.tlsSigner != null)
      this.tlsSigner.init(paramTlsContext); 
  }
  
  public void skipServerCredentials() throws IOException {
    if (this.keyExchange != 20)
      throw new TlsFatalAlert((short)10); 
  }
  
  public void processServerCertificate(Certificate paramCertificate) throws IOException {
    if (this.keyExchange == 20)
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
    if (this.tlsSigner == null) {
      try {
        this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey((ECPublicKeyParameters)this.serverPublicKey);
      } catch (ClassCastException classCastException) {
        throw new TlsFatalAlert((short)46, classCastException);
      } 
      TlsUtils.validateKeyUsage(certificate, 8);
    } else {
      if (!this.tlsSigner.isValidPublicKey(this.serverPublicKey))
        throw new TlsFatalAlert((short)46); 
      TlsUtils.validateKeyUsage(certificate, 128);
    } 
    super.processServerCertificate(paramCertificate);
  }
  
  public boolean requiresServerKeyExchange() {
    switch (this.keyExchange) {
      case 17:
      case 19:
      case 20:
        return true;
    } 
    return false;
  }
  
  public byte[] generateServerKeyExchange() throws IOException {
    if (!requiresServerKeyExchange())
      return null; 
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    this.ecAgreePrivateKey = TlsECCUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.namedCurves, this.clientECPointFormats, byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public void processServerKeyExchange(InputStream paramInputStream) throws IOException {
    if (!requiresServerKeyExchange())
      throw new TlsFatalAlert((short)10); 
    ECDomainParameters eCDomainParameters = TlsECCUtils.readECParameters(this.namedCurves, this.clientECPointFormats, paramInputStream);
    byte[] arrayOfByte = TlsUtils.readOpaque8(paramInputStream);
    this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey(TlsECCUtils.deserializeECPublicKey(this.clientECPointFormats, eCDomainParameters, arrayOfByte));
  }
  
  public void validateCertificateRequest(CertificateRequest paramCertificateRequest) throws IOException {
    if (this.keyExchange == 20)
      throw new TlsFatalAlert((short)40); 
    short[] arrayOfShort = paramCertificateRequest.getCertificateTypes();
    for (byte b = 0; b < arrayOfShort.length; b++) {
      switch (arrayOfShort[b]) {
        case 1:
        case 2:
        case 64:
        case 65:
        case 66:
          break;
        default:
          throw new TlsFatalAlert((short)47);
      } 
    } 
  }
  
  public void processClientCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    if (this.keyExchange == 20)
      throw new TlsFatalAlert((short)80); 
    if (paramTlsCredentials instanceof TlsAgreementCredentials) {
      this.agreementCredentials = (TlsAgreementCredentials)paramTlsCredentials;
    } else if (!(paramTlsCredentials instanceof TlsSignerCredentials)) {
      throw new TlsFatalAlert((short)80);
    } 
  }
  
  public void generateClientKeyExchange(OutputStream paramOutputStream) throws IOException {
    if (this.agreementCredentials == null)
      this.ecAgreePrivateKey = TlsECCUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.serverECPointFormats, this.ecAgreePublicKey.getParameters(), paramOutputStream); 
  }
  
  public void processClientCertificate(Certificate paramCertificate) throws IOException {
    if (this.keyExchange == 20)
      throw new TlsFatalAlert((short)10); 
  }
  
  public void processClientKeyExchange(InputStream paramInputStream) throws IOException {
    if (this.ecAgreePublicKey != null)
      return; 
    byte[] arrayOfByte = TlsUtils.readOpaque8(paramInputStream);
    ECDomainParameters eCDomainParameters = this.ecAgreePrivateKey.getParameters();
    this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey(TlsECCUtils.deserializeECPublicKey(this.serverECPointFormats, eCDomainParameters, arrayOfByte));
  }
  
  public byte[] generatePremasterSecret() throws IOException {
    if (this.agreementCredentials != null)
      return this.agreementCredentials.generateAgreement((AsymmetricKeyParameter)this.ecAgreePublicKey); 
    if (this.ecAgreePrivateKey != null)
      return TlsECCUtils.calculateECDHBasicAgreement(this.ecAgreePublicKey, this.ecAgreePrivateKey); 
    throw new TlsFatalAlert((short)80);
  }
}
