package javassist.compiler.ast;

import javassist.compiler.CompileError;



















public class IntConst
  extends ASTree
{
  private static final long serialVersionUID = 1L;
  protected long value;
  protected int type;
  
  public IntConst(long v, int tokenId) {
    this.value = v; this.type = tokenId;
  } public long get() {
    return this.value;
  } public void set(long v) {
    this.value = v;
  }
  
  public int getType() {
    return this.type;
  }
  public String toString() {
    return Long.toString(this.value);
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atIntConst(this);
  }
  
  public ASTree compute(int op, ASTree right) {
    if (right instanceof IntConst)
      return compute0(op, (IntConst)right); 
    if (right instanceof DoubleConst) {
      return compute0(op, (DoubleConst)right);
    }
    return null;
  } private IntConst compute0(int op, IntConst right) {
    int newType;
    long newValue;
    int type1 = this.type;
    int type2 = right.type;
    
    if (type1 == 403 || type2 == 403) {
      newType = 403;
    } else if (type1 == 401 && type2 == 401) {
      
      newType = 401;
    } else {
      newType = 402;
    } 
    long value1 = this.value;
    long value2 = right.value;
    
    switch (op) {
      case 43:
        newValue = value1 + value2;





































        
        return new IntConst(newValue, newType);case 45: newValue = value1 - value2; return new IntConst(newValue, newType);case 42: newValue = value1 * value2; return new IntConst(newValue, newType);case 47: newValue = value1 / value2; return new IntConst(newValue, newType);case 37: newValue = value1 % value2; return new IntConst(newValue, newType);case 124: newValue = value1 | value2; return new IntConst(newValue, newType);case 94: newValue = value1 ^ value2; return new IntConst(newValue, newType);case 38: newValue = value1 & value2; return new IntConst(newValue, newType);case 364: newValue = this.value << (int)value2; newType = type1; return new IntConst(newValue, newType);case 366: newValue = this.value >> (int)value2; newType = type1; return new IntConst(newValue, newType);case 370: newValue = this.value >>> (int)value2; newType = type1; return new IntConst(newValue, newType);
    } 
    return null;
  } private DoubleConst compute0(int op, DoubleConst right) {
    double newValue, value1 = this.value;
    double value2 = right.value;
    
    switch (op) {
      case 43:
        newValue = value1 + value2;
















        
        return new DoubleConst(newValue, right.type);case 45: newValue = value1 - value2; return new DoubleConst(newValue, right.type);case 42: newValue = value1 * value2; return new DoubleConst(newValue, right.type);case 47: newValue = value1 / value2; return new DoubleConst(newValue, right.type);case 37: newValue = value1 % value2; return new DoubleConst(newValue, right.type);
    } 
    return null;
  }
}
