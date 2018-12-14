package jobicade.betterhud.util;

import java.util.List;

public class Paginator<T> {
	private List<T> data;

	private int pageOffset;
	private int pageSize;

	public void setData(List<T> data) {
		this.data = data;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getPageIndex() {
		return pageOffset / pageSize;
	}

	public int getPageCount() {
		return (data.size() + pageSize - 1) / pageSize;
	}

	public int getItemCount() {
		return data.size();
	}

	public List<T> getData() {
		return data;
	}

	public List<T> getPage() {
		return data.subList(getLow(), getHigh());
	}

	public int getLow() {
		return getPageIndex() * pageSize;
	}

	public int getHigh() {
		return Math.min(data.size(), (getPageIndex() + 1) * pageSize);
	}

	public void setPage(int page) {
		pageOffset = page * pageSize;
	}

	public boolean hasPrevious() {
		return getPageIndex() > 0;
	}

	public boolean hasNext() {
		return getPageIndex() < getPageCount() - 1;
	}

	public boolean previousPage() {
		if(hasPrevious()) {
			setPage(getPageIndex() - 1);
			return true;
		}
		return false;
	}

	public boolean nextPage() {
		if(hasNext()) {
			setPage(getPageIndex() + 1);
			return true;
		}
		return false;
	}
}
