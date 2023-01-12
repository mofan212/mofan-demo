package indi.mofan.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author mofan
 * @date 2023/1/12 16:30
 */
@Getter
@Setter
@ToString
public class MainDto {
    private List<SubDto> subDtoList;

    private String str;

    private One2OneDto one2OneDto;
}
