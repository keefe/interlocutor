package us.categorize.advice;

//still not sure about the abstraction here
public interface SentimentAdvice {

	public static final SentimentAdvice noopAdvice = new SentimentAdvice() {
		
		@Override
		public String getSentiment() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public double getPositive() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public double getNeutral() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public double getNegative() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public double getMixed() {
			throw new UnsupportedOperationException();
		}
	};

	
	String getSentiment();
	double getPositive();
	double getNegative();
	double getNeutral();
	double getMixed();
}
