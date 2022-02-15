package org.bouncycastle.pqc.math.linearalgebra;

public class GF2nPolynomial {
  private GF2nElement[] coeff;
  
  private int size;
  
  public GF2nPolynomial(int paramInt, GF2nElement paramGF2nElement) {
    this.size = paramInt;
    this.coeff = new GF2nElement[this.size];
    for (byte b = 0; b < this.size; b++)
      this.coeff[b] = (GF2nElement)paramGF2nElement.clone(); 
  }
  
  private GF2nPolynomial(int paramInt) {
    this.size = paramInt;
    this.coeff = new GF2nElement[this.size];
  }
  
  public GF2nPolynomial(GF2nPolynomial paramGF2nPolynomial) {
    this.coeff = new GF2nElement[paramGF2nPolynomial.size];
    this.size = paramGF2nPolynomial.size;
    for (byte b = 0; b < this.size; b++)
      this.coeff[b] = (GF2nElement)paramGF2nPolynomial.coeff[b].clone(); 
  }
  
  public GF2nPolynomial(GF2Polynomial paramGF2Polynomial, GF2nField paramGF2nField) {
    this.size = paramGF2nField.getDegree() + 1;
    this.coeff = new GF2nElement[this.size];
    if (paramGF2nField instanceof GF2nONBField) {
      for (byte b = 0; b < this.size; b++) {
        if (paramGF2Polynomial.testBit(b)) {
          this.coeff[b] = GF2nONBElement.ONE((GF2nONBField)paramGF2nField);
        } else {
          this.coeff[b] = GF2nONBElement.ZERO((GF2nONBField)paramGF2nField);
        } 
      } 
    } else if (paramGF2nField instanceof GF2nPolynomialField) {
      for (byte b = 0; b < this.size; b++) {
        if (paramGF2Polynomial.testBit(b)) {
          this.coeff[b] = GF2nPolynomialElement.ONE((GF2nPolynomialField)paramGF2nField);
        } else {
          this.coeff[b] = GF2nPolynomialElement.ZERO((GF2nPolynomialField)paramGF2nField);
        } 
      } 
    } else {
      throw new IllegalArgumentException("PolynomialGF2n(Bitstring, GF2nField): B1 must be an instance of GF2nONBField or GF2nPolynomialField!");
    } 
  }
  
  public final void assignZeroToElements() {
    for (byte b = 0; b < this.size; b++)
      this.coeff[b].assignZero(); 
  }
  
  public final int size() {
    return this.size;
  }
  
  public final int getDegree() {
    for (int i = this.size - 1; i >= 0; i--) {
      if (!this.coeff[i].isZero())
        return i; 
    } 
    return -1;
  }
  
  public final void enlarge(int paramInt) {
    if (paramInt <= this.size)
      return; 
    GF2nElement[] arrayOfGF2nElement = new GF2nElement[paramInt];
    System.arraycopy(this.coeff, 0, arrayOfGF2nElement, 0, this.size);
    GF2nField gF2nField = this.coeff[0].getField();
    if (this.coeff[0] instanceof GF2nPolynomialElement) {
      for (int i = this.size; i < paramInt; i++)
        arrayOfGF2nElement[i] = GF2nPolynomialElement.ZERO((GF2nPolynomialField)gF2nField); 
    } else if (this.coeff[0] instanceof GF2nONBElement) {
      for (int i = this.size; i < paramInt; i++)
        arrayOfGF2nElement[i] = GF2nONBElement.ZERO((GF2nONBField)gF2nField); 
    } 
    this.size = paramInt;
    this.coeff = arrayOfGF2nElement;
  }
  
  public final void shrink() {
    int i;
    for (i = this.size - 1; this.coeff[i].isZero() && i > 0; i--);
    if (++i < this.size) {
      GF2nElement[] arrayOfGF2nElement = new GF2nElement[i];
      System.arraycopy(this.coeff, 0, arrayOfGF2nElement, 0, i);
      this.coeff = arrayOfGF2nElement;
      this.size = i;
    } 
  }
  
  public final void set(int paramInt, GF2nElement paramGF2nElement) {
    if (!(paramGF2nElement instanceof GF2nPolynomialElement) && !(paramGF2nElement instanceof GF2nONBElement))
      throw new IllegalArgumentException("PolynomialGF2n.set f must be an instance of either GF2nPolynomialElement or GF2nONBElement!"); 
    this.coeff[paramInt] = (GF2nElement)paramGF2nElement.clone();
  }
  
