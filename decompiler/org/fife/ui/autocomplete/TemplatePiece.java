package org.fife.ui.autocomplete;






















interface TemplatePiece
{
  String getText();
  
  public static class Text
    implements TemplatePiece
  {
    private String text;
    
    Text(String text) {
      this.text = text;
    }

    
    public String getText() {
      return this.text;
    }

    
    public String toString() {
      return "[TemplatePiece.Text: text=" + this.text + "]";
    }
  }


  
  public static class Param
    implements TemplatePiece
  {
    String text;


    
    Param(String text) {
      this.text = text;
    }

    
    public String getText() {
      return this.text;
    }

    
    public String toString() {
      return "[TemplatePiece.Param: param=" + this.text + "]";
    }
  }


  
  public static class ParamCopy
    implements TemplatePiece
  {
    private String text;


    
    ParamCopy(String text) {
      this.text = text;
    }

    
    public String getText() {
      return this.text;
    }

    
    public String toString() {
      return "[TemplatePiece.ParamCopy: param=" + this.text + "]";
    }
  }
}
