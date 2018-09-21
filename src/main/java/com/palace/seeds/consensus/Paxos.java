package com.palace.seeds.consensus;

public class Paxos {

	/**
	 * 
	 *paxos算法：分布式一致性领域，
	 *
	 *	在多个请求中选中一个请求
	 *	在多个请求中选中一个请求，并按照一定的规则序列化请求
	 *
	 *
	 *	这个算法能达到什么效果呢，只要保证超过半数的Server维持正常工作，同时连接工作Server的网络正常（网络允许消息丢失，重复，乱序），就一定能保证，
	 *
	 *
	 *	议案的提出是一个不断收敛的过程，
	 *	perpare阶段：
	 *		proposer提出一个sequence number给acceptor,如果这个sequence number比当前acceptor的sequence number小则不回复
	 *												  如果这个sequence number比当前的acceptor的sequence number大，则acceptor
	 *														回复已经接受到的最大的sequence number,并承诺不再接受比这个sequence number大的number
	 *		proposer需要对接受到的response做处理，如果接收到的response多于acceptor一半的个数，那么就可以对得到的回复处理，
	 *													如果回复的reponse中的的sequence number值相同，代表acceptor已经接受了prepare阶段中proposer
	 *														提出的sequence number,则表示收敛完成可以进入proposal阶段。
	 *											  如果接受到的response小于acceptor的一半的个数（这样一回复的个数来判断不好，因为可能存在网络原因导致
	 *												相应没有收到，应该是如果满足条件者返回acceptor接收到的最大的sequence number值，如果不满足条件则回复
	 *												一个约定的值代表不满足条件，这样proposer就会知道接受到多少个response，如果满足条件超过一半则收敛完成，
	 *												如果不满足的超过一半则代表这个收敛过程失败，这样都不必等待所有的response回来），则将sequence number加1
	 *												再次向acceptor提出请求，直到完成收敛。
	 *	perpare阶段和paxos的约束条件1不具有对等关系，约束一表示proposal只有被proposer提出后才能进入是否被批准的阶段，而prepare只是对sequence number进行收敛
	 *	此时还未进行proposal的提交。
	 *		
	 *													
	 *	zab协议和paxos协议对比：basic paxos提议的收敛速度不是很理想，极端情况下在存在无法收敛的情况。当proposer的sequence number得到收敛后，选举完成，那么需要提交
	 *	proposal给各个acceptor，每个acceptor接受提议，新加入的acceptor中途宕机重启的acceptor可以通过学习其他acceptor中已经提交的数据来是自己得到最新的proposal。
	 *	zab中简化了proposal sequence number的收敛的过程，只要leader还活着，客户端自己提交请求给leader，leader将受到的数据广播给其他follower,如果leader受到半数的response
	 *	则认为广播成功，则发送commit请求，让follower接受请求。而在两阶段提交协议中，只有全部相应成功才算成功。如果leader受到请求广播给其他follower失败，则重新选取leader，
	 *	如果leader广播协议成功，在commit阶段失败，其他的follower在选举leader成功后，可以比较当前follower接受到的proposal是否超过半数，也可以回退到上一个proposal上
	 *
	 *
	 *	basic paxos慢收敛--->fast paxos快速收敛算法，通过选取leader来解决收敛慢的问题。
	 *
	 *
	 *
	 */
}
