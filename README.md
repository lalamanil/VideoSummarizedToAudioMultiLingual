
AI-Powered Video Summarization and Multilingual Narration Using Google Cloud Platform APIs and Camunda Workflow Automation

Abstract:

This project presents an end-to-end, fully automated Video Summarization and Multilingual Narration System, built using Java, integrated with Camunda Workflow Platform, and powered by a suite of Google Cloud Platform (GCP) AI Services. It is designed to showcase advanced cloud-native capabilities for intelligent media processing and is part of a broader academic and professional contribution aligned with NIW and IEEE publication objectives.

The solution starts by programmatically validating whether the following GCP services are enabled for a given service account:

storage.googleapis.com (Cloud Storage),
videointelligence.googleapis.com (Video Intelligence API),
aiplatform.googleapis.com (Vertex AI),
translate.googleapis.com (Cloud Translation API),
speech.googleapis.com (Text-to-Speech API).

This verification is achieved using the Service Usage API, and appropriate user-friendly messages are displayed if any APIs are not enabled or if the service account lacks the required IAM permissions (such as roles/ serviceusage.viewer). Upon passing this validation stage, the application follows a Camunda-driven service task workflow that initiates the main logic.The system prompts the user to provide a local video file, which is uploaded to a preconfigured Cloud Storage bucket. The available target languages are then listed, and the user selects a preferred language for summarization and narration.

The processing pipeline includes:

Video Intelligence API – extracts full transcripts from the video.
Vertex AI (PaLM/Gemini model) – performs intelligent summarization of the transcripts.
Cloud Translation API – translates the summary into the user-selected language.
Text-to-Speech API – generates high-quality audio narration in the target language.

The final narrated .mp3 file is stored both in the GCS bucket and on the local machine for convenient access.

This robust Java-based architecture enforces IAM-driven role validation (e.g., roles/storage.admin, roles/ aiplatform.user, roles/ cloudtranslate.user) and guides the user in configuring essential properties such as the service account file, project ID, and bucket name via constants in the ApplicationConstants interface.

The application embodies the fusion of cloud AI, workflow automation, and  language accessibility — a powerful combination demonstrating how cloud-native design can be harnessed to solve real-world multimedia challenges at scale.


**Please refer to AI-Powered Video Summarization and Multilingual Narration Using Google….pdf **

## License

This project is licensed under the **Creative Commons Attribution-NonCommercial 4.0 International License**.

- You may use, share, or adapt this project for **non-commercial purposes**.
- You must **give appropriate credit** to the author.
- For commercial inquiries or licensing, please contact the author.

View full license: [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/)

Author: Anil Lalam
