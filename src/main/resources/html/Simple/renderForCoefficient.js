/**
 * 简单页面渲染类
 * 
 * 1）要显示的列从方法中获取，2）要编辑的列从方法中获取
 * 
 */

function _gSimMess(msg){
	$.messager.show({title:"消息提示",msg:msg,timeout:1000,showType:"slide",style:{
		right:'',
		top:document.body.scrollTop+document.documentElement.scrollTop,
		bottom:''
	}});
}

//创建input id，如果是编辑框为 _age_edit_Id,如果是添加框_age_add_Id
function _gCreateId(key,actionType){
	return "_"+key+"_"+actionType+"_Id";
}

function _gGetDataByTableName(tableName){
	 var httpClient = new HttpClient("handleTrans.cdo");
	 var cdoRequest=new CDO();
	 var cdoResponse=new CDO();
	 cdoRequest.setStringValue("strServiceName","TableService");
	 cdoRequest.setStringValue("strTransName","getInfoByTableName");
	 var ret=httpClient.handleTrans(cdoRequest,cdoResponse);
	 if(ret != null && ret.getCode() == 0){
		 return ;
	}else{
		if(ret==null){
			_gSimMess("数据获取失败");
		}else{
			_gSimMess(ret.getText());
		}
	}
}
function _gGetSubmitValToCDO(cdoSR,columns,formId,actionType,isQuery){
	var obj = _gGetSubmitVal(columns,formId,actionType);
	var cdoArr = new Array();
	 for(var i=0;i<columns.length;i++){
		 var column = columns[i];
		 var cdo =new CDO();
		 var columnType = column.columnType;
		 var columnName = column.columnName;
		 if(columnType == "_string" 
			 || columnType == "_date" 
			 || columnType == "_datetime" 
			 || columnType == "_time"
			 || columnType == "_combobox"){
			 if(isQuery){
				 if(obj[columnName]){
					 cdo.setStringValue(columnName,column.sqlKeyName+"=\""+obj[columnName]+"\"");
				 }else{
					 cdo.setStringValue(columnName,obj[columnName]);
				 }
			 }else{
				 cdo.setStringValue(columnName,obj[columnName]);
			 }
		 }
		 if(columnType == "_int" || columnType == "_long"){
			 if(isQuery){
				 cdo.setLongValue(columnName,column.sqlKeyName+"="+obj[columnName]);
			 }else{
				 cdo.setLongValue(columnName,obj[columnName]);
			 }
		 }
		 if(columnType == "_double"){
			 if(isQuery){
				 cdo.setDoubleValue(columnName,column.sqlKeyName+"="+obj[columnName]);
			 }else{
				 cdo.setDoubleValue(columnName,obj[columnName]);
			 }
		 }
		 if(aa)
		 cdoArr.push(cdo)
	 }
	 cdoSR.setCDOArrayValue("reqValueArr",cdoArr);
	 
}
function _gGetSubmitVal(columns,formId,actionType){
	var obj = new Object();
	for(var i=0;i<columns.length;i++){
		var column = columns[i];
		var key = column.columnName;
		obj[key]=_gGetValue(_gCreateId(key,actionType),column.columnType);
	}
	return obj;
}

function _gGetValue(id,type){
	switch(type){
	case "_string":
		return $("#"+id).textbox("getValue");
	case "_int":
		return $("#"+id).numberbox("getValue");
	case "_long":
		return $("#"+id).numberbox("getValue");
	case "_double":
		return $("#"+id).numberbox("getValue");
	case "_boolean":
		return $("#"+id).combobox("getValue");
	case "_date":
		return $("#"+id).datebox("getValue");
	case "_time":
		return $("#"+id).timespinner("getValue");
	case "_datetime":
		return $("#"+id).datetimebox("getValue");
	case "_combobox":
		return $("#"+id).combobox("getValue");
	default:
		return null;
	}
}

function SimpleTable(parmas){
	
	this.tableName="";
	this.zhTableName="";//表中文名称
	this.tableId="";//表id
	this.paginationId="";//翻页控件
	this.pageNumber=1;//第几页
	this.pageSize=20;//页大小
	this.columnsUrl="";
	this.dataUrl="";
	this.editUrl="";
	this.delUrl="";
	this.editIns = null;//编辑对象
	this.addIns = null;//添加对象
	this.tableParams = null;//当前表的配置参数
	this.queryParams = null;//当前表的查询参数
}
 
