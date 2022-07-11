package net.thumbtack.busserver.exception;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.busserver.error.Error;
import net.thumbtack.busserver.error.ErrorCode;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerException extends RuntimeException {
    
    private Error error;

    public ServerException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.error = new Error(errorCode.toString(), errorCode.getField(), errorCode.getMessage());
    }

}
