package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;

public class DSAUtil {
  public static final ASN1ObjectIdentifier[] dsaOids = new ASN1ObjectIdentifier[] { X9ObjectIdentifiers.id_dsa, OIWObjectIdentifiers.dsaWithSHA1 };
  
  public static boolean isDsaOid(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    for (byte b = 0; b != dsaOids.length; b++) {
      if (paramASN1ObjectIdentifier.equals(dsaOids[b]))
        return true; 
    } 
    return false;
  }
  
  static DSAParameters toDSAParameters(DSAParams paramDSAParams) {
    return (paramDSAParams != null) ? new DSAParameters(paramDSAParams.getP(), paramDSAParams.getQ(), paramDSAParams.getG()) : null;
  }
  
  public static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof BCDSAPublicKey)
      return (AsymmetricKeyParameter)((BCDSAPublicKey)paramPublicKey).engineGetKeyParameters(); 
    if (paramPublicKey instanceof DSAPublicKey)
      return (AsymmetricKeyParameter)(new BCDSAPublicKey((DSAPublicKey)paramPublicKey)).engineGetKeyParameters(); 
    try {
      byte[] arrayOfByte = paramPublicKey.getEncoded();
      BCDSAPublicKey bCDSAPublicKey = new BCDSAPublicKey(SubjectPublicKeyInfo.getInstance(arrayOfByte));
      return (AsymmetricKeyParameter)bCDSAPublicKey.engineGetKeyParameters();
    } catch (Exception exception) {
      throw new InvalidKeyException("can't identify DSA public key: " + paramPublicKey.getClass().getName());
    } 
  }
  
  public static AsymmetricKeyParameter generatePrivateKeyParameter(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof DSAPrivateKey) {
      DSAPrivateKey dSAPrivateKey = (DSAPrivateKey)paramPrivateKey;
      return (AsymmetricKeyParameter)new DSAPrivateKeyParameters(dSAPrivateKey.getX(), new DSAParameters(dSAPrivateKey.getParams().getP(), dSAPrivateKey.getParams().getQ(), dSAPrivateKey.getParams().getG()));
    } 
    throw new InvalidKeyException("can't identify DSA private key.");
  }
  
  static String generateKeyFingerprint(BigInteger paramBigInteger, DSAParams paramDSAParams) {
    return (new Fingerprint(Arrays.concatenate(paramBigInteger.toByteArray(), paramDSAParams.getP().toByteArray(), paramDSAParams.getQ().toByteArray(), paramDSAParams.getG().toByteArray()))).toString();
  }
}
