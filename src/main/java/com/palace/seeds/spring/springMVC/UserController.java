package com.palace.seeds.spring.springMVC;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/user")
public class UserController {


	@RequestMapping("/getUser")
	public @ResponseBody Map<String,Object> getUser(@RequestParam String lId){
		HashMap<String,Object> map = new HashMap<String, Object>(){ {put("name","xiaoMing");put("age",12);}};
		return map;
	}
}
