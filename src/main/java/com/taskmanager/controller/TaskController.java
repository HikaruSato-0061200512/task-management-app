package com.taskmanager.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taskmanager.entity.Task;
import com.taskmanager.service.TaskService;

@Controller
public class TaskController {
	
	private final TaskService taskService;
	
	@Autowired
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}
	
	//タスク一覧を各ステータスに表示する
	@GetMapping("/home")
	public String showHome(Model model) {
		
	    /**
	     * ログを出力するコード(いつでも使えるよう、コメントしている)
	    System.out.println("取得件数：" + taskService.findTaskByStatus("Next").size());
	    for (Task task : taskService.findTaskByStatus("Next")) {
	        System.out.println("タイトル：" + task.getTitle() + " | ステータス：" + task.getStatus());
	    }
	    */
		
		 // TaskServiceから各ステータスのタスクを取得(これは Thymeleaf で <div th:each="task : ${NextTasks}"> のように使われるためのデータ)
	    List<Task> nextTasks = taskService.findTaskByStatus("Next");
	    List<Task> doingTasks = taskService.findTaskByStatus("Doing");
	    List<Task> iceboxTasks = taskService.findTaskByStatus("Icebox");
	    
	    //TaskServiceからDoneまたはApprovedのタスクを取得
	    List<Task> allDoneTasks = taskService.findTaskByStatusList(Arrays.asList("Done", "Approved"));
	    
	    //「Done列」のタスクを「Done または Approved」に限定し、Archived（アーカイブ済み）を 表示しないようにする 
	    List<Task> filteredDoneTasks = allDoneTasks.stream()
	    		.filter(task -> !"Archived".equals(task.getStatus()))//task.getStatus() が "Archived" と等しくないときだけフィルタリング
	    		.collect(Collectors.toList());//新しいリストに集める (終端操作)
	    
	    
	    /**
	     * 「Doingに入った日」からの経過日数経過日数を計算
	     * (doingTasks というリストの中にあるすべてのタスクを、1つずつ task という変数に取り出して処理する)
	     * 拡張for文
	     */
	    for (Task task : doingTasks) {
	        if (task.getEnteredDoingAt() != null) {
	            long days = taskService.countBusinessDaysBetween(task.getEnteredDoingAt(), LocalDate.now());
	            task.setDaysSinceStarted(days); // 表示用
	        }
	    }
	    
	    for (Task task : nextTasks) {
	        if (task.getEnteredDoingAt() != null) {
	            long days = taskService.countBusinessDaysBetween(task.getEnteredDoingAt(), LocalDate.now());
	            task.setDaysSinceStarted(days);
	        }
	    }
	    
	    
	    model.addAttribute("NextTasks", nextTasks);
	    model.addAttribute("DoingTasks", doingTasks);
	    model.addAttribute("IceboxTasks", iceboxTasks);
	    model.addAttribute("DoneTasks", filteredDoneTasks); // Done列に表示
	   
	    return "home/home";
	    
	}
	
	//アーカイブを表示する
	@GetMapping("/archive")
	public String showArchive(Model model) {
		List<Task> archivedTasks = taskService.findTaskByStatus("Archived");
		model.addAttribute("ArchivedTasks", archivedTasks);
		
		return "/home/archive";
	}
	
	
	//タスク追加フォームを表示
	@GetMapping("/tasks/add")
	public String showAddForm(@RequestParam(name="status", required = false) String status, Model model) {
		Task task = new Task();
		
		//ステータスの初期値設定
		if(status != null) {
			task.setStatus(status);//パラメータで受け取ったステータスをセット
		}
		
		model.addAttribute("task", task);//空のTaskをフォームに渡す
		return "task/addForm";
	}
	
	//フォーム送信でタスクを保存
	@PostMapping("/tasks/add")
	public String addTask(@ModelAttribute Task task) {
		
		task.setCreatedAt(LocalDate.now()); // 登録日を保存
			
			// ★ 直接Doingとして作成された場合に記録
		    if ("Doing".equals(task.getStatus())) {
		        task.setEnteredDoingAt(LocalDate.now());
		        }
		    taskService.saveTask(task);//DBへ保存
			return "redirect:/home";//登録後に一覧に戻る
		}
	
	//タスクをクリックしてタスク編集フォームを表示
	@GetMapping("/tasks/edit/{taskId}")
	public String showEditForm(@PathVariable("taskId") Long taskId, Model model) {
		//IDに対応するタスクを取得する
		Task task = taskService.findTaskByTaskId(taskId);
		
		//modelにタスクを渡す（HTML画面で使えるようにする）
		model.addAttribute("task", task);
		
		//編集画面に遷移する
		return "task/editForm";
	}
	
	//フォーム送信でタスクを更新・削除する
	@PostMapping("/tasks/edit/{taskId}")
	public String updateOrDeleteTask(@PathVariable("taskId") Long taskId, @ModelAttribute Task task, @RequestParam("action") String action) {

		if("update".equals(action)) {
			// taskId を使って、既存のタスクをDBから取得
			Task taskToUpdate = taskService.findTaskByTaskId(taskId);
			
			// フォームから受け取った値で、更新
			taskToUpdate.setTitle(task.getTitle());
			taskToUpdate.setStatus(task.getStatus());
			taskToUpdate.setDescription(task.getDescription());
			taskToUpdate.setReport(task.getReport());
			taskToUpdate.setDeadline(task.getDeadline());
			
			/**
			 * 更新されたかの確認ログ
			System.out.println("更新後タスクの内容:");
			System.out.println("タイトル：" + taskToUpdate.getTitle());
			System.out.println("ステータス：" + taskToUpdate.getStatus());
			*/
			
			// 更新された内容を保存
			taskService.saveTask(taskToUpdate);
		} else if("delete".equals(action)) {
			taskService.deleteTask(taskId);
		}
		// 一覧画面にリダイレクト
		return "redirect:/home";
	}
	
	//「開始」ボタンを押したら started = trueになって、Doingになる
	@PostMapping("/tasks/start")
	public String startTask(@RequestParam("taskId") Long taskId) {
		Task task = taskService.findTaskByTaskId(taskId);
		task.setStatus("Doing");
		task.setStarted(true);//開始フラグをtrueにする
		task.setStartedAt(LocalDate.now());//開始ボタンを押した時にstartedAtを保存
		// ★ enteredDoingAt が未設定ならセット（1回だけ）
	    if (task.getEnteredDoingAt() == null) {
	        task.setEnteredDoingAt(LocalDate.now());
	    }
		taskService.saveTask(task);
		return "redirect:/home";
	}
	
	//「完了」ボタンを押したら completed = trueになって、Doneになる
	@PostMapping("/tasks/complete")
	public String completeTask(@RequestParam("taskId") Long taskId) {
		Task task = taskService.findTaskByTaskId(taskId);
		task.setStatus("Done");
		task.setCompleted(true); //完了フラグをtrueにする
		taskService.saveTask(task);
		return "redirect:/home";
	}
	
	//「承認」ボタンを押したら approved = trueになって、承認後のステータスApprovedになる
	@PostMapping("/tasks/approve")
	public String approveTask(@RequestParam("taskId") Long taskId) {
		Task task = taskService.findTaskByTaskId(taskId);
		
		// すでに承認済みなら、承認を解除
	    if ("Approved".equals(task.getStatus())) {
	        if (task.getPreviousStatus() != null) {
	            task.setStatus(task.getPreviousStatus());// ひとつ前の状態に戻す
	            task.setPreviousStatus(null);// previousStatus はリセット
	            task.setApproved(false); //承認フラグをfalse にする
	        }
	    } else {
	        task.setPreviousStatus(task.getStatus()); // 現在のステータスを保存
	        task.setStatus("Approved"); //ステータスをApprovedに変える
	        task.setApproved(true); //承認フラグをtrue にする
	    }
		
	    taskService.saveTask(task);
	    return "redirect:/home";
	}
	
	
	//「却下」ボタンを押したら approved = falseになって、Nextになり、フラグを全てfalseにする
	@PostMapping("/tasks/reject")
	public String rejectTask(@RequestParam("taskId") Long taskId) {
		Task task = taskService.findTaskByTaskId(taskId);
		task.setStatus("Next");
		task.setRejected(true); //却下フラグをtrueにする
		
		task.setApproved(false); //承認フラグをfalseにする
		task.setCompleted(false); //完了フラグをfalseにする
		task.setStarted(false); //開始フラグをfalseにする
		taskService.saveTask(task);
	    return "redirect:/home";
	}
	
	//「Colsed」ボタンを押したら approved = trueになって、承認後のステータスArchivedになる
	@PostMapping("/tasks/archive-all")
	public String archivesApproveTask() {
		List<Task> approvedTasks = taskService.findTaskByStatus("Approved");
		List<Task> nextTasks = taskService.findTaskByStatus("Next");
		for (Task task : approvedTasks) {
	        task.setStatus("Archived");
	        task.setApproved(false); //承認フラグをfalseにする
			task.setCompleted(false); //完了フラグをfalseにする
			task.setStarted(false); //開始フラグをfalseにする
			taskService.saveTask(task);
	    }
		
		for (Task task : nextTasks) {
			task.setStatus("Doing");
			if (task.getRejected()) {// task.isRejected() は Boolean の getter
	            task.setRejected(false);
	        }

			if (task.getEnteredDoingAt() == null) {
				task.setEnteredDoingAt(LocalDate.now());
			}
			taskService.saveTask(task);
		}
		return "redirect:/home";
	}
	
	
	
	//「やり直し」ボタンを押したら started = trueになって、Doingになる
		@PostMapping("/tasks/retry")
		public String againTask(@RequestParam("taskId") Long taskId) {
			Task task = taskService.findTaskByTaskId(taskId);
			task.setStatus("Doing");
			task.setStarted(true); //開始フラグをtrueにする
			task.setRejected(false); //却下フラグをfalseにする
			task.setStartedAt(LocalDate.now());//やり直しボタンを押した時にstartedAtを保存
			// ★ enteredDoingAt が未設定ならセット（1回だけ）
		    if (task.getEnteredDoingAt() == null) {
		        task.setEnteredDoingAt(LocalDate.now());
		    }
			taskService.saveTask(task);
		    return "redirect:/home";
	}
	
	
		//Archiveのタスクをクリックしてタスク編集フォームを表示
		@GetMapping("/tasks/achive/edit/{taskId}")
		public String showAchiveEditForm(@PathVariable("taskId") Long taskId, Model model) {
			//IDに対応するタスクを取得する
			Task task = taskService.findTaskByTaskId(taskId);
			
			//modelにタスクを渡す（HTML画面で使えるようにする）
			model.addAttribute("task", task);
			
			//編集画面に遷移する
			return "task/achiveEditForm";
		}
		
		//achiveフォーム送信でタスクを更新・戻る
		@PostMapping("/tasks/achive/edit/{taskId}")
		public String updateAchiveTask(@PathVariable("taskId") Long taskId, @ModelAttribute Task task, @RequestParam("action") String action) {

			if("update".equals(action)) {
				// taskId を使って、既存のタスクをDBから取得
				Task taskToUpdate = taskService.findTaskByTaskId(taskId);
				
				// フォームから受け取った値で、更新
				taskToUpdate.setTitle(task.getTitle());
				taskToUpdate.setStatus(task.getStatus());
				taskToUpdate.setDescription(task.getDescription());
				taskToUpdate.setReport(task.getReport());
				taskToUpdate.setDeadline(task.getDeadline());
				
				//経過日数をリセット
				taskToUpdate.setEnteredDoingAt(null);
				
				
				
				// 更新された内容を保存
				taskService.saveTask(taskToUpdate);
			}
			
			// アーカイブ一覧画面にリダイレクト
			return "redirect:/archive";
		}
		
		
		//ドラッグ＆ドロップによって変更されたタスクのステータス（列）をSpringのサーバー側で受け取る
		@PostMapping("/tasks/updateStatus")
		@ResponseBody
		public ResponseEntity<String> updateTaskStatus(@RequestParam Long taskId, @RequestParam String status) {
			Task task = taskService.findTaskByTaskId(taskId);
			
			if (task != null) {
				// 現在のステータスを保存
		        String currentStatus = task.getStatus();
		        
				task.setStatus(status);
				
				// DoingからNext/Iceboxに移動する場合は状態をリセット
		        if ("Doing".equals(currentStatus) && 
		            ("Next".equals(status) || "Icebox".equals(status))) {
		            
		            // 開始状態をリセット
		            task.setStarted(false);
		            task.setStartedAt(null);
		            task.setEnteredDoingAt(null);
		            
		            // 完了状態もリセット
		            if (task.getCompleted()) {
		                task.setCompleted(false);
		            }
		            
		            // やり直し状態もリセット
		            if (task.getRejected()) {
		                task.setRejected(false);
		            }
		            
		            // 承認状態もリセット
		            if (task.getApproved()) {
		                task.setApproved(false);
		            }
		        }
		        
		        // DoneからIceboxに移動する場合は状態をリセット
		        if ("Done".equals(currentStatus) && 
			            ("Icebox".equals(status))) {
			            
			            // 開始状態をリセット
			            task.setStarted(false);
			            task.setStartedAt(null);
			            task.setEnteredDoingAt(null);
			            
			            // 完了状態もリセット
			            if (task.getCompleted()) {
			                task.setCompleted(false);
			            }
			            
			            // やり直し状態もリセット
			            if (task.getRejected()) {
			                task.setRejected(false);
			            }
			            
			            // 承認状態もリセット
			            if (task.getApproved()) {
			                task.setApproved(false);
			            }
			        }
		        
		        // DoneからIceboxに移動する場合は状態をリセット
		        if ("Done".equals(currentStatus) && 
			            ("Icebox".equals(status))) {
			            
			            // 開始状態をリセット
			            task.setStarted(false);
			            task.setStartedAt(null);
			            task.setEnteredDoingAt(null);
			            
			            // 完了状態もリセット
			            if (task.getCompleted()) {
			                task.setCompleted(false);
			            }
			        }
		        
		        // DoneからDoing/Nextに移動する場合は却下扱いにする
		        if ("Done".equals(currentStatus) && 
		            ("Doing".equals(status) || "Next".equals(status))) {
		            
		            // 却下フラグを立てる
		            task.setRejected(true);
		        }
		        
		        // Next/IceboxからDoingに移動する場合は開始状態にして経過日数を開始
		        if (("Next".equals(currentStatus) || "Icebox".equals(currentStatus)) && 
		            "Doing".equals(status)) {
		            task.setEnteredDoingAt(LocalDate.now());
		        }
				

				taskService.saveTask(task);
				return ResponseEntity.ok("Status updated");
			} else {
				return ResponseEntity.badRequest().body("Task not found");
			}
		}//ここ理解してないから理解する。
	

}
