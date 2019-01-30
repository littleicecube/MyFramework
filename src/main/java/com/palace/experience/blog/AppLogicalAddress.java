package com.palace.experience.blog;

public class AppLogicalAddress {
/**
 * 
 逻辑地址空间:
 应用程序被编译连接后生成的文件是按照一定规则创建的,生成的文件从一端到另一端是有编排的一个逻辑地址空间
 其中每一个指令在文件中的顺序可以看做是该指令的逻辑地址,逻辑地址是给应用程序看的
 
 假设存在一个应用程序A
 经过编译后生成二进制代码,二进制代码一般会被划分为几个区域成为段,比如:代码段,数据段,堆栈段
对于每个区域都会存在一个结构来描述每个区域这个描述结构叫段描述符
段描述符共8字节64位其中有段基址信息,段界限信息,段属性信息
1)每个段的起始地址叫段基址
2)每段的长度叫段界限
3)每个段的一些基本属性信息叫段属性
1)段基址在段描述符中不是连续的空间,分别占用段描述表的第2字节,第3字节,第4字节,第7字节,长度共32位.用来描述从哪个地址开始作为段的开始地址
2)段界限占用段描述表的第0字节,第1字节和第6字节的低4位,长度共20位.用来描述从段基址开始多长的内存段属于当前段
3)段属性占用段描述表的第5字节和第6字节的高4位,长度共12位,用来描述物段的属性信息
|	第7字节	  	|	第6字节		|	第5字节	  	|	第4字节		|	第3字节	  	|	第2字节		|	第1字节	  	|	第0字节		|	
+---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+
|段基址(31..24)位	|		属性和段界限2(19..16)位		|					段基址(23..0)位				|		段界限(15..0)(位)			|
|	基地址2	  	| 属性内容	|	段界限2	|	属性内容	|						基址1						|			段界限1				|
|---------------+---------------+---------------+---------------+---------------+---------------+---------------+---------------+

假设应用程序A编译完成后的汇编代码如下:

org	0100h				;程序的入口地址是0100h,
	jmp	LABEL_BEGIN		;跳转到LABEL_BEGIN开始执行
	
//定义各个段的段描述符
段名称;                段基址,       	段界限     	, 	属性		;
LABEL_GDT:            0000,         0		, 	0       ;空描述符
LABEL_DESC_CODE:	  0400,  	CodeLen-1	, 	1		;代码段段描述符
LABEL_DESC_DATA:      0500,     DataLen-1	, 	1		;数据段段描述符
LABEL_DESC_STACK:     0600,     TopOfStack	, 	2		;对斩段段描述符

//段选择子
SelectorNormal		equ	LABEL_DESC_NORMAL	- LABEL_GDT	
SelectorCode		equ	LABEL_DESC_CODE		- LABEL_GDT	;代码段的段选择子
SelectorData		equ	LABEL_DESC_DATA		- LABEL_GDT ;数据段的段选择子
SelectorStack		equ	LABEL_DESC_STACK	- LABEL_GDT ;堆栈段的段选择子
 
//程序的入口
LABEL_BEGIN:
	mov	ax, cs
	mov	ds, ax
	mov	sp, 0100h
	
//代码段
LABEL_SEG_CODE32:
	mov	ax, SelectorData
	mov	ds, ax			; 数据段选择子
	mov	ax, SelectorVideo
	mov	gs, ax			; 视频段选择子

//数据段
LABEL_DATA:
	SPValueInRealMode	dw	0								;字符串
	PMMessage:			db	"In Protect Mode now. ^-^", 0	; 进入保护模式后显示此字符串
	StrTest:			db	"ABCDEFGHIJKLMNOPQRSTUVWXYZ", 0

//全局堆栈段
LABEL_STACK:
	times 512 db 0						;分配512个字节
	TopOfStack	equ	$ - LABEL_STACK - 1	;计算栈顶
	
	
程序运行时比如运行到代码段的第一行
1)会获取代码段的段选择子(类似数组中的index)
2)根据代码段的段选择子获取代码段的段描述符,由上可知段描述符是8个字节的结构体,解析其中的内容后保存在内存中
3)执行代码段的第一行代码时,会将解析出来的段基址存放到段寄存器中,在结合被执行代码的偏移地址生成线性地址
段基址+代码偏移  生成=>线性地址
4)intel中有段式内存管理模式和段页式内存管理模式也叫保护模式,在没开启段页式内存管理模式时,线性地址就是物理地址
然后线性地址会被当做物理地址发送到内存管理单元上,内存管理单元通过数据总线获取地址对应的内容

在一般的概念里还有一个逻辑地址,逻辑地址经过转换后成为线性地址
从上面可知每个段的段描述符中的段基址信息是可以被设定的,是编译链接器在生成可执行文件时根据配置设定的,在linux中每个段
的段基址都是一样的,逻辑地址等于线性地址,逻辑地址并没有准确的概念
不太准确的讲逻辑地址是被执行指令在可执行文件中的顺序号,比如指令在9行,他的逻辑地址是0x00000009
	 
 */
}
