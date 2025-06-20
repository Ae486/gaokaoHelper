package com.gaokao.helper.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页结果类
 * 
 * @author PLeiA
 * @since 2024-06-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 从Spring Data Page对象创建PageResult
     */
    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getContent());
        result.setTotal(page.getTotalElements());
        result.setPage(page.getNumber() + 1); // Spring Data页码从0开始，转换为从1开始
        result.setSize(page.getSize());
        result.setPages(page.getTotalPages());
        result.setHasNext(page.hasNext());
        result.setHasPrevious(page.hasPrevious());
        return result;
    }

    /**
     * 从列表和分页信息创建PageResult
     */
    public static <T> PageResult<T> of(List<T> records, Integer page, Integer size, Long total) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPage(page);
        result.setSize(size);
        result.setPages((int) Math.ceil((double) total / size));
        result.setHasNext(page < result.getPages());
        result.setHasPrevious(page > 1);
        return result;
    }

    /**
     * 从列表创建简单的PageResult（用于不需要分页的场景）
     */
    public static <T> PageResult<T> of(List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal((long) records.size());
        result.setPage(1);
        result.setSize(records.size());
        result.setPages(1);
        result.setHasNext(false);
        result.setHasPrevious(false);
        return result;
    }
}
