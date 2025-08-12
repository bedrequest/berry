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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
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

        // 크롤링한 리뷰 총 개수와 최대치 정의
        int totalCount = 0;
        final int MAX_COUNT = 100;

        // 기본 유저 정보 (필수 컬럼)
        final int DEFAULT_USER_ID = 1;
        final String DEFAULT_USER_EMAIL = "berry@user.com";

        // 이미 파일이 있으면 이어쓰기, 없으면 새로 생성
        boolean append = outFile.exists();

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(outFile, append),
                        Charset.forName("MS949")))) {

            // CSV 헤더: 파일이 새로 생성될 때만 작성
            if (!append) {
                writer.write("\"user_email\",\"user_id\",\"lodge_id\",\"reservation_id\",\"rating\",\"content\",\"ai_summary\",\"created_at\",\"reported_count\"");
                writer.newLine();
            }

            String url = "https://www.yeogi.com/domestic-accommodations/13218?checkIn=2025-08-08&checkOut=2025-08-09&personal=2";
            driver.get(url);

            WebElement reviewTab = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector(".css-1294han")));
            reviewTab.click();
            Thread.sleep(500);

            int page = 1;
            while (true) {
                wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.css-xogpio"))
                );
                List<WebElement> blocks = driver.findElements(By.cssSelector("div.css-xogpio"));
                System.out.printf("📦 [페이지 %d] 리뷰 블록: %d%n", page, blocks.size());

                // --- 내부 루프: 리뷰 블록 순회 ---
                for (WebElement b : blocks) {
                    // ① MAX_COUNT 도달 시 즉시 중단
                    if (totalCount >= MAX_COUNT) break;

                    try {
                        // 별점 계산
                        List<WebElement> stars = b.findElements(By.cssSelector("span.css-92ber1 > svg"));
                        double score = 0;
                        for (WebElement svg : stars) {
                            WebElement path = svg.findElement(By.tagName("path"));
                            String opacity = path.getAttribute("fill-opacity");
                            String fill = path.getAttribute("fill");
                            if ("0.5".equals(opacity)) score += 0.5;
                            else if ("currentColor".equals(fill)
                                    || "current".equals(fill)
                                    || "1".equals(opacity)) score += 1;
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

                        // CSV에 저장: user_email, user_id 포함
                        writer.write(String.format(
                                "\"%s\",%d,%d,NULL,%d,\"%s\",,\"%s\",0",
                                DEFAULT_USER_EMAIL,         // user_email
                                DEFAULT_USER_ID,            // user_id
                                304,                          // lodge_id (필요에 따라 변경)
                                (int) Math.round(score),    // rating
                                content,                    // content
                                createdAt                   // created_at
                        ));
                        writer.newLine();

                        totalCount++;  // ② 저장할 때마다 카운터++
                    } catch (Exception ex) {
                        System.err.println("→ 파싱/저장 에러, 스킵: " + ex.getMessage());
                    }
                }

                // ③ MAX_COUNT에 도달하면 전체 크롤링 중단
                if (totalCount >= MAX_COUNT) {
                    System.out.println("🛑 리뷰 " + MAX_COUNT + "개 크롤링 완료, 중단합니다.");
                    break;
                }

                // 다음 페이지로 이동
                WebElement nextBtn = driver.findElement(
                        By.cssSelector("div.gc-pagination.css-tdk8um button[aria-label='다음']")
                );
                if (!nextBtn.isEnabled()) break;
                nextBtn.click();
                page++;
                Thread.sleep(800);
            }

            writer.flush();
            System.out.println("✅ CSV 저장 완료! 총 리뷰 개수: " + totalCount);
        } finally {
            driver.quit();
        }
    }
}
