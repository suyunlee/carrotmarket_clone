package oreumi.group2.carrotClone.error;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(Exception.class)
    public String handleAny(Exception ex,
                            HttpServletRequest request,
                            Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        System.out.println(status);

        model.addAttribute("message", "오류가 발생했습니다. " + ex.getMessage());
        return "error/error-page";
    }
}