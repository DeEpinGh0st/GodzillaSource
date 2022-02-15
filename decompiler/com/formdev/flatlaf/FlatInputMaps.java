package com.formdev.flatlaf;

import com.formdev.flatlaf.util.SystemInfo;
import java.util.function.BooleanSupplier;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.InputMapUIResource;























class FlatInputMaps
{
  static void initInputMaps(UIDefaults defaults) {
    initBasicInputMaps(defaults);
    initTextComponentInputMaps(defaults);
    
    if (SystemInfo.isMacOS)
      initMacInputMaps(defaults); 
  }
  
  private static void initBasicInputMaps(UIDefaults defaults) {
    defaults.put("Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] { "SPACE", "pressed", "released SPACE", "released" }));



    
    modifyInputMap(defaults, "ComboBox.ancestorInputMap", new Object[] { "SPACE", "spacePopup", "UP", 

          
          mac("selectPrevious2", "selectPrevious"), "DOWN", 
          mac("selectNext2", "selectNext"), "KP_UP", 
          mac("selectPrevious2", "selectPrevious"), "KP_DOWN", 
          mac("selectNext2", "selectNext"),
          
          mac("alt UP", null), "togglePopup", 
          mac("alt DOWN", null), "togglePopup", 
          mac("alt KP_UP", null), "togglePopup", 
          mac("alt KP_DOWN", null), "togglePopup" });

    
    if (!SystemInfo.isMacOS) {
      modifyInputMap(defaults, "FileChooser.ancestorInputMap", new Object[] { "F2", "editFileName", "BACK_SPACE", "Go Up" });
    }




    
    Object[] bindings = (Object[])defaults.get("PopupMenu.selectedWindowInputMapBindings");
    Object[] rtlBindings = (Object[])defaults.get("PopupMenu.selectedWindowInputMapBindings.RightToLeft");
    if (bindings != null && rtlBindings != null) {
      Object[] newBindings = new Object[bindings.length + rtlBindings.length];
      System.arraycopy(bindings, 0, newBindings, 0, bindings.length);
      System.arraycopy(rtlBindings, 0, newBindings, bindings.length, rtlBindings.length);
      defaults.put("PopupMenu.selectedWindowInputMapBindings.RightToLeft", newBindings);
    } 
    
    modifyInputMap(defaults, "TabbedPane.ancestorInputMap", new Object[] { "ctrl TAB", "navigateNext", "shift ctrl TAB", "navigatePrevious" });




    
    modifyInputMap(() -> UIManager.getBoolean("Table.consistentHomeEndKeyBehavior"), defaults, "Table.ancestorInputMap", new Object[] { "HOME", "selectFirstRow", "END", "selectLastRow", "shift HOME", "selectFirstRowExtendSelection", "shift END", "selectLastRowExtendSelection", 






          
          mac("ctrl HOME", null), "selectFirstColumn", 
          mac("ctrl END", null), "selectLastColumn", 
          mac("shift ctrl HOME", null), "selectFirstColumnExtendSelection", 
          mac("shift ctrl END", null), "selectLastColumnExtendSelection" });

    
    if (!SystemInfo.isMacOS) {
      modifyInputMap(defaults, "Tree.focusInputMap", new Object[] { "ADD", "expand", "SUBTRACT", "collapse" });
    }
  }



























































  
  private static void initTextComponentInputMaps(UIDefaults defaults) {
    Object[] commonTextComponentBindings = { "LEFT", "caret-backward", "RIGHT", "caret-forward", "KP_LEFT", "caret-backward", "KP_RIGHT", "caret-forward", "shift LEFT", "selection-backward", "shift RIGHT", "selection-forward", "shift KP_LEFT", "selection-backward", "shift KP_RIGHT", "selection-forward", mac("ctrl LEFT", "alt LEFT"), "caret-previous-word", mac("ctrl RIGHT", "alt RIGHT"), "caret-next-word", mac("ctrl KP_LEFT", "alt KP_LEFT"), "caret-previous-word", mac("ctrl KP_RIGHT", "alt KP_RIGHT"), "caret-next-word", mac("ctrl shift LEFT", "shift alt LEFT"), "selection-previous-word", mac("ctrl shift RIGHT", "shift alt RIGHT"), "selection-next-word", mac("ctrl shift KP_LEFT", "shift alt KP_LEFT"), "selection-previous-word", mac("ctrl shift KP_RIGHT", "shift alt KP_RIGHT"), "selection-next-word", mac("HOME", "meta LEFT"), "caret-begin-line", mac("END", "meta RIGHT"), "caret-end-line", mac("shift HOME", "shift meta LEFT"), "selection-begin-line", mac("shift END", "shift meta RIGHT"), "selection-end-line", mac("ctrl A", "meta A"), "select-all", mac("ctrl BACK_SLASH", "meta BACK_SLASH"), "unselect", "BACK_SPACE", "delete-previous", "shift BACK_SPACE", "delete-previous", "ctrl H", "delete-previous", "DELETE", "delete-next", mac("ctrl BACK_SPACE", "alt BACK_SPACE"), "delete-previous-word", mac("ctrl DELETE", "alt DELETE"), "delete-next-word", mac("ctrl X", "meta X"), "cut-to-clipboard", mac("ctrl C", "meta C"), "copy-to-clipboard", mac("ctrl V", "meta V"), "paste-from-clipboard", "CUT", "cut-to-clipboard", "COPY", "copy-to-clipboard", "PASTE", "paste-from-clipboard", mac("shift DELETE", null), "cut-to-clipboard", mac("control INSERT", null), "copy-to-clipboard", mac("shift INSERT", null), "paste-from-clipboard", "control shift O", "toggle-componentOrientation" };




    
    (new Object[58])[0] = "ctrl B"; (new Object[58])[1] = "caret-backward"; (new Object[58])[2] = "ctrl F"; (new Object[58])[3] = "caret-forward"; (new Object[58])[4] = "HOME"; (new Object[58])[5] = "caret-begin"; (new Object[58])[6] = "END"; (new Object[58])[7] = "caret-end"; (new Object[58])[8] = "meta UP"; (new Object[58])[9] = "caret-begin"; (new Object[58])[10] = "meta DOWN"; (new Object[58])[11] = "caret-end"; (new Object[58])[12] = "meta KP_UP"; (new Object[58])[13] = "caret-begin"; (new Object[58])[14] = "meta KP_DOWN"; (new Object[58])[15] = "caret-end"; (new Object[58])[16] = "ctrl P"; (new Object[58])[17] = "caret-begin"; (new Object[58])[18] = "ctrl N"; (new Object[58])[19] = "caret-end"; (new Object[58])[20] = "ctrl V"; (new Object[58])[21] = "caret-end"; (new Object[58])[22] = "meta KP_LEFT"; (new Object[58])[23] = "caret-begin-line"; (new Object[58])[24] = "meta KP_RIGHT"; (new Object[58])[25] = "caret-end-line"; (new Object[58])[26] = "ctrl A"; (new Object[58])[27] = "caret-begin-line"; (new Object[58])[28] = "ctrl E"; (new Object[58])[29] = "caret-end-line"; (new Object[58])[30] = "shift meta UP"; (new Object[58])[31] = "selection-begin"; (new Object[58])[32] = "shift meta DOWN"; (new Object[58])[33] = "selection-end"; (new Object[58])[34] = "shift meta KP_UP"; (new Object[58])[35] = "selection-begin"; (new Object[58])[36] = "shift meta KP_DOWN"; (new Object[58])[37] = "selection-end"; (new Object[58])[38] = "shift HOME"; (new Object[58])[39] = "selection-begin"; (new Object[58])[40] = "shift END"; (new Object[58])[41] = "selection-end"; (new Object[58])[42] = "shift meta KP_LEFT"; (new Object[58])[43] = "selection-begin-line"; (new Object[58])[44] = "shift meta KP_RIGHT"; (new Object[58])[45] = "selection-end-line"; (new Object[58])[46] = "shift UP"; (new Object[58])[47] = "selection-begin-line"; (new Object[58])[48] = "shift DOWN"; (new Object[58])[49] = "selection-end-line"; (new Object[58])[50] = "shift KP_UP"; (new Object[58])[51] = "selection-begin-line"; (new Object[58])[52] = "shift KP_DOWN"; (new Object[58])[53] = "selection-end-line"; (new Object[58])[54] = "ctrl W"; (new Object[58])[55] = "delete-previous-word"; (new Object[58])[56] = "ctrl D"; (new Object[58])[57] = "delete-next"; Object[] macCommonTextComponentBindings = SystemInfo.isMacOS ? new Object[58] : null;









































    
    Object[] singleLineTextComponentBindings = { "ENTER", "notify-field-accept" };


    
    (new Object[8])[0] = "UP"; (new Object[8])[1] = "caret-begin-line"; (new Object[8])[2] = "DOWN"; (new Object[8])[3] = "caret-end-line"; (new Object[8])[4] = "KP_UP"; (new Object[8])[5] = "caret-begin-line"; (new Object[8])[6] = "KP_DOWN"; (new Object[8])[7] = "caret-end-line"; Object[] macSingleLineTextComponentBindings = SystemInfo.isMacOS ? new Object[8] : null;






    
    Object[] formattedTextComponentBindings = { "ESCAPE", "reset-field-edit", "UP", "increment", "DOWN", "decrement", "KP_UP", "increment", "KP_DOWN", "decrement" };
























    
    Object[] passwordTextComponentBindings = { mac("ctrl LEFT", "alt LEFT"), "caret-begin-line", mac("ctrl RIGHT", "alt RIGHT"), "caret-end-line", mac("ctrl KP_LEFT", "alt KP_LEFT"), "caret-begin-line", mac("ctrl KP_RIGHT", "alt KP_RIGHT"), "caret-end-line", mac("ctrl shift LEFT", "shift alt LEFT"), "selection-begin-line", mac("ctrl shift RIGHT", "shift alt RIGHT"), "selection-end-line", mac("ctrl shift KP_LEFT", "shift alt KP_LEFT"), "selection-begin-line", mac("ctrl shift KP_RIGHT", "shift alt KP_RIGHT"), "selection-end-line", mac("ctrl BACK_SPACE", "alt BACK_SPACE"), null, mac("ctrl DELETE", "alt DELETE"), null };







































    
    Object[] multiLineTextComponentBindings = { "UP", "caret-up", "DOWN", "caret-down", "KP_UP", "caret-up", "KP_DOWN", "caret-down", "shift UP", "selection-up", "shift DOWN", "selection-down", "shift KP_UP", "selection-up", "shift KP_DOWN", "selection-down", "PAGE_UP", "page-up", "PAGE_DOWN", "page-down", "shift PAGE_UP", "selection-page-up", "shift PAGE_DOWN", "selection-page-down", mac("ctrl shift PAGE_UP", "shift meta PAGE_UP"), "selection-page-left", mac("ctrl shift PAGE_DOWN", "shift meta PAGE_DOWN"), "selection-page-right", mac("ctrl HOME", "meta UP"), "caret-begin", mac("ctrl END", "meta DOWN"), "caret-end", mac("ctrl shift HOME", "shift meta UP"), "selection-begin", mac("ctrl shift END", "shift meta DOWN"), "selection-end", "ENTER", "insert-break", "TAB", "insert-tab", mac("ctrl T", "meta T"), "next-link-action", mac("ctrl shift T", "shift meta T"), "previous-link-action", mac("ctrl SPACE", "meta SPACE"), "activate-link-action" };

    
    (new Object[14])[0] = "ctrl N"; (new Object[14])[1] = "caret-down"; (new Object[14])[2] = "ctrl P"; (new Object[14])[3] = "caret-up"; (new Object[14])[4] = "shift alt UP"; (new Object[14])[5] = "selection-begin-paragraph"; (new Object[14])[6] = "shift alt DOWN"; (new Object[14])[7] = "selection-end-paragraph"; (new Object[14])[8] = "shift alt KP_UP"; (new Object[14])[9] = "selection-begin-paragraph"; (new Object[14])[10] = "shift alt KP_DOWN"; (new Object[14])[11] = "selection-end-paragraph"; (new Object[14])[12] = "ctrl V"; (new Object[14])[13] = "page-down"; Object[] macMultiLineTextComponentBindings = SystemInfo.isMacOS ? new Object[14] : null;













    
    defaults.put("TextField.focusInputMap", new LazyInputMapEx(new Object[][] { commonTextComponentBindings, macCommonTextComponentBindings, singleLineTextComponentBindings, macSingleLineTextComponentBindings }));




    
    defaults.put("FormattedTextField.focusInputMap", new LazyInputMapEx(new Object[][] { commonTextComponentBindings, macCommonTextComponentBindings, singleLineTextComponentBindings, macSingleLineTextComponentBindings, formattedTextComponentBindings }));





    
    defaults.put("PasswordField.focusInputMap", new LazyInputMapEx(new Object[][] { commonTextComponentBindings, macCommonTextComponentBindings, singleLineTextComponentBindings, macSingleLineTextComponentBindings, passwordTextComponentBindings }));






    
    Object multiLineInputMap = new LazyInputMapEx(new Object[][] { commonTextComponentBindings, macCommonTextComponentBindings, multiLineTextComponentBindings, macMultiLineTextComponentBindings });




    
    defaults.put("TextArea.focusInputMap", multiLineInputMap);
    defaults.put("TextPane.focusInputMap", multiLineInputMap);
    defaults.put("EditorPane.focusInputMap", multiLineInputMap);
  }

  
  private static void initMacInputMaps(UIDefaults defaults) {
    modifyInputMap(defaults, "List.focusInputMap", new Object[] { "meta A", "selectAll", "meta C", "copy", "meta V", "paste", "meta X", "cut", "HOME", null, "END", null, "PAGE_UP", null, "PAGE_DOWN", null, "ctrl A", null, "ctrl BACK_SLASH", null, "ctrl C", null, "ctrl DOWN", null, "ctrl END", null, "ctrl HOME", null, "ctrl INSERT", null, "ctrl KP_DOWN", null, "ctrl KP_LEFT", null, "ctrl KP_RIGHT", null, "ctrl KP_UP", null, "ctrl LEFT", null, "ctrl PAGE_DOWN", null, "ctrl PAGE_UP", null, "ctrl RIGHT", null, "ctrl SLASH", null, "ctrl SPACE", null, "ctrl UP", null, "ctrl V", null, "ctrl X", null, "SPACE", null, "shift ctrl DOWN", null, "shift ctrl END", null, "shift ctrl HOME", null, "shift ctrl KP_DOWN", null, "shift ctrl KP_LEFT", null, "shift ctrl KP_RIGHT", null, "shift ctrl KP_UP", null, "shift ctrl LEFT", null, "shift ctrl PAGE_DOWN", null, "shift ctrl PAGE_UP", null, "shift ctrl RIGHT", null, "shift ctrl SPACE", null, "shift ctrl UP", null, "shift DELETE", null, "shift INSERT", null, "shift SPACE", null });
















































    
    modifyInputMap(defaults, "List.focusInputMap.RightToLeft", new Object[] { "ctrl KP_LEFT", null, "ctrl KP_RIGHT", null, "ctrl LEFT", null, "ctrl RIGHT", null, "shift ctrl KP_LEFT", null, "shift ctrl KP_RIGHT", null, "shift ctrl LEFT", null, "shift ctrl RIGHT", null });










    
    modifyInputMap(defaults, "ScrollPane.ancestorInputMap", new Object[] { "END", "scrollEnd", "HOME", "scrollHome", "ctrl END", null, "ctrl HOME", null, "ctrl PAGE_DOWN", null, "ctrl PAGE_UP", null });







    
    modifyInputMap(defaults, "ScrollPane.ancestorInputMap.RightToLeft", new Object[] { "ctrl PAGE_DOWN", null, "ctrl PAGE_UP", null });




    
    modifyInputMap(defaults, "TabbedPane.ancestorInputMap", new Object[] { "ctrl UP", null, "ctrl KP_UP", null });


    
    modifyInputMap(defaults, "TabbedPane.focusInputMap", new Object[] { "ctrl DOWN", null, "ctrl KP_DOWN", null });




    
    modifyInputMap(defaults, "Table.ancestorInputMap", new Object[] { "alt TAB", "focusHeader", "shift alt TAB", "focusHeader", "meta A", "selectAll", "meta C", "copy", "meta V", "paste", "meta X", "cut", "HOME", null, "END", null, "PAGE_UP", null, "PAGE_DOWN", null, "ctrl A", null, "ctrl BACK_SLASH", null, "ctrl C", null, "ctrl DOWN", null, "ctrl END", null, "ctrl HOME", null, "ctrl INSERT", null, "ctrl KP_DOWN", null, "ctrl KP_LEFT", null, "ctrl KP_RIGHT", null, "ctrl KP_UP", null, "ctrl LEFT", null, "ctrl PAGE_DOWN", null, "ctrl PAGE_UP", null, "ctrl RIGHT", null, "ctrl SLASH", null, "ctrl SPACE", null, "ctrl UP", null, "ctrl V", null, "ctrl X", null, "F2", null, "F8", null, "SPACE", null, "shift ctrl DOWN", null, "shift ctrl END", null, "shift ctrl HOME", null, "shift ctrl KP_DOWN", null, "shift ctrl KP_LEFT", null, "shift ctrl KP_RIGHT", null, "shift ctrl KP_UP", null, "shift ctrl LEFT", null, "shift ctrl PAGE_DOWN", null, "shift ctrl PAGE_UP", null, "shift ctrl RIGHT", null, "shift ctrl SPACE", null, "shift ctrl UP", null, "shift DELETE", null, "shift INSERT", null, "shift SPACE", null });




















































    
    modifyInputMap(defaults, "Table.ancestorInputMap.RightToLeft", new Object[] { "ctrl KP_LEFT", null, "ctrl KP_RIGHT", null, "ctrl LEFT", null, "ctrl RIGHT", null, "shift ctrl KP_LEFT", null, "shift ctrl KP_RIGHT", null, "shift ctrl LEFT", null, "shift ctrl RIGHT", null });










    
    modifyInputMap(defaults, "Tree.focusInputMap", new Object[] { "LEFT", "selectParent", "RIGHT", "selectChild", "KP_LEFT", "selectParent", "KP_RIGHT", "selectChild", "shift LEFT", "selectParent", "shift RIGHT", "selectChild", "shift KP_LEFT", "selectParent", "shift KP_RIGHT", "selectChild", "alt LEFT", "selectParent", "alt RIGHT", "selectChild", "alt KP_LEFT", "selectParent", "alt KP_RIGHT", "selectChild", "shift HOME", "selectFirstExtendSelection", "shift END", "selectLastExtendSelection", "meta A", "selectAll", "meta C", "copy", "meta V", "paste", "meta X", "cut", "HOME", null, "END", null, "PAGE_UP", null, "PAGE_DOWN", null, "ctrl LEFT", null, "ctrl RIGHT", null, "ctrl KP_LEFT", null, "ctrl KP_RIGHT", null, "ctrl A", null, "ctrl BACK_SLASH", null, "ctrl C", null, "ctrl DOWN", null, "ctrl END", null, "ctrl HOME", null, "ctrl INSERT", null, "ctrl KP_DOWN", null, "ctrl KP_UP", null, "ctrl PAGE_DOWN", null, "ctrl PAGE_UP", null, "ctrl SLASH", null, "ctrl SPACE", null, "ctrl UP", null, "ctrl V", null, "ctrl X", null, "F2", null, "SPACE", null, "shift ctrl DOWN", null, "shift ctrl END", null, "shift ctrl HOME", null, "shift ctrl KP_DOWN", null, "shift ctrl KP_UP", null, "shift ctrl PAGE_DOWN", null, "shift ctrl PAGE_UP", null, "shift ctrl SPACE", null, "shift ctrl UP", null, "shift DELETE", null, "shift INSERT", null, "shift PAGE_DOWN", null, "shift PAGE_UP", null, "shift SPACE", null });
































































    
    defaults.put("Tree.focusInputMap.RightToLeft", new UIDefaults.LazyInputMap(new Object[] { "LEFT", "selectChild", "RIGHT", "selectParent", "KP_LEFT", "selectChild", "KP_RIGHT", "selectParent", "shift LEFT", "selectChild", "shift RIGHT", "selectParent", "shift KP_LEFT", "selectChild", "shift KP_RIGHT", "selectParent", "alt LEFT", "selectChild", "alt RIGHT", "selectParent", "alt KP_LEFT", "selectChild", "alt KP_RIGHT", "selectParent" }));
  }













  
  private static void modifyInputMap(UIDefaults defaults, String key, Object... bindings) {
    modifyInputMap(null, defaults, key, bindings);
  }

  
  private static void modifyInputMap(BooleanSupplier condition, UIDefaults defaults, String key, Object... bindings) {
    defaults.put(key, new LazyModifyInputMap(condition, defaults.remove(key), bindings));
  }
  
  private static <T> T mac(T value, T macValue) {
    return SystemInfo.isMacOS ? macValue : value;
  }



  
  private static class LazyInputMapEx
    implements UIDefaults.LazyValue
  {
    private final Object[][] bindingsArray;



    
    LazyInputMapEx(Object[]... bindingsArray) {
      this.bindingsArray = bindingsArray;
    }

    
    public Object createValue(UIDefaults table) {
      InputMap inputMap = new InputMapUIResource();
      for (Object[] bindings : this.bindingsArray)
        LookAndFeel.loadKeyBindings(inputMap, bindings); 
      return inputMap;
    }
  }


  
  private static class LazyModifyInputMap
    implements UIDefaults.LazyValue
  {
    private final BooleanSupplier condition;
    
    private final Object baseInputMap;
    
    private final Object[] bindings;

    
    LazyModifyInputMap(BooleanSupplier condition, Object baseInputMap, Object[] bindings) {
      this.condition = condition;
      this.baseInputMap = baseInputMap;
      this.bindings = bindings;
    }



    
    public Object createValue(UIDefaults table) {
      InputMap inputMap = (this.baseInputMap instanceof UIDefaults.LazyValue) ? (InputMap)((UIDefaults.LazyValue)this.baseInputMap).createValue(table) : (InputMap)this.baseInputMap;

      
      if (this.condition != null && !this.condition.getAsBoolean()) {
        return inputMap;
      }
      
      for (int i = 0; i < this.bindings.length; i += 2) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke((String)this.bindings[i]);
        if (this.bindings[i + 1] != null) {
          inputMap.put(keyStroke, this.bindings[i + 1]);
        } else {
          inputMap.remove(keyStroke);
        } 
      } 
      return inputMap;
    }
  }
}
