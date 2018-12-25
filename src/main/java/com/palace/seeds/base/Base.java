package com.palace.seeds.base;

import org.junit.Test;

public class Base {

	static int[] arr =new int[] {12,13,23,23,33,34,55,63,82,99};
	
	
	@Test
	public void sort() {
		
	}
	
	
	
	
	
	public static void main(String[] args) {
		int r = find(arr);
		System.out.println(r);
	}
	
	public static int find(int[] arr) {
		int key = 99;
		int left = 0,right = arr.length - 1;
		while(left < right) {
			int mid = left+(right-left)/2;
			if(arr[mid] < key) {
				left = mid+1;
			}else if(arr[mid] > key) {
				right = mid-1;
			}else {
				return  mid;
			}
		}
		return -1;
	}
	
}
