package com.tzavellas.circuitbreaker.aspectj;

import javax.management.JMException;

import org.junit.After;
import org.junit.Test;

import com.tzavellas.circuitbreaker.AbstractJmxTest;
import com.tzavellas.circuitbreaker.OpenCircuitException;
import com.tzavellas.circuitbreaker.support.JmxUtils;
import com.tzavellas.test.aj.TimeService;

public class JmxTest extends AbstractJmxTest {
	
	public JmxTest() throws JMException {
		mbean = JmxUtils.getCircuitInfo(TimeService.class);
	}
	
	@After
	public void disableJmx() {
		CircuitBreakerConfigurator.aspectOf().setEnableJmx(false);
		IntegrationPointBreaker.aspectOf(time).setEnableJmx(false);
	}
	
	@Test(expected=OpenCircuitException.class)
	public void enable_jmx_via_configurator() throws Exception {
		CircuitBreakerConfigurator.aspectOf().setEnableJmx(true);
		time = new TimeService();
		readStatsAndOpenCircuitViaJmx();
	}
	
	@Test(expected=OpenCircuitException.class)
	public void enable_jmx_via_aspect_setter() throws Exception {
		time = new TimeService();
		IntegrationPointBreaker.aspectOf(time).setEnableJmx(true);
		readStatsAndOpenCircuitViaJmx();
	}
}
