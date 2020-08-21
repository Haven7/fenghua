package haven.fenghua.vo;

import lombok.Data;

@Data
public class ResultVo {

    private Integer code;
    private String message;
    private boolean success;
    private Object data;
}
