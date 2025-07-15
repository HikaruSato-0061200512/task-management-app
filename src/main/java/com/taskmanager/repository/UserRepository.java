package com.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.entity.User;

//<User, Integer> ＝ エンティティとその主キー型
public interface UserRepository extends JpaRepository<User, Integer>{
	
	//「username と password の両方に一致するレコードを1件取得する」メソッド
	    User findByUserNameAndPassword(String userName, String password);

}
