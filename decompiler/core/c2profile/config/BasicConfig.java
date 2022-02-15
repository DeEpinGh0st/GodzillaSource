package core.c2profile.config;

import core.c2profile.CommandMode;
import org.yaml.snakeyaml.annotation.YamlComment;







































public class BasicConfig
{
  @YamlComment(Comment = "均衡Uri 如 /upload /login /download")
  public String[] uris = new String[0]; @YamlComment(Comment = "均衡Proxy 如 http://127.0.0.1:8080  socks5://127.0.0.1:1088")
  public String[] proxys = new String[0]; @YamlComment(Comment = "均衡Proxy 如 http://127.0.0.1:8080  socks5://127.0.0.1:1088")
  public CommandMode commandMode = CommandMode.EASY;
  @YamlComment(Comment = "是否使用默认代理")
  public boolean useDefaultProxy = true;
  @YamlComment(Comment = "是否开启均衡Uri 会随机使用其中任意一个uri")
  public boolean enabledBalanceUris = false;
  @YamlComment(Comment = "是否开启均衡Proxy 会随机使用其中任意一个proxy")
  public boolean enabledBalanceProxys = false;
  @YamlComment(Comment = "是否开启https证书强认证")
  public boolean enabledHttpsTrusted = true;
  @YamlComment(Comment = "是否合并返回包的 \"set-cookie\"")
  public boolean mergeResponseCookie = true;
  @YamlComment(Comment = "是否合并shell配置页面的请求头")
  public boolean mergeBasicHeader = true;
  @YamlComment(Comment = "关闭shell后是否清除shell在服务器的缓存")
  public boolean clearup = false;
}
