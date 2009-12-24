package com.tzavellas.circuitbreaker.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.propertyeditors.ClassEditor;

import com.tzavellas.circuitbreaker.util.Duration;

/**
 * A {@link BeanFactoryPostProcessor} that registers a property editors
 * for {@link Duration} and {@link Class} objects.
 * 
 * @author spiros
 */
public class SpringXmlConfigurationSupport implements BeanFactoryPostProcessor {
	
	private static final PropertyEditorRegistrar REGISTRAR = new PropertyEditorRegistrar() {
		public void registerCustomEditors(PropertyEditorRegistry registry) {
			registry.registerCustomEditor(Duration.class, new Duration.Editor());
			registry.registerCustomEditor(Class.class, new ClassEditor());
		}
	};
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
		bf.addPropertyEditorRegistrar(REGISTRAR);
	}
}
