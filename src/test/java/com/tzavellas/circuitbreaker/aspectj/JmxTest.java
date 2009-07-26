package com.tzavellas.circuitbreaker.aspectj;

import org.junit.After;
import org.junit.Test;

import com.tzavellas.circuitbreaker.AbstractJmxTest;
import com.tzavellas.circuitbreaker.OpenCircuitException;
import com.tzavellas.test.aj.TimeService;

public class JmxTest extends AbstractJmxTest {
	
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
	
	@Test(expected=OpenCircuitException.class)
	public void enable_jmx_via_configurator_bean() throws Exception {
		time = new TimeService();
		CircuitConfiguratorBean conf = new CircuitConfiguratorBean();
		conf.setAspectClass(IntegrationPointBreaker.class);
		conf.setTarget(time);
		conf.setEnableJmx(true);
		conf.configure();
		readStatsAndOpenCircuitViaJmx();
	}
	
	@Test(expected=OpenCircuitException.class)
	public void setEnableJmx_registers_once() throws Exception {
		time = new TimeService();
		IntegrationPointBreaker.aspectOf(time).setEnableJmx(true);
		IntegrationPointBreaker.aspectOf(time).setEnableJmx(true);
		readStatsAndOpenCircuitViaJmx();
	}
	
	@Test
	public void setEnableJmx_unregisters_once() throws Exception {
		time = new TimeService();
		IntegrationPointBreaker.aspectOf(time).setEnableJmx(true);
		IntegrationPointBreaker.aspectOf(time).setEnableJmx(false);
		IntegrationPointBreaker.aspectOf(time).setEnableJmx(false);
	}
	
	@Test
	public void prevent_jmx_name_collision_for_two_objects_of_the_same_type() throws Exception {
		CircuitBreakerConfigurator.aspectOf().setEnableJmx(true);
		time = new TimeService();
		TimeService time2 = new TimeService();
		IntegrationPointBreaker.aspectOf(time2).setEnableJmx(false);
	}
}
