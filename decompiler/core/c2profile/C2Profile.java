package core.c2profile;

import core.c2profile.config.BasicConfig;
import core.c2profile.config.CoreConfig;
import core.c2profile.location.ChannelLocationEnum;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.annotation.YamlClass;
import org.yaml.snakeyaml.annotation.YamlComment;
import org.yaml.snakeyaml.introspector.BeanAccess;































@YamlClass
public class C2Profile
{
  @YamlComment(Comment = "支持的Payload")
  public String supportPayload = "ALL"; @YamlComment(Comment = "基础配置")
  public BasicConfig basicConfig = new BasicConfig(); @YamlComment(Comment = "核心配置")
  public CoreConfig coreConfig = new CoreConfig(); @YamlComment(Comment = "静态变量")
  public HashMap staticVars = new HashMap<>();
  @YamlComment(Comment = "信道定位方式")
  public ChannelLocationEnum channelLocation = ChannelLocationEnum.FIND;
  @YamlComment(Comment = "Request配置")
  public C2Request request = new C2Request(); @YamlComment(Comment = "Response配置")
  public C2Response response = new C2Response();
  @YamlComment(Comment = "Payload配置")
  public HashMap payloadConfigs = new HashMap<>();
  @YamlComment(Comment = "Response配置")
  public HashMap pluginConfigs = new HashMap<>();
  
  public static final String CHANNEL_NAME = "@@@CHANNEL";
  
  public static void main(String[] args) throws Throwable {
    DumperOptions dumperOptions = new DumperOptions();
    dumperOptions.setProcessComments(true);
    dumperOptions.setPrettyFlow(true);



    
    C2Profile profile = new C2Profile();
    
    profile.channelLocation = ChannelLocationEnum.SUB;
    
    profile.basicConfig.mergeResponseCookie = true;
    
    profile.basicConfig.clearup = true;
    
    profile.basicConfig.commandMode = CommandMode.EASY;
    
    profile.basicConfig.useDefaultProxy = true;
    
    profile.coreConfig.errRetryNum = 100;
    profile.coreConfig.enabledErrRetry = true;
    profile.coreConfig.enabledDetailLog = true;
    
    profile.request.requestQueryString = "@@@CHANNEL";
    profile.request.enabledRequestBody = false;
    profile.response.responseCode = 403;
    profile.response.responseLeftBody = "<html>".getBytes();
    profile.response.responseRightBody = "</html>".getBytes();
    profile.response.responseMiddleBody = "@@@CHANNEL";

    
    Yaml yaml = new Yaml(dumperOptions);
    yaml.setBeanAccess(BeanAccess.FIELD);
    
    Files.write(Paths.get("c2.yaml", new String[0]), yaml.dumpAsMap(profile).getBytes(), new java.nio.file.OpenOption[0]);
    
    System.out.println(yaml.dumpAsMap(profile));
  }
}
