package org.springframework.core.convert.support;

import java.nio.charset.Charset;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.lang.Nullable;


































public class DefaultConversionService
  extends GenericConversionService
{
  @Nullable
  private static volatile DefaultConversionService sharedInstance;
  
  public DefaultConversionService() {
    addDefaultConverters(this);
  }












  
  public static ConversionService getSharedInstance() {
    DefaultConversionService cs = sharedInstance;
    if (cs == null) {
      synchronized (DefaultConversionService.class) {
        cs = sharedInstance;
        if (cs == null) {
          cs = new DefaultConversionService();
          sharedInstance = cs;
        } 
      } 
    }
    return cs;
  }






  
  public static void addDefaultConverters(ConverterRegistry converterRegistry) {
    addScalarConverters(converterRegistry);
    addCollectionConverters(converterRegistry);
    
    converterRegistry.addConverter((GenericConverter)new ByteBufferConverter((ConversionService)converterRegistry));
    converterRegistry.addConverter(new StringToTimeZoneConverter());
    converterRegistry.addConverter(new ZoneIdToTimeZoneConverter());
    converterRegistry.addConverter(new ZonedDateTimeToCalendarConverter());
    
    converterRegistry.addConverter((GenericConverter)new ObjectToObjectConverter());
    converterRegistry.addConverter((GenericConverter)new IdToEntityConverter((ConversionService)converterRegistry));
    converterRegistry.addConverter((GenericConverter)new FallbackObjectToStringConverter());
    converterRegistry.addConverter((GenericConverter)new ObjectToOptionalConverter((ConversionService)converterRegistry));
  }







  
  public static void addCollectionConverters(ConverterRegistry converterRegistry) {
    ConversionService conversionService = (ConversionService)converterRegistry;
    
    converterRegistry.addConverter((GenericConverter)new ArrayToCollectionConverter(conversionService));
    converterRegistry.addConverter((GenericConverter)new CollectionToArrayConverter(conversionService));
    
    converterRegistry.addConverter((GenericConverter)new ArrayToArrayConverter(conversionService));
    converterRegistry.addConverter((GenericConverter)new CollectionToCollectionConverter(conversionService));
    converterRegistry.addConverter((GenericConverter)new MapToMapConverter(conversionService));
    
    converterRegistry.addConverter((GenericConverter)new ArrayToStringConverter(conversionService));
    converterRegistry.addConverter((GenericConverter)new StringToArrayConverter(conversionService));
    
    converterRegistry.addConverter((GenericConverter)new ArrayToObjectConverter(conversionService));
    converterRegistry.addConverter((GenericConverter)new ObjectToArrayConverter(conversionService));
    
    converterRegistry.addConverter((GenericConverter)new CollectionToStringConverter(conversionService));
    converterRegistry.addConverter((GenericConverter)new StringToCollectionConverter(conversionService));
    
    converterRegistry.addConverter((GenericConverter)new CollectionToObjectConverter(conversionService));
    converterRegistry.addConverter((GenericConverter)new ObjectToCollectionConverter(conversionService));
    
    converterRegistry.addConverter((GenericConverter)new StreamConverter(conversionService));
  }
  
  private static void addScalarConverters(ConverterRegistry converterRegistry) {
    converterRegistry.addConverterFactory(new NumberToNumberConverterFactory());
    
    converterRegistry.addConverterFactory(new StringToNumberConverterFactory());
    converterRegistry.addConverter(Number.class, String.class, new ObjectToStringConverter());
    
    converterRegistry.addConverter(new StringToCharacterConverter());
    converterRegistry.addConverter(Character.class, String.class, new ObjectToStringConverter());
    
    converterRegistry.addConverter(new NumberToCharacterConverter());
    converterRegistry.addConverterFactory(new CharacterToNumberFactory());
    
    converterRegistry.addConverter(new StringToBooleanConverter());
    converterRegistry.addConverter(Boolean.class, String.class, new ObjectToStringConverter());
    
    converterRegistry.addConverterFactory(new StringToEnumConverterFactory());
    converterRegistry.addConverter(new EnumToStringConverter((ConversionService)converterRegistry));
    
    converterRegistry.addConverterFactory(new IntegerToEnumConverterFactory());
    converterRegistry.addConverter(new EnumToIntegerConverter((ConversionService)converterRegistry));
    
    converterRegistry.addConverter(new StringToLocaleConverter());
    converterRegistry.addConverter(Locale.class, String.class, new ObjectToStringConverter());
    
    converterRegistry.addConverter(new StringToCharsetConverter());
    converterRegistry.addConverter(Charset.class, String.class, new ObjectToStringConverter());
    
    converterRegistry.addConverter(new StringToCurrencyConverter());
    converterRegistry.addConverter(Currency.class, String.class, new ObjectToStringConverter());
    
    converterRegistry.addConverter(new StringToPropertiesConverter());
    converterRegistry.addConverter(new PropertiesToStringConverter());
    
    converterRegistry.addConverter(new StringToUUIDConverter());
    converterRegistry.addConverter(UUID.class, String.class, new ObjectToStringConverter());
  }
}
