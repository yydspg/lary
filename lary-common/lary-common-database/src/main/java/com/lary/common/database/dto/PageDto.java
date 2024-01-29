package com.lary.common.database.dto;

import com.lary.common.core.utils.PrincipalUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @author paul 2024/1/1
 */
@Slf4j
@Schema(description = "分页对象")
public class PageDto {
    public static final String ASC = "ASC";

    public static final String DESC = "DESC";

    /**
     * 最大分页大小,防止传入过大数,导致服务器内存溢出宕机
     */
    public static final Integer MAX_PAGE_SIZE = 100;

    /**
     * 当前页
     */
    @NotNull(message = "pageNum can not be null")
    @Schema(description = "current page" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNum;

    @NotNull(message = "pageSize can not be null")
    @Schema(description = "page size" , requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;

    @Schema(description = "sorted field array,split by ',' ")
    private String[] sortedColumns;

    @Schema(description = "Sort Field Method，split by ',', ASC ;DESC " )
    private String[] orders;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum =(pageNum < 0)? 1: pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if(pageSize<0){
            this.pageSize = 10;
        }
        this.pageSize = (pageSize > MAX_PAGE_SIZE) ? MAX_PAGE_SIZE:pageSize;
    }

    public String[] getSortedColumns() {
        return sortedColumns;
    }

    public void setSortedColumns(String[] sortedColumns) {
        this.sortedColumns = sortedColumns;
    }

    public String[] getOrders() {
        return orders;
    }

    public void setOrders(String[] orders) {
        this.orders = orders;
    }
    public String order(String[] sortedColumns,String[] orders){
        if (sortedColumns == null || sortedColumns.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int x = 0;
        for (String s : sortedColumns) {
            if (!PrincipalUtil.isField(s)) {
                log.error("illegal sorted field:{}",s);
                return null;
            }
            String order;
            if (orders != null && orders.length > x) {
                order = orders[x].toUpperCase();
                if (!(order.equals(ASC) || order.equals(DESC))) {
                    log.error("illegal sorted strategy:{}" +order);
                    return null;
                }
            }else {
                order = ASC;
            }
            String h = humpToUnderline(s);
            sb.append("`").append(h).append("`").append(order);
        }
        sb.deleteCharAt(sb.length());
        return sb.toString();
    }
    public static String humpToUnderline(String v){
        StringBuilder sb = new StringBuilder();
        char[] chars = v.toCharArray();
        for (char c : chars) {
            if (!Character.isUpperCase(c)) {
                sb.append(c);
            }
            sb.append("_").append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        return "PageDto{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", sortedColumns=" + Arrays.toString(sortedColumns) +
                ", orders=" + Arrays.toString(orders) +
                '}';
    }
}
