package org.bouncycastle.asn1.ua;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class DSTU4145NamedCurves {
  private static final BigInteger ZERO = BigInteger.valueOf(0L);
  
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  public static final ECDomainParameters[] params = new ECDomainParameters[10];
  
  static final ASN1ObjectIdentifier[] oids = new ASN1ObjectIdentifier[10];
  
  static final String oidBase = UAObjectIdentifiers.dstu4145le.getId() + ".2.";
  
  public static ASN1ObjectIdentifier[] getOIDs() {
    return oids;
  }
  
  public static ECDomainParameters getByOID(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str = paramASN1ObjectIdentifier.getId();
    if (str.startsWith(oidBase)) {
      int i = Integer.parseInt(str.substring(str.length() - 1));
      return params[i];
    } 
    return null;
  }
  
  static {
    BigInteger[] arrayOfBigInteger1 = new BigInteger[10];
    arrayOfBigInteger1[0] = new BigInteger("400000000000000000002BEC12BE2262D39BCF14D", 16);
    arrayOfBigInteger1[1] = new BigInteger("3FFFFFFFFFFFFFFFFFFFFFB12EBCC7D7F29FF7701F", 16);
    arrayOfBigInteger1[2] = new BigInteger("800000000000000000000189B4E67606E3825BB2831", 16);
    arrayOfBigInteger1[3] = new BigInteger("3FFFFFFFFFFFFFFFFFFFFFFB981960435FE5AB64236EF", 16);
    arrayOfBigInteger1[4] = new BigInteger("40000000000000000000000069A779CAC1DABC6788F7474F", 16);
    arrayOfBigInteger1[5] = new BigInteger("1000000000000000000000000000013E974E72F8A6922031D2603CFE0D7", 16);
    arrayOfBigInteger1[6] = new BigInteger("800000000000000000000000000000006759213AF182E987D3E17714907D470D", 16);
    arrayOfBigInteger1[7] = new BigInteger("3FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC079C2F3825DA70D390FBBA588D4604022B7B7", 16);
    arrayOfBigInteger1[8] = new BigInteger("40000000000000000000000000000000000000000000009C300B75A3FA824F22428FD28CE8812245EF44049B2D49", 16);
    arrayOfBigInteger1[9] = new BigInteger("3FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFBA3175458009A8C0A724F02F81AA8A1FCBAF80D90C7A95110504CF", 16);
    BigInteger[] arrayOfBigInteger2 = new BigInteger[10];
    arrayOfBigInteger2[0] = BigInteger.valueOf(2L);
    arrayOfBigInteger2[1] = BigInteger.valueOf(2L);
    arrayOfBigInteger2[2] = BigInteger.valueOf(4L);
    arrayOfBigInteger2[3] = BigInteger.valueOf(2L);
    arrayOfBigInteger2[4] = BigInteger.valueOf(2L);
    arrayOfBigInteger2[5] = BigInteger.valueOf(2L);
    arrayOfBigInteger2[6] = BigInteger.valueOf(4L);
    arrayOfBigInteger2[7] = BigInteger.valueOf(2L);
    arrayOfBigInteger2[8] = BigInteger.valueOf(2L);
    arrayOfBigInteger2[9] = BigInteger.valueOf(2L);
    ECCurve.F2m[] arrayOfF2m = new ECCurve.F2m[10];
    arrayOfF2m[0] = new ECCurve.F2m(163, 3, 6, 7, ONE, new BigInteger("5FF6108462A2DC8210AB403925E638A19C1455D21", 16), arrayOfBigInteger1[0], arrayOfBigInteger2[0]);
    arrayOfF2m[1] = new ECCurve.F2m(167, 6, ONE, new BigInteger("6EE3CEEB230811759F20518A0930F1A4315A827DAC", 16), arrayOfBigInteger1[1], arrayOfBigInteger2[1]);
    arrayOfF2m[2] = new ECCurve.F2m(173, 1, 2, 10, ZERO, new BigInteger("108576C80499DB2FC16EDDF6853BBB278F6B6FB437D9", 16), arrayOfBigInteger1[2], arrayOfBigInteger2[2]);
    arrayOfF2m[3] = new ECCurve.F2m(179, 1, 2, 4, ONE, new BigInteger("4A6E0856526436F2F88DD07A341E32D04184572BEB710", 16), arrayOfBigInteger1[3], arrayOfBigInteger2[3]);
    arrayOfF2m[4] = new ECCurve.F2m(191, 9, ONE, new BigInteger("7BC86E2102902EC4D5890E8B6B4981ff27E0482750FEFC03", 16), arrayOfBigInteger1[4], arrayOfBigInteger2[4]);
    arrayOfF2m[5] = new ECCurve.F2m(233, 1, 4, 9, ONE, new BigInteger("06973B15095675534C7CF7E64A21BD54EF5DD3B8A0326AA936ECE454D2C", 16), arrayOfBigInteger1[5], arrayOfBigInteger2[5]);
    arrayOfF2m[6] = new ECCurve.F2m(257, 12, ZERO, new BigInteger("1CEF494720115657E18F938D7A7942394FF9425C1458C57861F9EEA6ADBE3BE10", 16), arrayOfBigInteger1[6], arrayOfBigInteger2[6]);
    arrayOfF2m[7] = new ECCurve.F2m(307, 2, 4, 8, ONE, new BigInteger("393C7F7D53666B5054B5E6C6D3DE94F4296C0C599E2E2E241050DF18B6090BDC90186904968BB", 16), arrayOfBigInteger1[7], arrayOfBigInteger2[7]);
    arrayOfF2m[8] = new ECCurve.F2m(367, 21, ONE, new BigInteger("43FC8AD242B0B7A6F3D1627AD5654447556B47BF6AA4A64B0C2AFE42CADAB8F93D92394C79A79755437B56995136", 16), arrayOfBigInteger1[8], arrayOfBigInteger2[8]);
    arrayOfF2m[9] = new ECCurve.F2m(431, 1, 3, 5, ONE, new BigInteger("03CE10490F6A708FC26DFE8C3D27C4F94E690134D5BFF988D8D28AAEAEDE975936C66BAC536B18AE2DC312CA493117DAA469C640CAF3", 16), arrayOfBigInteger1[9], arrayOfBigInteger2[9]);
    ECPoint[] arrayOfECPoint = new ECPoint[10];
    arrayOfECPoint[0] = arrayOfF2m[0].createPoint(new BigInteger("2E2F85F5DD74CE983A5C4237229DAF8A3F35823BE", 16), new BigInteger("3826F008A8C51D7B95284D9D03FF0E00CE2CD723A", 16));
    arrayOfECPoint[1] = arrayOfF2m[1].createPoint(new BigInteger("7A1F6653786A68192803910A3D30B2A2018B21CD54", 16), new BigInteger("5F49EB26781C0EC6B8909156D98ED435E45FD59918", 16));
    arrayOfECPoint[2] = arrayOfF2m[2].createPoint(new BigInteger("4D41A619BCC6EADF0448FA22FAD567A9181D37389CA", 16), new BigInteger("10B51CC12849B234C75E6DD2028BF7FF5C1CE0D991A1", 16));
    arrayOfECPoint[3] = arrayOfF2m[3].createPoint(new BigInteger("6BA06FE51464B2BD26DC57F48819BA9954667022C7D03", 16), new BigInteger("25FBC363582DCEC065080CA8287AAFF09788A66DC3A9E", 16));
    arrayOfECPoint[4] = arrayOfF2m[4].createPoint(new BigInteger("714114B762F2FF4A7912A6D2AC58B9B5C2FCFE76DAEB7129", 16), new BigInteger("29C41E568B77C617EFE5902F11DB96FA9613CD8D03DB08DA", 16));
    arrayOfECPoint[5] = arrayOfF2m[5].createPoint(new BigInteger("3FCDA526B6CDF83BA1118DF35B3C31761D3545F32728D003EEB25EFE96", 16), new BigInteger("9CA8B57A934C54DEEDA9E54A7BBAD95E3B2E91C54D32BE0B9DF96D8D35", 16));
    arrayOfECPoint[6] = arrayOfF2m[6].createPoint(new BigInteger("02A29EF207D0E9B6C55CD260B306C7E007AC491CA1B10C62334A9E8DCD8D20FB7", 16), new BigInteger("10686D41FF744D4449FCCF6D8EEA03102E6812C93A9D60B978B702CF156D814EF", 16));
    arrayOfECPoint[7] = arrayOfF2m[7].createPoint(new BigInteger("216EE8B189D291A0224984C1E92F1D16BF75CCD825A087A239B276D3167743C52C02D6E7232AA", 16), new BigInteger("5D9306BACD22B7FAEB09D2E049C6E2866C5D1677762A8F2F2DC9A11C7F7BE8340AB2237C7F2A0", 16));
    arrayOfECPoint[8] = arrayOfF2m[8].createPoint(new BigInteger("324A6EDDD512F08C49A99AE0D3F961197A76413E7BE81A400CA681E09639B5FE12E59A109F78BF4A373541B3B9A1", 16), new BigInteger("1AB597A5B4477F59E39539007C7F977D1A567B92B043A49C6B61984C3FE3481AAF454CD41BA1F051626442B3C10", 16));
    arrayOfECPoint[9] = arrayOfF2m[9].createPoint(new BigInteger("1A62BA79D98133A16BBAE7ED9A8E03C32E0824D57AEF72F88986874E5AAE49C27BED49A2A95058068426C2171E99FD3B43C5947C857D", 16), new BigInteger("70B5E1E14031C1F70BBEFE96BDDE66F451754B4CA5F48DA241F331AA396B8D1839A855C1769B1EA14BA53308B5E2723724E090E02DB9", 16));
    byte b;
    for (b = 0; b < params.length; b++)
      params[b] = new ECDomainParameters((ECCurve)arrayOfF2m[b], arrayOfECPoint[b], arrayOfBigInteger1[b], arrayOfBigInteger2[b]); 
    for (b = 0; b < oids.length; b++)
      oids[b] = new ASN1ObjectIdentifier(oidBase + b); 
  }
}
