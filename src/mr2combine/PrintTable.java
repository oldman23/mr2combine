package mr2combine;

import java.util.ArrayList;

public class PrintTable {
	final int colSize;
	final ArrayList<String[]> rows = new ArrayList<String[]>(); 
	int currentColNum = 0;
	String[] currentRow;
	
	public PrintTable(String... cols) {
		colSize = cols.length;
		currentRow = new String[colSize];
		for (String col : cols) {
			add(col.toUpperCase());
		}
		flush();
	}
	
	public void flush() {
		rows.add(currentRow);
		currentRow = new String[colSize];
		currentColNum = 0;
	}
	
	public PrintTable add(Object o) {
		try {
			currentRow[currentColNum++] = o.toString();
		} catch (NullPointerException e) {
			System.out.println("error on add " + o);
			throw e;
		}
		return this;
	}
	
	boolean lines = true;
	
	public void printNoLines(String indent) {
		this.lines = false;
		print(indent);
		this.lines = true;
	}
	
	public void print(String indent) {
		int size = rows.size();
		
		int[] widths = findColWidths();
		
		int linenum = -1;
		for (int i = 0; i < size * 2; i++) {
			if ((i % 2) == 0) {
				/**
				 * print lines
				 */
				if (lines) {
					printLines(indent, widths);					
				}
			} else {
				linenum++;
				
				
				StringBuilder sb = new StringBuilder();
				if (lines) {
					sb.append("|");					
				} else {
					sb.append(" ");
				}
				String[] line = rows.get(linenum);
				
				for (int j = 0; j < colSize; j++) {
					String cell = line[j];
					if (linenum == 0) {
						sb.append(padCenter(cell, widths[j]));
					} else {
						sb.append(pad(cell, widths[j]));						
					}
					if (lines) {
						sb.append("|");						
					} else {
						sb.append(" ");
					}
				}
				System.out.println(indent + sb.toString());
			}
		}
		if (lines) {
			printLines(indent, widths);
		}
	}

	private Object padCenter(String cell, int width) {
		int left = 0;
		int right = 0;
		int remaining = width - cell.length();
		if (remaining > 0) {
			left = remaining / 2;
			right = remaining - left;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < left; i++) {
			sb.append(" ");
		}
		sb.append(cell);
		for (int i = 0; i < right; i++) {
			sb.append(" ");
		}
		// TODO Auto-generated method stub
		return sb.toString();
	}

	private String pad(String cell, int width) {
		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		sb.append(cell);
		int remaining = width - sb.length();
		for (int j = 0; j < remaining; j++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	private void printLines(String indent, int[] widths) {
		StringBuilder sb = new StringBuilder();
		sb.append("+");
		for (int width : widths) {
			for (int cah = 0; cah < width; cah++) {
				sb.append("-");
			}
			sb.append("+");
		}
		System.out.println(indent + sb.toString());
	}

	private int[] findColWidths() {
		int[] colWidths = new int[colSize];
		
		for (int i = 0; i < colSize; i++) {
			int max = 0;
			for (String[] row : rows) {
				int c = row[i].length();
				if (c > max) {
					max = c;
				}
			}
			colWidths[i] = max + 2;
		}
		
		return colWidths;
	}
	
	public static void main(String[] args) {
		PrintTable pt = new PrintTable("NAME", "FETISH");
		pt.add("Mr JimmyJam");
		pt.add("Blowing Hoes while Smokin Crack!");
		pt.flush();
		pt.add("Mr FundleFrocks");
		pt.add("Jumping on Big Boobiesss");
		pt.flush();
		pt.print("  ");
	}
}
