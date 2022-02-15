package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.util.io.TeeInputStream;

public class TlsDHEKeyExchange extends TlsDHKeyExchange {
  protected TlsSignerCredentials serverCredentials = null;
  
  public TlsDHEKeyExchange(int paramInt, Vector paramVector, DHParameters paramDHParameters) {
    super(paramInt, paramVector, paramDHParameters);
  }
  
  public void processServerCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    if (!(paramTlsCredentials instanceof TlsSignerCredentials))
      throw new TlsFatalAlert((short)80); 
    processServerCertificate(paramTlsCredentials.getCertificate());
    this.serverCredentials = (TlsSignerCredentials)paramTlsCredentials;
  }
  
  public byte[] generateServerKeyExchange() throws IOException {
    if (this.dhParameters == null)
      throw new TlsFatalAlert((short)80); 
    DigestInputBuffer digestInputBuffer = new DigestInputBuffer();
    this.dhAgreePrivateKey = TlsDHUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.dhParameters, digestInputBuffer);
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
    return digestInputBuffer.toByteArray();
  }
  
  public void processServerKeyExchange(InputStream paramInputStream) throws IOException {
    SecurityParameters securityParameters = this.context.getSecurityParameters();
    SignerInputBuffer signerInputBuffer = new SignerInputBuffer();
    TeeInputStream teeInputStream = new TeeInputStream(paramInputStream, signerInputBuffer);
    ServerDHParams serverDHParams = ServerDHParams.parse((InputStream)teeInputStream);
    DigitallySigned digitallySigned = parseSignature(paramInputStream);
    Signer signer = initVerifyer(this.tlsSigner, digitallySigned.getAlgorithm(), securityParameters);
    signerInputBuffer.updateSigner(signer);
    if (!signer.verifySignature(digitallySigned.getSignature()))
      throw new TlsFatalAlert((short)51); 
    this.dhAgreePublicKey = TlsDHUtils.validateDHPublicKey(serverDHParams.getPublicKey());
    this.dhParameters = validateDHParameters(this.dhAgreePublicKey.getParameters());
  }
  
  protected Signer initVerifyer(TlsSigner paramTlsSigner, SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, SecurityParameters paramSecurityParameters) {
    Signer signer = paramTlsSigner.createVerifyer(paramSignatureAndHashAlgorithm, this.serverPublicKey);
    signer.update(paramSecurityParameters.clientRandom, 0, paramSecurityParameters.clientRandom.length);
    signer.update(paramSecurityParameters.serverRandom, 0, paramSecurityParameters.serverRandom.length);
    return signer;
  }
}
