package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.util.Integers;

public class TraceOptimizer {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private static final SecureRandom R = new SecureRandom();
  
  public static void main(String[] paramArrayOfString) {
    TreeSet treeSet = new TreeSet(enumToList(ECNamedCurveTable.getNames()));
    treeSet.addAll(enumToList(CustomNamedCurves.getNames()));
    for (String str : treeSet) {
      X9ECParameters x9ECParameters = CustomNamedCurves.getByName(str);
      if (x9ECParameters == null)
        x9ECParameters = ECNamedCurveTable.getByName(str); 
      if (x9ECParameters != null && ECAlgorithms.isF2mCurve(x9ECParameters.getCurve())) {
        System.out.print(str + ":");
        implPrintNonZeroTraceBits(x9ECParameters);
      } 
    } 
  }
  
  public static void printNonZeroTraceBits(X9ECParameters paramX9ECParameters) {
    if (!ECAlgorithms.isF2mCurve(paramX9ECParameters.getCurve()))
      throw new IllegalArgumentException("Trace only defined over characteristic-2 fields"); 
    implPrintNonZeroTraceBits(paramX9ECParameters);
  }
  
  public static void implPrintNonZeroTraceBits(X9ECParameters paramX9ECParameters) {
    ECCurve eCCurve = paramX9ECParameters.getCurve();
    int i = eCCurve.getFieldSize();
    ArrayList<Integer> arrayList = new ArrayList();
    byte b;
    for (b = 0; b < i; b++) {
      BigInteger bigInteger = ONE.shiftLeft(b);
      ECFieldElement eCFieldElement = eCCurve.fromBigInteger(bigInteger);
      int j = calculateTrace(eCFieldElement);
      if (j != 0) {
        arrayList.add(Integers.valueOf(b));
        System.out.print(" " + b);
      } 
    } 
    System.out.println();
    for (b = 0; b < 'Ϩ'; b++) {
      BigInteger bigInteger = new BigInteger(i, R);
      ECFieldElement eCFieldElement = eCCurve.fromBigInteger(bigInteger);
      int j = calculateTrace(eCFieldElement);
      int k = 0;
      for (byte b1 = 0; b1 < arrayList.size(); b1++) {
        int m = ((Integer)arrayList.get(b1)).intValue();
        if (bigInteger.testBit(m))
          k ^= 0x1; 
      } 
      if (j != k)
        throw new IllegalStateException("Optimized-trace sanity check failed"); 
    } 
  }
  
  private static int calculateTrace(ECFieldElement paramECFieldElement) {
    int i = paramECFieldElement.getFieldSize();
    ECFieldElement eCFieldElement = paramECFieldElement;
    for (byte b = 1; b < i; b++) {
      paramECFieldElement = paramECFieldElement.square();
      eCFieldElement = eCFieldElement.add(paramECFieldElement);
    } 
    BigInteger bigInteger = eCFieldElement.toBigInteger();
    if (bigInteger.bitLength() > 1)
      throw new IllegalStateException(); 
    return bigInteger.intValue();
  }
  
  private static ArrayList enumToList(Enumeration paramEnumeration) {
    ArrayList arrayList = new ArrayList();
    while (paramEnumeration.hasMoreElements())
      arrayList.add(paramEnumeration.nextElement()); 
    return arrayList;
  }
}
