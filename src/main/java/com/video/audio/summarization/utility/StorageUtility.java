package com.video.audio.summarization.utility;

/**
 * @author lalamanil 
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import com.video.audio.summarization.constants.ApplicationConstants;

public class StorageUtility {

	private static GoogleCredentials googleCredentials;
	private static Storage storage;

	static {
		InputStream inputStream = StorageUtility.class.getClassLoader()
				.getResourceAsStream(ApplicationConstants.SERVICE_ACCOUNT_NAME);
		if (null != inputStream) {
			try {
				googleCredentials = GoogleCredentials.fromStream(inputStream)
						.createScoped("https://www.googleapis.com/auth/cloud-platform");
				if (null != googleCredentials) {
					storage = StorageOptions.newBuilder().setCredentials(googleCredentials).build().getService();
				} else {
					ApplicationLogger.print("google Credentials are null. Please check application logs");
				}
			} catch (IOException e) {
				// TODO: handle exception
				Throwable cause = e.getCause();
				if (cause instanceof InvalidKeySpecException || cause instanceof InvalidKeyException) {
					ApplicationLogger.print("❌ Invalid service account key format: " + cause.getMessage());
				} else {
					ApplicationLogger.print("❌ Failed to load credentials: " + e.getMessage());
					e.printStackTrace();
				}

			}

		}

	}

	public static boolean isBucketPresent(String bucketName) {
		boolean check = false;
		if (null == storage) {
			ApplicationLogger.print("Storage object is null. Please check application logs.");
			return check;
		}
		try {
			Bucket bucket = storage.get(bucketName);
			if (null != bucket) {
				ApplicationLogger.print("✅ Bucket exists: " + bucketName);
				check = true;
			} else {
				ApplicationLogger.print("❌ Bucket does not exist or access denied: " + bucketName);
			}
		} catch (StorageException e) {
			// TODO: handle exception
			int code = e.getCode();
			String message = e.getMessage();

			if (403 == code) {
				ApplicationLogger.print(
						"🚫 Access Denied: The service account does not have permission 'storage.buckets.get' for "
								+ bucketName
								+ ". Please assign the correct IAM role (e.g., roles/storage.objectAdmin).");
			} else {

				if (null != message && message.contains("Invalid grant: account not found")) {
					ApplicationLogger.print(
							"❌ Invalid service account: 'client_email' not found in IAM. Please provide a valid service account.");
				} else {
					ApplicationLogger.print(e.getReason());
					e.printStackTrace();
				}

			}
		}
		return check;
	}

	public static boolean uploadObjectToBucket(String filepatthtolocal, String objectName, String bucketName,
			byte[] bytecontent, boolean isFile, String contentType, DelegateExecution execution) {
		boolean uploaded = false;
		if (null != storage) {
			if (!isBucketPresent(bucketName)) {
				return uploaded;
			}
			try {
				byte[] content = null;
				if (isFile) {
					Path path = Paths.get(filepatthtolocal);
					content = Files.readAllBytes(path);
				} else {
					content = bytecontent;
				}
				// inserting video into GCS bucket
				Blob blob = storage.create(BlobInfo.newBuilder(bucketName, objectName).build(), content);
				if (null != blob) {
					ApplicationLogger.print("fileuploaded to GCS bucket:" + bucketName + " with object name:"
							+ objectName + " selflink:" + blob.getSelfLink());
					if (null != execution) {
						if (isFile) {
							execution.setVariable("SourceVideoGCSMediaLink", blob.getMediaLink());
						} else {
							execution.setVariable("AudioGCSMediaLink", blob.getMediaLink());
						}
					}
					// providing read access to all users
					if (ApplicationConstants.BUCKET_ACCESS_CONTROL_FINE_GRAINED) {
						blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
					}
					uploaded = true;
				}
			} catch (InvalidPathException e) {
				// TODO: handle exception
				ApplicationLogger.print("❌ Invalid path. Please provide valid path for video");
			} catch (StorageException e) {
				// TODO: handle exception
				String message = e.getMessage();
				int statuscode = e.getCode();
				ApplicationLogger.print("statusCode:" + statuscode);
				if (null != message) {
					if (message.contains("Invalid grant: account not found")) {
						ApplicationLogger.print(
								"❌ Invalid service account: 'client_email' not found in IAM. Please provide a valid service account.");
					} else {
						if (403 == statuscode && message.contains("Permission 'storage.objects.create' denied")) {
							ApplicationLogger.print(
									"❌ Service account does not have write access to Cloud Storage. Please assign the correct IAM role (e.g., roles/storage.objectAdmin).");
						} else {
							if (404 == statuscode && message.contains("The specified bucket does not exist")) {
								ApplicationLogger.print(
										"❌ The specified bucket does not exist. Please verify the bucket name and ensure it exists in your GCP project.");
							} else {
								if (403 == statuscode && message.contains("Access Not Configured")) {
									ApplicationLogger.print(
											"❌ Cloud Storage API is not enabled for the project. Please enable it at: https://console.cloud.google.com/apis/library/storage.googleapis.com");
								} else {
									ApplicationLogger.print("❗ Storage error: " + message);
									e.printStackTrace();
								}
							}
						}
					}
				} else {
					ApplicationLogger.print("❗ Storage error: No error message available.");

				}

			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			ApplicationLogger.print("storage object is null. Please check application logs");
		}

		return uploaded;
	}

	public static void main(String[] args) {
		String filepath = "/Users/lalamanil/voiceanalyzer/Motivational.mp4";
		boolean uploaded = uploadObjectToBucket(filepath, "Motivational.mp4", "videosummarytranslation", null, true,
				"video/mp4", null);
		System.out.println("uploaded status is:" + uploaded);

	}

}
