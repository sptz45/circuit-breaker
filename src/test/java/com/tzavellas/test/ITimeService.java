package com.tzavellas.test;

import java.util.Date;

public interface ITimeService {
	
	Date EXPECTED = new Date(); 

	Date networkTime();

	Date faultyNetworkTime();
}
