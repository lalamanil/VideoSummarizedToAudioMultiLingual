package com.video.audio.summarization.servicetasks;

/**
 * @author lalamanil 
 *
 */
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.video.audio.summarization.utility.ApplicationLogger;
import com.video.audio.summarization.utility.ServiceTaskMetaDataUtility;
import com.video.audio.summarization.utility.StoringLargeValuesForDelegateExecutionUtility;
import com.video.audio.summarization.utility.VertexAIGeminiAPI;

public class SummarizeTranscriptsViaVertexAIServiceTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {

		execution.setVariable("summary", null);
		boolean status = false;
		Object videotranscriptobject = execution.getVariable("videotranscripts");
		if (null != videotranscriptobject) {
			String videotranscript = (String) videotranscriptobject;
			String summary = VertexAIGeminiAPI.summarizeTranscript(videotranscript);

			if (null != summary && !summary.isEmpty()) {
				// ApplicationLogger.print("Summary:" + summary);
				summary = summary.replaceAll("\\*", "");
				status = true;
			} else {
				ApplicationLogger.print("Summmary is null or empty");
			}
			execution.setVariable("summary",
					StoringLargeValuesForDelegateExecutionUtility.LargeValueInsertion(summary, execution));
		} else {
			ApplicationLogger.print("videotranscriptobject is null. Not received transcript");
		}
		// added servicetask execution metadata
		ServiceTaskMetaDataUtility.addMetaDataOfServiceTask(execution.getCurrentActivityName(), status, execution);

		execution.setVariable("status", status);
	}

}
