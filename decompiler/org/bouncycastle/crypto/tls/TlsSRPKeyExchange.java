package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Vector;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.agreement.srp.SRP6Client;
import org.bouncycastle.crypto.agreement.srp.SRP6Server;
import org.bouncycastle.crypto.agreement.srp.SRP6Util;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.SRP6GroupParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.io.TeeInputStream;

public class TlsSRPKeyExchange extends AbstractTlsKeyExchange {
  protected TlsSigner tlsSigner;
  
  protected TlsSRPGroupVerifier groupVerifier;
  
  protected byte[] identity;
  
  protected byte[] password;
  
  protected AsymmetricKeyParameter serverPublicKey = null;
  
  protected SRP6GroupParameters srpGroup = null;
  
  protected SRP6Client srpClient = null;
  
  protected SRP6Server srpServer = null;
  
  protected BigInteger srpPeerCredentials = null;
  
  protected BigInteger srpVerifier = null;
  
  protected byte[] srpSalt = null;
  
  protected TlsSignerCredentials serverCredentials = null;
  
  protected static TlsSigner createSigner(int paramInt) {
    switch (paramInt) {
      case 21:
        return null;
      case 23:
        return new TlsRSASigner();
      case 22:
        return new TlsDSSSigner();
    } 
    throw new IllegalArgumentException("unsupported key exchange algorithm");
  }
  
