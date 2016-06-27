package org.eclipse.flux.core.woot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.flux.core.woot.WootOperations.Operations;

public class WString {

	private WChar chars[];
	private WChar start;
	private WChar end;
	private int numSite;
	private int clock;
	private List<WootOperations> operations;

	public WString(int numSite, int initClockValue) {
		System.out.println("Enter to constructor of WString, just initialized start end end");
		this.numSite = numSite;
		this.clock = initClockValue;
		start = new WChar(new ID(numSite, initClockValue, true, false));
		end = new WChar(new ID(numSite, initClockValue, false, true));
		chars = new WChar[0];
		operations = new ArrayList<WootOperations>();
	}

	public WString(int numSite, int initClockValue, WChar[] chars) {
		this(numSite, initClockValue);
		this.chars = chars;
	}

	public WChar[] getChars() {
		return chars;
	}

	public void setChars(WChar[] chars) {
		this.chars = chars;
	}

	public int length() {
		return chars.length;
	}

	public int getPos(WChar pw) {
		System.out.println("Enter to getPos method: WChar is - " + pw.toString());
		for (int i = 0; i < chars.length; i++) {
			if (chars[i].getPrevId().equals(pw.getPrevId()) && chars[i].getNextId().equals(pw.getNextId())) {
				System.out.println("getPos return position: " + i);
				return i;
			}
		}
		System.out.println("getPos return position: 0");
		return 0;
	}

	public void insert(WChar wchar, int pos) {
		System.out.println("Enter to insert method");

		WChar[] result = new WChar[chars.length + 1];

		WChar[] leftPart = Arrays.copyOfRange(chars, 0, pos + 1);
		WChar[] rightPart = Arrays.copyOfRange(chars, pos, chars.length);

		for (int i = 0; i < leftPart.length; i++) {
			result[i] = leftPart[i];
		}
		result[pos] = wchar;
		for (int i = 0; i < rightPart.length; i++) {
			result[pos + i + 1] = rightPart[i];
		}
		chars = result;
	}

