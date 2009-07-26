package com.tzavellas.circuitbreaker.spring;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringLoader {
	public static AbstractApplicationContext CONTEXT =
		new ClassPathXmlApplicationContext("/spring-impl-context.xml");
}
