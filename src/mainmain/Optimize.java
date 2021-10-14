package mainmain;

import static mainmain.Backup.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Optimize {

  public int delClass(String className01) {
    try {
      JavascriptExecutor js_UI청소 = (JavascriptExecutor) driver;
      js_UI청소.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
          driver.findElement(By.className(className01)));
    } catch (Exception e) {
      return -1;
    }
    return 0;
  }

  public int delId(String idName01) {
    try {
      JavascriptExecutor js_UI청소 = (JavascriptExecutor) driver;
      js_UI청소.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
          driver.findElement(By.id(idName01)));
    } catch (Exception e) {
      return -1;
    }
    return 0;
  }

  public void delay(int millis) {

    try {
      Thread.sleep(millis);
    } catch (Exception e) {
      System.out.println("아니 여기서 왜 에러가 나지");
    }
  }

  public String escapeWindowsFilename(String string) {
    return string.replace("\"", "＂").replace("\\", "＼").replace(":", "：").replace("\"", "＂").replace("/", "／")
        .replace("|", "｜").replace("*", "＊").replace("?", "？").replace("<", "＜").replace(">", "＞");
  }

  public String getTagValue(String tag, Element eElement) {
    NodeList nlList = ((Element) eElement).getElementsByTagName(tag).item(0).getChildNodes();
    Node nValue = (Node) nlList.item(0);
    if (nValue == null)
      return null;
    return nValue.getNodeValue();
  }

  public String getMobileURL(String url) {

    if (url.contains(".tistory.com/")) {
      return url.replace(".tistory.com/", ".tistory.com/m/");
    }
    if (url.contains(".com/")) {
      return url.replace(".com/", ".com/m/");
    }
    if (url.contains(".kr/")) {
      return url.replace(".kr/", ".kr/m/");
    }
    if (url.contains(".net/")) {
      return url.replace(".net/", ".net/m/");
    }
    // Log log = new log(Log.log);
    return url;
  }

  public String getPostID(String url) {

    return url.split("/")[url.split("/").length - 1];
  }
}
