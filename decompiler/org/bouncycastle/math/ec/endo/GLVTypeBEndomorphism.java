package org.bouncycastle.math.ec.endo;

import java.math.BigInteger;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPointMap;
import org.bouncycastle.math.ec.ScaleXPointMap;

public class GLVTypeBEndomorphism implements GLVEndomorphism {
  protected final ECCurve curve;
  
  protected final GLVTypeBParameters parameters;
  
  protected final ECPointMap pointMap;
  
  public GLVTypeBEndomorphism(ECCurve paramECCurve, GLVTypeBParameters paramGLVTypeBParameters) {
    this.curve = paramECCurve;
    this.parameters = paramGLVTypeBParameters;
    this.pointMap = (ECPointMap)new ScaleXPointMap(paramECCurve.fromBigInteger(paramGLVTypeBParameters.getBeta()));
  }
  
  public BigInteger[] decomposeScalar(BigInteger paramBigInteger) {
    int i = this.parameters.getBits();
    BigInteger bigInteger1 = calculateB(paramBigInteger, this.parameters.getG1(), i);
    BigInteger bigInteger2 = calculateB(paramBigInteger, this.parameters.getG2(), i);
    GLVTypeBParameters gLVTypeBParameters = this.parameters;
    BigInteger bigInteger3 = paramBigInteger.subtract(bigInteger1.multiply(gLVTypeBParameters.getV1A()).add(bigInteger2.multiply(gLVTypeBParameters.getV2A())));
    BigInteger bigInteger4 = bigInteger1.multiply(gLVTypeBParameters.getV1B()).add(bigInteger2.multiply(gLVTypeBParameters.getV2B())).negate();
    return new BigInteger[] { bigInteger3, bigInteger4 };
  }
  
  public ECPointMap getPointMap() {
    return this.pointMap;
  }
  
  public boolean hasEfficientPointMap() {
    return true;
  }
  
  protected BigInteger calculateB(BigInteger paramBigInteger1, BigInteger paramBigInteger2, int paramInt) {
    boolean bool = (paramBigInteger2.signum() < 0) ? true : false;
    BigInteger bigInteger = paramBigInteger1.multiply(paramBigInteger2.abs());
    boolean bool1 = bigInteger.testBit(paramInt - 1);
    bigInteger = bigInteger.shiftRight(paramInt);
    if (bool1)
      bigInteger = bigInteger.add(ECConstants.ONE); 
    return bool ? bigInteger.negate() : bigInteger;
  }
}
