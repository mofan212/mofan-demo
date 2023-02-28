package indi.mofan;

import cn.hutool.core.io.FileUtil;
import com.jayway.jsonpath.Configuration;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * @author mofan
 * @date 2023/2/28 15:01
 */
public class JsonPathTest {
    private static final String JSON = FileUtil.readString("json-path.json", Charset.defaultCharset());

    private static final Object DOCUMENT = Configuration.defaultConfiguration().jsonProvider().parse(JSON);

    @Test
    public void testPathExample() {

    }
}
