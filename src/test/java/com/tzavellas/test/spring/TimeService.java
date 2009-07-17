package com.tzavellas.test.spring;

import java.util.Date;
import com.tzavellas.test.ITimeService;

public class TimeService implements ITimeService {

	public Date networkTime() {
		return EXPECTED;
	}
	
	public Date faultyNetworkTime() {
		throw new IllegalStateException();
	}
}
