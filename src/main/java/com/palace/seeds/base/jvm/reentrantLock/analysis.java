package com.palace.seeds.base.jvm.reentrantLock;






public class analysis {
	
/**
 * 
 * 
 * AbstractQueueSynchronizer队列同步器，包含同步资源的额状态，多个线程需要争夺其中的状态，还包含head和tail指向
 * 用来构建双向队列
 * 
 *	public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
 * 
 * 通过调用同步队列中acquire来尝试获修改同步队列的状态，如果修改成功，意味着当前线程拥有了锁，可以往下执行，如果修改失败，因为没有获取锁
 * 则调用addWaiter(Node.EXCLUSIVE)来将当前线程封装成一个Node节点添加到同步器的队列中
 * 在调用acquireQueued(addWaiter(Node.EXCLUSIVE), arg)，将当前节点的前一个节点的waitStatus设置成-1，然后阻塞当前线程
 * 
 * 
 * condition:
 * 
 *await之前应该先调用trylock()来获取锁，获取锁成功后才能进行await操作，await操作将当前线程封装成node存放到condtion的队列中并不是在AbstractSychronizeQueue
 *的队列中，await操作完成后，将当前线程进行阻塞，然后唤醒一个AbstractSychronizeQueue队列中的一个线程，调用await操作完成后不用调用unlock()操作，因为await将其阻塞在condtion队列中。
 *
 *signal操作之前也应该调用trylock()来获取锁，signal操作将condition队列中的等待线程移除，然后添加到AbstractQueueSynchronizer队列中，此时的操作只是将其
 *添加的同步队列中，并没有唤醒线程，还需要借助AbstractSychronizeQueue中的release()方法来唤醒队里中的等待线程
 *
 *需要注意的是：await操作完成后将其添加到condition的等待队列中，移除的话只能通过signal操作将等待线程从condition的等待队里移动到AbstractQueueSynchronizer的等待队里中
 *然后在调用同步队列里的release()方法来唤醒线程。
 * 
 * 
 * ReadWriteLock
 * 
 * 通过AbstractQueueSynchronizer中的state来维护读写线程，其中state的高16代表读线程获取锁的次数，同一个线程多次调用tryAcquire()也会增加获取锁的次数，低16代表写线程获取锁的此时，如果一个写
 * 线程多次获取写锁低16位的计数会被多次加1。
 * 当读写线程获取锁时，会通过各自对应的内部类中的tryAcquire()来判断获取读写锁是否成功，其中读写都是判断共享的aqs中state状态，来决定是读线程获取锁还是写线程获取锁。
 *	 如果读线程获取锁，那么后续的读线程都会被允许获取锁，每个线程的ThreadLocal中会记录当前读线程获取读锁的次数，并修改aqs中state高16中读线程获取读锁的总次数，写线程在获取锁时会通过aqs的高16位来判断
 * 	是否有读线程获取锁，如果有则把当前写线程加入到aqs中的等待队列中，当所有读线程调用unlock后就会减少读线程中ThreadLocal中获取锁的次数，并减少aqs中高16位记录的读线程获取的次数，如果为0代表了所有的
 * 	读线程全部把锁释放了，那么就会唤醒一个aqs等待队列中的一个写线程。
 * 当写线程获取锁时，回修改aqs中低16位中记录的获取锁的次数，当其他的写线程来争用锁时会被加入到aqs的同步队列中，其他的读线程来获取锁时也会被加入到aqs的同步队列中，活动写线程释放锁时会从aqs等待队列中获取
 * 一个等待线程唤醒，当唤醒的写线程时，简单的唤醒即可，当唤醒的是读线程时会传播性的唤醒所有的读线程
 * 
 * 
 * 
 * 
 * 
 *同步的原理：
 *	1）创建一个共享的资源
 *	2）竞争创建的资源，如果竞争成功，代表获取使用权
 				   如果竞争失败，继续竞争，直到成功
 	在同步队列中，同步队列的实例代表了一个竞争资源，线程首先来获取同步队列的使用权，
 		如果获取队列的使用权成功，修改队列的状态，代表当前线程正在使用这个队列，如果有新的线程
 			来竞争，首先检查队列的状态是否被占用，如果被占用则把当前线程包装成一个node放到队列的列表中
 		如果获取队列的使用权失败，则将当前线程封装后放到队列的列表中
 		如果线程执行完成，则释放当前线程节点，并唤醒下一个等待的线程
 		
 		注意：如果按照上面的逻辑，如果先被阻塞的线程早于其他的线程被添加到线程队列中，如果在唤醒的时候也是早于其他线程
 			被唤醒，因此需要一种同步机制来实现不公平的竞争，也就是后来的线程也有可能早于被添加到队列中的线程获取锁
 			的使用权。
 	
 	
 * 
 */
}
