package org.bouncycastle.pqc.math.linearalgebra;

public class GF2mMatrix extends Matrix {
  protected GF2mField field;
  
  protected int[][] matrix;
  
  public GF2mMatrix(GF2mField paramGF2mField, byte[] paramArrayOfbyte) {
    this.field = paramGF2mField;
    byte b1 = 8;
    byte b2 = 1;
    while (paramGF2mField.getDegree() > b1) {
      b2++;
      b1 += 8;
    } 
    if (paramArrayOfbyte.length < 5)
      throw new IllegalArgumentException(" Error: given array is not encoded matrix over GF(2^m)"); 
    this.numRows = (paramArrayOfbyte[3] & 0xFF) << 24 ^ (paramArrayOfbyte[2] & 0xFF) << 16 ^ (paramArrayOfbyte[1] & 0xFF) << 8 ^ paramArrayOfbyte[0] & 0xFF;
    int i = b2 * this.numRows;
    if (this.numRows <= 0 || (paramArrayOfbyte.length - 4) % i != 0)
      throw new IllegalArgumentException(" Error: given array is not encoded matrix over GF(2^m)"); 
    this.numColumns = (paramArrayOfbyte.length - 4) / i;
    this.matrix = new int[this.numRows][this.numColumns];
    b2 = 4;
    for (byte b3 = 0; b3 < this.numRows; b3++) {
      for (byte b = 0; b < this.numColumns; b++) {
        for (byte b4 = 0; b4 < b1; b4 += 8)
          this.matrix[b3][b] = this.matrix[b3][b] ^ (paramArrayOfbyte[b2++] & 0xFF) << b4; 
        if (!this.field.isElementOfThisField(this.matrix[b3][b]))
          throw new IllegalArgumentException(" Error: given array is not encoded matrix over GF(2^m)"); 
      } 
    } 
  }
  
  public GF2mMatrix(GF2mMatrix paramGF2mMatrix) {
    this.numRows = paramGF2mMatrix.numRows;
    this.numColumns = paramGF2mMatrix.numColumns;
    this.field = paramGF2mMatrix.field;
    this.matrix = new int[this.numRows][];
    for (byte b = 0; b < this.numRows; b++)
      this.matrix[b] = IntUtils.clone(paramGF2mMatrix.matrix[b]); 
  }
  
  protected GF2mMatrix(GF2mField paramGF2mField, int[][] paramArrayOfint) {
    this.field = paramGF2mField;
    this.matrix = paramArrayOfint;
    this.numRows = paramArrayOfint.length;
    this.numColumns = (paramArrayOfint[0]).length;
  }
  
  public byte[] getEncoded() {
    byte b1 = 8;
    byte b2 = 1;
    while (this.field.getDegree() > b1) {
      b2++;
      b1 += 8;
    } 
    byte[] arrayOfByte = new byte[this.numRows * this.numColumns * b2 + 4];
    arrayOfByte[0] = (byte)(this.numRows & 0xFF);
    arrayOfByte[1] = (byte)(this.numRows >>> 8 & 0xFF);
    arrayOfByte[2] = (byte)(this.numRows >>> 16 & 0xFF);
    arrayOfByte[3] = (byte)(this.numRows >>> 24 & 0xFF);
    b2 = 4;
    for (byte b3 = 0; b3 < this.numRows; b3++) {
      for (byte b = 0; b < this.numColumns; b++) {
        for (byte b4 = 0; b4 < b1; b4 += 8)
          arrayOfByte[b2++] = (byte)(this.matrix[b3][b] >>> b4); 
      } 
    } 
    return arrayOfByte;
  }
  
  public boolean isZero() {
    for (byte b = 0; b < this.numRows; b++) {
      for (byte b1 = 0; b1 < this.numColumns; b1++) {
        if (this.matrix[b][b1] != 0)
          return false; 
      } 
    } 
    return true;
  }
  
