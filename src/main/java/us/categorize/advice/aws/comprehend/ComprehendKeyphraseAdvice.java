package us.categorize.advice.aws.comprehend;

import java.util.ArrayList;
import java.util.List;
import com.amazonaws.services.comprehend.model.DetectKeyPhrasesResult;
import com.amazonaws.services.comprehend.model.KeyPhrase;
import us.categorize.advice.Keyphrase;
import us.categorize.advice.KeyphraseAdvice;

public class ComprehendKeyphraseAdvice implements KeyphraseAdvice {

  private List<Keyphrase> phrases;

  public ComprehendKeyphraseAdvice(DetectKeyPhrasesResult keyphraseResult) {
    phrases = new ArrayList<>();
    for (KeyPhrase phrase : keyphraseResult.getKeyPhrases())
      phrases.add(new Keyphrase(phrase.getScore(), phrase.getText()));
  }

  @Override
  public List<Keyphrase> getKeyphrases() {
    return phrases;
  }

}
