package com.palace.seeds.dubbox.debug;

public class ExportRegister {
	
	public static interface IAccountService{
		public boolean add(String key,int val);
	}
	public  static class AccountService implements IAccountService{
		@Override
		public boolean add(String key, int val) {
			System.out.println("save key:"+key+",val:"+val+",succ");
			return true;
		}
	}
	
	/**
	 
	 
	 
	 
	 
	 
	 */
	
	 
}
	
