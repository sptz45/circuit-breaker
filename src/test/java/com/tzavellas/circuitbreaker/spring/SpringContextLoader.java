package com.tzavellas.circuitbreaker.spring;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContextLoader {
	static AbstractApplicationContext context = new ClassPathXmlApplicationContext("/circuit-breaker.xml");
}
