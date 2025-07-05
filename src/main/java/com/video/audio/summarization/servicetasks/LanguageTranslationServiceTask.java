package com.video.audio.summarization.servicetasks;

/**
 * @author lalamanil 
 *
 */
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.video.audio.summarization.utility.ApplicationLogger;
import com.video.audio.summarization.utility.LanguageTranslationAPI;
import com.video.audio.summarization.utility.ServiceTaskMetaDataUtility;
import com.video.audio.summarization.utility.StoringLargeValuesForDelegateExecutionUtility;

public class LanguageTranslationServiceTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub

		boolean status = false;
		execution.setVariable("translatedText", null);
		Object summary = execution.getVariable("summary");
		Object translationLanguageCode = execution.getVariable("translationLanguageCode");

		if (null != summary && null != translationLanguageCode) {
			String summaryString = (String) summary;
			String translationLanguageCodeString = (String) translationLanguageCode;
			String translatedText = LanguageTranslationAPI.translateToRequestedLanguage(translationLanguageCodeString,
					summaryString);
			if (null != translatedText && !translatedText.isEmpty()) {
				// ApplicationLogger.print("translated summary:" + translatedText);
				// remove Asterisk from translated text
				translatedText = translatedText.replaceAll("\\*", "");
				status = true;
			} else {
				ApplicationLogger.print("Translated Text is null or empty..");
			}
			execution.setVariable("translatedText",
					StoringLargeValuesForDelegateExecutionUtility.LargeValueInsertion(translatedText, execution));

		} else {
			ApplicationLogger.print("Either summary or translationLanguageCode is null. Can not be null");
		}

		ServiceTaskMetaDataUtility.addMetaDataOfServiceTask(execution.getCurrentActivityName(), status, execution);
		execution.setVariable("status", status);

	}

}
