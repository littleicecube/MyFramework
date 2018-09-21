package com.palace.seeds.consensus.raft;

public class EtcdRaft {
	
	
	
/*	
 * raftNode{
		proposeC:    proposeC,
		confChangeC: confChangeC,
		commitC:     commitC,
		errorC:      errorC,
		id:          id,
		peers:       peers,
		join:        join,
		waldir:      fmt.Sprintf("raftexample-%d", id),
		snapdir:     fmt.Sprintf("raftexample-%d-snap", id),
		getSnapshot: getSnapshot,
		snapCount:   defaultSnapCount,
		stopc:       make(chan struct{}),
		httpstopc:   make(chan struct{}),
		httpdonec:   make(chan struct{}),
		snapshotterReady: make(chan *snap.Snapshotter, 1),
	}

	raft{
		id:                        c.ID,
		lead:                      None,
		isLearner:                 false,
		raftLog:                   raftlog,
		maxMsgSize:                c.MaxSizePerMsg,
		maxInflight:               c.MaxInflightMsgs,
		prs:                       make(map[uint64]*Progress),
		learnerPrs:                make(map[uint64]*Progress),
		electionTimeout:           c.ElectionTick,
		heartbeatTimeout:          c.HeartbeatTick,
		logger:                    c.Logger,
		checkQuorum:               c.CheckQuorum,
		preVote:                   c.PreVote,
		readOnly:                  newReadOnly(c.ReadOnlyOption),
		disableProposalForwarding: c.DisableProposalForwarding,
	}
	
	raftLog{
		storage Storage
		unstable unstable
		committed uint64
		applied uint64
		logger Logger
	}

	unstable{
		snapshot *pb.Snapshot
		entries []pb.Entry
		offset  uint64
		logger Logger
	}

	Progress{
		Match, Next uint64
		State ProgressStateType
		Paused bool
		PendingSnapshot uint64
		RecentActive bool
		ins *inflights
		IsLearner bool
	}
	
	*/


	/**
	 *		
	 *
	 *
	 *
	 *
	 *
	 *raft中的Config结构体，配置一个raft节点的基本信息
	 *如：
	 *	id node节点的id
	 *	
	 *1)关于channel
	 *
	 */
	
}
