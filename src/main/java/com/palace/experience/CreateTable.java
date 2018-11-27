package com.palace.experience;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateTable {
 
	public static class Column{
		String order;
		String columnVal;
		int columnLen ;
		public int getColumnLen() {
			return columnLen;
		}
		public String getColumnVal() {
			return columnVal;
		}
	}
	public static class Row{
		List<Column> columnList ;
	}
	
	public static StringBuilder createTable(List<Row> rowList) {
		StringBuilder sb = new StringBuilder("");
		for(Row row : rowList) {
			List<Column> colList = row.columnList;
			StringBuilder colLine= new StringBuilder("");
			StringBuilder colVal= new StringBuilder("");
			for(Column col : colList) {
				createRow(colLine, colVal,col);
			}
			sb.append(colLine).append("+");
			sb.append("\r\n");
			sb.append(colVal).append("|");
			sb.append("\r\n");
		}
		
		List<Column> colList = rowList.get(0).columnList;
		StringBuilder colLine= new StringBuilder("");
		for(Column col : colList) {
			addLast(colLine,col);
		}
		sb.append(colLine).append("+");
		return sb;
	}
	
	public static void createRow(StringBuilder colLine,StringBuilder colVal,Column col) {
		colLine.append("+");
		colVal.append("|");
		for(int i=0;i<col.getColumnLen();i++) {
			colLine.append("-");
		}
		int len = col.getColumnLen();
		int valLen = col.getColumnVal().length();
		
		colVal.append(col.getColumnVal());
		for(int i =0;i<len-valLen;i++) {
			colVal.append(" ");
		}
	}

	public static void addLast(StringBuilder colLine,Column col) {
		colLine.append("+");
		for(int i=0;i<col.getColumnLen();i++) {
			colLine.append("-");
		}
	}
	
	static AtomicInteger cc = new AtomicInteger(1);
	public static String getOrder() {
		int val = cc.getAndIncrement();
		String sVal ="";
		if(val < 10 ) {
			sVal="000"+val;
		}else if(val < 100) {
			sVal="00"+val;
		}else if(val <  1000) {
			sVal="0"+val;
		}
		return "00000"+sVal;
	}
	
	public static void main(String[] args) {
		List<Row> rowList = new ArrayList<>();
		 for(String s : val.split("\r\n")) {
			 List<Column> colList = new ArrayList<>();
			 Column col = new Column();
			 col.columnVal = getOrder();
			 col.columnLen = 10;
			 colList.add(col);
			 
			 Column col1 = new Column();
			 col1.columnVal = s.trim();
			 col1.columnLen = 40;
			 colList.add(col1);
			 
			 Row row = new Row();
			 row.columnList = colList;
			 rowList.add(row);
		 }
		 String ss = createTable(rowList).toString();
		 System.out.println(ss);
	}
	
	static String val="		SPValueInRealMode	dw	0\r\n" + 
			"	PMMessage:			db	\"错误数据\"	\r\n" + 
			"	OffsetPMMessage		equ	PMMessage - $$\r\n" + 
			"	DataLen			equ	$ - SEGMENT_DATA\r\n" + 
			"\r\n" + 
			"	times 512 db 0\r\n" + 
			"	TopOfStack	equ	$ - SEGMENT_STACK-1\r\n" + 
			"\r\n" + 
			"	mov	ax, SelectorVideo\r\n" + 
			"	mov	gs, ax					\r\n" + 
			"	mov	edi, (80 * 13 + 0) * 2	\r\n" + 
			"	mov	ah, 0Ch					\r\n" + 
			"	mov	al, 'L'\r\n" + 
			"	mov	[gs:edi], ax							\r\n" + 
			"	jmp	SelectorCode16:0		\r\n" + 
			"	CodeALen	equ	$ - SEGMENT_CODE\r\n" + 
			"\r\n" + 
			"	mov	ax, cs\r\n" + 
			"	movzx	eax, ax\r\n" + 
			"	shl	eax, 4\r\n" + 
			"	add	eax, LABEL_SEG_CODE16\r\n" + 
			"	mov	word [LABEL_DESC_CODE16 + 2], ax\r\n" + 
			"	shr	eax, 16\r\n" + 
			"	mov	byte [LABEL_DESC_CODE16 + 4], al\r\n" + 
			"	mov	byte [LABEL_DESC_CODE16 + 7], ah";
	
}
