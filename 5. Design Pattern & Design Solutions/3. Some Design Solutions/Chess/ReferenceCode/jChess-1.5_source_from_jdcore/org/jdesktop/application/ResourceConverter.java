package org.jdesktop.application;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;















































public abstract class ResourceConverter
{
  protected final Class type;
  
  public abstract Object parseString(String paramString, ResourceMap paramResourceMap)
    throws ResourceConverter.ResourceConverterException;
  
  public String toString(Object paramObject)
  {
    return paramObject == null ? "null" : paramObject.toString();
  }
  
  protected ResourceConverter(Class paramClass) {
    if (paramClass == null) {
      throw new IllegalArgumentException("null type");
    }
    type = paramClass; }
  
  private ResourceConverter() { type = null; }
  

  public boolean supportsType(Class paramClass) { return type.equals(paramClass); }
  
  public static class ResourceConverterException extends Exception {
    private final String badString;
    
    private String maybeShorten(String paramString) {
      int i = paramString.length();
      return paramString.substring(0, 128) + "...[" + (i - 128) + " more characters]";
    }
    
    public ResourceConverterException(String paramString1, String paramString2, Throwable paramThrowable) {
      super(paramThrowable);
      badString = maybeShorten(paramString2);
    }
    
    public ResourceConverterException(String paramString1, String paramString2) {
      super();
      badString = maybeShorten(paramString2);
    }
    
    public String toString() {
      StringBuffer localStringBuffer = new StringBuffer(super.toString());
      localStringBuffer.append(" string: \"");
      localStringBuffer.append(badString);
      localStringBuffer.append("\"");
      return localStringBuffer.toString();
    }
  }
  
  public static void register(ResourceConverter paramResourceConverter) {
    if (paramResourceConverter == null) {
      throw new IllegalArgumentException("null resourceConverter");
    }
    resourceConverters.add(paramResourceConverter);
  }
  
  public static ResourceConverter forType(Class paramClass) {
    if (paramClass == null) {
      throw new IllegalArgumentException("null type");
    }
    for (ResourceConverter localResourceConverter : resourceConverters) {
      if (localResourceConverter.supportsType(paramClass)) {
        return localResourceConverter;
      }
    }
    return null;
  }
  
  private static ResourceConverter[] resourceConvertersArray = { new BooleanResourceConverter(new String[] { "true", "on", "yes" }), new IntegerResourceConverter(), new MessageFormatResourceConverter(), new FloatResourceConverter(), new DoubleResourceConverter(), new LongResourceConverter(), new ShortResourceConverter(), new ByteResourceConverter(), new URLResourceConverter(), new URIResourceConverter() };
  










  private static List<ResourceConverter> resourceConverters = new ArrayList(Arrays.asList(resourceConvertersArray));
  
  private static class BooleanResourceConverter extends ResourceConverter
  {
    private final String[] trueStrings;
    
    BooleanResourceConverter(String... paramVarArgs) {
      super();
      trueStrings = paramVarArgs;
    }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) {
      paramString = paramString.trim();
      for (String str : trueStrings) {
        if (paramString.equalsIgnoreCase(str)) {
          return Boolean.TRUE;
        }
      }
      return Boolean.FALSE;
    }
    