	public WString subseq(WChar cp, WChar cn) {
		System.out.println("Enter to subseq method: cp - " + cp.toString() + ", cn - " + cn.toString());
		WString result = new WString(numSite, clock);
		int start = 0;
		int end = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i].equals(cp))
				start = i;
			if (chars[i].equals(cn))
				end = i;
		}
		if (end > start) {
			result.setChars(Arrays.copyOfRange(chars, start + 1, end));
		}

		return result;
	}

	public boolean contains(WChar wchar) {
		System.out.println("Enter to contains method");
		for (WChar wcharItem : chars) {
			if (wcharItem.getSybmbol() == wchar.getSybmbol()) {
				return true;
			}
		}
		return false;
	}

	public String value() {
		StringBuilder result = new StringBuilder();
		for (WChar wcharItem : chars) {
			if (wcharItem != null && wcharItem.isVisible())
				result.append(wcharItem.getSybmbol());
		}
		return result.toString();
	}

	public WChar ithVisible(int pos) {
		System.out.println("Enter to ithVisible method");
		for (int i = 0; i < chars.length; i++) {
			if (chars[i].isVisible() == true && i == pos)
				return chars[pos];
		}

		return null;
	}

	public WChar getPrevWChar(ID id) {
		System.out.println("Enter to getPrevWChar method");
		for (WChar wChar : chars) {
			if (wChar.getPrevId().equals(id) && wChar.getNextId().equals(id)) {
				return wChar;
			}
		}
		return new WChar(new ID(true, false));
	}

	public WChar getWChar(ID id) {
		System.out.println("Enter tot getNextWChar method");
		for (WChar wChar : chars) {
			if (wChar.getId().equals(id)) {
				return wChar;
			}
		}
		return new WChar(new ID(false, true));
	}

	public void generateIns(int pos, char symbol) {
		System.out.println("Enter to generateIns method");
		
		ID prevId = getPrevOf(pos -1);
		ID nextId = getNextOf(pos);
		
		WChar cp = getWChar(prevId);
		WChar cn = getWChar(nextId);

		ID id = genID();

		WChar wchar = new WChar(id, symbol, true, prevId, nextId);
		integrateIns(wchar, cp, cn);
		WootOperations op = new WootOperations(WootOperations.Operations.INS, wchar, numSite);
		broadcast(op, wchar);
	}

	private ID getNextOf(int pos) {
		if (pos >= value().length()) {
			return getEnd().getId();
		} else {
			return ithVisible(pos).getId();
		}
	}

	private ID getPrevOf(int pos) {
		if (value().length() == 0) {
			return getStart().getId();
		} else {
			return ithVisible(pos).getId();
		}
	}

	public static void broadcast(WootOperations op, WChar wchar) {
	}

	public void integrateIns(WChar wchar, WChar cp, WChar cn) {
		System.out.println("Enter to IntegrateIns method");

		if (!isExecutable(Operations.INS, wchar, cp.getId(), cn.getId())) {
			System.err.println("Cannot integrate");
			return;
		}
		WString subWstring = subseq(cp, cn);
		if (subWstring.getChars().length == 0) {
			insert(wchar, indexOf(cp));
		} else {
			WString L = new WString(numSite, clock);
			L.setStart(cp);
			L.setEnd(cn);

			int i = 0;
			for (WChar wcharItem : subWstring.getChars()) {

				if (idLessThan(cp, cn)) {
					L.insert(wcharItem, i++);
				}
			}

			i = 0;
			while (i < L.length() - 1
					&& L.getChars()[i].getId().getClock() < wchar.getId().getClock()) {
				i++;
			}
			integrateIns(wchar, L.getChars()[i - 1], L.getChars()[i]);
		}
	}

	private boolean idLessThan(WChar cp, WChar cn) {
		if (cp.getId().isStart() && cn.getId().isStart() || cp.getId().isEnd() && cn.getId().isEnd()) {
			return false;
		} else
			return (cn.getId()
					.isStart()
							? false
							: cp.getId().isStart() ? true
									: cn.getId().isEnd() ? true
											: cp.getId().isEnd() ? false
													: (cp.getId().getSite() < cn.getId().getSite())
															&& cp.getId().getClock() < cn.getId().getClock());

	}

	private ID genID() {
		System.out.println("Enter to getID method");
		
		clock = clock + 1;
		
		return new ID(numSite, clock);
	}

	private void insert(WChar wchar) {
		System.out.println("Enter to inset method");

		int index = indexOf(wchar);
		insert(wchar, index);
	}

	private int indexOf(WChar wchar) {
		if (wchar.getId().isStart()) {
			return 0;
		} else if (wchar.getId().isEnd()) {
			return chars.length;
		} else {
			for (int i = 0; i < chars.length; i++) {
				if (chars[i].getId().equals(wchar.getId())) {
					return i + 1;
				}
			}
		}
		return chars.length;
	}

	public void generateDel(int pos) {
		System.out.println("Enter to generateDel method");

		WChar wchar = ithVisible(pos);
		if(wchar == null ) { 
			System.out.println("Position: " + pos + " are unavailable.");
			return;
		}
		
		integrateDel(wchar);
		WootOperations op = new WootOperations(WootOperations.Operations.DEL, wchar,
				wchar.getId().getSite());
		broadcast(op, wchar);
	}

	private void integrateDel(WChar wchar) {
		System.out.println("Enter to integrateDel method");

		wchar.setVisible(false);
	}

	public boolean isExecutable(Operations operation, WChar wchar, ID prev, ID next) {
		System.out.println("Enter to isExecutable method: operation - " + operation.name());
		System.out.println("Prev: " + prev.toString());
		System.out.println("Next: " + next.toString());

		//TODO rewrite to switch
		if (operation.name().equalsIgnoreCase(WootOperations.Operations.DEL.name())) {
			return contains(wchar);
		} else if (operation.name().equalsIgnoreCase(Operations.INS.name())) {
			return prev.isStart() || next.isEnd()
					|| (contains(getWChar(prev)) && contains(getWChar(next)));
		}
		return false;
	}

	public boolean addOperation(WootOperations op) {
		return operations.add(op);
	}

	public void removeOperation(WootOperations op) {
		int index = 0;

		for (int i = 0; i < operations.size(); i++) {
			if (operations.get(i).equals(op)) {
				index = i;
				break;
			}
		}
		operations.remove(index);
	}

	public WChar getStart() {
		return start;
	}

	public void setStart(WChar start) {
		this.start = start;
	}

	public WChar getEnd() {
		return end;
	}

	public void setEnd(WChar end) {
		this.end = end;
	}

}
