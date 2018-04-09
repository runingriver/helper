package org.helper.common;

/**
 * 使用枚举表述常量数据字段
 *
 * 在程序中使用1234等表述某些内容时,不能清晰地表明其作用,采用枚举,可读性提高
 *
 * -1:未执行,0:执行失败,1:执行中,2:成功
 */
public enum ExecuteStateEnum {
    NO_EXECUTE(-1,"未执行"),
    FAILED(0,"执行失败"),
    IN_EXECUTE(1,"执行中"),
    SUCCESS(2,"成功");

    private int state;
    private String stateInfo;

    ExecuteStateEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static ExecuteStateEnum stateOf(int index) {
        for (ExecuteStateEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }
}
