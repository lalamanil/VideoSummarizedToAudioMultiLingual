package com.video.audio.summarization.utility;
/**
 * @author lalamanil 
 *
 */
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

import com.video.audio.summarization.constants.ApplicationConstants;

public class EncodingDecodingUtility {

	public static String encode() {
		String base64String =null;
		BufferedReader br = null;

		try {

			InputStream inputStream = EncodingDecodingUtility.class.getClassLoader()
					.getResourceAsStream(ApplicationConstants.SERVICE_ACCOUNT_NAME);
			br = new BufferedReader(new InputStreamReader(inputStream));

			String data = null;
			StringBuilder builder = new StringBuilder();

			while ((data = br.readLine()) != null) {

				builder.append(data);

			}
			ApplicationLogger.print(builder.toString());

			 base64String = Base64.getEncoder().encodeToString(builder.toString().getBytes());

			ApplicationLogger.print(base64String);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
					ApplicationLogger.print("closing the inputstream");
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}

			}
		}
		
		return base64String;

	}

	public static InputStream decodeBase64String(String base64String) {
		InputStream inputStream = null;

		if (null != base64String && !base64String.trim().isEmpty()) {
			byte[] decodedBytes = Base64.getDecoder().decode(base64String.trim().getBytes());
			inputStream = new ByteArrayInputStream(decodedBytes);
		} else {
			ApplicationLogger.print("service account base64 is null or empty");
		}
		return inputStream;

	}

	public static void main(String[] args) {

		decodeBase64String(encode());

	}

}
