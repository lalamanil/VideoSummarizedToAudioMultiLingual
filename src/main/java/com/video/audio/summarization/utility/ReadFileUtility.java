package com.video.audio.summarization.utility;
/**
 * @author lalamanil 
 *
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadFileUtility {

	public static String readVideoFileFromLocal() {
		BufferedReader br = null;
		String validvideofilepath = null;
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			boolean inputlooper = true;
			while (inputlooper) {
				ApplicationLogger.print("Please provide the path of video");
				String videoPath = br.readLine();
				if (null == videoPath || videoPath.trim().isEmpty() || "null".equalsIgnoreCase(videoPath.trim())) {
					ApplicationLogger.print("file path cannot be null or empty. Please provide valid path");
				} else {
					videoPath = videoPath.trim();
					if (validateFilepath(videoPath)) {
						if (videoPath.toLowerCase().endsWith("mp4")) {
							validvideofilepath = videoPath;
							inputlooper=false;
						} else {
							ApplicationLogger.print("video file path should end with .mp4.");
						}
					}
				}

			}

		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		} 
		return validvideofilepath;

	}

	public static boolean validateFilepath(String filepath) {
		boolean isvalid = false;
		try {
			Path path = Paths.get(filepath);
			if (Files.exists(path)) {
				ApplicationLogger.print(filepath + ". Exits");
				isvalid = true;
			} else {
				ApplicationLogger.print("File doesnot exists. Please provide valid file path");
			}
		} catch (InvalidPathException e) {
			// TODO: handle exception
			ApplicationLogger.print("Invalid file path. Please provide valid file path");
			e.printStackTrace();

		}
		return isvalid;
	}

	public static String getFileNameFromPath(String filepath) {
		return Paths.get(filepath).getFileName().toString();

	}

}
