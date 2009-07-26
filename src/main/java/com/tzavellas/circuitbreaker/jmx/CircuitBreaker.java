package com.tzavellas.circuitbreaker.jmx;

import java.util.Date;

import com.tzavellas.circuitbreaker.CircuitInfo;
import com.tzavellas.circuitbreaker.support.CircuitBreakerAspectSupport;
import com.tzavellas.circuitbreaker.util.Duration;

public class CircuitBreaker implements CircuitBreakerMBean{
	
	private final CircuitBreakerAspectSupport breaker;
	private final CircuitInfo info;
	
	public CircuitBreaker(CircuitBreakerAspectSupport breaker) {
		this.breaker = breaker;
		info = breaker.getCircuitInfo();
	}

	/** {@inheritDoc} */
	public void setMaxMethodDuration(Duration d) {
		breaker.setMaxMethodDuration(d);
	}

	/** {@inheritDoc} */
	public int getCalls() {
		return info.getCalls();
	}

	/** {@inheritDoc} */
	public int getCurrentFailures() {
		return info.getCurrentFailures();
	}
	
	/** {@inheritDoc} */
	public String getCurrentFailuresDuration() {
		return info.getCurrentFailuresDuration().toString();
	}
	
	/** {@inheritDoc} */
	public String getMaxMethodDuration() {
		return breaker.getMaxMethodDuration().toString();
	}

	/** {@inheritDoc} */
	public int getFailures() {
		return info.getFailures();
	}

	/** {@inheritDoc} */
	public int getMaxFailures() {
		return info.getMaxFailures();
	}

	/** {@inheritDoc} */
	public Date getOpenTimestamp() {
		return info.getOpenTimestamp();
	}

	/** {@inheritDoc} */
	public String getTimeout() {
		return info.getTimeout().toString();
	}

	/** {@inheritDoc} */
	public int getTimesOpened() {
		return info.getTimesOpened();
	}

	/** {@inheritDoc} */
	public boolean isOpen() {
		return info.isOpen();
	}

	/** {@inheritDoc} */
	public void open() {
		info.open();
	}

	/** {@inheritDoc} */
	public void setCurrentFailuresDuration(String duration) {
		info.setCurrentFailuresDuration(Duration.valueOf(duration));
	}
	
	/** {@inheritDoc} */
	public void setMaxMethodDuration(String duration) {
		Duration d = null;
		if (duration != null)
			d = Duration.valueOf(duration);
		breaker.setMaxMethodDuration(d);
	}

	/** {@inheritDoc} */
	public void setMaxFailures(int maxFailures) {
		info.setMaxFailures(maxFailures);
	}

	/** {@inheritDoc} */
	public void setTimeout(String timeout) {
		info.setTimeout(Duration.valueOf(timeout));
	}

	/** {@inheritDoc} */
	public void close() {
		info.close();
	}

	/** {@inheritDoc} */
	public boolean isClosed() {
		return info.isClosed();
	}
	
	/** {@inheritDoc} */
	public boolean isHalfOpen() {
		return info.isHalfOpen();
	}
}
