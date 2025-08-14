package indi.mofan.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mofan
 * @date 2021/7/16 15:35
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Transaction {
    private Trader trader;
    private int year;
    private int value;
}
