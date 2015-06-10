package com.ibm.dsw.quote.base.xstream;

import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.mapper.Mapper;

public class EnhancedReflectionConverter extends ReflectionConverter{

	public EnhancedReflectionConverter(Mapper mapper,
			ReflectionProvider reflectionProvider) {
		super(mapper, reflectionProvider);
	}
	
	@Override 
    protected boolean shouldUnmarshalTransientFields() {
        return true;
    }

}
