package com.jang.mtg.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jang.doc.utils.AES256Util;
import com.jang.mtg.model.User;
import com.jang.mtg.service.UserService;





@Controller
public class LoginController {

	@Autowired // @Resource(name="userService")
	private UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.GET) // urlλ§? ?? ₯?΄? ?€?΄?¨λ°©μ
	public String toLoginView(Model model) {

		model.addAttribute("user", new User());
		return "login";

	}

	@RequestMapping(value = "/login", method = RequestMethod.POST) // ?Ό?? ?? ₯λ°μ? λ°©μ post
	public String onSubmit(@Valid User user, BindingResult result, Model model,HttpSession session) {

		if (result.hasFieldErrors("id") || result.hasFieldErrors("pass")) {
			model.addAllAttributes(result.getModel());
			return "login";
		}
		try {

			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

			// ?μ‘΄κ?κ³λ?? ?€? ? serviceκ°μ²΄? getUser()λ©μΈ?λ₯? ?ΈμΆν?¬ User ? λ³΄λ?? ?½?΄?¨?€.
			User loginUser = this.userService.getUser(user.getId());
			
			if (loginUser==null) {
				model.addAllAttributes(result.getModel());
				return "login";
			}
			

			if (passwordEncoder.matches(user.getPass(), loginUser.getPass())) {

				session.setAttribute("userId",loginUser.getId());
				session.setAttribute("userName", loginUser.getName());
				model.addAttribute("loginUser", loginUser);
				return "redirect:/index";
			} else {
				result.rejectValue("pass", "error.password.user", "");
				model.addAllAttributes(result.getModel());
				return "login";
			}

		} catch (EmptyResultDataAccessException e) {
			result.rejectValue("id", "error.id.user", "ΎΖΐΜ΅π°‘ ΄ΩΈ¨΄Ο΄Ω....");
			model.addAllAttributes(result.getModel());
			return "login";
		}
	}

	@RequestMapping(value = "/editUser", method = RequestMethod.GET)
	public String toUserEditView(Model model,HttpSession session) throws Exception {
		
		String userId = (String) session.getAttribute("userId");
		User loginUser = this.userService.getUser(userId);

		if (userService == null) {
			model.addAttribute("userId", "");
			model.addAttribute("msgCode", "?±λ‘λμ§???? ??΄????€"); // ?±λ‘λμ§???? ??΄?

			return "login";
		} else {

			// ??Έ?? password ?­? 

			// ????Ό λ³΅νΈ? aes256

			Path filePath = Paths.get("C:/jj/key3.txt");
			String key = Files.readString(filePath);
			AES256Util aes256 = new AES256Util(key);

			String hashBirthday = loginUser.getBirthday();
			String decBrithday = aes256.aesDecode(hashBirthday);

			loginUser.setBirthday(decBrithday);

			model.addAttribute("user", loginUser);
			return "editForm";
		}
	}

	@RequestMapping(value = "/editUser", method = RequestMethod.POST)
	public String onEditSave(@ModelAttribute User user, Model model) throws Exception {

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashPass = passwordEncoder.encode(user.getPass());
		user.setPass(hashPass);

		// ????Ό ??Έ?

		Path filePath = Paths.get("C:/jj/key3.txt");
		String key = Files.readString(filePath);

		AES256Util aes256 = new AES256Util(key);

		String hashBrithday = aes256.aesEncode(user.getBirthday());
		user.setBirthday(hashBrithday);

		if (this.userService.updateUser(user) != 0) {
			user.setPass("");
			model.addAttribute("msgCode", "?¬?©? ? λ³΄μ? κ°? ?? ???΅??€");
			model.addAttribute("user", user);
			return "login";
		} else {
			model.addAttribute("msgCode", "?¬?©? ? λ³΄μ? ? ?€?¨????΅??€");
			return "editForm";
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET) // ?λ¬΄λ°κ°μ?΄ ?€?΄???

	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";

	}

	@RequestMapping(value = "/findId", method = RequestMethod.GET) // ?λ¬΄λ°κ°μ?΄ ?€?΄???

	public String toFIndIdForm(Model model) {
		System.out.println("getλ°©μ");
		model.addAttribute("user", new User());
		return "findIdForm";

	}

	@RequestMapping(value = "/findId", method = RequestMethod.POST)
	public String findIdSubmit(@Valid User user, BindingResult result, Model model) {

		if (result.hasFieldErrors("name") || result.hasFieldErrors("email")) {
			model.addAllAttributes(result.getModel());
			return "findIdForm";
		}

		try {

			User findUser = this.userService.findId(user.getName(), user.getEmail());
			System.out.println(findUser);
			System.out.println("3");
			if (findUser.getName().equals(user.getName()) && findUser.getEmail().equals(user.getEmail())) {

				System.out.println("1");
				model.addAttribute("findUser", findUser);
				return "findIdSuccess";
			} 
			else {
				System.out.println("2");
				result.rejectValue("email", "error.email.user", "?΄λ©μΌ?΄ ?ΌμΉνμ§???΅??€.");
				model.addAllAttributes(result.getModel());
				return "findIdForm";
			}

		} catch (NullPointerException e ) {
			System.out.println("4");
			result.rejectValue("name", "error.name.user", "?΄λ¦? λ°? ?΄λ©μΌ?΄ μ‘΄μ¬?μ§???΅??€.");
			model.addAllAttributes(result.getModel());
			return "findIdForm";
		}

	}

	@RequestMapping(value = "/findPass", method = RequestMethod.GET) // ?λ¬΄λ°κ°μ?΄ ?€?΄???

	public String toFIndPassForm(Model model) {
		System.out.println("getλ°©μ");
		model.addAttribute("user", new User());
		return "findPassForm";

	}

	@RequestMapping(value = "/findPass", method = RequestMethod.POST)

	public String findPassSubmit(@Valid User user, BindingResult result, Model model, RedirectAttributes redirect) {

		System.out.println("postλ°©μ");
		if (result.hasFieldErrors("id") || result.hasFieldErrors("email")) {

			model.addAllAttributes(result.getModel());
			return "findPassForm";
		}
		try {

			User findUser = this.userService.findPass(user.getId(), user.getEmail());
			if(findUser.getId().equals(user.getId()) &&findUser.getEmail().equals(user.getEmail())) {
				
				model.addAttribute("findUser", findUser);
				return "updatePassForm";
			}
			else {
			
				result.rejectValue("email", "error.email.user", "??΄? λ°? ?΄λ©μΌ ? λ³΄κ??ΌμΉνμ§???΅??€.");
				model.addAllAttributes(result.getModel());
				return "findPassForm";
			}
			

		} catch (NullPointerException e) {

			result.rejectValue("id", "error.id.user", "??΄? λ°? ?΄λ©μΌ ? λ³΄κ??ΌμΉνμ§???΅??€.");
			return "findPassForm";
		}
	}

	@RequestMapping(value = "/updatePass", method = RequestMethod.GET) // ?λ¬΄λ°κ°μ?΄ ?€?΄???

	public String toFIndupdateForm(User user, Model model) {
		System.out.println("getλ°©μ");
		model.addAttribute("userId", user.getId());

		model.addAttribute("user", new User());
		return "updatePassForm";

	}

	@RequestMapping(value = "updatePass", method = RequestMethod.POST)
	public String updatePass(@Valid User user, BindingResult result, Model model) throws Exception {

		if (result.hasFieldErrors("id") || result.hasFieldErrors("pass")) {
			model.addAllAttributes(result.getModel());
			return "updatePassForm";
		}
		// passwd ??Έ? ?¨λ°©ν₯ ??Έ?

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashPass = passwordEncoder.encode(user.getPass());
		user.setPass(hashPass);

		if (this.userService.updatePass(user) == 1) {
			model.addAttribute("userId", user.getId());
			return "updatePassSuccess";
		} else {
			result.rejectValue("id", "error.password.user", "?¨?€?? λ³?κ²½μ ?€?¨????΅??€. ?€????΄μ£Όμ...");
			return "updatePassForm";
		}
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.GET) // ?λ¬΄λ°κ°μ?΄ ?€?΄???

	public String test() {
	
		return "test";

	}

}
