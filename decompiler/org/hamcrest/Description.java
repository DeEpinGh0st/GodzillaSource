package org.hamcrest;









public interface Description
{
  public static final Description NONE = new NullDescription();




  
  Description appendText(String paramString);



  
  Description appendDescriptionOf(SelfDescribing paramSelfDescribing);



  
  Description appendValue(Object paramObject);



  
  <T> Description appendValueList(String paramString1, String paramString2, String paramString3, T... paramVarArgs);



  
  <T> Description appendValueList(String paramString1, String paramString2, String paramString3, Iterable<T> paramIterable);



  
  Description appendList(String paramString1, String paramString2, String paramString3, Iterable<? extends SelfDescribing> paramIterable);



  
  public static final class NullDescription
    implements Description
  {
    public Description appendDescriptionOf(SelfDescribing value) {
      return this;
    }


    
    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
      return this;
    }

    
    public Description appendText(String text) {
      return this;
    }

    
    public Description appendValue(Object value) {
      return this;
    }


    
    public <T> Description appendValueList(String start, String separator, String end, T... values) {
      return this;
    }


    
    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
      return this;
    }

    
    public String toString() {
      return "";
    }
  }
}