  public TlsSRPKeyExchange(int paramInt, Vector paramVector, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this(paramInt, paramVector, new DefaultTlsSRPGroupVerifier(), paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  public TlsSRPKeyExchange(int paramInt, Vector paramVector, TlsSRPGroupVerifier paramTlsSRPGroupVerifier, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    super(paramInt, paramVector);
    this.tlsSigner = createSigner(paramInt);
    this.groupVerifier = paramTlsSRPGroupVerifier;
    this.identity = paramArrayOfbyte1;
    this.password = paramArrayOfbyte2;
    this.srpClient = new SRP6Client();
  }
  
  public TlsSRPKeyExchange(int paramInt, Vector paramVector, byte[] paramArrayOfbyte, TlsSRPLoginParameters paramTlsSRPLoginParameters) {
    super(paramInt, paramVector);
    this.tlsSigner = createSigner(paramInt);
    this.identity = paramArrayOfbyte;
    this.srpServer = new SRP6Server();
    this.srpGroup = paramTlsSRPLoginParameters.getGroup();
    this.srpVerifier = paramTlsSRPLoginParameters.getVerifier();
    this.srpSalt = paramTlsSRPLoginParameters.getSalt();
  }
  
  public void init(TlsContext paramTlsContext) {
    super.init(paramTlsContext);
    if (this.tlsSigner != null)
      this.tlsSigner.init(paramTlsContext); 
  }
  
  public void skipServerCredentials() throws IOException {
    if (this.tlsSigner != null)
      throw new TlsFatalAlert((short)10); 
  }
  
  public void processServerCertificate(Certificate paramCertificate) throws IOException {
    if (this.tlsSigner == null)
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
    if (!this.tlsSigner.isValidPublicKey(this.serverPublicKey))
      throw new TlsFatalAlert((short)46); 
    TlsUtils.validateKeyUsage(certificate, 128);
    super.processServerCertificate(paramCertificate);
  }
  
  public void processServerCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    if (this.keyExchange == 21 || !(paramTlsCredentials instanceof TlsSignerCredentials))
      throw new TlsFatalAlert((short)80); 
    processServerCertificate(paramTlsCredentials.getCertificate());
    this.serverCredentials = (TlsSignerCredentials)paramTlsCredentials;
  }
  
  public boolean requiresServerKeyExchange() {
    return true;
  }
  
  public byte[] generateServerKeyExchange() throws IOException {
    this.srpServer.init(this.srpGroup, this.srpVerifier, TlsUtils.createHash((short)2), this.context.getSecureRandom());
    BigInteger bigInteger = this.srpServer.generateServerCredentials();
    ServerSRPParams serverSRPParams = new ServerSRPParams(this.srpGroup.getN(), this.srpGroup.getG(), this.srpSalt, bigInteger);
    DigestInputBuffer digestInputBuffer = new DigestInputBuffer();
    serverSRPParams.encode(digestInputBuffer);
    if (this.serverCredentials != null) {
      SignatureAndHashAlgorithm signatureAndHashAlgorithm = TlsUtils.getSignatureAndHashAlgorithm(this.context, this.serverCredentials);
      Digest digest = TlsUtils.createHash(signatureAndHashAlgorithm);
      SecurityParameters securityParameters = this.context.getSecurityParameters();
      digest.update(securityParameters.clientRandom, 0, securityParameters.clientRandom.length);
      digest.update(securityParameters.serverRandom, 0, securityParameters.serverRandom.length);
      digestInputBuffer.updateDigest(digest);
      byte[] arrayOfByte1 = new byte[digest.getDigestSize()];
      digest.doFinal(arrayOfByte1, 0);
      byte[] arrayOfByte2 = this.serverCredentials.generateCertificateSignature(arrayOfByte1);
      DigitallySigned digitallySigned = new DigitallySigned(signatureAndHashAlgorithm, arrayOfByte2);
      digitallySigned.encode(digestInputBuffer);
    } 
    return digestInputBuffer.toByteArray();
  }
  
  public void processServerKeyExchange(InputStream paramInputStream) throws IOException {
    TeeInputStream teeInputStream;
    SecurityParameters securityParameters = this.context.getSecurityParameters();
    SignerInputBuffer signerInputBuffer = null;
    InputStream inputStream = paramInputStream;
    if (this.tlsSigner != null) {
      signerInputBuffer = new SignerInputBuffer();
      teeInputStream = new TeeInputStream(paramInputStream, signerInputBuffer);
    } 
    ServerSRPParams serverSRPParams = ServerSRPParams.parse((InputStream)teeInputStream);
    if (signerInputBuffer != null) {
      DigitallySigned digitallySigned = parseSignature(paramInputStream);
      Signer signer = initVerifyer(this.tlsSigner, digitallySigned.getAlgorithm(), securityParameters);
      signerInputBuffer.updateSigner(signer);
      if (!signer.verifySignature(digitallySigned.getSignature()))
        throw new TlsFatalAlert((short)51); 
    } 
    this.srpGroup = new SRP6GroupParameters(serverSRPParams.getN(), serverSRPParams.getG());
    if (!this.groupVerifier.accept(this.srpGroup))
      throw new TlsFatalAlert((short)71); 
    this.srpSalt = serverSRPParams.getS();
    try {
      this.srpPeerCredentials = SRP6Util.validatePublicValue(this.srpGroup.getN(), serverSRPParams.getB());
    } catch (CryptoException cryptoException) {
      throw new TlsFatalAlert((short)47, cryptoException);
    } 
    this.srpClient.init(this.srpGroup, TlsUtils.createHash((short)2), this.context.getSecureRandom());
  }
  
  public void validateCertificateRequest(CertificateRequest paramCertificateRequest) throws IOException {
    throw new TlsFatalAlert((short)10);
  }
  
  public void processClientCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  public void generateClientKeyExchange(OutputStream paramOutputStream) throws IOException {
    BigInteger bigInteger = this.srpClient.generateClientCredentials(this.srpSalt, this.identity, this.password);
    TlsSRPUtils.writeSRPParameter(bigInteger, paramOutputStream);
    (this.context.getSecurityParameters()).srpIdentity = Arrays.clone(this.identity);
  }
  
  public void processClientKeyExchange(InputStream paramInputStream) throws IOException {
    try {
      this.srpPeerCredentials = SRP6Util.validatePublicValue(this.srpGroup.getN(), TlsSRPUtils.readSRPParameter(paramInputStream));
    } catch (CryptoException cryptoException) {
      throw new TlsFatalAlert((short)47, cryptoException);
    } 
    (this.context.getSecurityParameters()).srpIdentity = Arrays.clone(this.identity);
  }
  
  public byte[] generatePremasterSecret() throws IOException {
    try {
      BigInteger bigInteger = (this.srpServer != null) ? this.srpServer.calculateSecret(this.srpPeerCredentials) : this.srpClient.calculateSecret(this.srpPeerCredentials);
      return BigIntegers.asUnsignedByteArray(bigInteger);
    } catch (CryptoException cryptoException) {
      throw new TlsFatalAlert((short)47, cryptoException);
    } 
  }
  
  protected Signer initVerifyer(TlsSigner paramTlsSigner, SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, SecurityParameters paramSecurityParameters) {
    Signer signer = paramTlsSigner.createVerifyer(paramSignatureAndHashAlgorithm, this.serverPublicKey);
    signer.update(paramSecurityParameters.clientRandom, 0, paramSecurityParameters.clientRandom.length);
    signer.update(paramSecurityParameters.serverRandom, 0, paramSecurityParameters.serverRandom.length);
    return signer;
  }
}