  public final GF2nElement at(int paramInt) {
    return this.coeff[paramInt];
  }
  
  public final boolean isZero() {
    for (byte b = 0; b < this.size; b++) {
      if (this.coeff[b] != null && !this.coeff[b].isZero())
        return false; 
    } 
    return true;
  }
  
  public final boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof GF2nPolynomial))
      return false; 
    GF2nPolynomial gF2nPolynomial = (GF2nPolynomial)paramObject;
    if (getDegree() != gF2nPolynomial.getDegree())
      return false; 
    for (byte b = 0; b < this.size; b++) {
      if (!this.coeff[b].equals(gF2nPolynomial.coeff[b]))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    return getDegree() + this.coeff.hashCode();
  }
  
  public final GF2nPolynomial add(GF2nPolynomial paramGF2nPolynomial) throws RuntimeException {
    GF2nPolynomial gF2nPolynomial;
    if (size() >= paramGF2nPolynomial.size()) {
      gF2nPolynomial = new GF2nPolynomial(size());
      byte b;
      for (b = 0; b < paramGF2nPolynomial.size(); b++)
        gF2nPolynomial.coeff[b] = (GF2nElement)this.coeff[b].add(paramGF2nPolynomial.coeff[b]); 
      while (b < size()) {
        gF2nPolynomial.coeff[b] = this.coeff[b];
        b++;
      } 
    } else {
      gF2nPolynomial = new GF2nPolynomial(paramGF2nPolynomial.size());
      byte b;
      for (b = 0; b < size(); b++)
        gF2nPolynomial.coeff[b] = (GF2nElement)this.coeff[b].add(paramGF2nPolynomial.coeff[b]); 
      while (b < paramGF2nPolynomial.size()) {
        gF2nPolynomial.coeff[b] = paramGF2nPolynomial.coeff[b];
        b++;
      } 
    } 
    return gF2nPolynomial;
  }
  
  public final GF2nPolynomial scalarMultiply(GF2nElement paramGF2nElement) throws RuntimeException {
    GF2nPolynomial gF2nPolynomial = new GF2nPolynomial(size());
    for (byte b = 0; b < size(); b++)
      gF2nPolynomial.coeff[b] = (GF2nElement)this.coeff[b].multiply(paramGF2nElement); 
    return gF2nPolynomial;
  }
  
  public final GF2nPolynomial multiply(GF2nPolynomial paramGF2nPolynomial) throws RuntimeException {
    int i = size();
    int j = paramGF2nPolynomial.size();
    if (i != j)
      throw new IllegalArgumentException("PolynomialGF2n.multiply: this and b must have the same size!"); 
    GF2nPolynomial gF2nPolynomial = new GF2nPolynomial((i << 1) - 1);
    for (byte b = 0; b < size(); b++) {
      for (byte b1 = 0; b1 < paramGF2nPolynomial.size(); b1++) {
        if (gF2nPolynomial.coeff[b + b1] == null) {
          gF2nPolynomial.coeff[b + b1] = (GF2nElement)this.coeff[b].multiply(paramGF2nPolynomial.coeff[b1]);
        } else {
          gF2nPolynomial.coeff[b + b1] = (GF2nElement)gF2nPolynomial.coeff[b + b1].add(this.coeff[b].multiply(paramGF2nPolynomial.coeff[b1]));
        } 
      } 
    } 
    return gF2nPolynomial;
  }
  
  public final GF2nPolynomial multiplyAndReduce(GF2nPolynomial paramGF2nPolynomial1, GF2nPolynomial paramGF2nPolynomial2) throws RuntimeException, ArithmeticException {
    return multiply(paramGF2nPolynomial1).reduce(paramGF2nPolynomial2);
  }
  
  public final GF2nPolynomial reduce(GF2nPolynomial paramGF2nPolynomial) throws RuntimeException, ArithmeticException {
    return remainder(paramGF2nPolynomial);
  }
  
  public final void shiftThisLeft(int paramInt) {
    if (paramInt > 0) {
      int j = this.size;
      GF2nField gF2nField = this.coeff[0].getField();
      enlarge(this.size + paramInt);
      int i;
      for (i = j - 1; i >= 0; i--)
        this.coeff[i + paramInt] = this.coeff[i]; 
      if (this.coeff[0] instanceof GF2nPolynomialElement) {
        for (i = paramInt - 1; i >= 0; i--)
          this.coeff[i] = GF2nPolynomialElement.ZERO((GF2nPolynomialField)gF2nField); 
      } else if (this.coeff[0] instanceof GF2nONBElement) {
        for (i = paramInt - 1; i >= 0; i--)
          this.coeff[i] = GF2nONBElement.ZERO((GF2nONBField)gF2nField); 
      } 
    } 
  }
  
  public final GF2nPolynomial shiftLeft(int paramInt) {
    if (paramInt <= 0)
      return new GF2nPolynomial(this); 
    GF2nPolynomial gF2nPolynomial = new GF2nPolynomial(this.size + paramInt, this.coeff[0]);
    gF2nPolynomial.assignZeroToElements();
    for (byte b = 0; b < this.size; b++)
      gF2nPolynomial.coeff[b + paramInt] = this.coeff[b]; 
    return gF2nPolynomial;
  }
  
  public final GF2nPolynomial[] divide(GF2nPolynomial paramGF2nPolynomial) throws RuntimeException, ArithmeticException {
    GF2nPolynomial[] arrayOfGF2nPolynomial = new GF2nPolynomial[2];
    GF2nPolynomial gF2nPolynomial = new GF2nPolynomial(this);
    gF2nPolynomial.shrink();
    int i = paramGF2nPolynomial.getDegree();
    GF2nElement gF2nElement = (GF2nElement)paramGF2nPolynomial.coeff[i].invert();
    if (gF2nPolynomial.getDegree() < i) {
      arrayOfGF2nPolynomial[0] = new GF2nPolynomial(this);
      arrayOfGF2nPolynomial[0].assignZeroToElements();
      arrayOfGF2nPolynomial[0].shrink();
      arrayOfGF2nPolynomial[1] = new GF2nPolynomial(this);
      arrayOfGF2nPolynomial[1].shrink();
      return arrayOfGF2nPolynomial;
    } 
    arrayOfGF2nPolynomial[0] = new GF2nPolynomial(this);
    arrayOfGF2nPolynomial[0].assignZeroToElements();
    int j;
    for (j = gF2nPolynomial.getDegree() - i; j >= 0; j = gF2nPolynomial.getDegree() - i) {
      GF2nElement gF2nElement1 = (GF2nElement)gF2nPolynomial.coeff[gF2nPolynomial.getDegree()].multiply(gF2nElement);
      GF2nPolynomial gF2nPolynomial1 = paramGF2nPolynomial.scalarMultiply(gF2nElement1);
      gF2nPolynomial1.shiftThisLeft(j);
      gF2nPolynomial = gF2nPolynomial.add(gF2nPolynomial1);
      gF2nPolynomial.shrink();
      (arrayOfGF2nPolynomial[0]).coeff[j] = (GF2nElement)gF2nElement1.clone();
    } 
    arrayOfGF2nPolynomial[1] = gF2nPolynomial;
    arrayOfGF2nPolynomial[0].shrink();
    return arrayOfGF2nPolynomial;
  }
  
  public final GF2nPolynomial remainder(GF2nPolynomial paramGF2nPolynomial) throws RuntimeException, ArithmeticException {
    GF2nPolynomial[] arrayOfGF2nPolynomial = new GF2nPolynomial[2];
    arrayOfGF2nPolynomial = divide(paramGF2nPolynomial);
    return arrayOfGF2nPolynomial[1];
  }
  
  public final GF2nPolynomial quotient(GF2nPolynomial paramGF2nPolynomial) throws RuntimeException, ArithmeticException {
    GF2nPolynomial[] arrayOfGF2nPolynomial = new GF2nPolynomial[2];
    arrayOfGF2nPolynomial = divide(paramGF2nPolynomial);
    return arrayOfGF2nPolynomial[0];
  }
  
  public final GF2nPolynomial gcd(GF2nPolynomial paramGF2nPolynomial) throws RuntimeException, ArithmeticException {
    GF2nPolynomial gF2nPolynomial1 = new GF2nPolynomial(this);
    GF2nPolynomial gF2nPolynomial2 = new GF2nPolynomial(paramGF2nPolynomial);
    gF2nPolynomial1.shrink();
    gF2nPolynomial2.shrink();
    while (!gF2nPolynomial2.isZero()) {
      GF2nPolynomial gF2nPolynomial = gF2nPolynomial1.remainder(gF2nPolynomial2);
      gF2nPolynomial1 = gF2nPolynomial2;
      gF2nPolynomial2 = gF2nPolynomial;
    } 
    GF2nElement gF2nElement = gF2nPolynomial1.coeff[gF2nPolynomial1.getDegree()];
    return gF2nPolynomial1.scalarMultiply((GF2nElement)gF2nElement.invert());
  }
}
