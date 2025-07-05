package com.video.audio.summarization.utility;

/**
 * @author lalamanil 
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.v3.LocationName;
import com.google.cloud.translate.v3.TranslateTextRequest;
import com.google.cloud.translate.v3.TranslateTextResponse;
import com.google.cloud.translate.v3.Translation;
import com.google.cloud.translate.v3.TranslationServiceClient;
import com.google.cloud.translate.v3.TranslationServiceSettings;
import com.video.audio.summarization.constants.ApplicationConstants;

public class LanguageTranslationAPI {

	private static TranslationServiceClient translationServiceClient;

	static {
		InputStream inputStream = LanguageTranslationAPI.class.getClassLoader()
				.getResourceAsStream(ApplicationConstants.SERVICE_ACCOUNT_NAME);
		if (null != inputStream) {
			try {
				GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream);
				if (null != googleCredentials) {
					TranslationServiceSettings translationServiceSettings = TranslationServiceSettings.newBuilder()
							.setCredentialsProvider(() -> googleCredentials).build();
					translationServiceClient = TranslationServiceClient.create(translationServiceSettings);
				}
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			ApplicationLogger.print(" Service Account Inputstream is null for LanguageTranslationAPI ");
		}
	}

	public static String translateToRequestedLanguage(String languageCode, String summary) {
		StringBuilder translatedSummary = new StringBuilder();
		if (null != translationServiceClient) {
			try {
				LocationName locationName = LocationName.of("gmemoridev2016", "global");
				TranslateTextRequest translateTextRequest = TranslateTextRequest.newBuilder()
						.setParent(locationName.toString()).setMimeType("text/plain")
						.setTargetLanguageCode(languageCode).addContents(summary).build();
				TranslateTextResponse translateTextResponse = translationServiceClient
						.translateText(translateTextRequest);
				if (null != translateTextResponse) {
					List<Translation> translations = translateTextResponse.getTranslationsList();
					if (null != translations && !translations.isEmpty()) {
						for (Translation translation : translations) {
							translatedSummary.append(translation.getTranslatedText());
							translatedSummary.append("\r\n");
						}

					}

				}
			} catch (PermissionDeniedException e) {
				// TODO: handle exception
				String message = e.getMessage();
				if (null != message) {
					if (message.contains("has not been used in project") || message.contains("it is disabled")) {
						ApplicationLogger.print(
								"❌ The Cloud Translation API is **not enabled** for this project. Please enable it at:\n"
										+ "👉 https://console.developers.google.com/apis/api/translate.googleapis.com/overview?project=");
					} else {
						if (message.contains(
								"PERMISSION_DENIED: Cloud IAM permission 'cloudtranslate.generalModels.predict' denied")) {
							ApplicationLogger.print(
									"❌ Access Denied: The service account does not have permission to use Cloud Translation API.Missing Role:Cloud Translation API User (`roles/cloudtranslate.user`)");
							ApplicationLogger.print(
									"👉 Reason: The service account is missing the IAM role required to perform translation using Google Cloud Translation models.");
						} else {
							ApplicationLogger.print("❗ Permission Denied: " + message);
							ApplicationLogger
									.print("❗ Unrecognized permission error. Please check IAM and API status.");
						}
					}

				} else {
					ApplicationLogger.print("❗ Permission denied but no error message was provided.");
					e.printStackTrace();
				}
			} catch (ApiException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return translatedSummary.toString();
	}

	public static void main(String[] args) {

		String translation = translateToRequestedLanguage("te",
				"n this paper, we present an end-to-end, cloud-native pipeline for automated video content summarization and multilingual narration, orchestrated using Camunda BPMN workflow engine with Java-based service tasks. The proposed system leverages key components of Google Cloud Platform (GCP) to build a scalable, modular, and extensible architecture suitable for real-world applications such as accessibility, content indexing, and educational transformation.\n"
						+ "\n"
						+ "The pipeline begins with Google Cloud Storage (GCS), where raw video files are uploaded and monitored. These video files are then processed by the Google Video Intelligence API, which extracts timestamped transcripts from the visual and audio streams. The extracted textual content is passed to the Vertex AI Gemini-1.5 Pro large language model, which performs contextual abstractive summarization, producing concise and semantically rich summaries. To enhance global accessibility, the summaries are translated into user-specified languages using the Google Cloud Translation API, and subsequently converted to natural-sounding audio via the Text-to-Speech API.\n"
						+ "\n"
						+ "To ensure traceability, analytics, and downstream integration, the entire workflow's metadata — including video references, summaries, language mappings, processing time, and audio output paths — is logged into Google BigQuery. This not only enables real-time dashboarding and searchability but also supports continuous model evaluation and auditing.\n"
						+ "\n"
						+ "The orchestration of this pipeline is implemented using Camunda BPM, where each processing stage is represented as a discrete, reusable Java service task, offering transparency, maintainability, and integration flexibility. The modular nature of this approach allows seamless adaptation to additional AI models or business rules.\n"
						+ "\n"
						+ "Our results demonstrate that the proposed architecture is effective for automating complex multimedia workflows with minimal human intervention, and is adaptable for broader applications in smart media processing, accessibility enhancement, and digital governance. This work contributes toward scalable, explainable, and multilingual AI content transformation — suitable for deployment in both public and enterprise environments.\n"
						+ "");

		System.out.println(translation);

	}

}
