package com.tzavellas.test.aj;

import com.tzavellas.test.IStockService;

public class StockService implements IStockService {
	
	public int getQuote(String ticker) {
		 return 5;
	 }
	 
	public int faultyGetQuote(String ticker) {
		 throw new ArithmeticException();
	 }
}
