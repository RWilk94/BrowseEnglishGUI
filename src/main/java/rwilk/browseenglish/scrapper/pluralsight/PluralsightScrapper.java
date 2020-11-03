package rwilk.browseenglish.scrapper.pluralsight;

import java.time.Duration;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluralsightScrapper {

  public static void main(String[] args) {
    PluralsightScrapper pluralsightScrapper = new PluralsightScrapper();
    pluralsightScrapper.webScrap();
  }

  public void webScrap() {
    // ScreenRecorder

    System.setProperty("webdriver.chrome.driver", "C:\\Users\\rawi\\IdeaProjects\\BrowseEnglishGUI\\driver\\chromedriver.exe");
    ChromeOptions chromeOptions = new ChromeOptions();
    // chromeOptions.addArguments("--mute-audio");
    // chromeOptions.addArguments("--headless");
    java.util.logging.Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
    System.setProperty("webdriver.chrome.silentOutput", "true");

    WebDriver driver = new ChromeDriver(chromeOptions);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5).getSeconds());
    Cookie jsessionid = new Cookie("JSESSIONID", "5e680b88a7b2eca9");
    Cookie psJwt = new Cookie("PsJwt-production", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJoYW5kbGUiOiIwZDJiYTA3Yi01ZmNlLTQ5YTgtYmJjYy1hNmRiNmE1ODlmMDYiLCJpYXQiOjE1ODYyNjQ0MDUsImV4cCI6MTU4Njg2OTIwNX0.4VJS5iAZJuWWwyPaofXxJqIDCrMWfkrfXYvm-GSEqFI");
    try {
      driver.get("https://google.com");
      driver.manage().addCookie(jsessionid);
      driver.manage().addCookie(psJwt);
      driver.get("https://app.pluralsight.com/course-player?clipId=e488b353-b959-4a09-8700-7e1109097a9a");

      WebElement element = driver.findElement(By.id("video-element"));

    } catch (Exception e) {
      log.error("Error: ", e);
    }

  }
//
//  public static void startRecording() throws Exception
//  {
//
//    File file = new File("C:\\videos");
//
//    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//    int width = screenSize.width;
//    int height = screenSize.height;
//
//    Rectangle captureSize = new Rectangle(0,0, width, height);
//
//
//    GraphicsConfiguration gc = GraphicsEnvironment
//        .getLocalGraphicsEnvironment()
//        .getDefaultScreenDevice()
//        .getDefaultConfiguration();
//
//
//    screenRecorder = new ScreenRecorder(gc, captureSize,
//        new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, MIME_AVI),
//        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
//            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
//            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
//            QualityKey, 1.0f,
//            KeyFrameIntervalKey, 15 * 60),
//        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
//            FrameRateKey, Rational.valueOf(30)),
//        null, file, "Video"); //Video puede ser cambiado al nombre que deseen
//    screenRecorder.start();
//  }

}
