package org.eclipse.flux.core.woot;

public class ID {

	private int site;
	private int clock;
	private boolean start;
	private boolean end;

	public ID(int site, int clock) {
		this.site = site;
		this.clock = clock;
	}

	public ID(int site, int clock, boolean start, boolean end) {
		super();
		this.site = site;
		this.clock = clock;
		this.start = start;
		this.end = end;
	}

	public ID(boolean start, boolean end) {
		this.start = start;
		this.end = end;
	}

	public int getSite() {
		return site;
	}

	public void setSite(int site) {
		this.site = site;
	}

	public int getClock() {
		return clock;
	}

	public void setClock(int clock) {
		this.clock = clock;
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (end ? 1231 : 1237);
		result = prime * result + clock;
		result = prime * result + site;
		result = prime * result + (start ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ID other = (ID) obj;
		if (end != other.end)
			return false;
		if (clock != other.clock)
			return false;
		if (site != other.site)
			return false;
		if (start != other.start)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ID [site=" + site + ", clock=" + clock + ", start=" + start + ", end=" + end + "]";
	}

}
