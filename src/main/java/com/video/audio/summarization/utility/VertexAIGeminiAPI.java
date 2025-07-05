package com.video.audio.summarization.utility;
/**
 * @author lalamanil 
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Candidate;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.video.audio.summarization.constants.ApplicationConstants;
import com.video.audio.summarization.servicetasks.SummarizeTranscriptsViaVertexAIServiceTask;

public class VertexAIGeminiAPI {
	private static VertexAI vertexAI = null;
	static {
		// TODO Auto-generated method stub
		InputStream inputStream = null;
		inputStream = SummarizeTranscriptsViaVertexAIServiceTask.class.getClassLoader()
				.getResourceAsStream(ApplicationConstants.SERVICE_ACCOUNT_NAME);
		if (null != inputStream) {
			try {
				GoogleCredentials credentials = GoogleCredentials.fromStream(inputStream)
						.createScoped("https://www.googleapis.com/auth/cloud-platform");
				if (null != credentials) {
					vertexAI = new VertexAI.Builder().setCredentials(credentials)
							.setProjectId(ApplicationConstants.projectId).setLocation(ApplicationConstants.location)
							.build();
				}
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			ApplicationLogger.print("Inputstream of service account is null. Please check application logs");
		}
	}

	public static String summarizeTranscript(String transcript) {
		StringBuilder summaryBuilder = new StringBuilder();
		if (null != vertexAI) {
			try {
				String prompt = "Summarize this text: " + transcript;
				GenerativeModel generativeModel = new GenerativeModel(ApplicationConstants.modelname, vertexAI);
				GenerateContentResponse generateContentResponse = generativeModel.generateContent(prompt);
				if (null != generateContentResponse) {
					List<Candidate> candidatelist = generateContentResponse.getCandidatesList();
					if (null != candidatelist && !candidatelist.isEmpty()) {
						for (Candidate candidate : candidatelist) {
							Content content = candidate.getContent();
							if (null != content) {
								List<Part> partList = content.getPartsList();
								if (null != partList && !partList.isEmpty()) {
									for (Part part : partList) {
										summaryBuilder.append(part.getText());
										summaryBuilder.append("\r\n");
									}
								}
							}

						}

					}
				}

			} catch (PermissionDeniedException e) {
				// TODO: handle exception
				String message = e.getMessage();
				if (null != message) {
					if (message.contains("has not been used in project") || message.contains("it is disabled")) {
						ApplicationLogger.print(
								"❌ The Vertex API is **not enabled** for this project. Please enable it at:\n"
										+ "👉 https://console.developers.google.com/apis/api/aiplatform.googleapis.com/overview?");
					} else {
						if (message.contains("PERMISSION_DENIED: Permission 'aiplatform.endpoints.predict'")) {
							ApplicationLogger.print(
									"❌ Access Denied: The service account does not have permission to use VertexAI.Missing Role:Vertex AI User (`roles/aiplatform.user`)");
							ApplicationLogger.print(
									"👉 Reason: The service account is missing the necessary IAM role to perform predictions using Vertex AI models, such as Gemini.");
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
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			ApplicationLogger.print("vertexAI object is null. Please check application logs");
		}
		return summaryBuilder.toString();
	}

	public static void main(String[] args) {
		String transcript = "it can feel like a different era sometimes but 15 years ago I signed the Affordable Care Act into law the gold back then was to establish that you're in America Healthcare is not just a privilege but a right for every single American\n"
				+ " and together we've taken a big step in the right direction\n"
				+ " today almost 50 million people have gotten quality affordable health care through the ACA that's one in seven Americans and the percentage of people without insurance has been nearly cut in half\n"
				+ " that means more sick kids get medicine\n" + " more cancer patients have life-saving surgery\n"
				+ " more people with pre-existing conditions enjoy the peace of mind they deserve\n"
				+ " but here's the thing even though people like to talk about Obamacare it didn't just happen because of me it happened because Americans of all ages from all across the country had the courage to speak up about why Health Care reform mattered to them\n"
				+ " when the Obama Presidential Center opens on the south side of Chicago next year visitors will have a chance to hear from some of the folks who made the ACA a reality and I'll get to see some of the objects that symbolized the fight for Health Care reform and helped convince Congress to do the right thing\n"
				+ " you'll learn about Jim Houser ran an auto repair shop with nine full-time employees thanks to the ACA Jim got help covering those workers and he proudly wore his master automobile technician pin to the State of the Union in 2011\n"
				+ " Jim also brought his daughter Helen law a 22 year old freelancer who also got covered under the ACA\n"
				+ " besides hearing their stories visitors will also get to see how people's lives have been changed forever thanks to the work we did together and you'll get to hear from some of the leaders in my Administration who worked and sacrificed for years to make it happen when I see people going in to the Obama Presidential Center I hope they come away with a sense that Against All Odds progress can be made and that counts on\n"
				+ " residential leadership elected representatives people all around the country\n"
				+ " a lot of folks told me I pay a political price if I made Health Care reform a priority some of them predicted it would cost me re-election but I've always said that I didn't run for president to put my approval rating up on a shelf and admire it I did it to make a difference so the choice was clear we were in the Oval Office talking and he said look this is shaping up to be a one-term presidency and that's okay with me as long as I can get done the things I want to get\n"
				+ " done and Healthcare reform is one of those things\n"
				+ " with everything going on right now it's easy to feel like regular folks can't change this country for the better but it could happen 15 years ago it can happen again\n"
				+ " the ACA taught us that some things are bigger than politics and working to make sure people don't lose their livelihoods when a family member gets sick is one of those things\n"
				+ " that's why we did it and we're not finished yet I've always said that the ACA is like a starter house it was a big step forward but still just a first step now it's up to all of us to keep building on and improving the ACA until everyone has access to quality affordable health coverage when you visit the Obama Center in 2026 I hope you leave ready to keep making a difference\n"
				+ " we look forward to seeing you next year";
		System.out.println("summary transcript is:" + VertexAIGeminiAPI.summarizeTranscript(transcript));

	}

}
