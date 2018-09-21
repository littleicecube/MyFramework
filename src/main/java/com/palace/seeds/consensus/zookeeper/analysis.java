package com.palace.seeds.consensus.zookeeper;






public class analysis {
	
/**
 * 一个mysqlserver服务器提供服务
 *		多个proposer提交请求到server端，server端根据锁来控制接收哪个proposer能提交成功。
 * 		当server挂掉以后就无法对外提供服务，proposer不能提交协议。
 * 一个主mysqlserver,多个从mysqlslaver,主库和从库之间半同步，当一个从mysqlslaver从主mysqlserver复制成功以后，mysqlserver就认为成功
 * 然后响应proposer提交成功。
 *		多个proposer提交协议到server端，主库和一个从库之间进行信息复制，其中有一个复制成功以后，则返回proposer成功
 *		当主库server挂掉以后从库可以进行相应，但是会存在一定程度的信息丢失，从库之间无法通信，主库和从库复制成功以后，并不知道和哪个从库通信成功，并不知道
 *		哪个从库的数据是和主库的数据最一致
 *
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */
}
