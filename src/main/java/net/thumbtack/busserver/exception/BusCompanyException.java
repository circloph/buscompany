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
public class BusCompanyException extends Exception {
    
    private Error error;

    public BusCompanyException(ErrorCode errorCode) {
        error = new Error(errorCode.toString(), errorCode.getField(), errorCode.getMessage());
    }

}
