package cn.lary.core.dto;

import java.io.Serial;
import java.io.Serializable;

//Data Transfer object, including Command, Query and Response,
public abstract class DTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
