package com.tzavellas.test.spring;

import com.tzavellas.test.IStockService;

public class StockService implements IStockService {
	
	public int getQuote(String ticker) {
		 return 5;
	 }
	 
	public int faultyGetQuote(String ticker) {
		 throw new RuntimeException();
	 }
}
