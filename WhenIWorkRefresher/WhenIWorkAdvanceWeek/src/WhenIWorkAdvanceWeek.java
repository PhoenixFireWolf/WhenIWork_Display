import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Fredrick Kaup
 *
 * This application should open a Chrome browser
 * navigate to the When I Work website
 * log in and set the window to the specified settings
 * to display the schedule, it will also auto refresh
 * every 6 hours and advance the week on Wednesday at 3am.
 */
public class WhenIWorkAdvanceWeek extends Thread {


	static RemoteWebDriver wd;
	static SessionId session_id;
	static URL url;
	static String dayToChange = "Wed";
	static int cHour = 03;
	static int cMin = 00;
	static int cSec = 00;
	static Robot robot;
	static LocalDateTime now = LocalDateTime.now();

	/**
	 * Main entry point for the application
	 * @param args - no arguments needed for this class/method
	 */
	public static void main(String[] args) {
		initialize();
		zoomOut();
		advance();
	}

	/**
	 * zoom out the browser view to 80%
	 */
	public static void zoomOut() {
		// Takes control and does not give it back
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_SUBTRACT);
			robot.keyRelease(KeyEvent.VK_SUBTRACT);
			robot.keyPress(KeyEvent.VK_SUBTRACT);
			robot.keyRelease(KeyEvent.VK_SUBTRACT);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * zoom back in to 100%
	 */
	public static void zoomIn() {
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_ADD);
			robot.keyRelease(KeyEvent.VK_ADD);
			robot.keyPress(KeyEvent.VK_ADD);
			robot.keyRelease(KeyEvent.VK_ADD);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} catch (AWTException e){
			e.printStackTrace();
		}
	}

	/**
	 * initializes the browser navigates to WhenIWork.com
	 * and logs in using provided credentials
	 * expand the webpage to full screen
	 * collapse the sidebar
	 */
	public static void initialize() {

		String path = "./chromedriver_74.exe";
		System.setProperty("webdriver.chrome.driver", path);

		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars");

		wd = new ChromeDriver(options);
		
		HttpCommandExecutor executor = (HttpCommandExecutor) wd.getCommandExecutor();
		url = executor.getAddressOfRemoteServer();
		session_id = wd.getSessionId();
		wd.get("https://appx.wheniwork.com/scheduler");
		
		WebDriverWait wait = new WebDriverWait(wd, 20);
		wd.manage().window().maximize();
		wd.findElement(By.id("email")).sendKeys("PhoenixFireWolf@live.com");
		wd.findElement(By.id("password")).sendKeys("u#htJv$7!@a39h7");
		wd.findElement(By.xpath("//button[@class='btn btn-primary btn-login btn-md btn-block']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//button[@class='btn btn-secondary toggle-fullscreen btn-icn btn-panel btn-md']"))).click();
		wd.findElement(By.xpath("//div[@class='hide-sidebar hint--right']")).click();
	}

	/**
	 * collects the current date and time and determines
	 * if the day we expect to advance date has been reached
	 *
	 * @return if the day for the update is today returns true
	 */
	public static boolean dayToChange() {
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
		return simpleDateformat.format(now).equals(dayToChange);
	}

	/**
	 * collects current time and determines if the time we wish to advance
	 * the week has arrived
	 *
	 * @return if the time for the update has arrived returns true
	 */
	public static boolean timeToChange() {
		int hour = now.getHour();
		int minute = now.getMinute();
		int second = now.getSecond();
		return hour == cHour && minute == cMin && second == cSec;
	}
	
	/**
	 * Tests to determine if browser opened by application is still open 
	 *
	 * @return if the browser opened by this app is still open returns true
	 */
	public static boolean stillOpen() {
		boolean open = true;
		try {
			wd.findElement(By.xpath("//button[@class='btn btn-secondary mr-2 navigate-today btn-panel btn-md']"));
		} catch (NoSuchElementException e) {
			open = false;
		}
		return open;
	}

	/**
	 * Continuance loop while browser is still open
	 * if browser was closed it will end the WebDriver
	 * While in the loop it will check for when it is time
	 * to advance the week, or time to refresh the browser
	 * and showing the most up to date schedule.
	 */
	public static void advance() {
		boolean stillOpen = stillOpen();
		while (stillOpen == true) {
			stillOpen = stillOpen();
			if (dayToChange() && timeToChange()) {
				zoomIn();
				wd.findElement(By.xpath("//button[@class='btn btn-secondary mr-2 navigate-today btn-panel btn-md']"))
						.click();
				zoomOut();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else if (now.getHour() % 6 == 0) {
				wd.navigate().refresh();
			}
		}
		
		if(stillOpen == false) {
			wd.quit();
		}
	}
}