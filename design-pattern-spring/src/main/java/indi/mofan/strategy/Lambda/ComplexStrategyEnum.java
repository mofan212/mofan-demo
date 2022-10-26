package indi.mofan.strategy.Lambda;

/**
 * @author mofan
 * @date 2022/10/26 15:57
 */
public enum ComplexStrategyEnum {
    /**
     * 1
     */
    FIRST("1", SimpleStrategyEnum.ONE),
    /**
     * 2
     */
    SECOND("2", SimpleStrategyEnum.TWO),
    /**
     * 3
     */
    THIRD("3", SimpleStrategyEnum.ONE);

    private final String code;
    private final SimpleStrategyEnum strategy;

    ComplexStrategyEnum(String code, SimpleStrategyEnum strategy) {
        this.code = code;
        this.strategy = strategy;
    }

    public String toDo() {
        return strategy.doSomething();
    }

    private enum SimpleStrategyEnum {
        /**
         * 1
         */
        ONE {
            @Override
            String doSomething() {
                return "ONE";
            }
        },

        /**
         * 2
         */
        TWO {
            @Override
            String doSomething() {
                return "TWO";
            }
        };

        abstract String doSomething();
    }
}