//获取要渲染的列
SimpleTable.prototype.getColumns = function(){
	return this.tableParams.columns;
}

//获取查询参数
SimpleTable.prototype.getQueryParams = function(){
	return null;
}

SimpleTable.prototype.renderTable = function(params){
	thisIns = this;
	if(params != null){
		for(var key in this){
			if(params[key]){
				this[key] = params[key];
			}
		}
	}
	//预定定义datagrid的参数
	var datagridParams={
		"singleSelect":true,
	 	"fitColumns":true,
		"pagination":false,
		"nowrap":false,
		"nowrap":true,
		"fitColumns":true,
		"columns":thisIns.getColumns()
	}
	
	//初始化自定参数
	if(params != null){
		for(var key in datagridParams){
			if(params[key]){
				datagridParams[key]=params[key];
			}
		}
	}
	
	var content =
		"<div style='margin-top:20px;margin-left:10px'>" +
		"	<table id='mainTabldId'></table>	" +
		"	<div id='paginationId'  style='width:85%;' ></div>" +
		"</div>";
	$("body").prepend(content);
	//初始化表格
	$("#"+this.tableId).datagrid(datagridParams);
	//加载数据
	var dataObj = this.getData(this.pageNumber,this.pageSize);
	//初始化翻页控件
	this.initPagination(dataObj.total);
	//渲染数据
	$("#"+this.tableId).datagrid("loadData",dataObj);
	//初始化button
	$("#"+this.tableId).datagrid({
		toolbar: this.toolBar(this)
	});
	//初始化search box 
	this.initSimpleSearchBox();
}


//初始化翻页控件
SimpleTable.prototype.initPagination = function(total){
	var thisIns = this;
	$("#"+this.paginationId).pagination({
		showPageList:false,
		showRefresh:false,
		total:total,
		layout:['first','links','last'],
		pageSize:this.pageSize,
		onSelectPage:function(pageNumber, pageSize){
			thisIns.pageNumber = pageNumber;
			thisIns.pageSize = pageSize;
			thisIns.reloadDataByPage(pageNumber,pageSize);
		},
		onChangePageSize:function(size){
			alert(size);
		}
	});
}

//根据页号加载对应的数据
SimpleTable.prototype.reloadDataByPage =function(pageNumber,pageSize){
	//加载数据
	var dataObj = this.getData(pageNumber-1,pageSize);
	//渲染数据
	$("#"+this.tableId).datagrid("loadData",dataObj);
	this.initPagination(dataObj.total);
}

//和后端交互获取数据
SimpleTable.prototype.getData = function(pageNumber,pageSize,queryParams){
	 var httpClient = new HttpClient("handleTrans.cdo");
	 var cdoRequest=new CDO();
	 var cdoResponse=new CDO();
	 cdoRequest.setStringValue("strServiceName","CoefficientService");
	 cdoRequest.setStringValue("strTransName","getData");
	 cdoRequest.setIntegerValue("pageSize",pageSize);
	 cdoRequest.setIntegerValue("pageNumber",pageNumber);
	 if(queryParams == undefined){
		 cdoRequest.setCDOValue("_queryParamsPrefix",new CDO());
	 }else{
		 cdoRequest.setCDOValue("_queryParamsPrefix",queryParams);
	 }
	 var ret=httpClient.handleTrans(cdoRequest,cdoResponse);
	 if(ret!=null&&ret.getCode()==0){
		return $.parseJSON(cdoResponse.getStringValue("queryResult"));
	}else{
		if(ret==null){
			alert("数据获取失败");
		}else{
			alert(ret.getText());
		}
	}
}


SimpleTable.prototype.rootSearchDivId = function(){
	return _gCreateId(this.tableId+"_rootSearchDivId","search");
}
SimpleTable.prototype.searchboxId = function(){
	return _gCreateId(this.tableId+"_searchboxId","search")
}
SimpleTable.prototype.doSearchId = function(){
	return _gCreateId(this.tableId+"_doSearchId","search");
}
SimpleTable.prototype.initSimpleSearchBox = function(){
	thisIns = this;
	var rootSearchDiv = thisIns.searchContent();
	$("body").prepend(rootSearchDiv);
	thisIns.searchContentInit();
	$(".datagrid-toolbar").append($("#"+thisIns.rootSearchDivId()));
}

