package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Vector;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.NaccacheSternKeyGenerationParameters;
import org.bouncycastle.crypto.params.NaccacheSternKeyParameters;
import org.bouncycastle.crypto.params.NaccacheSternPrivateKeyParameters;

public class NaccacheSternKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
  private static int[] smallPrimes = new int[] { 
      3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 
      37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 
      79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 
      131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 
      181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 
      239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 
      293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 
      359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 
      421, 431, 433, 439, 443, 449, 457, 461, 463, 467, 
      479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 
      557 };
  
  private NaccacheSternKeyGenerationParameters param;
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  public void init(KeyGenerationParameters paramKeyGenerationParameters) {
    this.param = (NaccacheSternKeyGenerationParameters)paramKeyGenerationParameters;
  }
  
  public AsymmetricCipherKeyPair generateKeyPair() {
    int i = this.param.getStrength();
    SecureRandom secureRandom = this.param.getRandom();
    int j = this.param.getCertainty();
    boolean bool = this.param.isDebug();
    if (bool)
      System.out.println("Fetching first " + this.param.getCntSmallPrimes() + " primes."); 
    Vector<BigInteger> vector = findFirstPrimes(this.param.getCntSmallPrimes());
    vector = permuteList(vector, secureRandom);
    BigInteger bigInteger1 = ONE;
    BigInteger bigInteger2 = ONE;
    int k;
    for (k = 0; k < vector.size() / 2; k++)
      bigInteger1 = bigInteger1.multiply(vector.elementAt(k)); 
    for (k = vector.size() / 2; k < vector.size(); k++)
      bigInteger2 = bigInteger2.multiply(vector.elementAt(k)); 
    BigInteger bigInteger3 = bigInteger1.multiply(bigInteger2);
    int m = i - bigInteger3.bitLength() - 48;
    BigInteger bigInteger4 = generatePrime(m / 2 + 1, j, secureRandom);
    BigInteger bigInteger5 = generatePrime(m / 2 + 1, j, secureRandom);
    long l = 0L;
    if (bool)
      System.out.println("generating p and q"); 
    BigInteger bigInteger6 = bigInteger4.multiply(bigInteger1).shiftLeft(1);
    BigInteger bigInteger7 = bigInteger5.multiply(bigInteger2).shiftLeft(1);
    while (true) {
      l++;
      BigInteger bigInteger8 = generatePrime(24, j, secureRandom);
      BigInteger bigInteger9 = bigInteger8.multiply(bigInteger6).add(ONE);
      if (!bigInteger9.isProbablePrime(j))
        continue; 
      while (true) {
        BigInteger bigInteger10 = generatePrime(24, j, secureRandom);
        if (bigInteger8.equals(bigInteger10))
          continue; 
        BigInteger bigInteger11 = bigInteger10.multiply(bigInteger7).add(ONE);
        if (!bigInteger11.isProbablePrime(j) || !bigInteger3.gcd(bigInteger8.multiply(bigInteger10)).equals(ONE))
          continue; 
        if (bigInteger9.multiply(bigInteger11).bitLength() < i) {
          if (bool)
            System.out.println("key size too small. Should be " + i + " but is actually " + bigInteger9.multiply(bigInteger11).bitLength()); 
          continue;
        } 
        if (bool)
          System.out.println("needed " + l + " tries to generate p and q."); 
        BigInteger bigInteger12 = bigInteger9.multiply(bigInteger11);
        BigInteger bigInteger13 = bigInteger9.subtract(ONE).multiply(bigInteger11.subtract(ONE));
        l = 0L;
        if (bool)
          System.out.println("generating g"); 
        while (true) {
          Vector<BigInteger> vector1 = new Vector();
          byte b1;
          for (b1 = 0; b1 != vector.size(); b1++) {
            BigInteger bigInteger14 = vector.elementAt(b1);
            BigInteger bigInteger15 = bigInteger13.divide(bigInteger14);
            while (true) {
              l++;
              BigInteger bigInteger16 = new BigInteger(i, j, secureRandom);
              if (bigInteger16.modPow(bigInteger15, bigInteger12).equals(ONE))
                continue; 
              vector1.addElement(bigInteger16);
              break;
            } 
          } 
          BigInteger bigInteger = ONE;
          for (b1 = 0; b1 < vector.size(); b1++)
            bigInteger = bigInteger.multiply(((BigInteger)vector1.elementAt(b1)).modPow(bigInteger3.divide(vector.elementAt(b1)), bigInteger12)).mod(bigInteger12); 
          b1 = 0;
          for (byte b2 = 0; b2 < vector.size(); b2++) {
            if (bigInteger.modPow(bigInteger13.divide(vector.elementAt(b2)), bigInteger12).equals(ONE)) {
              if (bool)
                System.out.println("g has order phi(n)/" + vector.elementAt(b2) + "\n g: " + bigInteger); 
              b1 = 1;
              break;
            } 
          } 
          if (b1 != 0)
            continue; 
          if (bigInteger.modPow(bigInteger13.divide(BigInteger.valueOf(4L)), bigInteger12).equals(ONE)) {
            if (bool)
              System.out.println("g has order phi(n)/4\n g:" + bigInteger); 
            continue;
          } 
          if (bigInteger.modPow(bigInteger13.divide(bigInteger8), bigInteger12).equals(ONE)) {
            if (bool)
              System.out.println("g has order phi(n)/p'\n g: " + bigInteger); 
            continue;
          } 
          if (bigInteger.modPow(bigInteger13.divide(bigInteger10), bigInteger12).equals(ONE)) {
            if (bool)
              System.out.println("g has order phi(n)/q'\n g: " + bigInteger); 
            continue;
          } 
          if (bigInteger.modPow(bigInteger13.divide(bigInteger4), bigInteger12).equals(ONE)) {
            if (bool)
              System.out.println("g has order phi(n)/a\n g: " + bigInteger); 
            continue;
          } 
          if (bigInteger.modPow(bigInteger13.divide(bigInteger5), bigInteger12).equals(ONE)) {
            if (bool)
              System.out.println("g has order phi(n)/b\n g: " + bigInteger); 
            continue;
          } 
          if (bool) {
            System.out.println("needed " + l + " tries to generate g");
            System.out.println();
            System.out.println("found new NaccacheStern cipher variables:");
            System.out.println("smallPrimes: " + vector);
            System.out.println("sigma:...... " + bigInteger3 + " (" + bigInteger3.bitLength() + " bits)");
            System.out.println("a:.......... " + bigInteger4);
            System.out.println("b:.......... " + bigInteger5);
            System.out.println("p':......... " + bigInteger8);
            System.out.println("q':......... " + bigInteger10);
            System.out.println("p:.......... " + bigInteger9);
            System.out.println("q:.......... " + bigInteger11);
            System.out.println("n:.......... " + bigInteger12);
            System.out.println("phi(n):..... " + bigInteger13);
            System.out.println("g:.......... " + bigInteger);
            System.out.println();
          } 
          return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)new NaccacheSternKeyParameters(false, bigInteger, bigInteger12, bigInteger3.bitLength()), (AsymmetricKeyParameter)new NaccacheSternPrivateKeyParameters(bigInteger, bigInteger12, bigInteger3.bitLength(), vector, bigInteger13));
        } 
        break;
      } 
      break;
    } 
  }
  
  private static BigInteger generatePrime(int paramInt1, int paramInt2, SecureRandom paramSecureRandom) {
    BigInteger bigInteger;
    for (bigInteger = new BigInteger(paramInt1, paramInt2, paramSecureRandom); bigInteger.bitLength() != paramInt1; bigInteger = new BigInteger(paramInt1, paramInt2, paramSecureRandom));
    return bigInteger;
  }
  
  private static Vector permuteList(Vector paramVector, SecureRandom paramSecureRandom) {
    Vector vector1 = new Vector();
    Vector vector2 = new Vector();
    for (byte b = 0; b < paramVector.size(); b++)
      vector2.addElement(paramVector.elementAt(b)); 
    vector1.addElement(vector2.elementAt(0));
    vector2.removeElementAt(0);
    while (vector2.size() != 0) {
      vector1.insertElementAt(vector2.elementAt(0), getInt(paramSecureRandom, vector1.size() + 1));
      vector2.removeElementAt(0);
    } 
    return vector1;
  }
  
  private static int getInt(SecureRandom paramSecureRandom, int paramInt) {
    if ((paramInt & -paramInt) == paramInt)
      return (int)(paramInt * (paramSecureRandom.nextInt() & Integer.MAX_VALUE) >> 31L); 
    while (true) {
      int i = paramSecureRandom.nextInt() & Integer.MAX_VALUE;
      int j = i % paramInt;
      if (i - j + paramInt - 1 >= 0)
        return j; 
    } 
  }
  
  private static Vector findFirstPrimes(int paramInt) {
    Vector<BigInteger> vector = new Vector(paramInt);
    for (int i = 0; i != paramInt; i++)
      vector.addElement(BigInteger.valueOf(smallPrimes[i])); 
    return vector;
  }
}
