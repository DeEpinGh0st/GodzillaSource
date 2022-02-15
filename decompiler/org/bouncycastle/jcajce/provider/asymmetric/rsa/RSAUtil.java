package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;

public class RSAUtil {
  public static final ASN1ObjectIdentifier[] rsaOids = new ASN1ObjectIdentifier[] { PKCSObjectIdentifiers.rsaEncryption, X509ObjectIdentifiers.id_ea_rsa, PKCSObjectIdentifiers.id_RSAES_OAEP, PKCSObjectIdentifiers.id_RSASSA_PSS };
  
  public static boolean isRsaOid(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    for (byte b = 0; b != rsaOids.length; b++) {
      if (paramASN1ObjectIdentifier.equals(rsaOids[b]))
        return true; 
    } 
    return false;
  }
  
  static RSAKeyParameters generatePublicKeyParameter(RSAPublicKey paramRSAPublicKey) {
    return new RSAKeyParameters(false, paramRSAPublicKey.getModulus(), paramRSAPublicKey.getPublicExponent());
  }
  
  static RSAKeyParameters generatePrivateKeyParameter(RSAPrivateKey paramRSAPrivateKey) {
    if (paramRSAPrivateKey instanceof RSAPrivateCrtKey) {
      RSAPrivateCrtKey rSAPrivateCrtKey = (RSAPrivateCrtKey)paramRSAPrivateKey;
      return (RSAKeyParameters)new RSAPrivateCrtKeyParameters(rSAPrivateCrtKey.getModulus(), rSAPrivateCrtKey.getPublicExponent(), rSAPrivateCrtKey.getPrivateExponent(), rSAPrivateCrtKey.getPrimeP(), rSAPrivateCrtKey.getPrimeQ(), rSAPrivateCrtKey.getPrimeExponentP(), rSAPrivateCrtKey.getPrimeExponentQ(), rSAPrivateCrtKey.getCrtCoefficient());
    } 
    RSAPrivateKey rSAPrivateKey = paramRSAPrivateKey;
    return new RSAKeyParameters(true, rSAPrivateKey.getModulus(), rSAPrivateKey.getPrivateExponent());
  }
  
  static String generateKeyFingerprint(BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    return (new Fingerprint(Arrays.concatenate(paramBigInteger1.toByteArray(), paramBigInteger2.toByteArray()))).toString();
  }
}