//关于combox的设定，combox本身作为一个input标签需要key，下拉列表的中初始化的时候每个一个选项是key value，需要制定才能初始胡
//在获取combox的value时，key需要和columnName相同
SimpleTable.prototype.searchContent = function(){
	thisIns = this;
	//创建收索框的顶级div
	var rootSearchDiv = "<div id='"+thisIns.rootSearchDivId()+"'>";
	var fields = this.queryColumns();
	for (var i = 0; i < fields.length; i++) {
		var muit = "";
		var field = fields[i];
		if(field.columnType == "_combobox"){
			
			muit +="<select id='"+_gCreateId(field.columnName,"search")+"'  style='width:120px'>"
			muit += "<option value= >请选择</option>";
			//muit +="<select id='_"+field.columnName+"_searchId'  style='width:120px'>"
			var list = field.list;
			for(var k=0;k<list.length;k++){
				var kv = list[k];
				 muit += "<option value='"+kv[field.valueName]+ "'>" + kv[field.keyName] + "</option>";
			}
			muit+="</select>";
		}
	}
	rootSearchDiv+= muit+"<a id='"+this.doSearchId()+"' href='javascript:thisIns.doSearch()'>查询</a></div>";
	return rootSearchDiv;
}
SimpleTable.prototype.searchContentInit = function(){
	thisIns = this;
	//创建收索框的顶级div
	var fields = this.queryColumns();
	for (var i = 0; i < fields.length; i++) {
		var muit = "";
		var field = fields[i];
		if(field.columnType == "_combobox"){
			var id = _gCreateId(field.columnName,"search");
			$("#"+id).combobox({
			    valueField:'value',
			    textField:'text'
			});
		}
	}
	
	$("#"+thisIns.doSearchId()).linkbutton({
	    iconCls: 'icon-search',
	    text:"查询" 
	});
}
SimpleTable.prototype.doSearch = function(value){
	var fields = this.queryColumns();
	var searchCDO = new CDO();
	_gGetSubmitValToCDO(searchCDO,fields,new Object(),"search",true);
	console.log(searchCDO.toXML());
	//加载数据
	var dataObj = this.getData(this.pageNumber-1,this.pageSize,searchCDO);
	//渲染数据
	$("#"+this.tableId).datagrid("loadData",dataObj);
	this.initPagination(dataObj.total);
}
SimpleTable.prototype.initMutiSearchBox = function(){
}
SimpleTable.prototype.toolBar = function(simpleTableIns){
	return[
		{
			text:"编辑",
			iconCls:"icon-edit",
			handler:function(){
				simpleTableIns.editFunc()
			}
		},'-',
		{
			text:"添加",
			"iconCls":"icon-save",
			handler:function(){
				simpleTableIns.addFunc()
			}
		},'-',
		{
			text:"删除",
			"iconCls":"icon-cancel",
			handler:function(){
				simpleTableIns.delFunc()
			}
		}
	]
}


//获取要编辑的数据，然后调用editIns渲染出对话框
SimpleTable.prototype.editFunc = function(){
	var row=$("#"+this.tableId).datagrid("getSelected");
	if(!row){
		_gSimMess("请选择要编辑的数据");
		return;
	}
	if(row.length>1){
		_gSimMess("每次只能编辑一条数据");
		return;
	}
	this.editIns.showEditDlg(this,this.tableId+"DlgId",row);
}
//调用addIns渲染出添加对话框
SimpleTable.prototype.addFunc = function(){
	this.addIns.showAddDlg(this);
}
//删除选择的数据
SimpleTable.prototype.delFunc = function(){
	var row=$("#"+this.tableId).datagrid("getSelected");
	if(!row){
		_gSimMess("请选择要删除的数据");
		return;
	}
	if(row.length>1){
		_gSimMess("每次只能删除一条数据");
		return;
	}
	
	 var httpClient = new HttpClient("handleTrans.cdo");
	 var cdoRequest=new CDO();
	 var cdoResponse=new CDO();
	 cdoRequest.setStringValue("strServiceName","CoefficientService");
	 cdoRequest.setStringValue("strTransName","delDataById");
	 cdoRequest.setIntegerValue("lId",row.lId);
	 var ret=httpClient.handleTrans(cdoRequest,cdoResponse);
	 if(ret!=null&&ret.getCode()==0){
		 _gSimMess("数据删除成功");
		 this.reloadDataByPage(this.pageNumber,this.pageSize);
	}else{
		if(ret==null){
			_gSimMess("数据删除失败");
		}else{
			_gSimMess(ret.getText());
		}
	}
}
SimpleTable.prototype.queryColumns = function(){
	return this.queryParams.columns;
}

