package com.jediterm.terminal;

import com.jediterm.terminal.ui.AbstractTabbedTerminalWidget;
import com.jediterm.terminal.ui.AbstractTabs;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.TerminalTabsImpl;
import com.jediterm.terminal.ui.settings.SettingsProvider;
import com.jediterm.terminal.ui.settings.TabbedSettingsProvider;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class TabbedTerminalWidget extends AbstractTabbedTerminalWidget<JediTermWidget> {
  public TabbedTerminalWidget(@NotNull TabbedSettingsProvider settingsProvider, @NotNull Function<AbstractTabbedTerminalWidget, JediTermWidget> createNewSessionAction) {
    super(settingsProvider, createNewSessionAction::apply);
  }

  
  public JediTermWidget createInnerTerminalWidget() {
    return new JediTermWidget((SettingsProvider)getSettingsProvider());
  }

  
  protected AbstractTabs<JediTermWidget> createTabbedPane() {
    return (AbstractTabs<JediTermWidget>)new TerminalTabsImpl();
  }
}
