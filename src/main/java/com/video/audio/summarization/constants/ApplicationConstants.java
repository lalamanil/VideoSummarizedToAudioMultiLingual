package com.video.audio.summarization.constants;

/**
 * @author lalamanil 
 *
 */
import java.util.Arrays;
import java.util.List;

public class ApplicationConstants {

	public static final String BUCKET_NAME = "videosummarytranslation";
	public static final String GCSBUCKETPATH = "gs://"+BUCKET_NAME+"/";
	//Specify access to individual objects by using object-level permissions (ACLs) 
	//if this flag is set true. Please provide public access permission to bucket.
	public static final boolean BUCKET_ACCESS_CONTROL_FINE_GRAINED=false;
	
	public static final String projectId = "gmemoridev2016";
	//For vertex AI API
	public static final String location = "us-central1";
	public static final String modelname = "gemini-2.5-pro";
	public static final String videoContentType = "video/mp4";
	public static final String audioContentType = "audio/mpeg";
	public static final String GCSAudioOutputFolder = "audio/";
	public static final String SummaryAudioOutputFloder = "/Users/lalamanil/voiceanalyzer/SummaryAudioOutput/";
	public static final List<String> APIs = Arrays.asList("storage.googleapis.com", "videointelligence.googleapis.com",
			"aiplatform.googleapis.com", "translate.googleapis.com", "speech.googleapis.com");
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\u001B[31m";
	public static final String GREEN = "\u001B[32m";
	public static final String YELLOW = "\u001B[33m";
	public static final String BLUE = "\u001B[34m";
	public static final String PURPLE = "\u001B[35m";
	public static final String CYAN = "\u001B[37m";
	public static final String SERVICE_ACCOUNT_NAME = "gmemoridev2016-ad074c59c0f8.json";

}