SimpleTable.prototype.simMess = function(msg){
	_gSimMess(msg);
}
 
this.globalSaveForDlg = function (formId,url){
	$("#"+formId).form("submit",{
		url:url,
		onSubmit:function(){
			return true;
		},
		success:function(obj){
			obj = $.parseJSON(obj);
		}
	})
}


/**
 * ===================================添加相关======================
 * @returns
 */
function SimpleAdd(addParams){
	
	this.addDlgId = null;
	this.dlgFormId = null;
	this.addUrl = null;
	this.$dialogIns = null;
	this.tableIns = null;
	this.addParams= addParams;
	this._typeObj = new FieldType();
	
	
	//获取对话框的id
	SimpleAdd.prototype.getAddDlgId = function(){
		if(this.editDlgId == null){
			return "mainAddDlgId";
		}
		return this.editDlgId;
	}
	SimpleAdd.prototype.saveValue =function(){
		 var httpClient = new HttpClient("handleTrans.cdo");
		 var cdoRequest=new CDO();
		 var cdoResponse=new CDO();
		 cdoRequest.setStringValue("strServiceName","CoefficientService");
		 cdoRequest.setStringValue("strTransName","saveData");
		 var columns = this.getColumns().columns;
		 _gGetSubmitValToCDO(cdoRequest,columns,this.dlgFormId,"add",false);
		 var ret=httpClient.handleTrans(cdoRequest,cdoResponse);
		 if(ret!=null&&ret.getCode()==0){
			 this.tableIns.reloadDataByPage(this.tableIns.pageNumber,this.tableIns.pageSize);
			 this.tableIns.simMess("添加成功");
			 this.$dialog.dialog("close");
			 return;
		}else{
			if(ret==null){
				 this.tableIns.simMess("保存失败");
			}else{
				 this.tableIns.simMess(ret.getText());
			}
		}
	}
	
	//对话框上方按钮
	SimpleAdd.prototype.getToolBar = function(){
		var thisIns = this;
		return [
			{
				text:"保存",
				"iconCls":"icon-save",
				handler:function save(){
					thisIns.saveValue();
				}
			},
			{
				text:"取消",
				"iconCls":"icon-cancel",
				handler:function cancel(){
					thisIns.$dialog.dialog("close");
				}
			}
		]
	}
	
	SimpleAdd.prototype.showAddDlg = function(tableIns){
		this.tableIns = tableIns;
		this.dlgFormId = this.getAddDlgId()+"FormId";
		var columns = this.getColumns();
		var columnsDes = columns.columns;
		var xmlVal = "<div style='bottom:20px'  align='center' width=100%  ><form>" +
						"<table border='1' bordercolor='#a0c6e5' style='border-collapse:collapse;'  align='center' width='100%' >";
		for(var i=0;i<columnsDes.length;i++){
			var column = columnsDes[i];
			xmlVal+= this.getFieldDesForAdd(column);
		}
		xmlVal += "</table></form></div>";
		var dlg={
		        height: 300,
		        width: 700,
		        content:xmlVal,
		        title: '添加'
		        };
		this.$dialog = $('<div id='+this.getAddDlgId()+'></div>').dialog(dlg);
		this.$dialog.dialog({
			 toolbar:this.getToolBar()
		});
		this.$dialog.dialog('open');
	}
	//获取待添加的列
	SimpleAdd.prototype.getColumns = function(){
		return this.addParams;
	}
	SimpleAdd.prototype.getFieldDesForAdd = function(column){
		var key = column.columnName;
		//获取key的td描述
		var strXml ="<tr>";
		strXml+=this.getKeyDes(column);
		//根据列类型，参数，获取key对应的value的td描述
		strXml +=this.getValDes(new Object(),key,"add",column);
		strXml+="</tr>";
		return strXml;
	}
	
	//key字段的td描述
	SimpleAdd.prototype.getKeyDes = function(column){
		return "<td align='right' width='50%' ><label for='"+column.label+"'>"+column.label+"</label></td>";
	}
	
	//value字段的td描述
	SimpleAdd.prototype.getValDes = function (row,key,atcionType,column){
		return "<td align='left' width='50%' >"+this._typeObj[column.columnType+"Type"](row,key,atcionType,column)+"</td>";
	}
}



