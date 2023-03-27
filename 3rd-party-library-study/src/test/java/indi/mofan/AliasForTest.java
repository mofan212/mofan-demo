package indi.mofan;

import indi.mofan.annotation.GroovyOrXmlTestConfig;
import indi.mofan.annotation.MyContextConfiguration;
import indi.mofan.annotation.MyTestConfig;
import indi.mofan.annotation.XmlTestConfig;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Arrays;

/**
 * @author mofan
 * @date 2023/3/27 11:20
 */
public class AliasForTest implements WithAssertions {

    private static final String PACKAGE = "indi.mofan";

    @MyContextConfiguration(PACKAGE)
    static class Class1 {
    }

    @Test
    public void testExplicitAliasesWithinAnAnnotation() {
        MyContextConfiguration annotation = AnnotationUtils.getAnnotation(Class1.class, MyContextConfiguration.class);
        assertThat(annotation)
                .isNotNull()
                .extracting(MyContextConfiguration::locations, as(InstanceOfAssertFactories.array(String[].class)))
                .contains(PACKAGE);
        assertThat(annotation)
                .isNotNull()
                .extracting(MyContextConfiguration::value, as(InstanceOfAssertFactories.array(String[].class)))
                .contains(PACKAGE);
    }

    @XmlTestConfig(xmlFiles = PACKAGE)
    static class Class2 {
    }

    @Test
    public void testExplicitAliasForAttributeInMetaAnnotation() {
        XmlTestConfig annotation = AnnotationUtils.getAnnotation(Class2.class, XmlTestConfig.class);
        assertThat(annotation)
                .isNotNull()
                .extracting(XmlTestConfig::xmlFiles, as(InstanceOfAssertFactories.array(String[].class)))
                .contains(PACKAGE);
        MyContextConfiguration metaAnnotation = AnnotationUtils.getAnnotation(Class2.class, MyContextConfiguration.class);
        assertThat(metaAnnotation).isNotNull().extracting(MyContextConfiguration::locations, as(InstanceOfAssertFactories.array(String[].class)))
                .isEmpty();
        metaAnnotation = AnnotatedElementUtils.findMergedAnnotation(Class2.class, MyContextConfiguration.class);
        assertThat(metaAnnotation).isNotNull().extracting(MyContextConfiguration::locations, as(InstanceOfAssertFactories.array(String[].class)))
                .isNotEmpty().contains(PACKAGE);
    }

    @MyTestConfig(PACKAGE)
    static class Class3 {}

    @Test
    public void testImplicitAliasesWithinAnAnnotation() {
        MyTestConfig annotation = AnnotationUtils.getAnnotation(Class3.class, MyTestConfig.class);
        assertThat(annotation).isNotNull()
                .extracting(MyTestConfig::value, as(InstanceOfAssertFactories.array(String[].class)))
                .containsOnly(PACKAGE);
        assertThat(annotation).isNotNull()
                .extracting(MyTestConfig::groovyScripts, MyTestConfig::xmlFiles)
                .containsAll(Arrays.asList(new String[]{PACKAGE}, (Object) new String[]{PACKAGE}));

        MyContextConfiguration mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(Class3.class, MyContextConfiguration.class);
        assertThat(mergedAnnotation).isNotNull()
                .extracting(MyContextConfiguration::locations, as(InstanceOfAssertFactories.array(String[].class)))
                .containsOnly(PACKAGE);
    }

    @MyContextConfiguration
    @GroovyOrXmlTestConfig(groovy = PACKAGE)
    static class Class4 {
    }

    @Test
    public void testTransitiveImplicitAliasesWithinAnAnnotation() {
        GroovyOrXmlTestConfig groovyOrXmlTestConfig = AnnotationUtils.getAnnotation(Class4.class, GroovyOrXmlTestConfig.class);
        assertThat(groovyOrXmlTestConfig).isNotNull()
                .extracting(GroovyOrXmlTestConfig::groovy, as(InstanceOfAssertFactories.array(String[].class)))
                .containsOnly(PACKAGE);

        MyTestConfig myTestConfig = AnnotatedElementUtils.findMergedAnnotation(Class4.class, MyTestConfig.class);
        assertThat(myTestConfig).isNotNull()
                .extracting(MyTestConfig::groovyScripts, MyTestConfig::value, MyTestConfig::xmlFiles)
                .containsAll(Arrays.asList(new String[]{PACKAGE}, new String[]{PACKAGE}, new String[]{PACKAGE}));
    }
}
