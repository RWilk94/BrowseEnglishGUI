package rwilk.browseenglish.scrapper.audio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class DikiAudio {

  public static void main(String[] args) throws IOException {
    // downloadAudio("dog");

    String baseUrl = "https://www.diki.pl/";

    String outputDirectory = "C:\\Users\\R.Wilk\\IdeaProjects\\BrowseEnglishGUI\\audio\\a1\\Czasowniki nieregularne w Past Simple - cz. 5";

    List<String> audioUrls = Arrays.asList(
        "images-common/en/mp3/past_simple.mp3",
        "images-common/en/mp3/sleep.mp3",
        "images-common/en-ame/mp3/sleep.mp3",
        "images-common/en/mp3/slept.mp3",
        "images-common/en-ame/mp3/slept.mp3",
        "images-common/en/mp3/slept.mp3",
        "images-common/en-ame/mp3/slept.mp3",
        "images-common/en/mp3/stand.mp3",
        "images-common/en-ame/mp3/stand.mp3",
        "images-common/en/mp3/stood.mp3",
        "images-common/en-ame/mp3/stood.mp3",
        "images-common/en/mp3/stood.mp3",
        "images-common/en-ame/mp3/stood.mp3",
        "images-common/en/mp3/swim.mp3",
        "images-common/en-ame/mp3/swim.mp3",
        "images-common/en/mp3/swam.mp3",
        "images-common/en-ame/mp3/swam.mp3",
        "images-common/en/mp3/swum.mp3",
        "images-common/en-ame/mp3/swum.mp3",
        "images-common/en/mp3/take.mp3",
        "images-common/en-ame/mp3/take.mp3",
        "images-common/en/mp3/took.mp3",
        "images-common/en-ame/mp3/took.mp3",
        "images-common/en/mp3/taken.mp3",
        "images-common/en-ame/mp3/taken.mp3",
        "images-common/en/mp3/teach.mp3",
        "images-common/en-ame/mp3/teach.mp3",
        "images-common/en/mp3/taught.mp3",
        "images-common/en-ame/mp3/taught.mp3",
        "images-common/en/mp3/taught.mp3",
        "images-common/en-ame/mp3/taught.mp3",
        "images-common/en/mp3/think.mp3",
        "images-common/en-ame/mp3/think.mp3",
        "images-common/en/mp3/thought.mp3",
        "images-common/en-ame/mp3/thought.mp3",
        "images-common/en/mp3/thought.mp3",
        "images-common/en-ame/mp3/thought.mp3",
        "images-common/en/mp3/understand.mp3",
        "images-common/en-ame/mp3/understand.mp3",
        "images-common/en/mp3/understood.mp3",
        "images-common/en-ame/mp3/understood.mp3",
        "images-common/en/mp3/understood.mp3",
        "images-common/en-ame/mp3/understood.mp3",
        "images-common/en/mp3/wake.mp3",
        "images-common/en-ame/mp3/wake.mp3",
        "images-common/en/mp3/woke.mp3",
        "images-common/en-ame/mp3/woke.mp3",
        "images-common/en/mp3/woken.mp3",
        "images-common/en-ame/mp3/woken.mp3",
        "images-common/en/mp3/wear.mp3",
        "images-common/en-ame/mp3/wear.mp3",
        "images-common/en/mp3/wore.mp3",
        "images-common/en-ame/mp3/wore.mp3",
        "images-common/en/mp3/worn.mp3",
        "images-common/en-ame/mp3/worn.mp3",
        "images-common/en/mp3/win.mp3",
        "images-common/en-ame/mp3/win.mp3",
        "images-common/en/mp3/won.mp3",
        "images-common/en-ame/mp3/won.mp3",
        "images-common/en/mp3/won.mp3",
        "images-common/en-ame/mp3/won.mp3"
    );

    for (String audioUrl : audioUrls) {

      String audioName = audioUrl.substring(audioUrl.lastIndexOf("/") + 1).replace(".mp3", "");
      if (audioUrl.contains("en-ame")) {
        audioName = "us_" + audioName;
      } else {
        audioName = "en_" + audioName;
      }

      downloadAudioLocally(new URL(baseUrl + audioUrl),
          outputDirectory,
          audioName);
    }
    System.out.println("DONE");
  }

//  public void downloadEnDikiAudio(String dataAudioUrl, String outputDirectory) throws IOException {
//    URL url = new URL("https://www.diki.pl/" + dataAudioUrl);
//    downloadAudio(url, outputDirectory, dataAudioUrl.substring(dataAudioUrl.lastIndexOf("/")));
//  }
//
//  public void downloadUsDikiAudio(String name, String outputDirectory) throws IOException {
//    URL url = new URL("https://www.diki.pl/images-common/en-ame/mp3/" + name + ".mp3");
//    downloadAudio(url, outputDirectory, name);
//  }

  public void downloadAudio(URL url, String outputDirectory, String name) throws IOException {
    URLConnection conn = new URL(url.toString()).openConnection();
    InputStream is = conn.getInputStream();

    OutputStream outstream = new FileOutputStream(new File(outputDirectory + "/" + name + ".mp3"));
    byte[] buffer = new byte[4096];
    int len;
    while ((len = is.read(buffer)) > 0) {
      outstream.write(buffer, 0, len);
    }
    outstream.close();
  }

  public static void downloadAudioLocally(URL url, String outputDirectory, String name) throws IOException {
    URLConnection conn = new URL(url.toString()).openConnection();
    InputStream is = conn.getInputStream();

    OutputStream outstream = new FileOutputStream(new File(outputDirectory + "/" + name + ".mp3"));
    byte[] buffer = new byte[4096];
    int len;
    while ((len = is.read(buffer)) > 0) {
      outstream.write(buffer, 0, len);
    }
    outstream.close();
  }

}
