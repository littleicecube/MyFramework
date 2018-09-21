package com.palace.seeds.db.mongo;

public class RuleLogMongoService{
	/* 
	private static MongoClient client;
	private static MongoCollection collection;
	
	public static void insertMany(List documents) {
		collection.insertMany(documents);
	}
	public static void insertOne(Document doc) {
		collection.insertOne(doc);
	}
	static Map<String,Object> optionMap =new HashMap<String,Object>();
	static String[] strParams = new String[] {"strUserName4log","strUpassword4log","strHost4log","strDBName4log","strTBName4log","nPort4log",
			"nConnectionsPerHost","nRhreadsAllowedToBlockForConnectionMultiplier"};
	
	public  static void initDataBase() {
		for(String key : strParams) {
			optionMap.put(key,getVal(key));
		}
		client = init(
				MapUtils.getString(optionMap, "strUserName4log"),
				MapUtils.getString(optionMap, "strUpassword4log"),
				MapUtils.getString(optionMap, "strHost4log"),
				MapUtils.getIntValue(optionMap, "nPort4log"),
				MapUtils.getString(optionMap, "strDBName4log"),
				getClientOption());
		collection = client.
				getDatabase(MapUtils.getString(optionMap, "strDBName4log"))
				.getCollection(MapUtils.getString(optionMap, "strTBName4log"));
	}
	private static MongoClient init(String userName,String password,
			String host,int port,String dbName,MongoClientOptions clientOpations) {
        List<ServerAddress> addrList = new ArrayList(Arrays.asList(new ServerAddress(host,port)));
        List<MongoCredential> creList = new ArrayList<>(Arrays.asList(MongoCredential.createScramSha1Credential(userName,dbName,password.toCharArray())));
        return new MongoClient(addrList,creList,clientOpations);
	}
	public static MongoClientOptions getClientOption() {
		MongoClientOptions.Builder build = new MongoClientOptions.Builder();
        build.connectionsPerHost(MapUtils.getIntValue(optionMap, "nConnectionsPerHost"));
        build.threadsAllowedToBlockForConnectionMultiplier(MapUtils.getIntValue(optionMap, "nRhreadsAllowedToBlockForConnectionMultiplier"));
        return build.build();
	}
	public static Object getVal(String name) {
		String val ="" ;
		try{
			val = ConfigUtil.getString(name);
		}catch(Exception e) {
			val = BusinessService.getInstance().getParameter(name);
		}
		if(name.startsWith("n")){
			return Integer.parseInt(val);
		}else if(name.startsWith("l")){
			return Long.parseLong(val);
		}else {
			return val;
		}
	}
	*/
}
