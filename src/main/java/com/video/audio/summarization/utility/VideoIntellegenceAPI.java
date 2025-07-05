package com.video.audio.summarization.utility;

/**
 * @author lalamanil 
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.videointelligence.v1.AnnotateVideoProgress;
import com.google.cloud.videointelligence.v1.AnnotateVideoRequest;
import com.google.cloud.videointelligence.v1.AnnotateVideoResponse;
import com.google.cloud.videointelligence.v1.Feature;
import com.google.cloud.videointelligence.v1.SpeechRecognitionAlternative;
import com.google.cloud.videointelligence.v1.SpeechTranscription;
import com.google.cloud.videointelligence.v1.SpeechTranscriptionConfig;
import com.google.cloud.videointelligence.v1.VideoAnnotationResults;
import com.google.cloud.videointelligence.v1.VideoContext;
import com.google.cloud.videointelligence.v1.VideoIntelligenceServiceClient;
import com.google.cloud.videointelligence.v1.VideoIntelligenceServiceSettings;
import com.video.audio.summarization.constants.ApplicationConstants;

public class VideoIntellegenceAPI {

	private static VideoIntelligenceServiceClient videoIntelligenceServiceClient;

	static {
		InputStream inputStream = VideoIntellegenceAPI.class.getClassLoader()
				.getResourceAsStream(ApplicationConstants.SERVICE_ACCOUNT_NAME);
		if (null != inputStream) {
			try {
				GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream);
				if (null != credentials) {
					VideoIntelligenceServiceSettings videoIntelligenceServiceSettings = VideoIntelligenceServiceSettings
							.newBuilder().setCredentialsProvider(() -> credentials).build();
					videoIntelligenceServiceClient = VideoIntelligenceServiceClient
							.create(videoIntelligenceServiceSettings);
					ApplicationLogger.print("Video Intelligence Service Client is created");
				}
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			ApplicationLogger.print("Inputstream is null while pulling the service account.");
		}
	}

	public static String getTranscriptsFromVideo(String gcspathofVideo) {
		StringBuilder finaltranscripts = new StringBuilder();
		if (null != videoIntelligenceServiceClient) {
			AnnotateVideoRequest annotateVideoRequest = AnnotateVideoRequest.newBuilder()
					.addFeatures(Feature.SPEECH_TRANSCRIPTION)
					.setVideoContext(VideoContext.newBuilder()
							.setSpeechTranscriptionConfig(SpeechTranscriptionConfig.newBuilder()
									.setLanguageCode("en-US").setEnableAutomaticPunctuation(true).build()))
					.setInputUri(gcspathofVideo).build();
			OperationFuture<AnnotateVideoResponse, AnnotateVideoProgress> futureResponse = videoIntelligenceServiceClient
					.annotateVideoAsync(annotateVideoRequest);
			try {
				while (!futureResponse.isDone()) {
					ApplicationLogger.print("⏳" + ApplicationConstants.PURPLE + "Video processing... Please wait."
							+ ApplicationConstants.RESET);
					TimeUnit.SECONDS.sleep(5);
				}
				AnnotateVideoResponse annotateVideoResponse = futureResponse.get();
				if (null != annotateVideoResponse) {
					List<VideoAnnotationResults> videoAnnotationResults = annotateVideoResponse
							.getAnnotationResultsList();
					if (null != videoAnnotationResults && !videoAnnotationResults.isEmpty()) {
						for (VideoAnnotationResults annotationResult : videoAnnotationResults) {
							List<SpeechTranscription> speechTranscriptions = annotationResult
									.getSpeechTranscriptionsList();
							if (null != speechTranscriptions && !speechTranscriptions.isEmpty()) {
								for (SpeechTranscription transcription : speechTranscriptions) {
									List<SpeechRecognitionAlternative> speechRecognitionAlternatives = transcription
											.getAlternativesList();
									if (null != speechRecognitionAlternatives
											&& !speechRecognitionAlternatives.isEmpty()) {
										for (SpeechRecognitionAlternative alternative : speechRecognitionAlternatives) {
											finaltranscripts.append(alternative.getTranscript());
											finaltranscripts.append("\r\n");
										}

									}

								}
							}
						}
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				// TODO: handle exception
				String message = e.getMessage();
				if (null != message) {
					if (message.contains("has not been used in project") || message.contains("it is disabled")) {
						ApplicationLogger.print(
								"❌ The Cloud video Intellegence API is **not enabled** for this project. Please enable it at:\n"
										+ "👉 visiting https://console.developers.google.com/apis/api/videointelligence.googleapis.com/overview?project=");
					} else {
						e.printStackTrace();
					}
				} else {
					e.printStackTrace();
				}
			}
		}
		return finaltranscripts.toString();
	}

	public static void main(String[] args) {

		System.out.println(getTranscriptsFromVideo("gs://videosummarytranslation/Motivational.mp4"));
	}

}
