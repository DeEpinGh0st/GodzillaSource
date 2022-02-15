package org.fife.ui.autocomplete;

import java.util.List;
import javax.swing.text.JTextComponent;

public interface ParameterChoicesProvider {
  List<Completion> getParameterChoices(JTextComponent paramJTextComponent, ParameterizedCompletion.Parameter paramParameter);
}
