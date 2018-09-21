package com.palace.seeds.db.jdbcsharding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;

import com.dangdang.ddframe.rdb.sharding.api.rule.BindingTableRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.ShardingRule;
import com.dangdang.ddframe.rdb.sharding.api.rule.TableRule;
import com.dangdang.ddframe.rdb.sharding.api.strategy.database.DatabaseShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.api.strategy.table.TableShardingStrategy;
import com.dangdang.ddframe.rdb.sharding.jdbc.ShardingDataSource;
import com.palace.seeds.db.jdbcsharding.algorithm.ModuloDatabaseShardingAlgorithm;
import com.palace.seeds.db.jdbcsharding.algorithm.ModuloTableShardingAlgorithm;

public class MainSharding {

	
	public static void main(String[] args) throws Exception {
		DataSource dataSource = getShardingDataSource();
		printSimpleSelect(dataSource);
	}
	
	private static void printSimpleSelect(final DataSource dataSource) throws Exception{
        String sql = "SELECT i.* FROM t_order o JOIN t_order_item i ON o.order_id=i.order_id WHERE o.user_id=? AND o.order_id=?";
      
            Connection conn = dataSource.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, 10);
            preparedStatement.setInt(2, 1001);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1));
                System.out.println(rs.getInt(2));
                System.out.println(rs.getInt(3));
            }
    }
	
	
/*    ShardingRule shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(orderTableRule, orderItemTableRule))
            .bindingTableRules(Collections.singletonList(new BindingTableRule(Arrays.asList(orderTableRule, orderItemTableRule))))
            .databaseShardingStrategy(new DatabaseShardingStrategy("user_id", new ModuloDatabaseShardingAlgorithm()))
            .tableShardingStrategy(new TableShardingStrategy("order_id", new ModuloTableShardingAlgorithm())).build();*/
	
/*	public static DataSource getShardingDataSource(){
		DataSourceRule dataSourceRule=new DataSourceRule(createDataSourceMap());
		TableRule orderTableRule=TableRule.builder("t_order").actualTables(Arrays.asList("t_order_0","t_order_1")).dataSourceRule(dataSourceRule).build();
		TableRule orderItemRule=TableRule.builder("t_order_item").actualTables(Arrays.asList("t_order_item_0","t_order_item_1")).dataSourceRule(dataSourceRule).build();
		ShardingRule shardingRule =ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(orderTableRule,orderItemRule))
					.bindingTableRules(Collections.singletonList(new BindingTableRule(Arrays.asList(orderTableRule,orderItemRule))))
					.databaseShardingStrategy(new DatabaseShardingStrategy("user_id", new ModuloDatabaseShardingAlgorithm()))
					.tableShardingStrategy(new TableShardingStrategy("order_id", new ModuloTableShardingAlgorithm())).build();
		return new ShardingDataSource(shardingRule);
	}*/
	
    private static ShardingDataSource getShardingDataSource() {
        DataSourceRule dataSourceRule = new DataSourceRule(createDataSourceMap());
        TableRule orderTableRule = TableRule.builder("t_order").actualTables(Arrays.asList("t_order_0", "t_order_1")).dataSourceRule(dataSourceRule).build();
        TableRule orderItemTableRule = TableRule.builder("t_order_item").actualTables(Arrays.asList("t_order_item_0", "t_order_item_1")).dataSourceRule(dataSourceRule).build();
        ShardingRule shardingRule = ShardingRule.builder().dataSourceRule(dataSourceRule).tableRules(Arrays.asList(orderTableRule, orderItemTableRule))
                .bindingTableRules(Collections.singletonList(new BindingTableRule(Arrays.asList(orderTableRule, orderItemTableRule))))
                .databaseShardingStrategy(new DatabaseShardingStrategy("user_id", new ModuloDatabaseShardingAlgorithm()))
                .tableShardingStrategy(new TableShardingStrategy("order_id", new ModuloTableShardingAlgorithm())).build();
        return new ShardingDataSource(shardingRule);
    }
	
	public static Map<String,DataSource> createDataSourceMap(){
		Map<String,DataSource> map=new HashMap<String,DataSource>();
		map.put("ds_0",createDataSource("ds_0"));
		map.put("ds_1",createDataSource("ds_1"));
		return map;
	}
	
	public static DataSource createDataSource(String ds){
		BasicDataSource bs=new BasicDataSource();
		bs.setDriverClassName(com.mysql.jdbc.Driver.class.getName());
		bs.setUrl(String.format("jdbc:mysql://localhost:3306/%s", ds));
		bs.setUsername("root");
		bs.setPassword("111111");
		return bs;
	}
	
	@Test
	public void testStringF(){
		String s = String.format("string/%sbing%saaa%s", "name1","name2","name3"); 
		System.out.println(s);
	}
}
