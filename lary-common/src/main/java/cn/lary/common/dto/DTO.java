package cn.lary.common.dto;

import java.io.Serial;
import java.io.Serializable;

//Data TransferDTO object, including Command, Query and Response,
public abstract class DTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
