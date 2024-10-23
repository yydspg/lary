package cn.lary.common.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * Response with batch page record to return,
 * usually use in page query
 */
@Getter
@Setter
public class PageResponse<T> extends Response<T> {


    private long totalCount = 0;

    private long pageSize = 1;

    private long pageIndex = 1;

    private Collection<T> data;

    public static <T> PageResponse<T> ok(long pageIndex,long pageSize,Collection<T> data) {
        PageResponse<T> response = new PageResponse<T>();
        response.setSuccess(true);
        response.setData(data);
        response.setPageIndex(pageIndex);
        response.setPageSize(pageSize);
        return response;
    }

    public static <T> PageResponse<T> fail(int errCode, String errMessage) {
        PageResponse<T> response = new PageResponse<T>();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }



}

