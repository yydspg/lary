package cn.lary.core.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public static <T> PageResponse<T> ok(long pageIndex,long pageSize,long total,Collection<T> data) {
        PageResponse<T> response = new PageResponse<T>();
        response.setSuccess(true);
        response.setData(data);
        response.setPageIndex(pageIndex);
        response.setPageSize(pageSize);
        response.setTotalCount(total);
        return response;
    }

    public static <T> PageResponse<T> fail(Integer errCode, String errMessage) {
        PageResponse<T> response = new PageResponse<T>();
        response.setSuccess(false);
        response.setErrCode(errCode);
        response.setErrMessage(errMessage);
        return response;
    }



}

