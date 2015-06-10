package com.ibm.dsw.quote.common.json;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ibm.dsw.quote.common.json.annotation.JsonGenericType;
import com.ibm.dsw.quote.common.json.annotation.JsonEmbedType;
import com.ibm.dsw.quote.common.json.annotation.JsonProperty;
import com.ibm.ead4j.opal.log.LogContext;
import com.ibm.ead4j.opal.log.LogContextFactory;

public final class JsonUtil {
	
	/** LogContext */
	private static final LogContext logContext = LogContextFactory.singleton().getLogContext();

    /**
     * Private Constructor for utility class.
     */
    private JsonUtil() {
        // do nothing
    }
    //Todo
    private static <T> void getObjectValue(T obj, JSONObject jsonObj,
			Map<String, Method> map, String name, String lowcaseMethodName,Field field)
			throws JSONException, IllegalAccessException,
			InvocationTargetException {
		Method method = map.get(lowcaseMethodName);
		if (method != null) {
			Class<?> clazz = field.getType();
			if (clazz.isPrimitive()) {
				if (clazz == String.class) {
					Object value = method.invoke(obj);
					method.invoke(obj);
				} 		
			} 
			
		}
	}

    /**
     * Convert a bean to json string.
     *
     * @param jsonBean
     *            the json bean
     * @return the string
     */
    public static String toJsonString(final Object jsonBean) {
        String result = null;
        try {
        	JSONObject jsonObj = new JSONObject();
        	Map<String, Method> map = extractMethods(jsonBean.getClass());
            Field[] fields = jsonBean.getClass().getFields();
        	for (Field field : fields) {
				String name = field.getName();
				String lowcaseMethodName = "get" + name.toLowerCase();
				if (field.getType().isPrimitive()) {
					if (field.isAnnotationPresent(JsonProperty.class)) {
						name = ((JsonProperty) field.getAnnotation(JsonProperty.class)).name();
					}	
					jsonObj.put(name, "");
				}
			}
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        
        
        
        return result;
    }
    
	private static <T, E> T translate(final JSONObject jsonObj, Class<T> valueType) {
		T obj = null;
		try {
			Map<String, Method> map = extractMethods(valueType);
			obj = valueType.newInstance();
			Field[] fields = valueType.getDeclaredFields();
			for (Field field : fields) {
				String name = field.getName();
				String lowcaseMethodName = "set" + name.toLowerCase();
				if (field.isAnnotationPresent(JsonProperty.class)) {
					name = ((JsonProperty) field.getAnnotation(JsonProperty.class)).name();
				}
				if (field.isAnnotationPresent(JsonGenericType.class)) {
					String arrayType = ((JsonGenericType) field.getAnnotation(JsonGenericType.class)).type();
					String embedObjType = "String";
					if (field.isAnnotationPresent(JsonEmbedType.class)) {
						embedObjType = ((JsonEmbedType) field.getAnnotation(JsonEmbedType.class)).type();
					}
					if (arrayType.equals("java.util.ArrayList")) {
						List<E> list = new ArrayList<E>();
						JSONArray array = jsonObj.getJSONArray(name);
						@SuppressWarnings("unchecked")
						Class<E> embedClass = (Class<E>) Class.forName(embedObjType);
						Map<String, Method> embedMap = extractMethods(embedClass);
						if (array != null && array.length() > 0) {
							for (int i = 0; i < array.length(); i++) {
								JSONObject subJson = array.getJSONObject(i);
								list.add(parseEmbedJson(subJson, embedClass,embedMap));
							}
							invokeMethod4Genericity(obj, list, map, name, lowcaseMethodName);
						}
					}
				} else {
					invokeMethod(obj, jsonObj, map, name, lowcaseMethodName, field);
				}
			}
		} catch (Exception e) {
			logContext.error(e.getMessage(), e);
		}
		return obj;
	}
    
    /**
     * This static method is used to generete a POJO java object.
     * @param <E>
     * @param jsonString
     * @param valueType
     * @return
     */
    public static <T, E> T parseJsonString(final String jsonString, Class<T> valueType) {
        T obj = null;      
        try {
        	JSONObject jsonObj =new JSONObject(jsonString);
        	obj = translate(jsonObj, valueType);        	
        } catch (Exception e) {
        	logContext.error(e.getMessage(),e);
        }
        return obj;
    }

    
    private static <E> E parseEmbedJson(JSONObject jsonObj, Class<E> valueType, Map<String, Method> map) {
    	E obj=null;
    	try {
        	obj = valueType.newInstance();
        	Field[] fields = valueType.getDeclaredFields();
        	for (Field field : fields) {      		        		
        		String name = field.getName();
        		String lowcaseMethodName = "set" + name.toLowerCase();
        		@SuppressWarnings("unused")
				Class<?> classType = field.getType();
        		Type type = field.getGenericType();
        		/** new change used annotation to handler generic type */
        		if (field.isAnnotationPresent(JsonProperty.class)) {
        			name = ((JsonProperty)field.getAnnotation(JsonProperty.class)).name();
        		}
        		if (field.isAnnotationPresent(JsonGenericType.class)) {
        			String arrayType = ((JsonGenericType)field.getAnnotation(JsonGenericType.class)).type();
        			String embedObjType = "String";
        			if (field.isAnnotationPresent(JsonEmbedType.class)) {
        				embedObjType = ((JsonEmbedType)field.getAnnotation(JsonEmbedType.class)).type();
        			}
        			if (arrayType.equals("java.util.List")) {
						List<E> list = new ArrayList<E>();
						JSONArray array = jsonObj.getJSONArray(name);
						@SuppressWarnings("unchecked")
						Class<E> embedClass = (Class<E>) Class.forName(embedObjType);
						Map<String, Method> embedMap = extractMethods(embedClass);
	    				if (array != null && array.length() > 0) {
	    					for (int i =0;i<array.length();i++) {
	    						JSONObject subJson = array.getJSONObject(i);
	    						list.add(parseEmbedJson(subJson,embedClass,embedMap));	    						
	    					}
	    					invokeMethod4Genericity(obj, list, map, name, lowcaseMethodName);
	    				}						
					}
        		} else {
        			invokeMethod(obj, jsonObj, map, name, lowcaseMethodName,field); 
        		}
        	}
    	} catch (Exception e) {
    		logContext.error(e.getMessage(),e);
    	}
    	return obj;
    }

	private static <T> Map<String, Method> extractMethods(Class<T> valueType) {
		Method[] methods = valueType.getDeclaredMethods();
		Map<String, Method> map = new HashMap<String, Method>();
		for (Method method : methods) {
			//if (method.getName().startsWith("set")) {
				Class<?>[] classTypes = method.getParameterTypes();
				if (classTypes == null) { 
					continue;
				}
				if (classTypes.length == 1) {					
					if (classTypes[0] == List.class) {						
						map.put(method.getName().toLowerCase(), method);
					} else {
						map.put(method.getName().toLowerCase(), method);
					}
				} else {
					continue;
				}
			//}	
		}
		return map;
	}

	private static <T> void invokeMethod(T obj, JSONObject jsonObj,
			Map<String, Method> map, String name, String lowcaseMethodName,Field field)
			throws JSONException, IllegalAccessException,
			InvocationTargetException {
		Method method = map.get(lowcaseMethodName);
		if (method != null && jsonObj.get(name) != null) {
			Class<?> clazz = field.getType();
			if (clazz.isPrimitive()) {
				if (clazz == int.class) {
					method.invoke(obj, jsonObj.getInt(name));
				} else if (clazz == long.class) {
					method.invoke(obj, jsonObj.getLong(name));
				} else if (clazz == boolean.class) {
					method.invoke(obj, jsonObj.getBoolean(name));
				} else {
					method.invoke(obj, jsonObj.get(name));
				}				
			} else if (clazz == String.class) {
				method.invoke(obj, jsonObj.get(name));
			} else {
				method.invoke(obj, jsonObj.get(name));
			}
			
		}
	}
	
	private static <T, E> void invokeMethod4Genericity(T obj, List<E> list,
			Map<String, Method> map, String name, String lowcaseMethodName)
			throws JSONException, IllegalAccessException,
			InvocationTargetException {
		Method method = map.get(lowcaseMethodName);
		if (method != null &&  list!= null) {
			method.invoke(obj, list);
		}
	}

}
