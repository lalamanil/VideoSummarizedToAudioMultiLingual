package com.video.audio.summarization.servicetasks;
/**
 * @author lalamanil 
 *
 */
import java.io.File;
import java.io.FileOutputStream;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.video.audio.summarization.constants.ApplicationConstants;
import com.video.audio.summarization.utility.ApplicationLogger;
import com.video.audio.summarization.utility.ServiceTaskMetaDataUtility;
import com.video.audio.summarization.utility.StorageUtility;
import com.video.audio.summarization.utility.TextToSpeechUtility;

public class ConvertSummaryToAudioServiceTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		boolean status = false;
		// translatedText,ttsLanguageCode
		Object objectTranslatedText = execution.getVariable("translatedText");
		Object objectTTSLanguageCode = execution.getVariable("ttsLanguageCode");
		String videoFileName = (String) execution.getVariable("videofilename");
		String audioFileName = videoFileName.replace("mp4", "mp3");
		if (null != objectTranslatedText) {
			if (null != objectTTSLanguageCode) {
				String translatedText = (String) objectTranslatedText;
				String ttsLanguageCode = (String) objectTTSLanguageCode;
				byte[] audiobytes = TextToSpeechUtility.convertTextToSpeech(translatedText, ttsLanguageCode);
				if (null != audiobytes) {
					boolean upoadedStatus = StorageUtility.uploadObjectToBucket(null,
							ApplicationConstants.GCSAudioOutputFolder + audioFileName, ApplicationConstants.BUCKET_NAME,
							audiobytes, Boolean.FALSE, ApplicationConstants.audioContentType, execution);
					FileOutputStream fileOutputStream = new FileOutputStream(
							new File(ApplicationConstants.SummaryAudioOutputFloder + audioFileName));
					fileOutputStream.write(audiobytes);
					ApplicationLogger.print("Audio content written to file:"
							+ ApplicationConstants.SummaryAudioOutputFloder + audioFileName);

					if (upoadedStatus) {
						status = true;
					} else {
						ApplicationLogger
								.print("Not uploaded into bucket (SummaryToAudio). Please check application logs");
					}
				} else {
					ApplicationLogger
							.print("Audiobytes from texttospeech are null or empty. Please check application logs");
				}

			} else {
				ApplicationLogger.print("objectTTSLanguageCode is null. Please check application logs");
			}

		} else {
			ApplicationLogger.print("objectTranslationText is null. Please check application logs");
		}
		ServiceTaskMetaDataUtility.addMetaDataOfServiceTask(execution.getCurrentActivityName(), status, execution);
		execution.setVariable("status", status);
	}

}
