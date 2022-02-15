package org.bouncycastle.math.ec.tools;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TreeSet;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECFieldElement;

public class F2mSqrtOptimizer {
  public static void main(String[] paramArrayOfString) {
    TreeSet treeSet = new TreeSet(enumToList(ECNamedCurveTable.getNames()));
    treeSet.addAll(enumToList(CustomNamedCurves.getNames()));
    for (String str : treeSet) {
      X9ECParameters x9ECParameters = CustomNamedCurves.getByName(str);
      if (x9ECParameters == null)
        x9ECParameters = ECNamedCurveTable.getByName(str); 
      if (x9ECParameters != null && ECAlgorithms.isF2mCurve(x9ECParameters.getCurve())) {
        System.out.print(str + ":");
        implPrintRootZ(x9ECParameters);
      } 
    } 
  }
  
  public static void printRootZ(X9ECParameters paramX9ECParameters) {
    if (!ECAlgorithms.isF2mCurve(paramX9ECParameters.getCurve()))
      throw new IllegalArgumentException("Sqrt optimization only defined over characteristic-2 fields"); 
    implPrintRootZ(paramX9ECParameters);
  }
  
  private static void implPrintRootZ(X9ECParameters paramX9ECParameters) {
    ECFieldElement eCFieldElement1 = paramX9ECParameters.getCurve().fromBigInteger(BigInteger.valueOf(2L));
    ECFieldElement eCFieldElement2 = eCFieldElement1.sqrt();
    System.out.println(eCFieldElement2.toBigInteger().toString(16).toUpperCase());
    if (!eCFieldElement2.square().equals(eCFieldElement1))
      throw new IllegalStateException("Optimized-sqrt sanity check failed"); 
  }
  
  private static ArrayList enumToList(Enumeration paramEnumeration) {
    ArrayList arrayList = new ArrayList();
    while (paramEnumeration.hasMoreElements())
      arrayList.add(paramEnumeration.nextElement()); 
    return arrayList;
  }
}
