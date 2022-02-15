package org.springframework.core.log;

import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;





































public abstract class LogMessage
  implements CharSequence
{
  @Nullable
  private String result;
  
  public int length() {
    return toString().length();
  }

  
  public char charAt(int index) {
    return toString().charAt(index);
  }

  
  public CharSequence subSequence(int start, int end) {
    return toString().subSequence(start, end);
  }





  
  public String toString() {
    if (this.result == null) {
      this.result = buildString();
    }
    return this.result;
  }



  
  abstract String buildString();



  
  public static LogMessage of(Supplier<? extends CharSequence> supplier) {
    return new SupplierMessage(supplier);
  }






  
  public static LogMessage format(String format, Object arg1) {
    return new FormatMessage1(format, arg1);
  }







  
  public static LogMessage format(String format, Object arg1, Object arg2) {
    return new FormatMessage2(format, arg1, arg2);
  }








  
  public static LogMessage format(String format, Object arg1, Object arg2, Object arg3) {
    return new FormatMessage3(format, arg1, arg2, arg3);
  }









  
  public static LogMessage format(String format, Object arg1, Object arg2, Object arg3, Object arg4) {
    return new FormatMessage4(format, arg1, arg2, arg3, arg4);
  }






  
  public static LogMessage format(String format, Object... args) {
    return new FormatMessageX(format, args);
  }
  
  private static final class SupplierMessage
    extends LogMessage
  {
    private Supplier<? extends CharSequence> supplier;
    
    SupplierMessage(Supplier<? extends CharSequence> supplier) {
      Assert.notNull(supplier, "Supplier must not be null");
      this.supplier = supplier;
    }

    
    String buildString() {
      return ((CharSequence)this.supplier.get()).toString();
    }
  }
  
  private static abstract class FormatMessage
    extends LogMessage
  {
    protected final String format;
    
    FormatMessage(String format) {
      Assert.notNull(format, "Format must not be null");
      this.format = format;
    }
  }
  
  private static final class FormatMessage1
    extends FormatMessage
  {
    private final Object arg1;
    
    FormatMessage1(String format, Object arg1) {
      super(format);
      this.arg1 = arg1;
    }

    
    protected String buildString() {
      return String.format(this.format, new Object[] { this.arg1 });
    }
  }

  
  private static final class FormatMessage2
    extends FormatMessage
  {
    private final Object arg1;
    private final Object arg2;
    
    FormatMessage2(String format, Object arg1, Object arg2) {
      super(format);
      this.arg1 = arg1;
      this.arg2 = arg2;
    }

    
    String buildString() {
      return String.format(this.format, new Object[] { this.arg1, this.arg2 });
    }
  }

  
  private static final class FormatMessage3
    extends FormatMessage
  {
    private final Object arg1;
    
    private final Object arg2;
    private final Object arg3;
    
    FormatMessage3(String format, Object arg1, Object arg2, Object arg3) {
      super(format);
      this.arg1 = arg1;
      this.arg2 = arg2;
      this.arg3 = arg3;
    }

    
    String buildString() {
      return String.format(this.format, new Object[] { this.arg1, this.arg2, this.arg3 });
    }
  }

  
  private static final class FormatMessage4
    extends FormatMessage
  {
    private final Object arg1;
    
    private final Object arg2;
    
    private final Object arg3;
    private final Object arg4;
    
    FormatMessage4(String format, Object arg1, Object arg2, Object arg3, Object arg4) {
      super(format);
      this.arg1 = arg1;
      this.arg2 = arg2;
      this.arg3 = arg3;
      this.arg4 = arg4;
    }

    
    String buildString() {
      return String.format(this.format, new Object[] { this.arg1, this.arg2, this.arg3, this.arg4 });
    }
  }
  
  private static final class FormatMessageX
    extends FormatMessage
  {
    private final Object[] args;
    
    FormatMessageX(String format, Object... args) {
      super(format);
      this.args = args;
    }

    
    String buildString() {
      return String.format(this.format, this.args);
    }
  }
}
