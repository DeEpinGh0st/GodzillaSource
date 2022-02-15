package org.fife.ui.autocomplete;

import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.fife.ui.rsyntaxtextarea.PopupWindowDecorator;













































public class ParameterizedCompletionChoicesWindow
  extends JWindow
{
  private AutoCompletion ac;
  private JList<Completion> list;
  private DefaultListModel<Completion> model;
  private List<List<Completion>> choicesListList;
  private JScrollPane sp;
  private static final Comparator<Completion> SORT_BY_RELEVANCE_COMPARATOR = new SortByRelevanceComparator();












  
  public ParameterizedCompletionChoicesWindow(Window parent, AutoCompletion ac, final ParameterizedCompletionContext context) {
    super(parent);
    this.ac = ac;
    ComponentOrientation o = ac.getTextComponentOrientation();
    
    this.model = new DefaultListModel<>();
    this.list = new JList<>(this.model);
    if (ac.getParamChoicesRenderer() != null) {
      this.list.setCellRenderer(ac.getParamChoicesRenderer());
    }
    this.list.addMouseListener(new MouseAdapter()
        {
          public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
              context.insertSelectedChoice();
            }
          }
        });
    this.sp = new JScrollPane(this.list);
    
    setContentPane(this.sp);
    applyComponentOrientation(o);
    setFocusableWindowState(false);

    
    PopupWindowDecorator decorator = PopupWindowDecorator.get();
    if (decorator != null) {
      decorator.decorate(this);
    }
  }








  
  public String getSelectedChoice() {
    Completion c = this.list.getSelectedValue();
    return (c == null) ? null : c.toString();
  }






  
  public void incSelection(int amount) {
    int selection = this.list.getSelectedIndex();
    selection += amount;
    if (selection < 0) {
      
      selection = this.model.getSize() - 1;
    } else {
      
      selection %= this.model.getSize();
    } 
    this.list.setSelectedIndex(selection);
    this.list.ensureIndexIsVisible(selection);
  }









  
  public void initialize(ParameterizedCompletion pc) {
    CompletionProvider provider = pc.getProvider();
    ParameterChoicesProvider pcp = provider.getParameterChoicesProvider();
    if (pcp == null) {
      this.choicesListList = null;
      
      return;
    } 
    int paramCount = pc.getParamCount();
    this.choicesListList = new ArrayList<>(paramCount);
    JTextComponent tc = this.ac.getTextComponent();
    
    for (int i = 0; i < paramCount; i++) {
      ParameterizedCompletion.Parameter param = pc.getParam(i);
      List<Completion> choices = pcp.getParameterChoices(tc, param);
      this.choicesListList.add(choices);
    } 
  }













  
  public void setLocationRelativeTo(Rectangle r) {
    Rectangle screenBounds = Util.getScreenBoundsForPoint(r.x, r.y);


    
    int y = r.y + r.height + 5;


    
    int x = r.x;
    if (x < screenBounds.x) {
      x = screenBounds.x;
    }
    else if (x + getWidth() > screenBounds.x + screenBounds.width) {
      x = screenBounds.x + screenBounds.width - getWidth();
    } 
    
    setLocation(x, y);
  }













  
  public void setParameter(int param, String prefix) {
    this.model.clear();
    List<Completion> temp = new ArrayList<>();
    
    if (this.choicesListList != null && param >= 0 && param < this.choicesListList.size()) {
      
      List<Completion> choices = this.choicesListList.get(param);
      if (choices != null) {
        for (Completion completion : choices) {
          String choice = completion.getReplacementText();
          if (prefix == null || Util.startsWithIgnoreCase(choice, prefix)) {
            temp.add(completion);
          }
        } 
      }

      
      Comparator<Completion> c = null;
      
      c = SORT_BY_RELEVANCE_COMPARATOR;
      
      temp.sort(c);
      for (Completion completion : temp) {
        this.model.addElement(completion);
      }
      
      int visibleRowCount = Math.min(this.model.size(), 10);
      this.list.setVisibleRowCount(visibleRowCount);

      
      if (visibleRowCount == 0 && isVisible()) {
        setVisible(false);
      }
      else if (visibleRowCount > 0) {
        Dimension size = getPreferredSize();
        if (size.width < 150) {
          setSize(150, size.height);
        } else {
          
          pack();
        } 
        
        if (this.sp.getVerticalScrollBar() != null && this.sp
          .getVerticalScrollBar().isVisible()) {
          size = getSize();
          int w = size.width + this.sp.getVerticalScrollBar().getWidth() + 5;
          setSize(w, size.height);
        } 
        this.list.setSelectedIndex(0);
        this.list.ensureIndexIsVisible(0);
        if (!isVisible()) {
          setVisible(true);
        }
      }
    
    }
    else {
      
      setVisible(false);
    } 
  }








  
  public void setVisible(boolean visible) {
    if (visible != isVisible()) {
      
      if (visible && this.model.size() == 0) {
        return;
      }
      super.setVisible(visible);
    } 
  }




  
  public void updateUI() {
    SwingUtilities.updateComponentTreeUI(this);
  }
}
