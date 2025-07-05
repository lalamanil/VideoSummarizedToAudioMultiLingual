package com.video.audio.summarization.servicetasks;

/**
 * @author lalamanil 
 *
 */
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import com.video.audio.summarization.constants.ApplicationConstants;
import com.video.audio.summarization.utility.ApplicationLogger;

public class PipelineExecutionMetaDataServiceTask implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		// TODO Auto-generated method stub

		Map<String, Object> variableMap = execution.getVariables();

		if (null != variableMap && !variableMap.isEmpty()) {

			variableMap.entrySet().forEach(entry -> {
				ApplicationLogger.print(ApplicationConstants.BLUE + entry.getKey() + ApplicationConstants.RESET + "--->"
						+ ApplicationConstants.PURPLE + entry.getValue() + ApplicationConstants.RESET);
			});

		}
	}

}
