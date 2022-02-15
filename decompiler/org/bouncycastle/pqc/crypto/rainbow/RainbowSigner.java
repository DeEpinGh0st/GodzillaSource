package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.pqc.crypto.rainbow.util.ComputeInField;
import org.bouncycastle.pqc.crypto.rainbow.util.GF2Field;

public class RainbowSigner implements MessageSigner {
  private static final int MAXITS = 65536;
  
  private SecureRandom random;
  
  int signableDocumentLength;
  
  private short[] x;
  
  private ComputeInField cf = new ComputeInField();
  
  RainbowKeyParameters key;
  
  public void init(boolean paramBoolean, CipherParameters paramCipherParameters) {
    if (paramBoolean) {
      if (paramCipherParameters instanceof ParametersWithRandom) {
        ParametersWithRandom parametersWithRandom = (ParametersWithRandom)paramCipherParameters;
        this.random = parametersWithRandom.getRandom();
        this.key = (RainbowPrivateKeyParameters)parametersWithRandom.getParameters();
      } else {
        this.random = new SecureRandom();
        this.key = (RainbowPrivateKeyParameters)paramCipherParameters;
      } 
    } else {
      this.key = (RainbowPublicKeyParameters)paramCipherParameters;
    } 
    this.signableDocumentLength = this.key.getDocLength();
  }
  
  private short[] initSign(Layer[] paramArrayOfLayer, short[] paramArrayOfshort) {
    short[] arrayOfShort1 = new short[paramArrayOfshort.length];
    arrayOfShort1 = this.cf.addVect(((RainbowPrivateKeyParameters)this.key).getB1(), paramArrayOfshort);
    short[] arrayOfShort2 = this.cf.multiplyMatrix(((RainbowPrivateKeyParameters)this.key).getInvA1(), arrayOfShort1);
    for (byte b = 0; b < paramArrayOfLayer[0].getVi(); b++) {
      this.x[b] = (short)this.random.nextInt();
      this.x[b] = (short)(this.x[b] & 0xFF);
    } 
    return arrayOfShort2;
  }
  
  public byte[] generateSignature(byte[] paramArrayOfbyte) {
    boolean bool;
    Layer[] arrayOfLayer = ((RainbowPrivateKeyParameters)this.key).getLayers();
    int i = arrayOfLayer.length;
    this.x = new short[(((RainbowPrivateKeyParameters)this.key).getInvA2()).length];
    byte[] arrayOfByte = new byte[arrayOfLayer[i - 1].getViNext()];
    short[] arrayOfShort = makeMessageRepresentative(paramArrayOfbyte);
    byte b = 0;
    do {
      bool = true;
      byte b1 = 0;
      try {
        short[] arrayOfShort1 = initSign(arrayOfLayer, arrayOfShort);
        byte b2;
        for (b2 = 0; b2 < i; b2++) {
          short[] arrayOfShort4 = new short[arrayOfLayer[b2].getOi()];
          short[] arrayOfShort5 = new short[arrayOfLayer[b2].getOi()];
          byte b3;
          for (b3 = 0; b3 < arrayOfLayer[b2].getOi(); b3++) {
            arrayOfShort4[b3] = arrayOfShort1[b1];
            b1++;
          } 
          arrayOfShort5 = this.cf.solveEquation(arrayOfLayer[b2].plugInVinegars(this.x), arrayOfShort4);
          if (arrayOfShort5 == null)
            throw new Exception("LES is not solveable!"); 
          for (b3 = 0; b3 < arrayOfShort5.length; b3++)
            this.x[arrayOfLayer[b2].getVi() + b3] = arrayOfShort5[b3]; 
        } 
        short[] arrayOfShort2 = this.cf.addVect(((RainbowPrivateKeyParameters)this.key).getB2(), this.x);
        short[] arrayOfShort3 = this.cf.multiplyMatrix(((RainbowPrivateKeyParameters)this.key).getInvA2(), arrayOfShort2);
        for (b2 = 0; b2 < arrayOfByte.length; b2++)
          arrayOfByte[b2] = (byte)arrayOfShort3[b2]; 
      } catch (Exception exception) {
        bool = false;
      } 
    } while (!bool && ++b < 65536);
    if (b == 65536)
      throw new IllegalStateException("unable to generate signature - LES not solvable"); 
    return arrayOfByte;
  }
  
  public boolean verifySignature(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    short[] arrayOfShort1 = new short[paramArrayOfbyte2.length];
    for (byte b1 = 0; b1 < paramArrayOfbyte2.length; b1++) {
      short s = (short)paramArrayOfbyte2[b1];
      s = (short)(s & 0xFF);
      arrayOfShort1[b1] = s;
    } 
    short[] arrayOfShort2 = makeMessageRepresentative(paramArrayOfbyte1);
    short[] arrayOfShort3 = verifySignatureIntern(arrayOfShort1);
    boolean bool = true;
    if (arrayOfShort2.length != arrayOfShort3.length)
      return false; 
    for (byte b2 = 0; b2 < arrayOfShort2.length; b2++)
      bool = (bool && arrayOfShort2[b2] == arrayOfShort3[b2]) ? true : false; 
    return bool;
  }
  
  private short[] verifySignatureIntern(short[] paramArrayOfshort) {
    short[][] arrayOfShort1 = ((RainbowPublicKeyParameters)this.key).getCoeffQuadratic();
    short[][] arrayOfShort2 = ((RainbowPublicKeyParameters)this.key).getCoeffSingular();
    short[] arrayOfShort3 = ((RainbowPublicKeyParameters)this.key).getCoeffScalar();
    short[] arrayOfShort4 = new short[arrayOfShort1.length];
    int i = (arrayOfShort2[0]).length;
    byte b1 = 0;
    short s = 0;
    for (byte b2 = 0; b2 < arrayOfShort1.length; b2++) {
      b1 = 0;
      for (byte b = 0; b < i; b++) {
        for (byte b3 = b; b3 < i; b3++) {
          s = GF2Field.multElem(arrayOfShort1[b2][b1], GF2Field.multElem(paramArrayOfshort[b], paramArrayOfshort[b3]));
          arrayOfShort4[b2] = GF2Field.addElem(arrayOfShort4[b2], s);
          b1++;
        } 
        s = GF2Field.multElem(arrayOfShort2[b2][b], paramArrayOfshort[b]);
        arrayOfShort4[b2] = GF2Field.addElem(arrayOfShort4[b2], s);
      } 
      arrayOfShort4[b2] = GF2Field.addElem(arrayOfShort4[b2], arrayOfShort3[b2]);
    } 
    return arrayOfShort4;
  }
  
  private short[] makeMessageRepresentative(byte[] paramArrayOfbyte) {
    short[] arrayOfShort = new short[this.signableDocumentLength];
    byte b1 = 0;
    byte b2 = 0;
    while (b2 < paramArrayOfbyte.length) {
      arrayOfShort[b2] = (short)paramArrayOfbyte[b1];
      arrayOfShort[b2] = (short)(arrayOfShort[b2] & 0xFF);
      b1++;
      if (++b2 >= arrayOfShort.length)
        break; 
    } 
    return arrayOfShort;
  }
}