/**
 * =================================编辑相关======================================
 */
function SimpleEdit(editParams){
	
	this.editDlgId = null;
	this.dlgFormId = null;
	this.editUrl = null;
	this.tableIns = null;
	this.$dialogIns = null;
	this.editParams =editParams;
	this._typeObj = new FieldType();
	
	SimpleEdit.prototype.getEditDlgId = function(){
		if(this.editDlgId == null){
			return "mainEditDlgId";
		}
		return this.editDlgId;
	}
	SimpleEdit.prototype.saveValue = function(){
		 var httpClient = new HttpClient("handleTrans.cdo");
		 var cdoRequest=new CDO();
		 var cdoResponse=new CDO();
		 cdoRequest.setStringValue("strServiceName","CoefficientService");
		 cdoRequest.setStringValue("strTransName","editData");
		 var columns = this.getColumns().columns;
		 _gGetSubmitValToCDO(cdoRequest,columns,this.dlgFormId,"edit",false);
		 var ret=httpClient.handleTrans(cdoRequest,cdoResponse);
		 if(ret!=null&&ret.getCode()==0){
			 this.tableIns.reloadDataByPage(this.tableIns.pageNumber,this.tableIns.pageSize);
			 this.tableIns.simMess("修改成功");
			 this.$dialog.dialog("close");
			 return;
		}else{
			if(ret==null){
				 this.tableIns.simMess("保存失败");
			}else{
				 this.tableIns.simMess(ret.getText());
			}
		}
	}
	
	SimpleEdit.prototype.showEditDlg = function(tableIns,DlgId,row){
		this.editDlgId = DlgId;
		this.dlgFormId = DlgId+"FormId";
		this.tableIns = tableIns;
		var columns = this.getColumns();
		var columnsDes = columns.columns;
		var xmlVal = "<div style='bottom:20px'  align='center' width=100%  ><form>" +
						"<table border='1' bordercolor='#a0c6e5' style='border-collapse:collapse;'  align='center' width='100%' >";
		for(var i=0;i<columnsDes.length;i++){
			var column = columnsDes[i];
			xmlVal+= this.getFieldDes(row,column);
		}
		xmlVal += "</table></form></div>";
		var dlg={
		        height: 300,
		        width: 700,
		        content:xmlVal,
		        title: '编辑'
		        };
		this.$dialog = $('<div id='+this.getEditDlgId()+'></div>').dialog(dlg);
		this.$dialog.dialog({
			 toolbar:this.getToolBar()
		});
		this.$dialog.dialog('open');
	}
	//对话框上方按钮
	SimpleEdit.prototype.getToolBar = function(){
		var thisIns = this;
		return [
			{
				text:"保存",
				"iconCls":"icon-save",
				handler:function save(){
					 thisIns.saveValue();
				}
			},
			{
				text:"取消",
				"iconCls":"icon-cancel",
				handler:function cancel(){
					thisIns.$dialog.dialog("close");
				}
			}
		]
	}
	//获取待编辑的列
	SimpleEdit.prototype.getColumns = function(){
		return this.editParams;
	}
	//渲染编辑对话框
	SimpleEdit.prototype.getFieldDes = function(row,column){
		var key = column.columnName;
		//获取key的td描述
		var strXml ="<tr>";
		strXml+=this.getKeyDes(column);
		//根据列类型，参数，获取key对应的value的td描述
		strXml +=this.getValDes(row,key,"edit",column);
		strXml+="</tr>";
		return strXml;
	}
	
	//key字段的td描述
	SimpleEdit.prototype.getKeyDes = function(column){
		return "<td align='right' width='50%' ><label for='"+column.label+"'>"+column.label+"</label></td>";
		//return "<td align='right' width='50%' >"+keyName+"</td>";
	}
	
	//value字段的td描述
	SimpleEdit.prototype.getValDes = function (row,key,actionType,column){
		return "<td align='left' width='50%' >"+this._typeObj[column.columnType+"Type"](row,key,actionType,column)+"</td>";
	}
}




