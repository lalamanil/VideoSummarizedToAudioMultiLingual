package com.video.audio.summarization.utility;
/**
 * @author lalamanil 
 *
 */
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.variable.Variables;

public class StoringLargeValuesForDelegateExecutionUtility {

	public static Object LargeValueInsertion(String longvalue, DelegateExecution execution) {
		Object object = null;
		if (null != longvalue && !longvalue.trim().isEmpty()) {
			object = Variables.objectValue(longvalue).serializationDataFormat(Variables.SerializationDataFormats.JAVA)
					.create();
		} else {
			ApplicationLogger.print("long value is null or empty..");

		}
		return object;
	}

}
