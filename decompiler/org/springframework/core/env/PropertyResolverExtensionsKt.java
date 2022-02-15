package org.springframework.core.env;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;




















@Metadata(mv = {1, 1, 18}, bv = {1, 0, 3}, k = 2, d1 = {"\000\024\n\000\n\002\020\016\n\002\030\002\n\002\b\005\n\002\020\000\n\000\032\027\020\000\032\004\030\0010\001*\0020\0022\006\020\003\032\0020\001H\002\032$\020\004\032\004\030\001H\005\"\006\b\000\020\005\030\001*\0020\0022\006\020\003\032\0020\001H\b¢\006\002\020\006\032&\020\007\032\002H\005\"\n\b\000\020\005\030\001*\0020\b*\0020\0022\006\020\003\032\0020\001H\b¢\006\002\020\006¨\006\t"}, d2 = {"get", "", "Lorg/springframework/core/env/PropertyResolver;", "key", "getProperty", "T", "(Lorg/springframework/core/env/PropertyResolver;Ljava/lang/String;)Ljava/lang/Object;", "getRequiredProperty", "", "spring-core"})
public final class PropertyResolverExtensionsKt
{
  @Nullable
  public static final String get(@NotNull PropertyResolver $this$get, @NotNull String key) {
    Intrinsics.checkParameterIsNotNull($this$get, "$this$get"); Intrinsics.checkParameterIsNotNull(key, "key"); return $this$get.getProperty(key);
  }
}