/*
 * ============================根据列类型获取编辑列的内容====================================
 */
function FieldType(){
	
	FieldType.prototype.callType = function(type){
		var valDes = this[type].call(type);
	}
	
	FieldType.prototype._stringType = function (row,key,type,column){
		var htmlTr="";
		if(column.isEdit){
			htmlTr = "<input class='easyui-textbox' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?"":row[key])+"'/>";
		 }else{
			 htmlTr = "<input class='easyui-textbox'  editable='false' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?"":row[key])+"'/>";
		 }
		return htmlTr;
	}
	FieldType.prototype._intType = function(row,key,type,column){
		return this._longType(row,key,type,column);
	}
	FieldType.prototype._longType = function (row,key,type,column){
		var htmlTr="";
		if(column.isEdit){
			htmlTr+="<input class='easyui-numberbox' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?0:row[key])+"'/>";
		 }else{
			 htmlTr+="<input class='easyui-numberbox' editable='false' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?0:row[key])+"'/>";
		 }
		 return htmlTr;
	}
	
	FieldType.prototype._doubleType = function (row,key,type,column){
		var htmlTr="";
		if(column.isEdit){
			htmlTr+="<input class='easyui-numberbox' data-options='precision:5' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?0:row[key])+"'/>";
		 }else{
			 htmlTr+="<input class='easyui-numberbox' data-options='precision:5' editable='false' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?0.0:row[key])+"'/>";
		 }
		return htmlTr;
	}
	
	FieldType.prototype._booleanType = function (row,key,type,column){
		var htmlTr ="<select class='easyui-combobox' id='"+_gCreateId(key,type)+"'>";
		 if(val == true){
			 htmlTr +="  <option value='true' selected='selected'>true</option><option value='false' >false</option>";
		 }else{
			 htmlTr +="  <option value='true' >true</option><option value='false' selected='selected' >false</option>";
		 }
		 htmlTr+="</select>";
		 return htmlTr;
	}
	
	FieldType.prototype._dateType = function (row,key,type,column){
		var htmlTr="";
		if(column.isEdit){
			 htmlTr+="<input class='easyui-datebox' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?"2099-12-12":row[key])+"'/>";
		}else{
			htmlTr+="<input class='easyui-datebox' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?"2099-12-12":row[key])+"'/>";	
		}
		return htmlTr;
	}
	
	FieldType.prototype._timeType = function (row,key,type,column){
		var htmlTr="";
		if(column.isEdit){
			 htmlTr+="<input class='easyui-timespinner' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?"23:59:59":row[key])+"'/>";
		}else{
			 htmlTr+="<input class='easyui-timespinner' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?"23:59:59":row[key])+"'/>";
		}
		return htmlTr;
	}
	
	FieldType.prototype._dateTimeType = function (row,key,type,column){
		var htmlTr="";
		if(column.isEdit){
			 htmlTr+="<input class='easyui-datetimebox' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?"2099-12-12":row[key])+"'/>";
		}else{
			 htmlTr+="<input class='easyui-datetimebox' id='"+_gCreateId(key,type)+"' value='"+(!row[key]?"2099-12-12":row[key])+"'/>";
		}
		return htmlTr;
	}
	
	FieldType.prototype._comboboxType = function (row,key,type,column){
		//var list = $.parseJSON(column.list);
		var list = column.list;
		var val = row[key];
		var htmlTr ="<select class='easyui-combobox' data-options=valueField:'value',textField:'text' id='"+_gCreateId(key,type)+"'>";
		for(var i=0;i<list.length;i++){
			var kv = list[i];
			if(val == kv["value"]){
				 htmlTr +="<option value='"+kv["value"]+"' selected='selected'>"+kv["text"]+"</option>";
			}else{
				 htmlTr +="<option value='"+kv["value"]+"' >"+kv["text"]+"</option>";
			}
		}
		 htmlTr+="</select>";
		 return htmlTr;
	}
	
	
	
}



/**
 * ==========================消息提示=========================
 */



