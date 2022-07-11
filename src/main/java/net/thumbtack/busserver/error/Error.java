package net.thumbtack.busserver.error;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Error {

    private String errorCode;
    private String field;
    private String message;
    

    public Error(String errorCode, String field, String message) {
        this.errorCode = errorCode;
        this.field = field;
        this.message = message;
    }

}
