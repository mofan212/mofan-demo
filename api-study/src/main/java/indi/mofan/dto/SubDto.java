package indi.mofan.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author mofan
 * @date 2023/1/12 16:30
 */
@Setter
@Getter
@ToString
public class SubDto {
    List<GrandchildDto> grandchildDtoList;
}
