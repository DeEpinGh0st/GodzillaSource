package org.bouncycastle.cms.jcajce;

import java.security.PrivateKey;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.operator.SymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyUnwrapper;

interface JcaJceExtHelper extends JcaJceHelper {
  JceAsymmetricKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, PrivateKey paramPrivateKey);
  
  JceKTSKeyUnwrapper createAsymmetricUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, PrivateKey paramPrivateKey, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2);
  
  SymmetricKeyUnwrapper createSymmetricUnwrapper(AlgorithmIdentifier paramAlgorithmIdentifier, SecretKey paramSecretKey);
}
