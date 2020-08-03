package com.palace.seeds.alg;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

public class QuickSort {

	int[] arr = new int[] {32,11,62,94,45,102,9,28,43,70,23};
	int len = arr.length - 1;
	
	
	@Test
	public void insert() {
		for(int i=0;i<len;i++) {
			int c = arr[i];
			for(int j=i+1;j<len;j++) {
				 if(c > arr[j]) {
					 arr[j-1] = arr[j];
					 arr[j] = c;
				 }
			}
			System.err.println(JSONObject.toJSON(arr));
		}
	}
	@Test
	public void maoPao() {
		for(int i=0;i<len;i++) {
			for(int j=0;j<len-i;j++) {
				if(arr[j]<arr[j+1]) {
					int tmp = arr[j+1];
					arr[j+1] = arr[j];
					arr[j]=tmp;
				}
			}
			System.err.println(JSONObject.toJSON(arr));
		}
	}
	public void quickSort() {
		int l,r;
		l = 0;
		r = len;
		int index = 3;
		while(l < r) {
			if(arr[l] > arr[index]) {
				arr[index] = arr[l];
				index = l;
			}else {
				l++;
			}
			
			//找到最左边比基准数据大的
			
			if(arr[r] < arr[index]) {
				arr[index] = arr[r];
				index = r;
			}else {
				r--;
			}
		}
	}
}
