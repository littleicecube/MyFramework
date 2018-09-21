JavaImporter(Packages.com.cdoframework.cdolib.data.cdo.CDO);

function getName(){
	return "xiaozhang";
}
function sum(a,b){
	return a+b;
}
function mul(a,b,c){
	return a*b+sum(a,c);
}
function msg(){
	println("println test");
}
function cdoSum(cdo){
	return cdo.getIntegerValue("age")+cdo.getIntegerValue("age");
}
function cal(cdo){
	 cdo.setIntegerValue("age",cdo.getIntegerValue("age")*2);
	 cdo.setBooleanValue("isOpen",cdo.getBooleanValue("isOpen")?false:true);
	 cdo.setStringValue("name",cdo.getStringValue("name")+"change");
	 cdo.setDoubleValue("money",100.123);
	 println("println test");
	 return cdo;
}

function addTest(){
	var counter=0;
	var d = 9989999;
	var s=new Date().getTime();
	while(true){
		counter++;
		//if((d=d-1.12345)<10){
		if((d=d-1.12345)<10){
			break;
		}
	}
	return counter;
}
function addTest1(){
	var counter=0;
	var d = 9989999;
	var s=new Date().getTime();
	while(true){
		counter++;
		//if((d=d-1.12345)<10){
		if((d=d-1.12345)<10){
			break;
		}
	}
	println(new Date().getTime()-s);
	return counter;
}
