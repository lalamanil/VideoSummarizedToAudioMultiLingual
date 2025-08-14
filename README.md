##Track: Voice & Conversational AI

##AI-Powered Video Summarization and Multilingual Narration Using Google Cloud Platform APIs and Camunda Workflow Automation


Abstract:
---------

This project presents an end-to-end, fully automated Video Summarization and Multilingual Narration System, built using Java, integrated with **Camunda Workflow Platform, and powered by a suite of Google Cloud Platform (GCP) AI Services.** It is designed to showcase advanced cloud-native capabilities for intelligent media processing and is part of a broader academic and professional contribution aligned with NIW and IEEE publication objectives.

The solution starts by programmatically validating whether the following GCP services are enabled for a given service account:

**storage.googleapis.com (Cloud Storage)**,
**videointelligence.googleapis.com (Video Intelligence API)**,
**aiplatform.googleapis.com (Vertex AI)**,
**translate.googleapis.com (Cloud Translation API)**,
**speech.googleapis.com (Text-to-Speech API)**.

This verification is achieved using the Service Usage API, and appropriate user-friendly messages are displayed if any APIs are not enabled or if the service account lacks the required IAM permissions (such as roles/ serviceusage.viewer). Upon passing this validation stage, the application follows a Camunda-driven service task workflow that initiates the main logic.The system prompts the user to provide a local video file, which is uploaded to a preconfigured Cloud Storage bucket. The available target languages are then listed, and the user selects a preferred language for summarization and narration.

The processing pipeline includes:

Video Intelligence API – extracts full transcripts from the video.
Vertex AI (PaLM/Gemini model) – performs intelligent summarization of the transcripts.
Cloud Translation API – translates the summary into the user-selected language.
Text-to-Speech API – generates high-quality audio narration in the target language.

The final narrated .mp3 file is stored both in the GCS bucket and on the local machine for convenient access.

This robust Java-based architecture enforces IAM-driven role validation (e.g., roles/storage.admin, roles/ aiplatform.user, roles/ cloudtranslate.user) and guides the user in configuring essential properties such as the service account file, project ID, and bucket name via constants in the ApplicationConstants interface.

The application embodies the fusion of cloud AI, workflow automation, and  language accessibility — a powerful combination demonstrating how cloud-native design can be harnessed to solve real-world multimedia challenges at scale.

## AI-Powered Video Summarization and Multilingual Narration.pdf will provide in-details steps to run this application


Potential Use Cases & Applications
----------------------------------

Education & E-Learning
-----------------------

1) Automatic summarization of lecture recordings, webinars, and training videos.

2)Multilingual narration for global student reach.

3)Quick revision materials for online courses (MOOCs).

Corporate & Business Communication
-----------------------------------

1)Summarizing long corporate town halls, meetings, and training sessions.

2)Generating multilingual video updates for international teams.

3)Archiving key points from hours-long recordings.

Media & Journalism
-------------------

1)Producing quick, language-localized summaries of news reports.

2)Helping media houses create region-specific content from a single source.

Government & Public Service
----------------------------

1)Summarizing policy announcements and public service videos.

2)Translating important government communications into multiple languages.

3)Increasing accessibility for diverse populations.

Healthcare & Medical Training
------------------------------

1)Creating concise, multilingual summaries of medical training videos.

2)Helping non-English-speaking medical staff access critical knowledge quickly.

Legal & Compliance
--------------------

1)Summarizing legal depositions, hearings, and compliance training videos.

2)Producing multilingual versions for international stakeholders.

Tourism & Cultural Preservation
---------------------------------

1)Summarizing documentaries or cultural videos for tourists.

2)Providing narrations in multiple languages for museums and heritage sites.

Research & Academia
--------------------

1)Creating concise multilingual research presentations.

2)Supporting IEEE and academic publications with demo summaries.

3)Processing recorded conferences for easier knowledge sharing.

Disaster Response & Crisis Communication
------------------------------------------

1)Summarizing urgent video updates from disaster zones.

2)Translating them into local languages for affected communities.

Social Media & Content Creation
----------------------------------
1)Converting long-form videos into short, multilingual clips for TikTok, YouTube Shorts, etc.

2)Expanding content reach to non-English-speaking audiences.




## License

This project is licensed under the **Creative Commons Attribution-NonCommercial 4.0 International License**.

- You may use, share, or adapt this project for **non-commercial purposes**.
- You must **give appropriate credit** to the author.
- For commercial inquiries or licensing, please contact the author.

View full license: [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/)

Author: Anil Lalam
