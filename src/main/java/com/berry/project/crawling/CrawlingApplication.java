package com.berry.project.crawling;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ConditionalOnProperty(name = "crawl.enabled", havingValue = "true")
@SpringBootApplication
public class CrawlingApplication implements CommandLineRunner {

    @Autowired
    private CrawledReviewRepository reviewRepo;

    public static void main(String[] args) {
        SpringApplication.run(CrawlingApplication.class, args);
    }

    // 특수문자 및 이모지 제거 (한글, 영어, 숫자, 기본 구두점 허용)
    private static String removeSpecialChars(String text) {
        return text.replaceAll("[^\\uAC00-\\uD7A3a-zA-Z0-9 .,!?()~\\[\\]{}\"'/:\n\r\\-]", "");
    }

    @Override
    public void run(String... args) throws Exception {
        System.setProperty("webdriver.chrome.driver",
                "D:\\web_0226_kms\\html_workspace\\resources\\chromedriver-win64\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        File dir = new File("D:\\web_0226_kms\\html_workspace\\review");
        if (!dir.exists()) dir.mkdirs();
        File outFile = new File(dir, "reviews_ready_for_import.csv");

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(outFile, false),
                        Charset.forName("MS949")))) {

            // CSV 헤더 (테이블 컬럼명 순서)
            writer.write("\"user_id\",\"lodge_id\",\"reservation_id\",\"rating\",\"content\",\"ai_summary\",\"created_at\",\"reported_count\"");
            writer.newLine();

            String url = "https://www.yeogi.com/domestic-accommodations/6867?checkIn=2025-07-24&checkOut=2025-07-25&personal=2";
            driver.get(url);

            WebElement reviewTab = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector(".css-1294han")));
            reviewTab.click();
            Thread.sleep(500);

            int page = 1;
            while (true) {
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.css-xogpio")));
                List<WebElement> blocks = driver.findElements(By.cssSelector("div.css-xogpio"));
                System.out.printf("📦 [페이지 %d] 리뷰 블록: %d%n", page, blocks.size());

                for (WebElement b : blocks) {
                    try {
                        // 닉네임 (로깅용)
                        String nick = b.findElement(By.cssSelector("p.css-y9z2ll")).getText().trim();

                        // 별점 계산
                        List<WebElement> stars = b.findElements(By.cssSelector("span.css-92ber1 > svg"));
                        double score = 0;
                        for (WebElement svg : stars) {
                            WebElement path = svg.findElement(By.tagName("path"));
                            String opacity = path.getAttribute("fill-opacity");
                            String fill = path.getAttribute("fill");
                            if ("0.5".equals(opacity)) score += 0.5;
                            else if ("currentColor".equals(fill) || "current".equals(fill) || "1".equals(opacity)) score += 1;
                        }

                        // 리뷰 내용 정제
                        String raw = b.findElement(By.cssSelector("p.css-nyr29c")).getText();
                        String cleaned = removeSpecialChars(raw);
                        String content = cleaned.replace("\"", "\"\"")
                                .replace("\r\n", " ")
                                .replace("\n", " ")
                                .trim();

                        // 생성일시
                        String createdAt = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

                        // CSV에 저장
                        writer.write(String.format("%d,%d,NULL,%d,\"%s\",,\"%s\",0",
                                1,               // user_id (기본값)
                                2,            // lodge_id (변경 시 여기만 수정)
                                (int) Math.round(score),
                                content,
                                createdAt));
                        writer.newLine();

                    } catch (Exception ex) {
                        System.err.println("→ 파싱/저장 에러, 스킵: " + ex.getMessage());
                    }
                }

                // 다음 페이지 확인
                WebElement next = driver.findElement(
                        By.cssSelector("div.gc-pagination.css-tdk8um button[aria-label='다음']"));
                if (!next.isEnabled()) break;
                next.click();
                page++;
                Thread.sleep(800);
            }

            writer.flush();
            System.out.println("✅ CSV 저장 완료!");
        } finally {
            driver.quit();
        }
    }
}
