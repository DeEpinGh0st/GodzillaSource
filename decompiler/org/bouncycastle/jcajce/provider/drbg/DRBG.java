package org.bouncycastle.jcajce.provider.drbg;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.SP800SecureRandom;
import org.bouncycastle.crypto.prng.SP800SecureRandomBuilder;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.symmetric.util.ClassUtil;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class DRBG {
  private static final String PREFIX = DRBG.class.getName();
  
  private static final String[][] initialEntropySourceNames = new String[][] { { "sun.security.provider.Sun", "sun.security.provider.SecureRandom" }, { "org.apache.harmony.security.provider.crypto.CryptoProvider", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl" }, { "com.android.org.conscrypt.OpenSSLProvider", "com.android.org.conscrypt.OpenSSLRandom" }, { "org.conscrypt.OpenSSLProvider", "org.conscrypt.OpenSSLRandom" } };
  
  private static final Object[] initialEntropySourceAndSpi = findSource();
  
  private static final Object[] findSource() {
    byte b = 0;
    while (b < initialEntropySourceNames.length) {
      String[] arrayOfString = initialEntropySourceNames[b];
      try {
        return new Object[] { Class.forName(arrayOfString[0]).newInstance(), Class.forName(arrayOfString[1]).newInstance() };
      } catch (Throwable throwable) {
        b++;
      } 
    } 
    return null;
  }
  
  private static SecureRandom createInitialEntropySource() {
    return (initialEntropySourceAndSpi != null) ? new CoreSecureRandom() : new SecureRandom();
  }
  
  private static EntropySourceProvider createEntropySource() {
    final String sourceClass = System.getProperty("org.bouncycastle.drbg.entropysource");
    return AccessController.<EntropySourceProvider>doPrivileged(new PrivilegedAction<EntropySourceProvider>() {
          public EntropySourceProvider run() {
            try {
              Class<EntropySourceProvider> clazz = ClassUtil.loadClass(DRBG.class, sourceClass);
              return clazz.newInstance();
            } catch (Exception exception) {
              throw new IllegalStateException("entropy source " + sourceClass + " not created: " + exception.getMessage(), exception);
            } 
          }
        });
  }
  
  private static SecureRandom createBaseRandom(boolean paramBoolean) {
    if (System.getProperty("org.bouncycastle.drbg.entropysource") != null) {
      EntropySourceProvider entropySourceProvider = createEntropySource();
      EntropySource entropySource = entropySourceProvider.get(128);
      byte[] arrayOfByte1 = paramBoolean ? generateDefaultPersonalizationString(entropySource.getEntropy()) : generateNonceIVPersonalizationString(entropySource.getEntropy());
      return (SecureRandom)(new SP800SecureRandomBuilder(entropySourceProvider)).setPersonalizationString(arrayOfByte1).buildHash((Digest)new SHA512Digest(), Arrays.concatenate(entropySource.getEntropy(), entropySource.getEntropy()), paramBoolean);
    } 
    HybridSecureRandom hybridSecureRandom = new HybridSecureRandom();
    byte[] arrayOfByte = paramBoolean ? generateDefaultPersonalizationString(hybridSecureRandom.generateSeed(16)) : generateNonceIVPersonalizationString(hybridSecureRandom.generateSeed(16));
    return (SecureRandom)(new SP800SecureRandomBuilder(hybridSecureRandom, true)).setPersonalizationString(arrayOfByte).buildHash((Digest)new SHA512Digest(), hybridSecureRandom.generateSeed(32), paramBoolean);
  }
  
  private static byte[] generateDefaultPersonalizationString(byte[] paramArrayOfbyte) {
    return Arrays.concatenate(Strings.toByteArray("Default"), paramArrayOfbyte, Pack.longToBigEndian(Thread.currentThread().getId()), Pack.longToBigEndian(System.currentTimeMillis()));
  }
  
  private static byte[] generateNonceIVPersonalizationString(byte[] paramArrayOfbyte) {
    return Arrays.concatenate(Strings.toByteArray("Nonce"), paramArrayOfbyte, Pack.longToLittleEndian(Thread.currentThread().getId()), Pack.longToLittleEndian(System.currentTimeMillis()));
  }
  
  private static class CoreSecureRandom extends SecureRandom {
    CoreSecureRandom() {
      super((SecureRandomSpi)DRBG.initialEntropySourceAndSpi[1], (Provider)DRBG.initialEntropySourceAndSpi[0]);
    }
  }
  
  public static class Default extends SecureRandomSpi {
    private static final SecureRandom random = DRBG.createBaseRandom(true);
    
    protected void engineSetSeed(byte[] param1ArrayOfbyte) {
      random.setSeed(param1ArrayOfbyte);
    }
    
    protected void engineNextBytes(byte[] param1ArrayOfbyte) {
      random.nextBytes(param1ArrayOfbyte);
    }
    
    protected byte[] engineGenerateSeed(int param1Int) {
      return random.generateSeed(param1Int);
    }
  }
  
  private static class HybridSecureRandom extends SecureRandom {
    private final AtomicBoolean seedAvailable = new AtomicBoolean(false);
    
    private final AtomicInteger samples = new AtomicInteger(0);
    
    private final SecureRandom baseRandom = DRBG.createInitialEntropySource();
    
    private final SP800SecureRandom drbg = (new SP800SecureRandomBuilder(new EntropySourceProvider() {
          public EntropySource get(int param2Int) {
            return new DRBG.HybridSecureRandom.SignallingEntropySource(param2Int);
          }
        })).setPersonalizationString(Strings.toByteArray("Bouncy Castle Hybrid Entropy Source")).buildHMAC((Mac)new HMac((Digest)new SHA512Digest()), this.baseRandom.generateSeed(32), false);
    
    HybridSecureRandom() {
      super(null, null);
    }
    
    public void setSeed(byte[] param1ArrayOfbyte) {
      if (this.drbg != null)
        this.drbg.setSeed(param1ArrayOfbyte); 
    }
    
    public void setSeed(long param1Long) {
      if (this.drbg != null)
        this.drbg.setSeed(param1Long); 
    }
    
    public byte[] generateSeed(int param1Int) {
      byte[] arrayOfByte = new byte[param1Int];
      if (this.samples.getAndIncrement() > 20 && this.seedAvailable.getAndSet(false)) {
        this.samples.set(0);
        this.drbg.reseed(null);
      } 
      this.drbg.nextBytes(arrayOfByte);
      return arrayOfByte;
    }
    
    private class SignallingEntropySource implements EntropySource {
      private final int byteLength;
      
      private final AtomicReference entropy = new AtomicReference();
      
      private final AtomicBoolean scheduled = new AtomicBoolean(false);
      
      SignallingEntropySource(int param2Int) {
        this.byteLength = (param2Int + 7) / 8;
      }
      
      public boolean isPredictionResistant() {
        return true;
      }
      
      public byte[] getEntropy() {
        byte[] arrayOfByte = this.entropy.getAndSet(null);
        if (arrayOfByte == null || arrayOfByte.length != this.byteLength) {
          arrayOfByte = DRBG.HybridSecureRandom.this.baseRandom.generateSeed(this.byteLength);
        } else {
          this.scheduled.set(false);
        } 
        if (!this.scheduled.getAndSet(true))
          (new Thread(new EntropyGatherer(this.byteLength))).start(); 
        return arrayOfByte;
      }
      
      public int entropySize() {
        return this.byteLength * 8;
      }
      
      private class EntropyGatherer implements Runnable {
        private final int numBytes;
        
        EntropyGatherer(int param3Int) {
          this.numBytes = param3Int;
        }
        
        public void run() {
          DRBG.HybridSecureRandom.SignallingEntropySource.this.entropy.set(DRBG.HybridSecureRandom.this.baseRandom.generateSeed(this.numBytes));
          DRBG.HybridSecureRandom.this.seedAvailable.set(true);
        }
      }
    }
  }
  
  public static class Mappings extends AsymmetricAlgorithmProvider {
    public void configure(ConfigurableProvider param1ConfigurableProvider) {
      param1ConfigurableProvider.addAlgorithm("SecureRandom.DEFAULT", DRBG.PREFIX + "$Default");
      param1ConfigurableProvider.addAlgorithm("SecureRandom.NONCEANDIV", DRBG.PREFIX + "$NonceAndIV");
    }
  }
  
  public static class NonceAndIV extends SecureRandomSpi {
    private static final SecureRandom random = DRBG.createBaseRandom(false);
    
    protected void engineSetSeed(byte[] param1ArrayOfbyte) {
      random.setSeed(param1ArrayOfbyte);
    }
    
    protected void engineNextBytes(byte[] param1ArrayOfbyte) {
      random.nextBytes(param1ArrayOfbyte);
    }
    
    protected byte[] engineGenerateSeed(int param1Int) {
      return random.generateSeed(param1Int);
    }
  }
}
