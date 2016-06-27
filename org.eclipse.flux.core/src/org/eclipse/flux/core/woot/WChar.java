package org.eclipse.flux.core.woot;

/**
 * Woot Char
 * 
 * @author Ihor Yatsenko
 *
 */
public class WChar {

	private char sybmbol;
	private boolean visible;
	private ID prevId;
	private ID nextId;
	private ID id;

	public WChar(ID id, char sybmbol, boolean visible, ID prev, ID next) {
		this.id = id;
		this.sybmbol = sybmbol;
		this.visible = visible;
		this.prevId = prev;
		this.nextId = next;
	}

	public WChar(ID id) {
		this.id = id;
		this.visible = false;
		this.sybmbol = Character.MIN_VALUE;
	}

	public char getSybmbol() {
		return sybmbol;
	}

	public void setSybmbol(char sybmbol) {
		this.sybmbol = sybmbol;
	}

	public ID getPrevId() {
		return prevId;
	}

	public void setPrevId(ID prevId) {
		this.prevId = prevId;
	}

	public void setNextId(ID nextId) {
		this.nextId = nextId;
	}

	public ID getNextId() {
		return nextId;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((nextId == null) ? 0 : nextId.hashCode());
		result = prime * result + ((prevId == null) ? 0 : prevId.hashCode());
		result = prime * result + sybmbol;
		result = prime * result + (visible ? 1231 : 1237);
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
		WChar other = (WChar) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (nextId == null) {
			if (other.nextId != null)
				return false;
		} else if (!nextId.equals(other.nextId))
			return false;
		if (prevId == null) {
			if (other.prevId != null)
				return false;
		} else if (!prevId.equals(other.prevId))
			return false;
		if (sybmbol != other.sybmbol)
			return false;
		if (visible != other.visible)
			return false;
		return true;
	}

	/*
	 * @Override public String toString() { return "WChar [sybmbol=" + sybmbol +
	 * ", visible=" + visible + ", prevId=" + prevId + ", nextId=" + nextId +
	 * ", id=" + id + "]"; }
	 */

	@Override
	public String toString() {
		return "" + sybmbol;
	}

}
