package indi.constant;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author mofan
 * @date 2023/3/31 15:41
 */
public class JsonConstant {
    public static final String JSON;

    static {
        try {
            JSON = FileUtils.readFileToString(ResourceUtils.getFile("classpath:json-path.json"), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
