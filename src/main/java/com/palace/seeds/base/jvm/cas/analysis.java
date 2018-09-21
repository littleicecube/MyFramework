package com.palace.seeds.base.jvm.cas;






public class analysis {
/**
 *   private Node enq(final Node node) {
        for (;;) {
            Node t = tail;
            node.prev = t;
            if (compareAndSetTail(t, node)) {
                t.next = node;
                return t;
            }
        }
    }
    
 	队列中cas和volatile的用法，以上是一个双向队列，保证在多线程下的无锁添加
 	首先通过volatile特性保证每次都能获取内存在最新的tail变量的指向
 	然后通过cas来修改tail的指向，来保证tail指向当前线程要添加的节点
 	volatile类型修饰的tail节点在进入for循环时就将指向保存在本地变量中，当当前线程cas操作成功后，其他线程
 	也可能执行cas成功，此时tail已经指向了下一个线程的中要添加的node，但是由于当前线程已经保存了tail在本线程的快照，
 	因此将新添加的节点指向快照即可
 	
 	需要注意的是以上代码是同步队列中的节点添加代码，在ConcurrentQuque中节点的添加和tail指向的后移和这里有区别，
 	ConcurrentQueue中的tail后移并不一定很及时，并不一定是在新的节点添加成功后，tail就执向新的节点，其二ConcurrentQueue
 	的两边是单向的，并不是双向的
    
 */
}
