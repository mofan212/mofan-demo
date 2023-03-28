package indi.mofan;

import indi.mofan.annotation.GroovyOrXmlTestConfig;
import indi.mofan.annotation.MyAnnotation;
import indi.mofan.annotation.MyContextConfiguration;
import indi.mofan.annotation.MyMetaAnnotation;
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
    static class Class3 {
    }

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

    @GroovyOrXmlTestConfig(groovy = PACKAGE)
    static class Class4 {
    }

    @GroovyOrXmlTestConfig(xml = PACKAGE)
    static class Class5 {
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

        MyContextConfiguration myContextConfiguration = AnnotatedElementUtils.findMergedAnnotation(Class4.class, MyContextConfiguration.class);
        assertThat(myContextConfiguration).isNotNull()
                .extracting(MyContextConfiguration::locations, as(InstanceOfAssertFactories.array(String[].class)))
                .containsOnly(PACKAGE);

        myContextConfiguration = AnnotatedElementUtils.findMergedAnnotation(Class5.class, MyContextConfiguration.class);
        assertThat(myContextConfiguration).isNotNull()
                .extracting(MyContextConfiguration::locations, as(InstanceOfAssertFactories.array(String[].class)))
                .containsOnly(PACKAGE);
    }

    @MyAnnotation
    static class Class6 {
    }

    @MyAnnotation(myValue = "mofan", mySort = 1)
    static class Class7 {
    }

    @Test
    public void testMyAnnotation() {
        MyMetaAnnotation metaAnnotation = AnnotatedElementUtils.getMergedAnnotation(Class6.class, MyMetaAnnotation.class);
        assertThat(metaAnnotation).isNotNull()
                .extracting(MyMetaAnnotation::value, MyMetaAnnotation::sort)
                .containsExactly("testValue", 0);

        metaAnnotation = AnnotatedElementUtils.findMergedAnnotation(Class7.class, MyMetaAnnotation.class);
        assertThat(metaAnnotation).isNotNull()
                .extracting(MyMetaAnnotation::value, MyMetaAnnotation::sort)
                .containsExactly("mofan", 1);
    }
}
