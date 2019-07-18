import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Properties;

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
	static String dayToChange = "WEDNESDAY";
	static int cHour = 03;
	static int cMin = 00;
	static int cSec = 00;
	//static int rHour = 13;
	//static int rMin = 16;
	//static int rSec = 00;
	static boolean flag = true;
	static Robot robot;
	static LocalDateTime now;
	static WebDriverWait wait;

	/**
	 * Main entry point for the application
	 * @param args - no arguments needed for this class/method
	 */
	public static void main(String[] args) {
		initialize();
	}

	/**
	 * zoom out the browser view to 80%
	 */
	public static void zoomOut() {
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_SUBTRACT);
		robot.keyRelease(KeyEvent.VK_SUBTRACT);
		robot.keyPress(KeyEvent.VK_SUBTRACT);
		robot.keyRelease(KeyEvent.VK_SUBTRACT);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.mouseMove(500, 500);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseMove(0, 0);
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
	
	public static void scroll(int upOrDown) {
		pause();
		robot.keyPress(upOrDown);
		robot.keyRelease(upOrDown);
	}
	
	public static void pause() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
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

		Properties prop = new Properties();
		FileInputStream ip;
		
		try {
			robot = new Robot();
			ip= new FileInputStream("config.properties");
			prop.load(ip);
		} catch (IOException | AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String path = "./chromedriver_74.exe";
		System.setProperty("webdriver.chrome.driver", path);

		ChromeOptions options = new ChromeOptions();
		options.addArguments("disable-infobars");

		wd = new ChromeDriver(options);
		
		HttpCommandExecutor executor = (HttpCommandExecutor) wd.getCommandExecutor();
		url = executor.getAddressOfRemoteServer();
		session_id = wd.getSessionId();
		wd.get("https://appx.wheniwork.com/scheduler");
		
		wd.manage().window().maximize();
		wait = new WebDriverWait(wd, 20);
		wd.findElement(By.id("email")).sendKeys(prop.getProperty("username"));
		wd.findElement(By.id("password")).sendKeys(prop.getProperty("password"));
		wd.findElement(By.xpath("//button[@class='btn btn-primary btn-login btn-md btn-block']")).click();

		refreshOrAdvance();
	}

	/**
	 * collects the current date and time and determines
	 * if the day we expect to advance date has been reached
	 *
	 * @return if the day for the update is today returns true
	 */
	public static boolean dayToChange() {
		return now.getDayOfWeek().name().equals(dayToChange);
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
	
	public static boolean refresh() {
		int hour = now.getHour();
		int minute = now.getMinute();
		int second = now.getSecond();
		return hour % 3 == 0 && minute == 0 && second <= 30;
	}
	
	/*
	 * Move the week forward
	 */
	public static void advanceWeek() {
		zoomIn();
		wd.findElement(By.xpath("//button[@class='btn btn-secondary mr-2 navigate-today btn-panel btn-md']"))
				.click();
		zoomOut();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Refresh the browser window to get and changes to the schedule
	 */
	public static void getUpdate() {
		wd.navigate().refresh();
		zoomIn();
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//button[@class='btn btn-secondary toggle-fullscreen btn-icn btn-panel btn-md']"))).click();
		wd.findElement(By.xpath("//div[@class='hide-sidebar hint--right']")).click();
		zoomOut();
		flag = false;
	}

	/**
	 * Continuance loop while browser is still open
	 * if browser was closed it will end the WebDriver
	 * While in the loop it will check for when it is time
	 * to advance the week, or time to refresh the browser
	 * and showing the most up to date schedule.
	 */
	public static void refreshOrAdvance() {
		
		wait.until(ExpectedConditions.presenceOfElementLocated(
				By.xpath("//button[@class='btn btn-secondary toggle-fullscreen btn-icn btn-panel btn-md']"))).click();
		wd.findElement(By.xpath("//div[@class='hide-sidebar hint--right']")).click();
		zoomOut();
		
		boolean stillOpen = stillOpen();
		
		while (stillOpen == true) {
			stillOpen = stillOpen();
			now = LocalDateTime.now();
			if (refresh() || (dayToChange() && timeToChange())) {
				if (dayToChange() && timeToChange()) {
					advanceWeek();
				}
				if (refresh() && flag) {
					getUpdate();
				}
			} else {
				scroll(KeyEvent.VK_PAGE_DOWN);
				scroll(KeyEvent.VK_PAGE_UP);
			}
			if (!flag && now.getSecond() > 30) {
				flag = true;
			}
		}
		
		if(stillOpen == false) {
			wd.quit();
		}
	}
}