package com.palace.experience;

public class Address {
 /**
  * 
1)逻辑地址[虚拟地址];2)线性地址;3)物理地址

+----------+----------------------------------------+
|000000001 |mov	ax, cs                              |
+----------+----------------------------------------+
|000000002 |movzx eax, ax                           |
+----------+----------------------------------------+
|000000003 |shl	eax, 4                              |
+----------+----------------------------------------+
|000000004 |add	eax, 12                             |
+----------+----------------------------------------+
|000000005 |mov	word [LABEL_DESC_CODE16 + 2], ax    |
+----------+----------------------------------------+
|000000006 |shr	eax, 16                             |
+----------+----------------------------------------+
|000000007 |mov	byte [LABEL_DESC_CODE16 + 4], al    |
+----------+----------------------------------------+
|000000008 |mov	byte [LABEL_DESC_CODE16 + 7], ah    |
+----------+----------------------------------------+
|000000009 |xor	eax, eax                            |
+----------+----------------------------------------+
|000000010 |mov	ax, cs                              |
+----------+----------------------------------------+
|000000011 |shl	eax, 4                              |
+----------+----------------------------------------+
|000000012 |add	eax, LABEL_SEG_CODE32               |
+----------+----------------------------------------+
|000000013 |mov	word [LABEL_DESC_CODE32 + 2], ax    |
+----------+----------------------------------------+
|000000014 |shr	eax, 16                             |
+----------+----------------------------------------+
|000000015 |mov	byte [LABEL_DESC_CODE32 + 4], al    |
+----------+----------------------------------------+
|000000016 |mov	byte [LABEL_DESC_CODE32 + 7], ah    |
+----------+----------------------------------------+





%include	"pm.inc"	; 常量, 宏, 以及一些说明
org	07c00h
	jmp	LABEL_BEGIN

[SECTION .gdt]
LABEL_GDT:	   		Descriptor       0,                0, 0           		; 空描述符
LABEL_DESC_CODE32: 	Descriptor       0, SegCode32Len - 1, DA_C + DA_32		; 非一致代码段
LABEL_DESC_VIDEO:  	Descriptor 0B8000h,           0ffffh, DA_DRW	     	; 显存首地址
GdtLen		equ	$ - LABEL_GDT												; GDT长度
GdtPtr		dw	GdtLen - 1													; GDT界限
			dd	0															; GDT基地址

; GDT 选择子
SelectorCode32		equ	LABEL_DESC_CODE32	- LABEL_GDT
SelectorVideo		equ	LABEL_DESC_VIDEO	- LABEL_GDT
; END of [SECTION .gdt]

[SECTION .s16]
[BITS	16]
LABEL_BEGIN:
	mov	ax, cs
	mov	ds, ax
	mov	es, ax
	mov	ss, ax
	mov	sp, 0100h

	; 初始化 32 位代码段描述符
	xor	eax, eax
	mov	ax, cs
	shl	eax, 4
	add	eax, LABEL_SEG_CODE32
	mov	word [LABEL_DESC_CODE32 + 2], ax
	shr	eax, 16
	mov	byte [LABEL_DESC_CODE32 + 4], al
	mov	byte [LABEL_DESC_CODE32 + 7], ah





































  */
}