  public Matrix computeInverse() {
    if (this.numRows != this.numColumns)
      throw new ArithmeticException("Matrix is not invertible."); 
    int[][] arrayOfInt1 = new int[this.numRows][this.numRows];
    for (int i = this.numRows - 1; i >= 0; i--)
      arrayOfInt1[i] = IntUtils.clone(this.matrix[i]); 
    int[][] arrayOfInt2 = new int[this.numRows][this.numRows];
    int j;
    for (j = this.numRows - 1; j >= 0; j--)
      arrayOfInt2[j][j] = 1; 
    for (j = 0; j < this.numRows; j++) {
      if (arrayOfInt1[j][j] == 0) {
        boolean bool = false;
        for (int n = j + 1; n < this.numRows; n++) {
          if (arrayOfInt1[n][j] != 0) {
            bool = true;
            swapColumns(arrayOfInt1, j, n);
            swapColumns(arrayOfInt2, j, n);
            n = this.numRows;
          } 
        } 
        if (!bool)
          throw new ArithmeticException("Matrix is not invertible."); 
      } 
      int k = arrayOfInt1[j][j];
      int m = this.field.inverse(k);
      multRowWithElementThis(arrayOfInt1[j], m);
      multRowWithElementThis(arrayOfInt2[j], m);
      for (byte b = 0; b < this.numRows; b++) {
        if (b != j) {
          k = arrayOfInt1[b][j];
          if (k != 0) {
            int[] arrayOfInt3 = multRowWithElement(arrayOfInt1[j], k);
            int[] arrayOfInt4 = multRowWithElement(arrayOfInt2[j], k);
            addToRow(arrayOfInt3, arrayOfInt1[b]);
            addToRow(arrayOfInt4, arrayOfInt2[b]);
          } 
        } 
      } 
    } 
    return new GF2mMatrix(this.field, arrayOfInt2);
  }
  
  private static void swapColumns(int[][] paramArrayOfint, int paramInt1, int paramInt2) {
    int[] arrayOfInt = paramArrayOfint[paramInt1];
    paramArrayOfint[paramInt1] = paramArrayOfint[paramInt2];
    paramArrayOfint[paramInt2] = arrayOfInt;
  }
  
  private void multRowWithElementThis(int[] paramArrayOfint, int paramInt) {
    for (int i = paramArrayOfint.length - 1; i >= 0; i--)
      paramArrayOfint[i] = this.field.mult(paramArrayOfint[i], paramInt); 
  }
  
  private int[] multRowWithElement(int[] paramArrayOfint, int paramInt) {
    int[] arrayOfInt = new int[paramArrayOfint.length];
    for (int i = paramArrayOfint.length - 1; i >= 0; i--)
      arrayOfInt[i] = this.field.mult(paramArrayOfint[i], paramInt); 
    return arrayOfInt;
  }
  
  private void addToRow(int[] paramArrayOfint1, int[] paramArrayOfint2) {
    for (int i = paramArrayOfint2.length - 1; i >= 0; i--)
      paramArrayOfint2[i] = this.field.add(paramArrayOfint1[i], paramArrayOfint2[i]); 
  }
  
  public Matrix rightMultiply(Matrix paramMatrix) {
    throw new RuntimeException("Not implemented.");
  }
  
  public Matrix rightMultiply(Permutation paramPermutation) {
    throw new RuntimeException("Not implemented.");
  }
  
  public Vector leftMultiply(Vector paramVector) {
    throw new RuntimeException("Not implemented.");
  }
  
  public Vector rightMultiply(Vector paramVector) {
    throw new RuntimeException("Not implemented.");
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof GF2mMatrix))
      return false; 
    GF2mMatrix gF2mMatrix = (GF2mMatrix)paramObject;
    if (!this.field.equals(gF2mMatrix.field) || gF2mMatrix.numRows != this.numColumns || gF2mMatrix.numColumns != this.numColumns)
      return false; 
    for (byte b = 0; b < this.numRows; b++) {
      for (byte b1 = 0; b1 < this.numColumns; b1++) {
        if (this.matrix[b][b1] != gF2mMatrix.matrix[b][b1])
          return false; 
      } 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = (this.field.hashCode() * 31 + this.numRows) * 31 + this.numColumns;
    for (byte b = 0; b < this.numRows; b++) {
      for (byte b1 = 0; b1 < this.numColumns; b1++)
        i = i * 31 + this.matrix[b][b1]; 
    } 
    return i;
  }
  
  public String toString() {
    String str = this.numRows + " x " + this.numColumns + " Matrix over " + this.field.toString() + ": \n";
    for (byte b = 0; b < this.numRows; b++) {
      for (byte b1 = 0; b1 < this.numColumns; b1++)
        str = str + this.field.elementToStr(this.matrix[b][b1]) + " : "; 
      str = str + "\n";
    } 
    return str;
  }
}
