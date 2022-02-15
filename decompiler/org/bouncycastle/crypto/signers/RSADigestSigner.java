package org.bouncycastle.crypto.signers;

import java.io.IOException;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSABlindedEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;

public class RSADigestSigner implements Signer {
  private final AsymmetricBlockCipher rsaEngine = (AsymmetricBlockCipher)new PKCS1Encoding((AsymmetricBlockCipher)new RSABlindedEngine());
  
  private final AlgorithmIdentifier algId;
  
  private final Digest digest;
  
  private boolean forSigning;
  
  private static final Hashtable oidMap = new Hashtable<Object, Object>();
  
  public RSADigestSigner(Digest paramDigest) {
    this(paramDigest, (ASN1ObjectIdentifier)oidMap.get(paramDigest.getAlgorithmName()));
  }
  
  public RSADigestSigner(Digest paramDigest, ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.digest = paramDigest;
    this.algId = new AlgorithmIdentifier(paramASN1ObjectIdentifier, (ASN1Encodable)DERNull.INSTANCE);
  }
  
  public String getAlgorithmName() {
    return this.digest.getAlgorithmName() + "withRSA";
  }
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    AsymmetricKeyParameter asymmetricKeyParameter;
    this.forSigning = paramBoolean;
    if (paramCipherParameters instanceof ParametersWithRandom) {
      asymmetricKeyParameter = (AsymmetricKeyParameter)((ParametersWithRandom)paramCipherParameters).getParameters();
    } else {
      asymmetricKeyParameter = (AsymmetricKeyParameter)paramCipherParameters;
    } 
    if (paramBoolean && !asymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("signing requires private key"); 
    if (!paramBoolean && asymmetricKeyParameter.isPrivate())
      throw new IllegalArgumentException("verification requires public key"); 
    reset();
    this.rsaEngine.init(paramBoolean, paramCipherParameters);
  }
  
  public void update(byte paramByte) {
    this.digest.update(paramByte);
  }
  
  public void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.digest.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  public byte[] generateSignature() throws CryptoException, DataLengthException {
    if (!this.forSigning)
      throw new IllegalStateException("RSADigestSigner not initialised for signature generation."); 
    byte[] arrayOfByte = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte, 0);
    try {
      byte[] arrayOfByte1 = derEncode(arrayOfByte);
      return this.rsaEngine.processBlock(arrayOfByte1, 0, arrayOfByte1.length);
    } catch (IOException iOException) {
      throw new CryptoException("unable to encode signature: " + iOException.getMessage(), iOException);
    } 
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte) {
    byte[] arrayOfByte2;
    byte[] arrayOfByte3;
    if (this.forSigning)
      throw new IllegalStateException("RSADigestSigner not initialised for verification"); 
    byte[] arrayOfByte1 = new byte[this.digest.getDigestSize()];
    this.digest.doFinal(arrayOfByte1, 0);
    try {
      arrayOfByte2 = this.rsaEngine.processBlock(paramArrayOfbyte, 0, paramArrayOfbyte.length);
      arrayOfByte3 = derEncode(arrayOfByte1);
    } catch (Exception exception) {
      return false;
    } 
    if (arrayOfByte2.length == arrayOfByte3.length)
      return Arrays.constantTimeAreEqual(arrayOfByte2, arrayOfByte3); 
    if (arrayOfByte2.length == arrayOfByte3.length - 2) {
      int i = arrayOfByte2.length - arrayOfByte1.length - 2;
      int j = arrayOfByte3.length - arrayOfByte1.length - 2;
      arrayOfByte3[1] = (byte)(arrayOfByte3[1] - 2);
      arrayOfByte3[3] = (byte)(arrayOfByte3[3] - 2);
      int k = 0;
      byte b;
      for (b = 0; b < arrayOfByte1.length; b++)
        k |= arrayOfByte2[i + b] ^ arrayOfByte3[j + b]; 
      for (b = 0; b < i; b++)
        k |= arrayOfByte2[b] ^ arrayOfByte3[b]; 
      return (k == 0);
    } 
    Arrays.constantTimeAreEqual(arrayOfByte3, arrayOfByte3);
    return false;
  }
  
  public void reset() {
    this.digest.reset();
  }
  
  private byte[] derEncode(byte[] paramArrayOfbyte) throws IOException {
    DigestInfo digestInfo = new DigestInfo(this.algId, paramArrayOfbyte);
    return digestInfo.getEncoded("DER");
  }
  
  static {
    oidMap.put("RIPEMD128", TeleTrusTObjectIdentifiers.ripemd128);
    oidMap.put("RIPEMD160", TeleTrusTObjectIdentifiers.ripemd160);
    oidMap.put("RIPEMD256", TeleTrusTObjectIdentifiers.ripemd256);
    oidMap.put("SHA-1", X509ObjectIdentifiers.id_SHA1);
    oidMap.put("SHA-224", NISTObjectIdentifiers.id_sha224);
    oidMap.put("SHA-256", NISTObjectIdentifiers.id_sha256);
    oidMap.put("SHA-384", NISTObjectIdentifiers.id_sha384);
    oidMap.put("SHA-512", NISTObjectIdentifiers.id_sha512);
    oidMap.put("SHA-512/224", NISTObjectIdentifiers.id_sha512_224);
    oidMap.put("SHA-512/256", NISTObjectIdentifiers.id_sha512_256);
    oidMap.put("SHA3-224", NISTObjectIdentifiers.id_sha3_224);
    oidMap.put("SHA3-256", NISTObjectIdentifiers.id_sha3_256);
    oidMap.put("SHA3-384", NISTObjectIdentifiers.id_sha3_384);
    oidMap.put("SHA3-512", NISTObjectIdentifiers.id_sha3_512);
    oidMap.put("MD2", PKCSObjectIdentifiers.md2);
    oidMap.put("MD4", PKCSObjectIdentifiers.md4);
    oidMap.put("MD5", PKCSObjectIdentifiers.md5);
  }
}
