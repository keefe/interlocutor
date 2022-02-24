package us.categorize.advice;

//this doesn't seem the right abstraction at all but let's move to move
public interface SentimentAdvice extends Advice{
	String getSentiment();
	double getPositive();
	double getNegative();
	double getNeutral();
	double getMixed();
}
