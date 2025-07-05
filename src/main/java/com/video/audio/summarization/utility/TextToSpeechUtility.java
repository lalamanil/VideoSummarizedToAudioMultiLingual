package com.video.audio.summarization.utility;

/**
 * @author lalamanil 
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.ListVoicesRequest;
import com.google.cloud.texttospeech.v1.ListVoicesResponse;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.TextToSpeechSettings;
import com.google.cloud.texttospeech.v1.Voice;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;
import com.video.audio.summarization.constants.ApplicationConstants;

public class TextToSpeechUtility {

	private static TextToSpeechClient textToSpeechClient;

	static {
		InputStream inputStream = TextToSpeechUtility.class.getClassLoader()
				.getResourceAsStream(ApplicationConstants.SERVICE_ACCOUNT_NAME);
		if (null != inputStream) {
			try {
				GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream)
						.createScoped("https://www.googleapis.com/auth/cloud-platform");

				TextToSpeechSettings textToSpeechSettings = TextToSpeechSettings.newBuilder()
						.setCredentialsProvider(() -> googleCredentials).build();
				textToSpeechClient = TextToSpeechClient.create(textToSpeechSettings);
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			ApplicationLogger.print("Service account inputstream null for TextToSpeechUtility");
		}
	}

	public static void listofLanguageCodeThatsupports() {

		if (null != textToSpeechClient) {
			Set<String> languageCodeSet = new HashSet<String>();
			ListVoicesRequest listVoicesRequest = ListVoicesRequest.getDefaultInstance();
			ListVoicesResponse listVoicesResponse = textToSpeechClient.listVoices(listVoicesRequest);
			List<Voice> voiceList = listVoicesResponse.getVoicesList();
			if (null != voiceList && !voiceList.isEmpty()) {
				for (Voice voice : voiceList) {
					// Dispaly the supported language codes for this voice. Example "en-us"
					List<ByteString> languageCode = voice.getLanguageCodesList().asByteStringList();
					for (ByteString code : languageCode) {
						languageCodeSet.add(code.toStringUtf8());
					}
				}
			}
			languageCodeSet.forEach(entry -> {
				ApplicationLogger.print(entry);
			});
		} else {
			ApplicationLogger.print("textToSpeechClient is null. Please check application logs");
		}
	}

	public static byte[] convertTextToSpeech(String summaryText, String ttsLanguageCode) {
		byte[] audioByteArray = null;
		if (null != textToSpeechClient) {
			try {
				SynthesisInput synthesisInput = SynthesisInput.newBuilder().setText(summaryText).build();
				// configuring languge and voice type
				VoiceSelectionParams voiceSelectionParams = VoiceSelectionParams.newBuilder()
						.setLanguageCode(ttsLanguageCode).setSsmlGender(SsmlVoiceGender.NEUTRAL).build();
				// configuring audio type
				AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3).build();
				SynthesizeSpeechResponse synthesizeSpeechResponse = textToSpeechClient.synthesizeSpeech(synthesisInput,
						voiceSelectionParams, audioConfig);
				ByteString audioContent = synthesizeSpeechResponse.getAudioContent();
				if (null != audioContent) {
					audioByteArray = audioContent.toByteArray();

				} else {
					ApplicationLogger.print("audioContent received from TextToSpeech is null");
				}
			} catch (PermissionDeniedException e) {
				// TODO: handle exception
				String message = e.getMessage();
				if (message.contains("has not been used in project") || message.contains("it is disabled")) {
					ApplicationLogger.print(
							"❌ Cloud Text-to-Speech API is **not enabled** for this project. Please enable it at:\n"
									+ "👉 https://console.developers.google.com/apis/api/texttospeech.googleapis.com/overview?project=");
				} else {
					ApplicationLogger.print("❗ Permission Denied: " + message);
					ApplicationLogger.print("❗ Unrecognized permission error. Please check IAM and API status.");

				}

			} catch (ApiException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			ApplicationLogger.print("textToSpeechClient is null. Please check application logs");
		}

		System.out.println("audioByteType::" + audioByteArray);

		return audioByteArray;

	}

}
