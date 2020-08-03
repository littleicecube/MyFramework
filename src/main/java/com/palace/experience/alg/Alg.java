package com.palace.experience.alg;

import java.util.HashMap;
import java.util.Map;

public class Alg {
	
	
	
	
	
	
	public static void main(String[] args) {
		String str = "aa";
		int len = str.length();
		int c  = 0;
		int step = 0;
		for(int i=0;i<len;i++) {
			
		}
	}
	
	
	  static public class ListNode {
	       int val;
	       ListNode next;
	      ListNode(int x) { val = x; }
	 }
	static class Solution2 {
	    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

	        ListNode p = l1;
	        ListNode last = null;
	        int inc = 0;
	        while(p != null){
	            int sum = p.val + l2.val;
	            sum = sum + inc;
	            if(sum >= 10){
	                inc = 1;
	                sum = sum % 10;
	            }else{
	                inc = 0;
	            }
	            p.val = sum;
	            last = p;
	            p = p.next;
	            l2 = l2.next;
	        }
	        if(inc > 0){
	            ListNode node = new ListNode(inc);
	            last.next = node;
	        }
	        return l1;
	    }
	    
	    
	    public static void main(String[] args) {
	    	ListNode l1 = new ListNode(2);
	    	ListNode l2 = new ListNode(4);
	    	ListNode l3 = new ListNode(9);
	    	l1.next = l2;
	    	l2.next = l3;

	    	ListNode l11 = new ListNode(5);
	    	ListNode l22 = new ListNode(6);
	    	ListNode l33 = new ListNode(0);
	    	l11.next = l22;
	    	l22.next = l33;
	    	ListNode ret = new Solution2().addTwoNumbers(l1,l11);
	    	while(l1 != null) {
	    		System.err.println(l1.val);
	    		l1 = l1.next;
	    	}
	    	
		}
	    
	}
	
	static class Solution {
	    public int[] twoSum(int[] nums, int target) {
	        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
	        for(int i=0;i<nums.length;i++){
	            int val = nums[i];
	            int des = target - val; 
	            if(map.containsKey(des)){
	                return new int[]{i,map.get(des)};
	            }else{
	                map.put(val,i);
	            }
	        }
	        return null;
	    }
	}
}
