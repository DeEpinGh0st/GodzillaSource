package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;























public class LineNumberAttribute
  extends AttributeInfo
{
  public static final String tag = "LineNumberTable";
  
  LineNumberAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }
  
  private LineNumberAttribute(ConstPool cp, byte[] i) {
    super(cp, "LineNumberTable", i);
  }




  
  public int tableLength() {
    return ByteArray.readU16bit(this.info, 0);
  }







  
  public int startPc(int i) {
    return ByteArray.readU16bit(this.info, i * 4 + 2);
  }







  
  public int lineNumber(int i) {
    return ByteArray.readU16bit(this.info, i * 4 + 4);
  }





  
  public int toLineNumber(int pc) {
    int n = tableLength();
    int i = 0;
    for (; i < n; i++) {
      if (pc < startPc(i)) {
        if (i == 0)
          return lineNumber(0); 
        break;
      } 
    } 
    return lineNumber(i - 1);
  }







  
  public int toStartPc(int line) {
    int n = tableLength();
    for (int i = 0; i < n; i++) {
      if (line == lineNumber(i))
        return startPc(i); 
    } 
    return -1;
  }






  
  public static class Pc
  {
    public int index;





    
    public int line;
  }





  
  public Pc toNearPc(int line) {
    int n = tableLength();
    int nearPc = 0;
    int distance = 0;
    if (n > 0) {
      distance = lineNumber(0) - line;
      nearPc = startPc(0);
    } 
    
    for (int i = 1; i < n; i++) {
      int d = lineNumber(i) - line;
      if ((d < 0 && d > distance) || (d >= 0 && (d < distance || distance < 0))) {
        
        distance = d;
        nearPc = startPc(i);
      } 
    } 
    
    Pc res = new Pc();
    res.index = nearPc;
    res.line = line + distance;
    return res;
  }







  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    byte[] src = this.info;
    int num = src.length;
    byte[] dest = new byte[num];
    for (int i = 0; i < num; i++) {
      dest[i] = src[i];
    }
    LineNumberAttribute attr = new LineNumberAttribute(newCp, dest);
    return attr;
  }



  
  void shiftPc(int where, int gapLength, boolean exclusive) {
    int n = tableLength();
    for (int i = 0; i < n; i++) {
      int pos = i * 4 + 2;
      int pc = ByteArray.readU16bit(this.info, pos);
      if (pc > where || (exclusive && pc == where))
        ByteArray.write16bit(pc + gapLength, this.info, pos); 
    } 
  }
}
