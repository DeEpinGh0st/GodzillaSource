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
import org.bouncycastle.crypto.util.PublicKeyFactory;

public class TlsDHKeyExchange extends AbstractTlsKeyExchange {
  protected TlsSigner tlsSigner;
  
  protected DHParameters dhParameters;
  
  protected AsymmetricKeyParameter serverPublicKey;
  
  protected TlsAgreementCredentials agreementCredentials;
  
  protected DHPrivateKeyParameters dhAgreePrivateKey;
  
  protected DHPublicKeyParameters dhAgreePublicKey;
  
  public TlsDHKeyExchange(int paramInt, Vector paramVector, DHParameters paramDHParameters) {
    super(paramInt, paramVector);
    switch (paramInt) {
      case 7:
      case 9:
      case 11:
        this.tlsSigner = null;
        break;
      case 5:
        this.tlsSigner = new TlsRSASigner();
        break;
      case 3:
        this.tlsSigner = new TlsDSSSigner();
        break;
      default:
        throw new IllegalArgumentException("unsupported key exchange algorithm");
    } 
    this.dhParameters = paramDHParameters;
  }
  
  public void init(TlsContext paramTlsContext) {
    super.init(paramTlsContext);
    if (this.tlsSigner != null)
      this.tlsSigner.init(paramTlsContext); 
  }
  
  public void skipServerCredentials() throws IOException {
    if (this.keyExchange != 11)
      throw new TlsFatalAlert((short)10); 
  }
  
  public void processServerCertificate(Certificate paramCertificate) throws IOException {
    if (this.keyExchange == 11)
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
        this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey((DHPublicKeyParameters)this.serverPublicKey);
        this.dhParameters = validateDHParameters(this.dhAgreePublicKey.getParameters());
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
      case 3:
      case 5:
      case 11:
        return true;
    } 
    return false;
  }
  
  public byte[] generateServerKeyExchange() throws IOException {
    if (!requiresServerKeyExchange())
      return null; 
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.dhParameters, byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }
  
  public void processServerKeyExchange(InputStream paramInputStream) throws IOException {
    if (!requiresServerKeyExchange())
      throw new TlsFatalAlert((short)10); 
    ServerDHParams serverDHParams = ServerDHParams.parse(paramInputStream);
    this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(serverDHParams.getPublicKey());
    this.dhParameters = validateDHParameters(this.dhAgreePublicKey.getParameters());
  }
  
  public void validateCertificateRequest(CertificateRequest paramCertificateRequest) throws IOException {
    if (this.keyExchange == 11)
      throw new TlsFatalAlert((short)40); 
    short[] arrayOfShort = paramCertificateRequest.getCertificateTypes();
    for (byte b = 0; b < arrayOfShort.length; b++) {
      switch (arrayOfShort[b]) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 64:
          break;
        default:
          throw new TlsFatalAlert((short)47);
      } 
    } 
  }
  
  public void processClientCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    if (this.keyExchange == 11)
      throw new TlsFatalAlert((short)80); 
    if (paramTlsCredentials instanceof TlsAgreementCredentials) {
      this.agreementCredentials = (TlsAgreementCredentials)paramTlsCredentials;
    } else if (!(paramTlsCredentials instanceof TlsSignerCredentials)) {
      throw new TlsFatalAlert((short)80);
    } 
  }
  
  public void generateClientKeyExchange(OutputStream paramOutputStream) throws IOException {
    if (this.agreementCredentials == null)
      this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralClientKeyExchange(this.context.getSecureRandom(), this.dhParameters, paramOutputStream); 
  }
  
  public void processClientCertificate(Certificate paramCertificate) throws IOException {
    if (this.keyExchange == 11)
      throw new TlsFatalAlert((short)10); 
  }
  
  public void processClientKeyExchange(InputStream paramInputStream) throws IOException {
    if (this.dhAgreePublicKey != null)
      return; 
    BigInteger bigInteger = TlsDHUtils.readDHParameter(paramInputStream);
    this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(new DHPublicKeyParameters(bigInteger, this.dhParameters));
  }
  
  public byte[] generatePremasterSecret() throws IOException {
    if (this.agreementCredentials != null)
      return this.agreementCredentials.generateAgreement((AsymmetricKeyParameter)this.dhAgreePublicKey); 
    if (this.dhAgreePrivateKey != null)
      return TlsDHUtils.calculateDHBasicAgreement(this.dhAgreePublicKey, this.dhAgreePrivateKey); 
    throw new TlsFatalAlert((short)80);
  }
  
  protected int getMinimumPrimeBits() {
    return 1024;
  }
  
  protected DHParameters validateDHParameters(DHParameters paramDHParameters) throws IOException {
    if (paramDHParameters.getP().bitLength() < getMinimumPrimeBits())
      throw new TlsFatalAlert((short)71); 
    return TlsDHUtils.validateDHParameters(paramDHParameters);
  }
}
