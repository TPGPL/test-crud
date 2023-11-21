package pl.edu.pw.mwotest.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ServiceResponse<T> {
    private T data;
    private String message;
    private boolean success;
}