    public boolean supportsType(Class paramClass) { return (paramClass.equals(Boolean.class)) || (paramClass.equals(Boolean.TYPE)); }
  }
  
  private static abstract class NumberResourceConverter extends ResourceConverter {
    private final Class primitiveType;
    
    NumberResourceConverter(Class paramClass1, Class paramClass2) {
      super();
      primitiveType = paramClass2;
    }
    
    protected abstract Number parseString(String paramString) throws NumberFormatException;
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      try {
        return parseString(paramString);
      }
      catch (NumberFormatException localNumberFormatException) {
        throw new ResourceConverter.ResourceConverterException("invalid " + type.getSimpleName(), paramString, localNumberFormatException);
      }
    }
    
    public boolean supportsType(Class paramClass) {
      return (paramClass.equals(type)) || (paramClass.equals(primitiveType));
    }
  }
  
  private static class FloatResourceConverter extends ResourceConverter.NumberResourceConverter {
    FloatResourceConverter() {
      super(Float.TYPE);
    }
    
    protected Number parseString(String paramString) throws NumberFormatException {
      return Float.valueOf(Float.parseFloat(paramString));
    }
  }
  
  private static class DoubleResourceConverter extends ResourceConverter.NumberResourceConverter {
    DoubleResourceConverter() {
      super(Double.TYPE);
    }
    

    protected Number parseString(String paramString) throws NumberFormatException { return Double.valueOf(Double.parseDouble(paramString)); }
  }
  
  private static abstract class INumberResourceConverter extends ResourceConverter {
    private final Class primitiveType;
    
    INumberResourceConverter(Class paramClass1, Class paramClass2) {
      super();
      primitiveType = paramClass2;
    }
    
    protected abstract Number parseString(String paramString, int paramInt) throws NumberFormatException;
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      try {
        String[] arrayOfString = paramString.split("&");
        int i = arrayOfString.length == 2 ? Integer.parseInt(arrayOfString[1]) : -1;
        return parseString(arrayOfString[0], i);
      }
      catch (NumberFormatException localNumberFormatException) {
        throw new ResourceConverter.ResourceConverterException("invalid " + type.getSimpleName(), paramString, localNumberFormatException);
      }
    }
    
    public boolean supportsType(Class paramClass) {
      return (paramClass.equals(type)) || (paramClass.equals(primitiveType));
    }
  }
  
  private static class ByteResourceConverter extends ResourceConverter.INumberResourceConverter {
    ByteResourceConverter() {
      super(Byte.TYPE);
    }
    
    protected Number parseString(String paramString, int paramInt) throws NumberFormatException {
      return Byte.valueOf(paramInt == -1 ? Byte.decode(paramString).byteValue() : Byte.parseByte(paramString, paramInt));
    }
  }
  
  private static class IntegerResourceConverter extends ResourceConverter.INumberResourceConverter {
    IntegerResourceConverter() {
      super(Integer.TYPE);
    }
    
    protected Number parseString(String paramString, int paramInt) throws NumberFormatException {
      return Integer.valueOf(paramInt == -1 ? Integer.decode(paramString).intValue() : Integer.parseInt(paramString, paramInt));
    }
  }
  
  private static class LongResourceConverter extends ResourceConverter.INumberResourceConverter {
    LongResourceConverter() {
      super(Long.TYPE);
    }
    
    protected Number parseString(String paramString, int paramInt) throws NumberFormatException {
      return Long.valueOf(paramInt == -1 ? Long.decode(paramString).longValue() : Long.parseLong(paramString, paramInt));
    }
  }
  
  private static class ShortResourceConverter extends ResourceConverter.INumberResourceConverter {
    ShortResourceConverter() {
      super(Short.TYPE);
    }
    
    protected Number parseString(String paramString, int paramInt) throws NumberFormatException {
      return Short.valueOf(paramInt == -1 ? Short.decode(paramString).shortValue() : Short.parseShort(paramString, paramInt));
    }
  }
  
  private static class MessageFormatResourceConverter extends ResourceConverter {
    MessageFormatResourceConverter() {
      super();
    }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) {
      return new MessageFormat(paramString);
    }
  }
  
  private static class URLResourceConverter extends ResourceConverter {
    URLResourceConverter() {
      super();
    }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      try {
        return new URL(paramString);
      }
      catch (MalformedURLException localMalformedURLException) {
        throw new ResourceConverter.ResourceConverterException("invalid URL", paramString, localMalformedURLException);
      }
    }
  }
  
  private static class URIResourceConverter extends ResourceConverter {
    URIResourceConverter() {
      super();
    }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      try {
        return new URI(paramString);
      }
      catch (URISyntaxException localURISyntaxException) {
        throw new ResourceConverter.ResourceConverterException("invalid URI", paramString, localURISyntaxException);
      }
    }
  }
}
