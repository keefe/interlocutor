package us.categorize.advice.aws.comprehend;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;

import us.categorize.SentimentAdvisor;
import us.categorize.advice.Advice;
import us.categorize.advice.Advisor;
import us.categorize.advice.SentimentAdvice;
import us.categorize.model.Conversation;
import us.categorize.model.Message;

public class ComprehendAdvisor implements Advisor {

	private final AmazonComprehend comprehendClient;
	
	public ComprehendAdvisor()
	{
        // Create credentials using a provider chain. For more information, see
        // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html
        AWSCredentialsProvider awsCreds = DefaultAWSCredentialsProviderChain.getInstance();
        comprehendClient =
            AmazonComprehendClientBuilder.standard()
                                         .withCredentials(awsCreds)
                                         .build();

	}
	
	@Override
	public Advice advise(Conversation conversation) {
    	StringBuilder text = new StringBuilder();
    	int length = 0;
    	for(Message message : conversation.content()) {
    		text.append(message.getText()+"\n");
    		length += message.getText().length();
    	}
    	System.out.println("Total chars " + length + " total units " + (length / 300.0) + " cost " + 0.0001*Math.max(length / 300.0,3.0));
        DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(text.toString())
                .withLanguageCode("en");
		DetectSentimentResult detectSentimentResult = comprehendClient.detectSentiment(detectSentimentRequest);
		System.out.println(detectSentimentResult);
		
		return new ComprehendSentimentAdvice(detectSentimentResult);
	}

}
