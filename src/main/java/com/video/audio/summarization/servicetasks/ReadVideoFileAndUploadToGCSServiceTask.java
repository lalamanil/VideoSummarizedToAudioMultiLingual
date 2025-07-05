package com.video.audio.summarization.servicetasks;

/**
 * @author lalamanil 
 *
 */
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.video.audio.summarization.constants.ApplicationConstants;
import com.video.audio.summarization.model.ServiceAccountModel;
import com.video.audio.summarization.utility.ApplicationLogger;
import com.video.audio.summarization.utility.ReadFileUtility;
import com.video.audio.summarization.utility.ReadLanguageCodes;
import com.video.audio.summarization.utility.ServiceTaskMetaDataUtility;
import com.video.audio.summarization.utility.ServiceUsageUtility;
import com.video.audio.summarization.utility.StorageUtility;

public class ReadVideoFileAndUploadToGCSServiceTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		execution.setVariable("videofilename", null);
		boolean status = false;
		ServiceAccountModel serviceAccountModel = ServiceUsageUtility.getServiceAccountModel();
		if (ServiceUsageUtility.checkAllRequiredAPIAreEnabled(serviceAccountModel)) {

			// reading video from local system
			String videoPath = ReadFileUtility.readVideoFileFromLocal();
			if (null != videoPath) {
				String fileName = ReadFileUtility.getFileNameFromPath(videoPath);
				ApplicationLogger.print("FileName:" + fileName);
				// Inserting video into GCS bucket
				status = StorageUtility.uploadObjectToBucket(videoPath, fileName, ApplicationConstants.BUCKET_NAME,
						null, Boolean.TRUE, ApplicationConstants.videoContentType, execution);
				ApplicationLogger.print("upload status of " + fileName + " in GCS is:" + status);

				if (status) {
					// adding video filename
					execution.setVariable("videofilename", fileName);

					// read the language code for translation
					String translationLanguageCode = ReadLanguageCodes.requestlanguageCode();
					// adding translationLanguageCode to DelegateExecution
					String language = translationLanguageCode.split("@@")[0];
					execution.setVariable("language", language);

					// setting up language code for translation
					execution.setVariable("translationLanguageCode", translationLanguageCode.split("@@")[1]);

					Map<String, String> ttsLaguageCodeMap = ReadLanguageCodes.getTTSLanguageCodes();

					if (ttsLaguageCodeMap.containsKey(language)) {
						// setting up language code for TextToSpeech API
						execution.setVariable("ttsLanguageCode", ttsLaguageCodeMap.get(language));

					}
				}

			}
		}
		execution.setVariable("status", status);
		// adding script execution status meta data
		ServiceTaskMetaDataUtility.addMetaDataOfServiceTask(execution.getCurrentActivityName(), status, execution);

	}

}
