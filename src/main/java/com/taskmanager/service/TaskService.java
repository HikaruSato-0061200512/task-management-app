package com.taskmanager.service;

import java.time.LocalDate;
import java.util.List;

import com.taskmanager.entity.Task;

public interface TaskService {
	
	//タスクを全て取得する　ホーム画面や一覧画面で使用
	List<Task> findAllTasks();
	
	//ステータスでタスクを絞り込んで取得　「Doing」「Done」などカンバン表示で活用
	List<Task> findTaskByStatus(String status);
	
	//タスクを保存　フォーム送信後の処理など
	Task saveTask(Task task);
	
	//IDをタスクを取得する　編集画面に表示するときなど
	Task findTaskByTaskId(Long taskId);
	
	//タスクを削除する　削除ボタンを押したときなど
	void deleteTask(Long taskId);
	
	//複数のステータスを取得する
	List<Task> findTaskByStatusList(List<String> statuses);
	
	//土日を除いた営業日数（平日のみの経過日数）をカウントする
	long countBusinessDaysBetween(LocalDate start, LocalDate end);

}
