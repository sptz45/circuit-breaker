package com.tzavellas.circuitbreaker.spring;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

class SpringLoader {
	static AbstractApplicationContext CONTEXT =
		new ClassPathXmlApplicationContext("/spring-impl-context.xml");
}
