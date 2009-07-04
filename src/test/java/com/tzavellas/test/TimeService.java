package com.tzavellas.test;

import java.util.Date;

@IntegrationPoint
public class TimeService {

	public static final Date EXPECTED = new Date(); 
	
	public Date networkTime() {
		return EXPECTED;
	}
	
	public Date faultyNetworkTime() {
		throw new IllegalStateException();
	}
}
