package com.palace.experience.blog;

public class AppLogicalAddress2 {
/**
 * 
 逻辑地址空间:
 应用程序被编译连接后生成的文件是按照一定规则创建的,生成的文件从一端到另一端是有编排的一个逻辑地址空间
 其中每一个指令在文件中的顺序可以看做是该指令的逻辑地址,逻辑地址是给应用程序看的
 
 假设存在一个应用程序A
 经过编译后生成二进制代码,二进制代码一般会被划分为几个区域比如基本信息段,代码段,数据段,堆栈段
每一个段都是一类地址的集合,段地址中对应的信息可能是数据区的,也可能是代码区的

//基本信息
SEGMENT_BEGIN:	+----------+----------------------------------------+
				|000000000 |mov	ax, cs                              |
				+----------+----------------------------------------+
				|000000001 |movzx	eax, ax                         |
				+----------+----------------------------------------+
				|000000002 |shl	eax, 4                              |
				+----------+----------------------------------------+
				|000000003 |add	eax, LABEL_SEG_CODE16               |
				+----------+----------------------------------------+
				|000000004 |mov	word [LABEL_DESC_CODE16 + 2], ax    |
				+----------+----------------------------------------+
//数据段
SEGMENT_DATA:	+----------+----------------------------------------+
				|000000021 |SPValueInRealMode	dw	0               |
				+----------+----------------------------------------+
				|000000022 |PMMessage:			db	"错误数据"      	|
				+----------+----------------------------------------+
				|000000023 |OffsetPMMessage		equ	PMMessage - $$  |
				+----------+----------------------------------------+
				|000000024 |DataLen			    equ	$ - SEGMENT_DATA|
				+----------+----------------------------------------+
				|	...... |    ......							    |
				+----------+----------------------------------------+
//堆栈段
SEGMENT_STACK:	+----------+----------------------------------------+
				|000000031 |times 512 db 0                          |
				+----------+----------------------------------------+
				|000000032 |  ......                                |
				+----------+----------------------------------------+
				|000000033 |  ......                                |
				+----------+----------------------------------------+
				|000000034 |  ......                                |
				+----------+----------------------------------------+
				|000000035 |  ......                                |
				+----------+----------------------------------------+
				|000000036 |TopOfStack	equ	$ - SEGMENT_STACK-1     |
				+----------+----------------------------------------+
				|	...... |    ......							    |
				+----------+----------------------------------------+
//代码段
SEGMENT_CODE:	+----------+----------------------------------------+
				|000000040 |mov	gs, ax                              |
				+----------+----------------------------------------+
				|000000041 |mov	edi, (80 * 13 + 0) * 2              |
				+----------+----------------------------------------+
				|000000042 |mov	ah, 0Ch                             |
				+----------+----------------------------------------+
				|000000043 |mov	al, 'L'                             |
				+----------+----------------------------------------+
				|000000044 |mov	[gs:edi], ax                        |
				+----------+----------------------------------------+
				|000000045 |jmp	SelectorCode16:0                    |
				+----------+----------------------------------------+
				|000000046 |CodeALen	equ	$ - SEGMENT_CODE        |
				+----------+----------------------------------------+
				|	...... |    ......							    |
				+----------+----------------------------------------+


既然每个段的用途不同那么每个段就会有边界,那么就需要一个结构用来描述每个段,这个结构叫:段描述符
段描述符共8字节64位其中有段基址信息,段界限信息,段属性信息
1)每个段的起始地址叫段基址
2)每段的长度叫段界限
3)每个段的一些基本属性信息叫段属性
1)段基址占用段描述表的第2字节,第3字节,第4字节第7字节,长度共32位.用来描述从哪个地址开始作物应用程序的开始地址
2)段界限占用段描述表的第0字节,第1字节和第6字节的低4位,长度共20位.用来描述从段基址开始多长的物理内存段属于当前段
3)段属性占用段描述表的第5字节和第6字节的高4位,长度共12位,用来描述物段的属性信息
|	第7字节	  	|	第6字节		|	第5字节	  	|	第4字节		|	第3字节	  	|	第2字节		|	第1字节	  	|	第0字节		|	
+---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+
|段基址(31..24)位	|		属性和段界限2(19..16)位		|					段基址(23..0)位				|		段界限(15..0)(位)			|
|	基地址2	  	| 属性内容	|	段界限2	|	属性内容	|						基址1						|			段界限1				|
|---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+

如果为数据段,代码段,堆栈段都创建一个段描述符那么合起来就是一个段描述符表
按照段基址+偏移地址组织起来的是线性地址,线性地址是给cpu看的,逻辑地址是给应用程序看的,比如在c语言中&var获取的地址是逻辑地址,

segment_describe_table[0] = new 代码段描述符();
segment_describe_table[1] = new 堆栈段描述符();
segment_describe_table[2] = new 数据段描述符();

index是一个段选择子segment_describe_table[index]获取相应的段描述符
	
	 
 */
}
