package indi.mofan;

import lombok.SneakyThrows;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author mofan
 * @date 2023/6/12 20:13
 */
public class HttpClientTest implements WithAssertions {
    @Test
    @SneakyThrows
    public void testSimplyUse() {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI("https://www.baidu.com")).build();
            // 输出后乱码，怎么解决呢？
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // System.out.println(response.body());
            assertThat(response.body()).isNotEmpty();
        }
    }

    @Test
    @SneakyThrows
    @Disabled("图片网址可能已失效")
    public void testGetPic() {
        // 创建目标目录
        File dir = new File("./target/images/");
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            if (!mkdirs) {
                Assertions.fail();
            }
        }

        String prefix = "<a href=\"\" id=\"img\"><img src=\"";
        String suffix = "\" data-pic=";
        // 尝试 20 次
        int count = 20;
        for (int i = 0; i < 10; i++) {
            int random = ThreadLocalRandom.current().nextInt(-500, 500);
            // 以 https://pic.netbian.com/tupian/30759.html 为例，结尾都是数字
            try (HttpClient client = HttpClient.newHttpClient();) {
                int number = 30759 + random;
                HttpRequest request = HttpRequest.newBuilder(new URI("https://pic.netbian.com/tupian/" + number + ".html")).build();
                // 获取 byte 数组的响应，防止输出到控制台乱码
                HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
                if (response.statusCode() != 200) {
                    i--;
                    continue;
                }
                // 获取网页信息
                String html = new String(response.body(), "gbk");

                // 获取图片链接
                String link = html.substring(html.indexOf(prefix) + prefix.length());
                link = link.substring(0, link.indexOf(suffix));

                HttpRequest imageReq = HttpRequest.newBuilder(new URI("https://pic.netbian.com/" + link)).build();
                HttpResponse<InputStream> imageResp = client.send(imageReq, HttpResponse.BodyHandlers.ofInputStream());
                // 获取图片输入流
                InputStream inputStream = imageResp.body();
                File img = new File(dir.getAbsolutePath() + File.separatorChar + number + ".jpg");
                if (img.exists()) {
                    i--;
                    if (count <= 0) {
                        return;
                    }
                    count--;
                    continue;
                }
                FileOutputStream fos = new FileOutputStream(img);
                try (inputStream; fos) {
                    byte[] bytes = new byte[1024];
                    int len;
                    while ((len = inputStream.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                    }
                } catch (IOException e) {
                    Assertions.fail();
                }
            }
        }
    }
}
