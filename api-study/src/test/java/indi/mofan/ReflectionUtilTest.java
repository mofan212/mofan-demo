package indi.mofan;

import indi.mofan.dto.GrandchildDto;
import indi.mofan.dto.MainDto;
import indi.mofan.dto.One2OneDto;
import indi.mofan.dto.SubDto;
import indi.mofan.util.ReflectionUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author mofan
 * @date 2023/1/12 16:29
 */
public class ReflectionUtilTest {
    @Test
    @SneakyThrows
    public void testAssign4GrandchildDto() {
        GrandchildDto grandchildDto = new GrandchildDto();
        grandchildDto.setId(123456789L);

        GrandchildDto secondGrandchildDto = new GrandchildDto();
        secondGrandchildDto.setId(147258963L);

        GrandchildDto thirdGrandchildDto = new GrandchildDto();
        thirdGrandchildDto.setId(963852741L);

        SubDto subDto = new SubDto();
        subDto.setGrandchildDtoList(new ArrayList<>(Arrays.asList(grandchildDto, secondGrandchildDto)));

        SubDto anotherSubDto = new SubDto();
        anotherSubDto.setGrandchildDtoList(new ArrayList<>(Collections.singletonList(thirdGrandchildDto)));

        One2OneDto one2OneDto = new One2OneDto();
        one2OneDto.setNumber(10);

        One2OneDto anotherOne2OneDto = new One2OneDto();
        anotherOne2OneDto.setNumber(20);

        MainDto mainDto = new MainDto();
        mainDto.setSubDtoList(new ArrayList<>(Collections.singletonList(subDto)));
        mainDto.setStr("main");
        mainDto.setOne2OneDto(one2OneDto);

        MainDto anotherMainDto = new MainDto();
        anotherMainDto.setSubDtoList(new ArrayList<>(Collections.singletonList(anotherSubDto)));
        anotherMainDto.setStr("MAIN");
        anotherMainDto.setOne2OneDto(anotherOne2OneDto);

        List<MainDto> mainDtoList = Arrays.asList(mainDto, anotherMainDto);

        ReflectionUtil.doWith(mainDtoList, new String[]{"subDtoList", "grandchildDtoList", "id"}, (field, object) ->
                field.set(object, 987654321L)
        );
        mainDtoList.forEach(i -> i.getSubDtoList().forEach(j -> j.getGrandchildDtoList().forEach(k ->
                Assertions.assertEquals(987654321L, k.getId().longValue())
        )));

        ReflectionUtil.doWith(mainDtoList, new String[]{"str"}, (field, object) -> field.set(object, "test"));
        mainDtoList.forEach(i -> Assertions.assertEquals("test", i.getStr()));

        ReflectionUtil.doWith(mainDtoList, new String[]{"one2OneDto", "number"}, (field, object) -> field.set(object, 123));
        mainDtoList.forEach(i -> Assertions.assertEquals(123, i.getOne2OneDto().getNumber()));
    }
}
