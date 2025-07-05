package com.video.audio.summarization.utility;

/**
 * @author lalamanil 
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.video.audio.summarization.constants.ApplicationConstants;

public class ReadLanguageCodes {

	public static Map<String, String> getGoogleSupportedLanguageCodes() {
		InputStream inputStream = ReadLanguageCodes.class.getClassLoader()
				.getResourceAsStream("LanguageTranslationCode.txt");
		Map<String, String> languageCodeMap = new TreeMap<String, String>();
		if (null != inputStream) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(inputStream));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (!line.trim().isEmpty()) {
						String[] languageCode = line.trim().split(",");
						languageCodeMap.put(languageCode[0], languageCode[1]);
					}
				}
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				if (null != br) {
					try {
						br.close();
						ApplicationLogger.print("closing br in getGoogleSupportedLanguageCodes ");
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		} else {
			ApplicationLogger.print("Inputstream for the LanguageTranslationCode.txt is null");
		}
		return languageCodeMap;
	}

	public static Map<String, String> getTTSLanguageCodes() {
		Map<String, String> languageCodeMap = new HashMap<String, String>();
		InputStream inputStream = ReadLanguageCodes.class.getClassLoader()
				.getResourceAsStream("TextToSpeechLanguageCodes.txt");

		if (null != inputStream) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(inputStream));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (!line.trim().isEmpty()) {
						String[] ttsLanguageCode = line.trim().split(",");
						languageCodeMap.put(ttsLanguageCode[0], ttsLanguageCode[1]);
					}

				}
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {

				if (null != br) {
					try {
						br.close();
						//ApplicationLogger.print("Closing bufferedReader for TextToSpeechLanguageCodes.txt");
					} catch (IOException e) {
						// TODO: handle exception
						e.printStackTrace();
					}

				}
			}

		} else {
			ApplicationLogger.print("Inputstream for TextToSpeechLanguageCodes.txt is null.");
		}

		return languageCodeMap;

	}

	public static String requestlanguageCode() {
		BufferedReader bufferedReader = null;
		String code = null;
		String language = null;
		Map<String, String> languageCode = getGoogleSupportedLanguageCodes();
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(System.in));
			languageCode.entrySet().forEach(entry -> {
				ApplicationLogger.print(ApplicationConstants.GREEN + entry.getKey() + ApplicationConstants.RESET);
			});
			boolean loop = true;
			ApplicationLogger
					.print("Please enter the language to which audio summary should translate from above list");
			while (loop) {
				language = bufferedReader.readLine();
				if (null != language && !language.isEmpty()) {
					if (languageCode.containsKey(language)) {
						code = languageCode.get(language);
						loop = false;
					}
				}
				if (loop) {
					ApplicationLogger.print(
							code + " is not supported. Please provide valid translation language from above list. ");
				}
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (null != bufferedReader) {
				try {
					bufferedReader.close();

					//ApplicationLogger.print("Closing buffered reader stream in requestlanguageCode");
				} catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}
		return language + "@@" + code;
	}

	public static void main(String[] args) {

		ApplicationLogger.print(requestlanguageCode());
	}

}
