package indi.mofan.strategy;

/**
 * @author mofan
 * @date 2022/10/26 15:47
 */
public enum StrategyEnum {
    /**
     * 1
     */
    ONE {
        @Override
        public String doSomething(String param) {
            return StrategyConstants.CONSTANT + param;
        }
    },

    /**
     * 2
     */
    TWO {
        @Override
        public String doSomething(String param) {
            return StrategyConstants.CONSTANT + param;
        }
    },

    /**
     * 3
     */
    THREE {
        @Override
        public String doSomething(String param) {
            return StrategyConstants.CONSTANT + param;
        }
    };

    public abstract String doSomething(String param);
}
