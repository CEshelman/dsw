package com.ibm.dsw.quote.base.xstream;

import java.lang.reflect.Field;  
import java.lang.reflect.Modifier;  
  
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;  
  
/** 
 * <pre> 
 * </pre> 
 * @author tl 
 * @version 1.0
 */  
public class EnhancedModeReflectProvider extends Sun14ReflectionProvider  
{  
    @Override  
    protected boolean fieldModifiersSupported(Field field)  
    {  
        return !(Modifier.isStatic(field.getModifiers()));  
    }
    
    @Override 
    public boolean fieldDefinedInClass(String fieldName, Class type) {
        try {
            Field field = fieldDictionary.field(type, fieldName, null);
            return fieldModifiersSupported(field);
        } catch (ObjectAccessException e) {
            return false;
        }
    }
      
}
