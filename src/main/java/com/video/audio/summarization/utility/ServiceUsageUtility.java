package com.video.audio.summarization.utility;
/**
 * @author lalamanil 
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.rpc.ApiException;
import com.google.api.gax.rpc.PermissionDeniedException;
import com.google.api.serviceusage.v1.ListServicesRequest;
import com.google.api.serviceusage.v1.Service;
import com.google.api.serviceusage.v1.ServiceUsageClient;
import com.google.api.serviceusage.v1.ServiceUsageSettings;
import com.google.auth.oauth2.GoogleCredentials;
import com.video.audio.summarization.constants.ApplicationConstants;
import com.video.audio.summarization.model.ServiceAccountModel;

public class ServiceUsageUtility {

	static ServiceUsageClient serviceUsageClient;
	public static ServiceAccountModel serviceAccountModel;

	static {
		InputStream inputStream = ServiceUsageUtility.class.getClassLoader()
				.getResourceAsStream(ApplicationConstants.SERVICE_ACCOUNT_NAME);
		if (null != inputStream) {
			try {
				GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream)
						.createScoped("https://www.googleapis.com/auth/cloud-platform");
				if (null != googleCredentials) {
					ServiceUsageSettings serviceUsageSettings = ServiceUsageSettings.newBuilder()
							.setCredentialsProvider(() -> googleCredentials).build();
					serviceUsageClient = ServiceUsageClient.create(serviceUsageSettings);
				} else {
					ApplicationLogger.print("googleCredentials are null. Please check application logs");
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
		} else {
			ApplicationLogger.print("Inputstream for Service Account not found.Please check");
		}
	}

	public static ServiceAccountModel getServiceAccountModel() {
		ServiceAccountModel serviceAccountModel = null;
		InputStream inputStream = ServiceUsageUtility.class.getClassLoader()
				.getResourceAsStream(ApplicationConstants.SERVICE_ACCOUNT_NAME);
		if (null != inputStream) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				serviceAccountModel = mapper.readValue(inputStream, ServiceAccountModel.class);
			} catch (DatabindException e) {
				// TODO: handle exception
				ApplicationLogger.print("❌ reading the serviceAccountModel from inputstream." + e.getMessage());
			} catch (StreamReadException e) {
				// TODO: handle exception
				ApplicationLogger.print("❌ reading the serviceAccountModel from inputstream." + e.getMessage());
			} catch (IOException e) {
				// TODO: handle exception
				ApplicationLogger.print("❌ reading the serviceAccountModel from inputstream." + e.getMessage());
			}
		}
		return serviceAccountModel;
	}

	// To check Service usage API is Enable and Provide access to service account
	public static boolean checkServiceusageAPIEnableOrNot() {
		boolean check = false;
		ServiceAccountModel serviceAccountModel = getServiceAccountModel();
		if (null != serviceAccountModel) {
			if (null != serviceAccountModel.getProject_id() && !serviceAccountModel.getProject_id().trim().isEmpty()) {
				String parent = "projects/" + serviceAccountModel.getProject_id().trim();
				if (null != serviceUsageClient) {
					try {
						serviceUsageClient
								.listServices(ListServicesRequest.newBuilder().setParent(parent).setPageSize(1).build())
								.iterateAll().iterator().hasNext();
						check = true;
					} catch (PermissionDeniedException e) {
						// TODO: handle exception
						String message = e.getMessage();
						if (null != message) {
							if (message.contains("has not been used in project")
									|| message.contains("it is disabled")) {
								ApplicationLogger.print(
										"❌ The Service Usage API is **not enabled** for this project. Please enable it at:\n"
												+ "👉 https://console.developers.google.com/apis/api/serviceusage.googleapis.com/overview?project="
												+ serviceAccountModel.getProject_id());
							} else {
								if (message.contains("Permission denied to list services")) {
									ApplicationLogger
											.print("❌ The service account does not have sufficient permissions.\n"
													+ "Please grant the role: `roles/serviceusage.viewer` to the service account.");
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
					}
				} else {
					ApplicationLogger
							.print("❗ serviceUsageClient object is null or empty. Please check application logs");
				}
			} else {
				ApplicationLogger.print("❗ projectId attribute in ServiceAccountModel is null or empty");
			}
		} else {
			ApplicationLogger.print("❗ serviceAccountModel object is null");
		}
		return check;
	}

	public static boolean checkAllRequiredAPIAreEnabled(ServiceAccountModel serviceAccountModel) {
		boolean check = false;
		int count = 0;
		if (checkServiceusageAPIEnableOrNot()) {
			String parent = "projects/" + serviceAccountModel.getProject_id();
			ListServicesRequest request = ListServicesRequest.newBuilder().setParent(parent).setFilter("state:ENABLED")
					.build();
			ApplicationLogger
					.print("List of APIs should be Enabled for project: " + serviceAccountModel.getProject_id());
			try {
				List<String> apilist = new ArrayList<String>();
				Iterable<Service> services = serviceUsageClient.listServices(request).iterateAll();
				for (Service service : services) {
					apilist.add(service.getConfig().getName());
				}

				List<String> requiredApis = ApplicationConstants.APIs;

				for (String requiredApi : requiredApis) {
					if (apilist.contains(requiredApi)) {
						ApplicationLogger.print("✅ " + requiredApi + " enabled");
						count++;
					} else {
						ApplicationLogger.print("❌ " + requiredApi + ApplicationConstants.RED
								+ " not enabled. Please enable it to move forward" + ApplicationConstants.RESET);
					}

				}

				if (count == requiredApis.size()) {
					check = true;
				}

			} catch (ApiException e) {
				// TODO: handle exception
				ApplicationLogger.print(e.getMessage());
				e.printStackTrace();
			}

		}
		return check;
	}

}
