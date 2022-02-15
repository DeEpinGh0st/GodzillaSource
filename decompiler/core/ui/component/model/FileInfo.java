package core.ui.component.model;

import java.text.DecimalFormat;
import util.functions;



public class FileInfo
{
  private static final String[] ShowSize = new String[] { "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };

  
  private long size;

  
  public FileInfo(String size) {
    this.size = functions.stringToLong(size, 0L).longValue();
  }
  
  public long getSize() {
    return this.size;
  }
  
  public void setSize(int size) {
    this.size = size;
  }

  
  public String toString() {
    int em = -1;
    float tmp = (float)this.size;
    float lastTmp = 0.0F;
    if (this.size >= 1024L) {
      while ((tmp /= 1024.0F) >= 1.0F) {
        em++;
        lastTmp = tmp;
      } 
      return (new DecimalFormat(".00")).format(lastTmp) + ShowSize[em];
    } 
    return Long.toString(this.size);
  }
}
