package com.tzavellas.test;

public class StockService {
	
	 public int getQuote(String ticker) {
		 return 5;
	 }
	 
	 public int faultyGetQuote(String ticker) {
		 throw new RuntimeException();
	 }

}
