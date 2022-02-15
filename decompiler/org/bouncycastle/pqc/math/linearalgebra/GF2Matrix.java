package org.bouncycastle.pqc.math.linearalgebra;

import java.security.SecureRandom;

public class GF2Matrix extends Matrix {
  private int[][] matrix;
  
  private int length;
  
  public GF2Matrix(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length < 9)
      throw new ArithmeticException("given array is not an encoded matrix over GF(2)"); 
    this.numRows = LittleEndianConversions.OS2IP(paramArrayOfbyte, 0);
    this.numColumns = LittleEndianConversions.OS2IP(paramArrayOfbyte, 4);
    int i = (this.numColumns + 7 >>> 3) * this.numRows;
    if (this.numRows <= 0 || i != paramArrayOfbyte.length - 8)
      throw new ArithmeticException("given array is not an encoded matrix over GF(2)"); 
    this.length = this.numColumns + 31 >>> 5;
    this.matrix = new int[this.numRows][this.length];
    int j = this.numColumns >> 5;
    int k = this.numColumns & 0x1F;
    byte b1 = 8;
    for (byte b2 = 0; b2 < this.numRows; b2++) {
      byte b = 0;
      while (b < j) {
        this.matrix[b2][b] = LittleEndianConversions.OS2IP(paramArrayOfbyte, b1);
        b++;
        b1 += 4;
      } 
      for (b = 0; b < k; b += 8)
        this.matrix[b2][j] = this.matrix[b2][j] ^ (paramArrayOfbyte[b1++] & 0xFF) << b; 
    } 
  }
  
  public GF2Matrix(int paramInt, int[][] paramArrayOfint) {
    int j;
    if ((paramArrayOfint[0]).length != paramInt + 31 >> 5)
      throw new ArithmeticException("Int array does not match given number of columns."); 
    this.numColumns = paramInt;
    this.numRows = paramArrayOfint.length;
    this.length = (paramArrayOfint[0]).length;
    int i = paramInt & 0x1F;
    if (i == 0) {
      j = -1;
    } else {
      j = (1 << i) - 1;
    } 
    for (byte b = 0; b < this.numRows; b++)
      paramArrayOfint[b][this.length - 1] = paramArrayOfint[b][this.length - 1] & j; 
    this.matrix = paramArrayOfint;
  }
  
  public GF2Matrix(int paramInt, char paramChar) {
    this(paramInt, paramChar, new SecureRandom());
  }
  
  public GF2Matrix(int paramInt, char paramChar, SecureRandom paramSecureRandom) {
    if (paramInt <= 0)
      throw new ArithmeticException("Size of matrix is non-positive."); 
    switch (paramChar) {
      case 'Z':
        assignZeroMatrix(paramInt, paramInt);
        return;
      case 'I':
        assignUnitMatrix(paramInt);
        return;
      case 'L':
        assignRandomLowerTriangularMatrix(paramInt, paramSecureRandom);
        return;
      case 'U':
        assignRandomUpperTriangularMatrix(paramInt, paramSecureRandom);
        return;
      case 'R':
        assignRandomRegularMatrix(paramInt, paramSecureRandom);
        return;
    } 
    throw new ArithmeticException("Unknown matrix type.");
  }
  
  public GF2Matrix(GF2Matrix paramGF2Matrix) {
    this.numColumns = paramGF2Matrix.getNumColumns();
    this.numRows = paramGF2Matrix.getNumRows();
    this.length = paramGF2Matrix.length;
    this.matrix = new int[paramGF2Matrix.matrix.length][];
    for (byte b = 0; b < this.matrix.length; b++)
      this.matrix[b] = IntUtils.clone(paramGF2Matrix.matrix[b]); 
  }
  
  private GF2Matrix(int paramInt1, int paramInt2) {
    if (paramInt2 <= 0 || paramInt1 <= 0)
      throw new ArithmeticException("size of matrix is non-positive"); 
    assignZeroMatrix(paramInt1, paramInt2);
  }
  
  private void assignZeroMatrix(int paramInt1, int paramInt2) {
    this.numRows = paramInt1;
    this.numColumns = paramInt2;
    this.length = paramInt2 + 31 >>> 5;
    this.matrix = new int[this.numRows][this.length];
    for (byte b = 0; b < this.numRows; b++) {
      for (byte b1 = 0; b1 < this.length; b1++)
        this.matrix[b][b1] = 0; 
    } 
  }
  
  private void assignUnitMatrix(int paramInt) {
    this.numRows = paramInt;
    this.numColumns = paramInt;
    this.length = paramInt + 31 >>> 5;
    this.matrix = new int[this.numRows][this.length];
    byte b;
    for (b = 0; b < this.numRows; b++) {
      for (byte b1 = 0; b1 < this.length; b1++)
        this.matrix[b][b1] = 0; 
    } 
    for (b = 0; b < this.numRows; b++) {
      int i = b & 0x1F;
      this.matrix[b][b >>> 5] = 1 << i;
    } 
  }
  
  private void assignRandomLowerTriangularMatrix(int paramInt, SecureRandom paramSecureRandom) {
    this.numRows = paramInt;
    this.numColumns = paramInt;
    this.length = paramInt + 31 >>> 5;
    this.matrix = new int[this.numRows][this.length];
    for (byte b = 0; b < this.numRows; b++) {
      int i = b >>> 5;
      int j = b & 0x1F;
      int k = 31 - j;
      j = 1 << j;
      int m;
      for (m = 0; m < i; m++)
        this.matrix[b][m] = paramSecureRandom.nextInt(); 
      this.matrix[b][i] = paramSecureRandom.nextInt() >>> k | j;
      for (m = i + 1; m < this.length; m++)
        this.matrix[b][m] = 0; 
    } 
  }
  
  private void assignRandomUpperTriangularMatrix(int paramInt, SecureRandom paramSecureRandom) {
    int j;
    this.numRows = paramInt;
    this.numColumns = paramInt;
    this.length = paramInt + 31 >>> 5;
    this.matrix = new int[this.numRows][this.length];
    int i = paramInt & 0x1F;
    if (i == 0) {
      j = -1;
    } else {
      j = (1 << i) - 1;
    } 
    for (byte b = 0; b < this.numRows; b++) {
      int k = b >>> 5;
      int m = b & 0x1F;
      int n = m;
      m = 1 << m;
      int i1;
      for (i1 = 0; i1 < k; i1++)
        this.matrix[b][i1] = 0; 
      this.matrix[b][k] = paramSecureRandom.nextInt() << n | m;
      for (i1 = k + 1; i1 < this.length; i1++)
        this.matrix[b][i1] = paramSecureRandom.nextInt(); 
      this.matrix[b][this.length - 1] = this.matrix[b][this.length - 1] & j;
    } 
  }
  
  private void assignRandomRegularMatrix(int paramInt, SecureRandom paramSecureRandom) {
    this.numRows = paramInt;
    this.numColumns = paramInt;
    this.length = paramInt + 31 >>> 5;
    this.matrix = new int[this.numRows][this.length];
    GF2Matrix gF2Matrix1 = new GF2Matrix(paramInt, 'L', paramSecureRandom);
    GF2Matrix gF2Matrix2 = new GF2Matrix(paramInt, 'U', paramSecureRandom);
    GF2Matrix gF2Matrix3 = (GF2Matrix)gF2Matrix1.rightMultiply(gF2Matrix2);
    Permutation permutation = new Permutation(paramInt, paramSecureRandom);
    int[] arrayOfInt = permutation.getVector();
    for (byte b = 0; b < paramInt; b++)
      System.arraycopy(gF2Matrix3.matrix[b], 0, this.matrix[arrayOfInt[b]], 0, this.length); 
  }
  
  public static GF2Matrix[] createRandomRegularMatrixAndItsInverse(int paramInt, SecureRandom paramSecureRandom) {
    GF2Matrix[] arrayOfGF2Matrix = new GF2Matrix[2];
    int i = paramInt + 31 >> 5;
    GF2Matrix gF2Matrix1 = new GF2Matrix(paramInt, 'L', paramSecureRandom);
    GF2Matrix gF2Matrix2 = new GF2Matrix(paramInt, 'U', paramSecureRandom);
    GF2Matrix gF2Matrix3 = (GF2Matrix)gF2Matrix1.rightMultiply(gF2Matrix2);
    Permutation permutation = new Permutation(paramInt, paramSecureRandom);
    int[] arrayOfInt = permutation.getVector();
    int[][] arrayOfInt1 = new int[paramInt][i];
    for (byte b1 = 0; b1 < paramInt; b1++)
      System.arraycopy(gF2Matrix3.matrix[arrayOfInt[b1]], 0, arrayOfInt1[b1], 0, i); 
    arrayOfGF2Matrix[0] = new GF2Matrix(paramInt, arrayOfInt1);
    GF2Matrix gF2Matrix4 = new GF2Matrix(paramInt, 'I');
    for (byte b2 = 0; b2 < paramInt; b2++) {
      int k = b2 & 0x1F;
      int m = b2 >>> 5;
      int n = 1 << k;
      for (int i1 = b2 + 1; i1 < paramInt; i1++) {
        int i2 = gF2Matrix1.matrix[i1][m] & n;
        if (i2 != 0)
          for (byte b = 0; b <= m; b++)
            gF2Matrix4.matrix[i1][b] = gF2Matrix4.matrix[i1][b] ^ gF2Matrix4.matrix[b2][b];  
      } 
    } 
    GF2Matrix gF2Matrix5 = new GF2Matrix(paramInt, 'I');
    for (int j = paramInt - 1; j >= 0; j--) {
      int k = j & 0x1F;
      int m = j >>> 5;
      int n = 1 << k;
      for (int i1 = j - 1; i1 >= 0; i1--) {
        int i2 = gF2Matrix2.matrix[i1][m] & n;
        if (i2 != 0)
          for (int i3 = m; i3 < i; i3++)
            gF2Matrix5.matrix[i1][i3] = gF2Matrix5.matrix[i1][i3] ^ gF2Matrix5.matrix[j][i3];  
      } 
    } 
    arrayOfGF2Matrix[1] = (GF2Matrix)gF2Matrix5.rightMultiply(gF2Matrix4.rightMultiply(permutation));
    return arrayOfGF2Matrix;
  }
  
  public int[][] getIntArray() {
    return this.matrix;
  }
  
  public int getLength() {
    return this.length;
  }
  
  public int[] getRow(int paramInt) {
    return this.matrix[paramInt];
  }
  
  public byte[] getEncoded() {
    int i = this.numColumns + 7 >>> 3;
    i *= this.numRows;
    i += 8;
    byte[] arrayOfByte = new byte[i];
    LittleEndianConversions.I2OSP(this.numRows, arrayOfByte, 0);
    LittleEndianConversions.I2OSP(this.numColumns, arrayOfByte, 4);
    int j = this.numColumns >>> 5;
    int k = this.numColumns & 0x1F;
    byte b1 = 8;
    for (byte b2 = 0; b2 < this.numRows; b2++) {
      byte b = 0;
      while (b < j) {
        LittleEndianConversions.I2OSP(this.matrix[b2][b], arrayOfByte, b1);
        b++;
        b1 += 4;
      } 
      for (b = 0; b < k; b += 8)
        arrayOfByte[b1++] = (byte)(this.matrix[b2][j] >>> b & 0xFF); 
    } 
    return arrayOfByte;
  }
  
  public double getHammingWeight() {
    int j;
    double d1 = 0.0D;
    double d2 = 0.0D;
    int i = this.numColumns & 0x1F;
    if (i == 0) {
      j = this.length;
    } else {
      j = this.length - 1;
    } 
    for (byte b = 0; b < this.numRows; b++) {
      int k;
      for (k = 0; k < j; k++) {
        int m = this.matrix[b][k];
        for (byte b2 = 0; b2 < 32; b2++) {
          int n = m >>> b2 & 0x1;
          d1 += n;
          d2++;
        } 
      } 
      k = this.matrix[b][this.length - 1];
      for (byte b1 = 0; b1 < i; b1++) {
        int m = k >>> b1 & 0x1;
        d1 += m;
        d2++;
      } 
    } 
    return d1 / d2;
  }
  
  public boolean isZero() {
    for (byte b = 0; b < this.numRows; b++) {
      for (byte b1 = 0; b1 < this.length; b1++) {
        if (this.matrix[b][b1] != 0)
          return false; 
      } 
    } 
    return true;
  }
  
  public GF2Matrix getLeftSubMatrix() {
    if (this.numColumns <= this.numRows)
      throw new ArithmeticException("empty submatrix"); 
    int i = this.numRows + 31 >> 5;
    int[][] arrayOfInt = new int[this.numRows][i];
    int j = (1 << (this.numRows & 0x1F)) - 1;
    if (j == 0)
      j = -1; 
    for (int k = this.numRows - 1; k >= 0; k--) {
      System.arraycopy(this.matrix[k], 0, arrayOfInt[k], 0, i);
      arrayOfInt[k][i - 1] = arrayOfInt[k][i - 1] & j;
    } 
    return new GF2Matrix(this.numRows, arrayOfInt);
  }
  
  public GF2Matrix extendLeftCompactForm() {
    int i = this.numColumns + this.numRows;
    GF2Matrix gF2Matrix = new GF2Matrix(this.numRows, i);
    int j = this.numRows - 1 + this.numColumns;
    int k = this.numRows - 1;
    while (k >= 0) {
      System.arraycopy(this.matrix[k], 0, gF2Matrix.matrix[k], 0, this.length);
      gF2Matrix.matrix[k][j >> 5] = gF2Matrix.matrix[k][j >> 5] | 1 << (j & 0x1F);
      k--;
      j--;
    } 
    return gF2Matrix;
  }
  
  public GF2Matrix getRightSubMatrix() {
    if (this.numColumns <= this.numRows)
      throw new ArithmeticException("empty submatrix"); 
    int i = this.numRows >> 5;
    int j = this.numRows & 0x1F;
    GF2Matrix gF2Matrix = new GF2Matrix(this.numRows, this.numColumns - this.numRows);
    for (int k = this.numRows - 1; k >= 0; k--) {
      if (j != 0) {
        int m = i;
        for (byte b = 0; b < gF2Matrix.length - 1; b++)
          gF2Matrix.matrix[k][b] = this.matrix[k][m++] >>> j | this.matrix[k][m] << 32 - j; 
        gF2Matrix.matrix[k][gF2Matrix.length - 1] = this.matrix[k][m++] >>> j;
        if (m < this.length)
          gF2Matrix.matrix[k][gF2Matrix.length - 1] = gF2Matrix.matrix[k][gF2Matrix.length - 1] | this.matrix[k][m] << 32 - j; 
      } else {
        System.arraycopy(this.matrix[k], i, gF2Matrix.matrix[k], 0, gF2Matrix.length);
      } 
    } 
    return gF2Matrix;
  }
  
  public GF2Matrix extendRightCompactForm() {
    GF2Matrix gF2Matrix = new GF2Matrix(this.numRows, this.numRows + this.numColumns);
    int i = this.numRows >> 5;
    int j = this.numRows & 0x1F;
    for (int k = this.numRows - 1; k >= 0; k--) {
      gF2Matrix.matrix[k][k >> 5] = gF2Matrix.matrix[k][k >> 5] | 1 << (k & 0x1F);
      if (j != 0) {
        int m = i;
        int n;
        for (n = 0; n < this.length - 1; n++) {
          int i1 = this.matrix[k][n];
          gF2Matrix.matrix[k][m++] = gF2Matrix.matrix[k][m++] | i1 << j;
          gF2Matrix.matrix[k][m] = gF2Matrix.matrix[k][m] | i1 >>> 32 - j;
        } 
        n = this.matrix[k][this.length - 1];
        gF2Matrix.matrix[k][m++] = gF2Matrix.matrix[k][m++] | n << j;
        if (m < gF2Matrix.length)
          gF2Matrix.matrix[k][m] = gF2Matrix.matrix[k][m] | n >>> 32 - j; 
      } else {
        System.arraycopy(this.matrix[k], 0, gF2Matrix.matrix[k], i, this.length);
      } 
    } 
    return gF2Matrix;
  }
  
  public Matrix computeTranspose() {
    int[][] arrayOfInt = new int[this.numColumns][this.numRows + 31 >>> 5];
    for (byte b = 0; b < this.numRows; b++) {
      for (byte b1 = 0; b1 < this.numColumns; b1++) {
        int i = b1 >>> 5;
        int j = b1 & 0x1F;
        int k = this.matrix[b][i] >>> j & 0x1;
        int m = b >>> 5;
        int n = b & 0x1F;
        if (k == 1)
          arrayOfInt[b1][m] = arrayOfInt[b1][m] | 1 << n; 
      } 
    } 
    return new GF2Matrix(this.numRows, arrayOfInt);
  }
  
  public Matrix computeInverse() {
    if (this.numRows != this.numColumns)
      throw new ArithmeticException("Matrix is not invertible."); 
    int[][] arrayOfInt1 = new int[this.numRows][this.length];
    for (int i = this.numRows - 1; i >= 0; i--)
      arrayOfInt1[i] = IntUtils.clone(this.matrix[i]); 
    int[][] arrayOfInt2 = new int[this.numRows][this.length];
    int j;
    for (j = this.numRows - 1; j >= 0; j--) {
      int k = j >> 5;
      int m = j & 0x1F;
      arrayOfInt2[j][k] = 1 << m;
    } 
    for (j = 0; j < this.numRows; j++) {
      int k = j >> 5;
      int m = 1 << (j & 0x1F);
      if ((arrayOfInt1[j][k] & m) == 0) {
        boolean bool = false;
        for (int i1 = j + 1; i1 < this.numRows; i1++) {
          if ((arrayOfInt1[i1][k] & m) != 0) {
            bool = true;
            swapRows(arrayOfInt1, j, i1);
            swapRows(arrayOfInt2, j, i1);
            i1 = this.numRows;
          } 
        } 
        if (!bool)
          throw new ArithmeticException("Matrix is not invertible."); 
      } 
      for (int n = this.numRows - 1; n >= 0; n--) {
        if (n != j && (arrayOfInt1[n][k] & m) != 0) {
          addToRow(arrayOfInt1[j], arrayOfInt1[n], k);
          addToRow(arrayOfInt2[j], arrayOfInt2[n], 0);
        } 
      } 
    } 
    return new GF2Matrix(this.numColumns, arrayOfInt2);
  }
  
  public Matrix leftMultiply(Permutation paramPermutation) {
    int[] arrayOfInt = paramPermutation.getVector();
    if (arrayOfInt.length != this.numRows)
      throw new ArithmeticException("length mismatch"); 
    int[][] arrayOfInt1 = new int[this.numRows][];
    for (int i = this.numRows - 1; i >= 0; i--)
      arrayOfInt1[i] = IntUtils.clone(this.matrix[arrayOfInt[i]]); 
    return new GF2Matrix(this.numRows, arrayOfInt1);
  }
  
  public Vector leftMultiply(Vector paramVector) {
    if (!(paramVector instanceof GF2Vector))
      throw new ArithmeticException("vector is not defined over GF(2)"); 
    if (paramVector.length != this.numRows)
      throw new ArithmeticException("length mismatch"); 
    int[] arrayOfInt1 = ((GF2Vector)paramVector).getVecArray();
    int[] arrayOfInt2 = new int[this.length];
    int i = this.numRows >> 5;
    int j = 1 << (this.numRows & 0x1F);
    byte b = 0;
    int k = 0;
    while (k < i) {
      int m = 1;
      while (true) {
        int n = arrayOfInt1[k] & m;
        if (n != 0)
          for (byte b1 = 0; b1 < this.length; b1++)
            arrayOfInt2[b1] = arrayOfInt2[b1] ^ this.matrix[b][b1];  
        b++;
        m <<= 1;
        if (m == 0)
          k++; 
      } 
    } 
    for (k = 1; k != j; k <<= 1) {
      int m = arrayOfInt1[i] & k;
      if (m != 0)
        for (byte b1 = 0; b1 < this.length; b1++)
          arrayOfInt2[b1] = arrayOfInt2[b1] ^ this.matrix[b][b1];  
      b++;
    } 
    return new GF2Vector(arrayOfInt2, this.numColumns);
  }
  
  public Vector leftMultiplyLeftCompactForm(Vector paramVector) {
    if (!(paramVector instanceof GF2Vector))
      throw new ArithmeticException("vector is not defined over GF(2)"); 
    if (paramVector.length != this.numRows)
      throw new ArithmeticException("length mismatch"); 
    int[] arrayOfInt1 = ((GF2Vector)paramVector).getVecArray();
    int[] arrayOfInt2 = new int[this.numRows + this.numColumns + 31 >>> 5];
    int i = this.numRows >>> 5;
    byte b = 0;
    int j = 0;
    while (j < i) {
      int m = 1;
      while (true) {
        int n = arrayOfInt1[j] & m;
        if (n != 0) {
          int i1;
          for (i1 = 0; i1 < this.length; i1++)
            arrayOfInt2[i1] = arrayOfInt2[i1] ^ this.matrix[b][i1]; 
          i1 = this.numColumns + b >>> 5;
          int i2 = this.numColumns + b & 0x1F;
          arrayOfInt2[i1] = arrayOfInt2[i1] | 1 << i2;
        } 
        b++;
        m <<= 1;
        if (m == 0)
          j++; 
      } 
    } 
    j = 1 << (this.numRows & 0x1F);
    int k;
    for (k = 1; k != j; k <<= 1) {
      int m = arrayOfInt1[i] & k;
      if (m != 0) {
        int n;
        for (n = 0; n < this.length; n++)
          arrayOfInt2[n] = arrayOfInt2[n] ^ this.matrix[b][n]; 
        n = this.numColumns + b >>> 5;
        int i1 = this.numColumns + b & 0x1F;
        arrayOfInt2[n] = arrayOfInt2[n] | 1 << i1;
      } 
      b++;
    } 
    return new GF2Vector(arrayOfInt2, this.numRows + this.numColumns);
  }
  
  public Matrix rightMultiply(Matrix paramMatrix) {
    int i;
    if (!(paramMatrix instanceof GF2Matrix))
      throw new ArithmeticException("matrix is not defined over GF(2)"); 
    if (paramMatrix.numRows != this.numColumns)
      throw new ArithmeticException("length mismatch"); 
    GF2Matrix gF2Matrix1 = (GF2Matrix)paramMatrix;
    GF2Matrix gF2Matrix2 = new GF2Matrix(this.numRows, paramMatrix.numColumns);
    int j = this.numColumns & 0x1F;
    if (j == 0) {
      i = this.length;
    } else {
      i = this.length - 1;
    } 
    for (byte b = 0; b < this.numRows; b++) {
      byte b1 = 0;
      int k;
      for (k = 0; k < i; k++) {
        int m = this.matrix[b][k];
        for (byte b3 = 0; b3 < 32; b3++) {
          int n = m & 1 << b3;
          if (n != 0)
            for (byte b4 = 0; b4 < gF2Matrix1.length; b4++)
              gF2Matrix2.matrix[b][b4] = gF2Matrix2.matrix[b][b4] ^ gF2Matrix1.matrix[b1][b4];  
          b1++;
        } 
      } 
      k = this.matrix[b][this.length - 1];
      for (byte b2 = 0; b2 < j; b2++) {
        int m = k & 1 << b2;
        if (m != 0)
          for (byte b3 = 0; b3 < gF2Matrix1.length; b3++)
            gF2Matrix2.matrix[b][b3] = gF2Matrix2.matrix[b][b3] ^ gF2Matrix1.matrix[b1][b3];  
        b1++;
      } 
    } 
    return gF2Matrix2;
  }
  
  public Matrix rightMultiply(Permutation paramPermutation) {
    int[] arrayOfInt = paramPermutation.getVector();
    if (arrayOfInt.length != this.numColumns)
      throw new ArithmeticException("length mismatch"); 
    GF2Matrix gF2Matrix = new GF2Matrix(this.numRows, this.numColumns);
    for (int i = this.numColumns - 1; i >= 0; i--) {
      int j = i >>> 5;
      int k = i & 0x1F;
      int m = arrayOfInt[i] >>> 5;
      int n = arrayOfInt[i] & 0x1F;
      for (int i1 = this.numRows - 1; i1 >= 0; i1--)
        gF2Matrix.matrix[i1][j] = gF2Matrix.matrix[i1][j] | (this.matrix[i1][m] >>> n & 0x1) << k; 
    } 
    return gF2Matrix;
  }
  
  public Vector rightMultiply(Vector paramVector) {
    if (!(paramVector instanceof GF2Vector))
      throw new ArithmeticException("vector is not defined over GF(2)"); 
    if (paramVector.length != this.numColumns)
      throw new ArithmeticException("length mismatch"); 
    int[] arrayOfInt1 = ((GF2Vector)paramVector).getVecArray();
    int[] arrayOfInt2 = new int[this.numRows + 31 >>> 5];
    for (byte b = 0; b < this.numRows; b++) {
      int i = 0;
      int j;
      for (j = 0; j < this.length; j++)
        i ^= this.matrix[b][j] & arrayOfInt1[j]; 
      j = 0;
      for (byte b1 = 0; b1 < 32; b1++)
        j ^= i >>> b1 & 0x1; 
      if (j == 1)
        arrayOfInt2[b >>> 5] = arrayOfInt2[b >>> 5] | 1 << (b & 0x1F); 
    } 
    return new GF2Vector(arrayOfInt2, this.numRows);
  }
  
  public Vector rightMultiplyRightCompactForm(Vector paramVector) {
    if (!(paramVector instanceof GF2Vector))
      throw new ArithmeticException("vector is not defined over GF(2)"); 
    if (paramVector.length != this.numColumns + this.numRows)
      throw new ArithmeticException("length mismatch"); 
    int[] arrayOfInt1 = ((GF2Vector)paramVector).getVecArray();
    int[] arrayOfInt2 = new int[this.numRows + 31 >>> 5];
    int i = this.numRows >> 5;
    int j = this.numRows & 0x1F;
    for (byte b = 0; b < this.numRows; b++) {
      int k = arrayOfInt1[b >> 5] >>> (b & 0x1F) & 0x1;
      int m = i;
      if (j != 0) {
        int i1 = 0;
        for (byte b2 = 0; b2 < this.length - 1; b2++) {
          i1 = arrayOfInt1[m++] >>> j | arrayOfInt1[m] << 32 - j;
          k ^= this.matrix[b][b2] & i1;
        } 
        i1 = arrayOfInt1[m++] >>> j;
        if (m < arrayOfInt1.length)
          i1 |= arrayOfInt1[m] << 32 - j; 
        k ^= this.matrix[b][this.length - 1] & i1;
      } else {
        for (byte b2 = 0; b2 < this.length; b2++)
          k ^= this.matrix[b][b2] & arrayOfInt1[m++]; 
      } 
      int n = 0;
      for (byte b1 = 0; b1 < 32; b1++) {
        n ^= k & 0x1;
        k >>>= 1;
      } 
      if (n == 1)
        arrayOfInt2[b >> 5] = arrayOfInt2[b >> 5] | 1 << (b & 0x1F); 
    } 
    return new GF2Vector(arrayOfInt2, this.numRows);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof GF2Matrix))
      return false; 
    GF2Matrix gF2Matrix = (GF2Matrix)paramObject;
    if (this.numRows != gF2Matrix.numRows || this.numColumns != gF2Matrix.numColumns || this.length != gF2Matrix.length)
      return false; 
    for (byte b = 0; b < this.numRows; b++) {
      if (!IntUtils.equals(this.matrix[b], gF2Matrix.matrix[b]))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = (this.numRows * 31 + this.numColumns) * 31 + this.length;
    for (byte b = 0; b < this.numRows; b++)
      i = i * 31 + this.matrix[b].hashCode(); 
    return i;
  }
  
  public String toString() {
    int j;
    int i = this.numColumns & 0x1F;
    if (i == 0) {
      j = this.length;
    } else {
      j = this.length - 1;
    } 
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.numRows; b++) {
      stringBuffer.append(b + ": ");
      int k;
      for (k = 0; k < j; k++) {
        int m = this.matrix[b][k];
        for (byte b2 = 0; b2 < 32; b2++) {
          int n = m >>> b2 & 0x1;
          if (n == 0) {
            stringBuffer.append('0');
          } else {
            stringBuffer.append('1');
          } 
        } 
        stringBuffer.append(' ');
      } 
      k = this.matrix[b][this.length - 1];
      for (byte b1 = 0; b1 < i; b1++) {
        int m = k >>> b1 & 0x1;
        if (m == 0) {
          stringBuffer.append('0');
        } else {
          stringBuffer.append('1');
        } 
      } 
      stringBuffer.append('\n');
    } 
    return stringBuffer.toString();
  }
  
  private static void swapRows(int[][] paramArrayOfint, int paramInt1, int paramInt2) {
    int[] arrayOfInt = paramArrayOfint[paramInt1];
    paramArrayOfint[paramInt1] = paramArrayOfint[paramInt2];
    paramArrayOfint[paramInt2] = arrayOfInt;
  }
  
  private static void addToRow(int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt) {
    for (int i = paramArrayOfint2.length - 1; i >= paramInt; i--)
      paramArrayOfint2[i] = paramArrayOfint1[i] ^ paramArrayOfint2[i]; 
  }
}
