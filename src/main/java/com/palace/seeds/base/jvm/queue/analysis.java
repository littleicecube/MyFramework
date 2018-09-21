package com.palace.seeds.base.jvm.queue;






public class analysis {

	/**
	public boolean offer(E e) {
1	    final Node<E> newNode = new Node<E>(e);
2	    for (Node<E> t = tail, p = t;;) {
3	        Node<E> q = p.next;
4	        if (q == null) {
5	            if (p.casNext(null, newNode)) {
6	                if (p != t)
7	                    casTail(t, newNode);
8	                return true;
9	            }
10	        }else if (p == q)
11	            p = (t != (t = tail)) ? t : head;
12	        else{
13	            p = (p != t && t != (t = tail)) ? t : q;
	        }
	    }
	}
	
	
	循环开始时都是获取，tail节点的next节点为q，然后进行判断，
		如果q==null，则将当前节点添加到链表的尾部
		如果q!=null&&q!=p，则将q节点赋值给p节点实现往后移动的操作，如此循环，知道移动到最后一个节点
			此时最后一个节点的next节点为null,然后把当前节点添加到节点尾部，循环将新增加的节点添加的链表的尾部，
			然后发现p!=t成立，然后将t节点指向新添加的节点。然而并没有移动tail节点的指向
			
			
	发现tail节点并不总是指向最后一个节点，只是在符合一定的条件下指向最后一个节点，然而这样并不影响其他线程往队列中添加节点，
	影响的只是往链表中添加的速率，当tail移动的慢的时候，需要执行多次循环才能遍历到最后一个节点，才能将新的数据添加到文件的尾部
			
		
	
	

	
	 -------			 --data-----next----
	| head	|---------->|	name | 	null	|
	 -------			 -----^-------------
							  |
	 --------				  |
	| tail	 |----------------|
	 --------
	
	System.out.println(sr == (sr)); //true
	System.out.println(sr == (sr=ds)); //false 先执行的sr=ds,执行完成以后外层sr值并未改变，所以结果为false
	
	
	























	

**/
}