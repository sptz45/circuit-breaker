package com.tzavellas.test.aj;

import java.util.Date;

import com.tzavellas.test.ITimeService;
import com.tzavellas.test.IntegrationPoint;

@IntegrationPoint
public class TimeService implements ITimeService {

	public Date networkTime() {
		return EXPECTED;
	}
	
	public Date faultyNetworkTime() {
		throw new IllegalStateException();
	}
}

