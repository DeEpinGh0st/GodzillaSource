package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.util.io.TeeInputStream;

public class TlsECDHEKeyExchange extends TlsECDHKeyExchange {
  protected TlsSignerCredentials serverCredentials = null;
  
  public TlsECDHEKeyExchange(int paramInt, Vector paramVector, int[] paramArrayOfint, short[] paramArrayOfshort1, short[] paramArrayOfshort2) {
    super(paramInt, paramVector, paramArrayOfint, paramArrayOfshort1, paramArrayOfshort2);
  }
  
  public void processServerCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    if (!(paramTlsCredentials instanceof TlsSignerCredentials))
      throw new TlsFatalAlert((short)80); 
    processServerCertificate(paramTlsCredentials.getCertificate());
    this.serverCredentials = (TlsSignerCredentials)paramTlsCredentials;
  }
  
  public byte[] generateServerKeyExchange() throws IOException {
    DigestInputBuffer digestInputBuffer = new DigestInputBuffer();
    this.ecAgreePrivateKey = TlsECCUtils.generateEphemeralServerKeyExchange(this.context.getSecureRandom(), this.namedCurves, this.clientECPointFormats, digestInputBuffer);
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
    ECDomainParameters eCDomainParameters = TlsECCUtils.readECParameters(this.namedCurves, this.clientECPointFormats, (InputStream)teeInputStream);
    byte[] arrayOfByte = TlsUtils.readOpaque8((InputStream)teeInputStream);
    DigitallySigned digitallySigned = parseSignature(paramInputStream);
    Signer signer = initVerifyer(this.tlsSigner, digitallySigned.getAlgorithm(), securityParameters);
    signerInputBuffer.updateSigner(signer);
    if (!signer.verifySignature(digitallySigned.getSignature()))
      throw new TlsFatalAlert((short)51); 
    this.ecAgreePublicKey = TlsECCUtils.validateECPublicKey(TlsECCUtils.deserializeECPublicKey(this.clientECPointFormats, eCDomainParameters, arrayOfByte));
  }
  
  public void validateCertificateRequest(CertificateRequest paramCertificateRequest) throws IOException {
    short[] arrayOfShort = paramCertificateRequest.getCertificateTypes();
    for (byte b = 0; b < arrayOfShort.length; b++) {
      switch (arrayOfShort[b]) {
        case 1:
        case 2:
        case 64:
          break;
        default:
          throw new TlsFatalAlert((short)47);
      } 
    } 
  }
  
  public void processClientCredentials(TlsCredentials paramTlsCredentials) throws IOException {
    if (paramTlsCredentials instanceof TlsSignerCredentials)
      return; 
    throw new TlsFatalAlert((short)80);
  }
  
  protected Signer initVerifyer(TlsSigner paramTlsSigner, SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm, SecurityParameters paramSecurityParameters) {
    Signer signer = paramTlsSigner.createVerifyer(paramSignatureAndHashAlgorithm, this.serverPublicKey);
    signer.update(paramSecurityParameters.clientRandom, 0, paramSecurityParameters.clientRandom.length);
    signer.update(paramSecurityParameters.serverRandom, 0, paramSecurityParameters.serverRandom.length);
    return signer;
  }
}
