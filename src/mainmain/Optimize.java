package mainmain;

import static mainmain.Backup.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;

public class Optimize {

 public int delClass(String className01) {
  JavascriptExecutor js_UI청소 = (JavascriptExecutor) driver;
  js_UI청소.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
    driver.findElement(By.className(className01)));
  return 0;
 }

 public int delId(String idName01) {
  JavascriptExecutor js_UI청소 = (JavascriptExecutor) driver;
  js_UI청소.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
    driver.findElement(By.id(idName01)));
  return 0;
 }

 public void delay(int millis) {

  try {
    Thread.sleep(millis);
  } catch (Exception e) {
    System.out.println("아니 여기서 왜 에러가 나지");
  }
 }
}
