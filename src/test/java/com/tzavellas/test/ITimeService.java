package com.tzavellas.test;

import java.util.Date;

public interface ITimeService {
	
	public static final Date EXPECTED = new Date(); 

	Date networkTime();

	Date faultyNetworkTime();

}