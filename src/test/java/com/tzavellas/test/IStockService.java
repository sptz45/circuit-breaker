package com.tzavellas.test;

public interface IStockService {

	int getQuote(String ticker);

	int faultyGetQuote(String ticker);
}
