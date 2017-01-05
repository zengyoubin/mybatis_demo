package mybatis.bean;

import lombok.Data;

import java.util.List;


@Data
public class PageList<T> {

    public PageList() {
    }

    public PageList(int page, int perPage) {
        this(null, page, perPage, 0, 0);
    }

    public PageList(List<T> list, int page, int perPage, int pageCount, int rowCount) {
        if (page < 1 || perPage < 1)
            throw new IllegalArgumentException("invalid page or per page");
        this.list = list;
        this.page = page;
        this.perPage = perPage;
        this.pageCount = pageCount;
        this.rowCount = rowCount;
    }


    private int page;

    private int perPage;

    private int rowCount;

    private int pageCount;

    private List<T> list;

    public int getNext() {
        return page >= pageCount ? -1 : page + 1;
    }

    public int getPre() {
        return page == 1 ? -1 : page + 1;
    }
}
