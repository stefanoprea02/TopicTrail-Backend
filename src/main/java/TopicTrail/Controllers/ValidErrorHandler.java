package TopicTrail.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ValidErrorHandler {
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, ArrayList<String>>> handleException(WebExchangeBindException e){
        Map<String, ArrayList<String>> cause = new HashMap<>();

        List<FieldError> errors = e.getFieldErrors();

        for(FieldError error : errors){
            if(cause.get(error.getField()) != null){
                ArrayList<String> list = cause.get(error.getField());
                list.add(error.getDefaultMessage());
                cause.put(error.getField(), list);
            }else{
                ArrayList<String> list = new ArrayList<>();
                list.add(error.getDefaultMessage());
                cause.put(error.getField(), list);
            }
        }

        return ResponseEntity.badRequest().body(cause);
    }
}
