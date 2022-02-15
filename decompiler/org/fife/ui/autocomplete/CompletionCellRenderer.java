package org.fife.ui.autocomplete;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.text.View;


















































































public class CompletionCellRenderer
  extends DefaultListCellRenderer
{
  private static Color altBG;
  private Font font;
  private boolean showTypes;
  private String typeColor;
  private boolean selected;
  private Color realBG;
  private String paramColor;
  private Icon emptyIcon;
  private Rectangle paintTextR;
  private DefaultListCellRenderer delegate;
  private static final String SUBSTANCE_RENDERER_CLASS_NAME = "org.pushingpixels.substance.api.renderer.SubstanceDefaultListCellRenderer";
  private static final String PREFIX = "<html><nobr>";
  
  public CompletionCellRenderer() {
    init();
  }











  
  public CompletionCellRenderer(DefaultListCellRenderer delegate) {
    setDelegateRenderer(delegate);
    init();
  }








  
  protected Icon createEmptyIcon() {
    return new EmptyIcon(16);
  }







  
  private String createParamColor() {
    return Util.isLightForeground(getForeground()) ? 
      Util.getHexString(Util.getHyperlinkForeground()) : "#aa0077";
  }







  
  private String createTypeColor() {
    return "#808080";
  }










  
  public void delegateToSubstanceRenderer() throws Exception {
    Class<?> clazz = Class.forName("org.pushingpixels.substance.api.renderer.SubstanceDefaultListCellRenderer");
    
    DefaultListCellRenderer delegate = (DefaultListCellRenderer)clazz.newInstance();
    setDelegateRenderer(delegate);
  }








  
  public static Color getAlternateBackground() {
    return altBG;
  }







  
  public DefaultListCellRenderer getDelegateRenderer() {
    return this.delegate;
  }








  
  public Font getDisplayFont() {
    return this.font;
  }








  
  protected Icon getEmptyIcon() {
    if (this.emptyIcon == null) {
      this.emptyIcon = createEmptyIcon();
    }
    return this.emptyIcon;
  }








  
  protected Icon getIcon(String resource) {
    URL url = getClass().getResource(resource);
    if (url == null) {
      File file = new File(resource);
      try {
        url = file.toURI().toURL();
      } catch (MalformedURLException mue) {
        mue.printStackTrace();
      } 
    } 
    return (url != null) ? new ImageIcon(url) : null;
  }













  
  public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean selected, boolean hasFocus) {
    super.getListCellRendererComponent(list, value, index, selected, hasFocus);
    if (this.font != null) {
      setFont(this.font);
    }
    this.selected = selected;
    this.realBG = (altBG != null && (index & 0x1) == 1) ? altBG : list.getBackground();
    
    Completion c = (Completion)value;
    setIcon(c.getIcon());
    
    if (c instanceof FunctionCompletion) {
      FunctionCompletion fc = (FunctionCompletion)value;
      prepareForFunctionCompletion(list, fc, index, selected, hasFocus);
    }
    else if (c instanceof VariableCompletion) {
      VariableCompletion vc = (VariableCompletion)value;
      prepareForVariableCompletion(list, vc, index, selected, hasFocus);
    }
    else if (c instanceof TemplateCompletion) {
      TemplateCompletion tc = (TemplateCompletion)value;
      prepareForTemplateCompletion(list, tc, index, selected, hasFocus);
    }
    else if (c instanceof MarkupTagCompletion) {
      MarkupTagCompletion mtc = (MarkupTagCompletion)value;
      prepareForMarkupTagCompletion(list, mtc, index, selected, hasFocus);
    } else {
      
      prepareForOtherCompletion(list, c, index, selected, hasFocus);
    } 


    
    if (this.delegate != null) {
      this.delegate.getListCellRendererComponent(list, getText(), index, selected, hasFocus);
      
      this.delegate.setFont(getFont());
      this.delegate.setIcon(getIcon());
      return this.delegate;
    } 
    
    if (!selected && (index & 0x1) == 1 && altBG != null) {
      setBackground(altBG);
    }
    
    return this;
  }









  
  public boolean getShowTypes() {
    return this.showTypes;
  }


  
  private void init() {
    setShowTypes(true);
    this.typeColor = createTypeColor();
    this.paramColor = createParamColor();
    this.paintTextR = new Rectangle();
  }





  
  protected void paintComponent(Graphics g) {
    g.setColor(this.realBG);
    int iconW = 0;
    if (getIcon() != null) {
      iconW = getIcon().getIconWidth();
    }
    if (this.selected && iconW > 0) {
      g.fillRect(0, 0, iconW, getHeight());
      g.setColor(getBackground());
      g.fillRect(iconW, 0, getWidth() - iconW, getHeight());
    } else {
      
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    } 
    if (getIcon() != null) {
      Icon icon = getIcon();
      icon.paintIcon(this, g, 0, (getHeight() - icon.getIconHeight()) / 2);
    } 
    
    String text = getText();
    if (text != null) {
      this.paintTextR.setBounds(iconW, 0, getWidth() - iconW, getHeight());
      this.paintTextR.x += 3;
      int space = this.paintTextR.height - g.getFontMetrics().getHeight();
      View v = (View)getClientProperty("html");
      if (v != null) {

        
        this.paintTextR.y += space / 2;
        this.paintTextR.height -= space;
        v.paint(g, this.paintTextR);
      } else {
        
        int textX = this.paintTextR.x;
        int textY = this.paintTextR.y;
        
        g.drawString(text, textX, textY);
      } 
    } 
  }













  
  protected void prepareForFunctionCompletion(JList list, FunctionCompletion fc, int index, boolean selected, boolean hasFocus) {
    StringBuilder sb = new StringBuilder("<html><nobr>");
    sb.append(fc.getName());
    
    char paramListStart = fc.getProvider().getParameterListStart();
    if (paramListStart != '\000') {
      sb.append(paramListStart);
    }
    
    int paramCount = fc.getParamCount();
    for (int i = 0; i < paramCount; i++) {
      ParameterizedCompletion.Parameter param = fc.getParam(i);
      String type = param.getType();
      String name = param.getName();
      if (type != null) {
        if (!selected) {
          sb.append("<font color='").append(this.paramColor).append("'>");
        }
        sb.append(type);
        if (!selected) {
          sb.append("</font>");
        }
        if (name != null) {
          sb.append(' ');
        }
      } 
      if (name != null) {
        sb.append(name);
      }
      if (i < paramCount - 1) {
        sb.append(fc.getProvider().getParameterListSeparator());
      }
    } 
    
    char paramListEnd = fc.getProvider().getParameterListEnd();
    if (paramListEnd != '\000') {
      sb.append(paramListEnd);
    }
    
    if (getShowTypes() && fc.getType() != null) {
      sb.append(" : ");
      if (!selected) {
        sb.append("<font color='").append(this.typeColor).append("'>");
      }
      sb.append(fc.getType());
      if (!selected) {
        sb.append("</font>");
      }
    } 
    
    setText(sb.toString());
  }













  
  protected void prepareForMarkupTagCompletion(JList list, MarkupTagCompletion mc, int index, boolean selected, boolean hasFocus) {
    StringBuilder sb = new StringBuilder("<html><nobr>");
    sb.append(mc.getName());
    
    setText(sb.toString());
  }














  
  protected void prepareForOtherCompletion(JList list, Completion c, int index, boolean selected, boolean hasFocus) {
    StringBuilder sb = new StringBuilder("<html><nobr>");
    sb.append(c.getInputText());
    
    if (c instanceof BasicCompletion) {
      String definition = ((BasicCompletion)c).getShortDescription();
      if (definition != null) {
        sb.append(" - ");
        if (!selected) {
          sb.append("<font color='").append(this.typeColor).append("'>");
        }
        sb.append(definition);
        if (!selected) {
          sb.append("</font>");
        }
      } 
    } 
    
    setText(sb.toString());
  }













  
  protected void prepareForTemplateCompletion(JList list, TemplateCompletion tc, int index, boolean selected, boolean hasFocus) {
    StringBuilder sb = new StringBuilder("<html><nobr>");
    sb.append(tc.getInputText());
    
    String definition = tc.getShortDescription();
    if (definition != null) {
      sb.append(" - ");
      if (!selected) {
        sb.append("<font color='").append(this.typeColor).append("'>");
      }
      sb.append(definition);
      if (!selected) {
        sb.append("</font>");
      }
    } 
    
    setText(sb.toString());
  }













  
  protected void prepareForVariableCompletion(JList list, VariableCompletion vc, int index, boolean selected, boolean hasFocus) {
    StringBuilder sb = new StringBuilder("<html><nobr>");
    sb.append(vc.getName());
    
    if (getShowTypes() && vc.getType() != null) {
      sb.append(" : ");
      if (!selected) {
        sb.append("<font color='").append(this.typeColor).append("'>");
      }
      sb.append(vc.getType());
      if (!selected) {
        sb.append("</font>");
      }
    } 
    
    setText(sb.toString());
  }










  
  public static void setAlternateBackground(Color altBG) {
    CompletionCellRenderer.altBG = altBG;
  }












  
  public void setDelegateRenderer(DefaultListCellRenderer delegate) {
    this.delegate = delegate;
  }








  
  public void setDisplayFont(Font font) {
    this.font = font;
  }








  
  protected void setIconWithDefault(Completion completion) {
    setIconWithDefault(completion, getEmptyIcon());
  }










  
  protected void setIconWithDefault(Completion completion, Icon defaultIcon) {
    Icon icon = completion.getIcon();
    setIcon((icon != null) ? icon : ((defaultIcon != null) ? defaultIcon : this.emptyIcon));
  }








  
  public void setParamColor(Color color) {
    if (color != null) {
      this.paramColor = Util.getHexString(color);
    }
  }








  
  public void setShowTypes(boolean show) {
    this.showTypes = show;
  }











  
  public void setTypeColor(Color color) {
    if (color != null) {
      this.typeColor = Util.getHexString(color);
    }
  }





  
  public void updateUI() {
    super.updateUI();
    if (this.delegate != null) {
      SwingUtilities.updateComponentTreeUI(this.delegate);
    }
    this.paramColor = createParamColor();
  }
}
