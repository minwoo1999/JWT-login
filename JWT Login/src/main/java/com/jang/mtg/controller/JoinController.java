package com.jang.mtg.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jang.doc.utils.AES256Util;
import com.jang.mtg.model.User;
import com.jang.mtg.service.UserService;





@Controller
public class JoinController {

	@Autowired 
	private UserService userService;

	@RequestMapping(value = "/join", method = RequestMethod.GET) // ??κ°?? ??­ form μΆλ ₯
	public String userJoinForm(Model model) {
		model.addAttribute("user", new User());
		return "joinForm";
	}

	@RequestMapping(value = "/join", method = RequestMethod.POST) // ?? ₯?Ό ?΄?© ???₯
	public String onSubmit(@Valid User user, BindingResult result, Model model) throws Exception {

		if (result.hasErrors()) {
			model.addAllAttributes(result.getModel());
			return "joinForm";
		}
		//passwd ??Έ? ?¨λ°©ν₯ ??Έ?
		
		BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
		String hashPass=passwordEncoder.encode(user.getPass());
		user.setPass(hashPass);
		
		//????Ό ??Έ? aes256 ?λ°©ν₯ ??Έ?
		
		Path filePath=Paths.get("C://jj/key3.txt");
		String key=Files.readString(filePath);
		
		AES256Util aes256=new AES256Util(key);
		
		String hashBirthday=aes256.aesEncode(user.getBirthday());
		user.setBirthday(hashBirthday);
		

		if (this.userService.insertUser(user) != 0) {
			user.setPass("");
			model.addAttribute("user", user);
			model.addAttribute("msgCode", "?±λ‘λ??΅??€. λ‘κ·Έ?Έ??¬ μ£Όμ­??€.");// ?±λ‘μ±κ³?
			return "login";
		} else {
			model.addAttribute("msgCode", "?±λ‘? ?€?¨????΅??€. ?€? ????¬ μ£Όμ­??€.");// ?±λ‘μ€?¨
			return "joinForm";
		}

	}

	@RequestMapping(value = "/idCheck", method = RequestMethod.GET) // IDμ€λ³΅μ²΄ν¬
	@ResponseBody
	public String idCheck(HttpServletRequest request) {
		
		System.out.println("1");

		String userId = request.getParameter("userId");

		Gson gson = new Gson();
		JsonObject object = new JsonObject();

		User loginUser = this.userService.getUser(userId);

		if (loginUser != null) {
			object.addProperty("msg", "false");
			return gson.toJson(object).toString();
		} else {
			object.addProperty("msg", "true");
			return gson.toJson(object).toString();
		}
	}
	


}
