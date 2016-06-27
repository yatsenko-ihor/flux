package org.eclipse.flux.core.woot;

public class WootOperations {

	public enum Operations {
		INS, DEL
	}

	private Operations op;
	private WChar wchar;
	private int from;

	public WootOperations(Operations op, WChar wchar, int site) {
		this.op = op;
		this.wchar = wchar;
		this.from = site;
	}

	public Operations getOp() {
		return op;
	}

	public void setOp(Operations op) {
		this.op = op;
	}

	public WChar getWchar() {
		return wchar;
	}

	public void setWchar(WChar wchar) {
		this.wchar = wchar;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

}
