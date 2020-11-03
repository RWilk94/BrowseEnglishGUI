package rwilk.browseenglish.scrapper.pluralsight;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.time.Duration;
import java.util.logging.Level;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.monte.media.FormatKeys.EncodingKey;
import static org.monte.media.FormatKeys.FrameRateKey;
import static org.monte.media.FormatKeys.KeyFrameIntervalKey;
import static org.monte.media.FormatKeys.MIME_AVI;
import static org.monte.media.FormatKeys.MediaTypeKey;
import static org.monte.media.FormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.CompressorNameKey;
import static org.monte.media.VideoFormatKeys.DepthKey;
import static org.monte.media.VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE;
import static org.monte.media.VideoFormatKeys.QualityKey;

public class VideoRecorder {

  public static final String USER_DIR = "C:\\Users\\rawi\\IdeaProjects\\BrowseEnglishGUI\\src\\main\\java\\rwilk\\browseenglish\\scrapper";
  public static final String DOWNLOADED_FILES_FOLDER = "pluralsight";

  private ScreenRecorder screenRecorder;

  public static void main(String[] args) throws Exception {

    VideoRecorder videoRecorder = new VideoRecorder();
    videoRecorder.startRecording();

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
    } catch (Exception e) {
      e.printStackTrace();
    }

    WebElement element = driver.findElement(By.name("q"));
    element.sendKeys("BreizhCamp 2018");
    element.submit();
    System.out.println("Page title is: " + driver.getTitle());
    driver.quit();
    videoRecorder.stopRecording();
  }

  public void startRecording() throws Exception {
    File file = new File(System.getProperty(USER_DIR) + File.separator + DOWNLOADED_FILES_FOLDER);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width;
    int height = screenSize.height;

    Rectangle captureSize = new Rectangle(0, 0, width, height);

    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

    this.screenRecorder = new SpecializedScreenRecorder(gc, captureSize, new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, DepthKey, 24, FrameRateKey,
            Rational.valueOf(15), QualityKey, 1.0f, KeyFrameIntervalKey, 15 * 60),
        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)), null, file, "MyVideo");
    this.screenRecorder.start();

  }

  public void stopRecording() throws Exception {
    this.screenRecorder.stop();
  }
}