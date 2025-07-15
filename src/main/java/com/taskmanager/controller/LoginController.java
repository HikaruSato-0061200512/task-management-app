package com.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taskmanager.bean.UsersBean;
import com.taskmanager.entity.User;
import com.taskmanager.form.LoginForm;
import com.taskmanager.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
	
	/**
	 * この中に Spring がインスタンスを自動で入れてくれる
	 * 自分でnew しなくていい
	 */
	@Autowired
	private UserRepository usersRepository;
	
	@Autowired
	HttpSession session;
	
	@RequestMapping(path="/", method = RequestMethod.GET)//GET用
	public String showLoginForm() {
		return "login";
	}
	
	/**
	 * loginForm はフォームから送られてきた username と password を受け取る。
	 * session はログイン成功時にユーザー情報を保存するためのもの。
	 * model は画面にエラーを返したいときに使う。
	 */
	@RequestMapping(path = "/login", method = RequestMethod.POST)//POST用
	public String doLogin(@ModelAttribute LoginForm loginForm, HttpSession session, Model model) {
		//フォームに入力された内容を取得する
		String userName = loginForm.getUserName();
		String password = loginForm.getPassword();
		User users = usersRepository.findByUserNameAndPassword(userName, password);
		
		 // 🔽 一時的にDBの全ユーザーを出力して確認
	    List<User> allUsers = usersRepository.findAll();
	    for (User u : allUsers) {
	        System.out.println("DB user: " + u.getUserName() + " / " + u.getPassword());
	    }

		if (users != null) {
			UsersBean usersBean = new UsersBean();
			usersBean.setUserName(users.getUserName());// ← users から取得
			usersBean.setPassword(null);// ← ユーザー情報をセッションに格納する際にパスワードは基本的に保存しない
			session.setAttribute("users", usersBean);
			// 一覧へ画面遷移
			return "redirect:/home";

		} else {
			model.addAttribute("errMessage", "ユーザ名、またはパスワードが間違っています。");
			return "login";
		}
	}

	}