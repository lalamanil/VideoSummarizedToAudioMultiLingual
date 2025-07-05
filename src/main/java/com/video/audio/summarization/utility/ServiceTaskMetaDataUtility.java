package com.video.audio.summarization.utility;

/**
 * @author lalamanil 
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.delegate.DelegateExecution;

public class ServiceTaskMetaDataUtility {

	public static void addMetaDataOfServiceTask(String activityName, Boolean status, DelegateExecution execution) {

		Object object = execution.getVariable("scriptsExecuted");
		List<Map<String, String>> scriptStatusList = null;
		if (null != object) {
			scriptStatusList = (List<Map<String, String>>) object;
		} else {
			scriptStatusList = new ArrayList<Map<String, String>>();
		}
		Map<String, String> entry = new HashMap<String, String>();
		entry.put("script", activityName);
		entry.put("status", status ? "success" : "failure");
		scriptStatusList.add(entry);
		execution.setVariable("scriptsExecuted", scriptStatusList);

	}

}
