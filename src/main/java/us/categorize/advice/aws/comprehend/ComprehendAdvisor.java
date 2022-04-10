package us.categorize.advice.aws.comprehend;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesRequest;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;

import us.categorize.advice.Advisor;
import us.categorize.advice.KeyphraseAdvice;
import us.categorize.advice.SentimentAdvice;
import us.categorize.model.Conversation;
import us.categorize.model.Message;

//TODO generics here are wrong, need to sort that out
public class ComprehendAdvisor<C> implements Advisor<C> {

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
	public SentimentAdvice detectSentiment(Conversation<C> conversation) {
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

	@Override
	public KeyphraseAdvice detectKeyphrases(Conversation<C> conversation) {

    	StringBuilder text = new StringBuilder();
    	int length = 0;
    	for(Message message : conversation.content()) {
    		text.append(message.getText()+"\n");
    		length += message.getText().length();
    	}
		
        DetectKeyPhrasesRequest detectKeyPhrasesRequest = new DetectKeyPhrasesRequest().withText(text.toString())
                .withLanguageCode("en");
		DetectKeyPhrasesResult detectKeyPhrasesResult = comprehendClient.detectKeyPhrases(detectKeyPhrasesRequest);
		detectKeyPhrasesResult.getKeyPhrases().forEach(System.out::println);

		
		return null;
	}

}
