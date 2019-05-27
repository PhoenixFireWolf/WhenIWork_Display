import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WhenIWorkRefresher {

	public static void main(String[] args) {
		
		String path = "libs/chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", path);
		
		ChromeOptions options = new ChromeOptions(); 
		options.addArguments("disable-infobars");

		WebDriver wd = new ChromeDriver(options);
		
		WebDriverWait wait = new WebDriverWait(wd, 20);
		wd.get("https://appx.wheniwork.com/scheduler");
		wd.manage().window().maximize();
		wd.findElement(By.id("email")).sendKeys("PhoenixFireWolf@live.com");
		wd.findElement(By.id("password")).sendKeys("u#htJv$7!@a39h7");
		wd.findElement(By.xpath("//button[@class='btn btn-primary btn-login btn-md btn-block']")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
				"//button[@class='btn btn-secondary toggle-fullscreen btn-icn btn-panel btn-md']"))).click();
		wd.findElement(By.xpath("//div[@class='hide-sidebar hint--right']")).click();
		
		Robot robot;
		try {
			robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			
			robot.keyPress(KeyEvent.VK_MINUS);
			robot.keyRelease(KeyEvent.VK_MINUS);
			robot.keyPress(KeyEvent.VK_MINUS);
			robot.keyRelease(KeyEvent.VK_MINUS);
			
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		resetWhenIWork(wd, options);
		
	}
	
	public static String dayOfWeek() {
		Date now = new Date();
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E");
        return simpleDateformat.format(now);
	}
	
	public static String timeOfDay() {
		LocalDateTime now = LocalDateTime.now();
		int hour = now.getHour();
		int minute = now.getMinute();
		int second = now.getSecond();
		return String.format("%02d:%02d:%02d", hour, minute, second);
	}
	
	public static void resetWhenIWork(WebDriver wd, ChromeOptions options) {
		
		while(true) {
			System.out.println("dayOfWeek: " + dayOfWeek() + " timeOfDay: " + timeOfDay() + " is it true: " + (timeOfDay().equals("11:20:00") || timeOfDay().equals("11:21:00")));
			if(dayOfWeek().equals("Sat") && (timeOfDay().equals("3:00:00"))) {
				
				wd.quit();
				wd = new ChromeDriver(options);
				
				WebDriverWait wait = new WebDriverWait(wd, 20);
				wd.get("https://appx.wheniwork.com/scheduler");
				wd.manage().window().maximize();
				wd.findElement(By.id("email")).sendKeys("PhoenixFireWolf@live.com");
				wd.findElement(By.id("password")).sendKeys("u#htJv$7!@a39h7");
				wd.findElement(By.xpath("//button[@class='btn btn-primary btn-login btn-md btn-block']")).click();
				wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
						"//button[@class='btn btn-secondary toggle-fullscreen btn-icn btn-panel btn-md']"))).click();
				wd.findElement(By.xpath("//div[@class='hide-sidebar hint--right']")).click();
				
				Robot robot;
				try {
					robot = new Robot();
					robot.keyPress(KeyEvent.VK_CONTROL);
					
					robot.keyPress(KeyEvent.VK_MINUS);
					robot.keyRelease(KeyEvent.VK_MINUS);
					robot.keyPress(KeyEvent.VK_MINUS);
					robot.keyRelease(KeyEvent.VK_MINUS);
					
					robot.keyRelease(KeyEvent.VK_CONTROL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}