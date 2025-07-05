package com.video.audio.summarization;

import java.io.InputStream;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.runtime.ProcessInstance;
/**
 * @author lalamanil 
 *
 */
public class App {

	public static void main(String[] args) {
		// create the embedded process engine configuration
		try {
				
			ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
					.buildProcessEngine();
			// Deploy the BPMN process engine
			InputStream inputStream = App.class.getClassLoader()
					.getResourceAsStream("VideoSummarizationtoAudioMultilingual.bpmn");
			System.out.println("Inputstream is:" + inputStream);
			Deployment deployment = processEngine.getRepositoryService().createDeployment()
					.addInputStream("VideoSummarizationtoAudioMultilingual.bpmn", inputStream)
					.name("My Process Deployment").deploy();
			System.out.println("Deployment ID:" + deployment.getId());
			ProcessInstance instance = processEngine.getRuntimeService()
					.startProcessInstanceByKey("VideoSummerizationPipeline");
			String instanceId = instance.getId();
			System.out.println("Process stated with ID:" + instanceId);

		} catch (ProcessEngineException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
