package org.bouncycastle.pqc.crypto.sphincs;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;

public class SPHINCS256KeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
  private SecureRandom random;
  
  private Digest treeDigest;
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    this.random = paramKeyGenerationParameters.getRandom();
    this.treeDigest = ((SPHINCS256KeyGenerationParameters)paramKeyGenerationParameters).getTreeDigest();
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    Tree.leafaddr leafaddr = new Tree.leafaddr();
    byte[] arrayOfByte1 = new byte[1088];
    this.random.nextBytes(arrayOfByte1);
    byte[] arrayOfByte2 = new byte[1056];
    System.arraycopy(arrayOfByte1, 32, arrayOfByte2, 0, 1024);
    leafaddr.level = 11;
    leafaddr.subtree = 0L;
    leafaddr.subleaf = 0L;
    HashFunctions hashFunctions = new HashFunctions(this.treeDigest);
    Tree.treehash(hashFunctions, arrayOfByte2, 1024, 5, arrayOfByte1, leafaddr, arrayOfByte2, 0);
    return new AsymmetricCipherKeyPair(new SPHINCSPublicKeyParameters(arrayOfByte2), new SPHINCSPrivateKeyParameters(arrayOfByte1));
  }
}
