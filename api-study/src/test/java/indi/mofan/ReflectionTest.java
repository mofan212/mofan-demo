package indi.mofan;

import indi.mofan.dto.GrandchildDto;
import indi.mofan.dto.MainDto;
import indi.mofan.dto.One2OneDto;
import indi.mofan.dto.SubDto;
import indi.mofan.util.ReflectionUtil;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author mofan
 * @date 2023/1/12 16:29
 */
public class ReflectionTest {
    @Test
    @SneakyThrows
    public void testAssign4GrandchildDto() {
        GrandchildDto grandchildDto = new GrandchildDto();
        grandchildDto.setId(123456789L);

        SubDto subDto = new SubDto();
        subDto.setGrandchildDtoList(new ArrayList<>(Collections.singletonList(grandchildDto)));

        One2OneDto one2OneDto = new One2OneDto();
        one2OneDto.setNumber(10);

        MainDto mainDto = new MainDto();
        mainDto.setSubDtoList(new ArrayList<>(Collections.singletonList(subDto)));
        mainDto.setStr("main");
        mainDto.setOne2OneDto(one2OneDto);

        ReflectionUtil.doWith(Collections.singletonList(mainDto), new String[]{"subDtoList", "grandchildDtoList", "id"}, (field, object) -> {
            field.set(object, 987654321L);
        });
        System.out.println(mainDto);

        ReflectionUtil.doWith(Collections.singletonList(mainDto), new String[]{"str"}, (field, object) -> {
            field.set(object, "test");
        });
        System.out.println(mainDto);

        ReflectionUtil.doWith(Collections.singletonList(mainDto), new String[]{"one2OneDto", "number"}, (field, object) -> {
            field.set(object, 123);
        });
        System.out.println(mainDto);
    }
}
