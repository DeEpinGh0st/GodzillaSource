package org.fife.ui.autocomplete;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;




























public class RoundRobinAutoCompletion
  extends AutoCompletion
{
  private List<CompletionProvider> cycle = new ArrayList<>();








  
  public RoundRobinAutoCompletion(CompletionProvider provider) {
    super(provider);
    this.cycle.add(provider);

    
    setHideOnCompletionProviderChange(false);


    
    setHideOnNoText(false);


    
    setAutoCompleteSingleChoices(false);
  }








  
  public void addCompletionProvider(CompletionProvider provider) {
    this.cycle.add(provider);
  }









  
  public boolean advanceProvider() {
    CompletionProvider currentProvider = getCompletionProvider();
    int i = (this.cycle.indexOf(currentProvider) + 1) % this.cycle.size();
    setCompletionProvider(this.cycle.get(i));
    return (i == 0);
  }





  
  protected Action createAutoCompleteAction() {
    return new CycleAutoCompleteAction();
  }




  
  public void resetProvider() {
    CompletionProvider currentProvider = getCompletionProvider();
    CompletionProvider defaultProvider = this.cycle.get(0);
    if (currentProvider != defaultProvider) {
      setCompletionProvider(defaultProvider);
    }
  }


  
  private class CycleAutoCompleteAction
    extends AutoCompletion.AutoCompleteAction
  {
    private CycleAutoCompleteAction() {}


    
    public void actionPerformed(ActionEvent e) {
      if (RoundRobinAutoCompletion.this.isAutoCompleteEnabled()) {
        if (RoundRobinAutoCompletion.this.isPopupVisible()) {

          
          RoundRobinAutoCompletion.this.advanceProvider();
        }
        else {
          
          RoundRobinAutoCompletion.this.resetProvider();
        } 



        
        for (int i = 1; i < RoundRobinAutoCompletion.this.cycle.size(); i++) {
          List<Completion> completions = RoundRobinAutoCompletion.this.getCompletionProvider().getCompletions(RoundRobinAutoCompletion.this.getTextComponent());
          if (completions.size() > 0) {
            break;
          }


          
          RoundRobinAutoCompletion.this.advanceProvider();
        } 
      } 
      
      super.actionPerformed(e);
    }
  }
}
