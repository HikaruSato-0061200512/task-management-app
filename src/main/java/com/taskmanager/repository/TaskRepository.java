package com.taskmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmanager.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
	
	List<Task> findByStatus(String status);
	
	List<Task> findByStatusIn(List<String> statuses);

}
