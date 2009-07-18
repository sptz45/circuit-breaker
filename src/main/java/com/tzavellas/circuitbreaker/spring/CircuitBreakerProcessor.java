package com.tzavellas.circuitbreaker.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.tzavellas.circuitbreaker.util.Duration;

/**
 * A {@link BeanFactoryPostProcessor} that registers a property editor
 * for {@link Duration} objects.
 * 
 * <p>To be used when configuring a {@link CircuitBreaker} in a
 * {@code org.springframework.beans.factory.BeanFactory}. 
 * 
 * @author spiros
 */
public class CircuitBreakerProcessor implements BeanFactoryPostProcessor {
	
	private static final PropertyEditorRegistrar REGISTRAR = new PropertyEditorRegistrar() {
		public void registerCustomEditors(PropertyEditorRegistry registry) {
			registry.registerCustomEditor(Duration.class, new Duration.Editor());
		}
	};
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory bf) throws BeansException {
		bf.addPropertyEditorRegistrar(REGISTRAR);
	}
}