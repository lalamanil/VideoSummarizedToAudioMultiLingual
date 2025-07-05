package com.video.audio.summarization.servicetasks;
/**
 * @author lalamanil 
 *
 */
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.video.audio.summarization.constants.ApplicationConstants;
import com.video.audio.summarization.utility.ApplicationLogger;
import com.video.audio.summarization.utility.ServiceTaskMetaDataUtility;
import com.video.audio.summarization.utility.StoringLargeValuesForDelegateExecutionUtility;
import com.video.audio.summarization.utility.VideoIntellegenceAPI;

public class PullTranscriptsFromVideoServiceTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub
		execution.setVariable("videotranscripts", null);
		boolean status = false;
		Object fileName = execution.getVariable("videofilename");
		if (null != fileName) {
			String gcsvideopath = ApplicationConstants.GCSBUCKETPATH + String.valueOf(fileName);
			execution.setVariable("gcsvideopath", gcsvideopath);
			ApplicationLogger.print("gcsvideopath is:" + gcsvideopath);
			String transcripts = VideoIntellegenceAPI.getTranscriptsFromVideo(gcsvideopath);
			//ApplicationLogger.print("Transcripts:" + transcripts.trim());
			execution.setVariable("videotranscripts", StoringLargeValuesForDelegateExecutionUtility.LargeValueInsertion(transcripts.trim(), execution));
			status = true;

		} else {
			ApplicationLogger.print("videofilename attribute is received is null");
		}
		execution.setVariable("status", status);
		// added workflow execution meta data
		ServiceTaskMetaDataUtility.addMetaDataOfServiceTask(execution.getCurrentActivityName(), status, execution);
	}

}
