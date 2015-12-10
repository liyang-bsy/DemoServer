package net.lylab.vicp.web.utils;

import net.vicp.lylab.core.BaseObject;

public class Page extends BaseObject {

	int pageNo, pageSize, total, index;

	public Page() {
		setPageNoAndSize(1, 20);
	}

	public Page(int pageNo, int pageSize) {
		setPageNoAndSize(pageNo, pageSize);
		total = 0;
	}

	public void setPageNoAndSize(int pageNo, int pageSize) {
		if(pageNo < 1 || pageSize < 1)
			throw new IllegalArgumentException("Bad parameter page no/page size[" + pageNo + "/" + pageSize + "]");
		this.pageNo = pageNo;
		this.pageSize = pageSize;
		index = (pageNo - 1) * pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
