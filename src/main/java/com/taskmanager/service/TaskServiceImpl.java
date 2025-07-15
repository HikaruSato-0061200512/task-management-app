package com.taskmanager.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskmanager.entity.Task;
import com.taskmanager.repository.TaskRepository;


@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> findAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> findTaskByStatus(String status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    public Task findTaskByTaskId(Long taskId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        return taskOpt.orElse(null);
    }

    @Override
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    
    
    @Override
    public List<Task> findTaskByStatusList(List<String> statuses) {
	    return taskRepository.findByStatusIn(statuses);
	}
    
    
    //土日を除いた営業日数（平日のみの経過日数）をカウントする
    @Override
    public long countBusinessDaysBetween(LocalDate start, LocalDate end) {
    	long days = 0;//営業日の日数をカウントするための変数 days を用意
    	LocalDate date = start.plusDays(1);//ループの中で日付を1日ずつ進めてチェックするための変数 date を用意(翌日からカウント)
    	
    	while (!date.isAfter(end)) {
            DayOfWeek day = date.getDayOfWeek();//現在チェックしている日付 date の曜日を取得して、変数dayに保存
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {//取得した曜日が土日出ない場合(論理否定)
                days++;
            }
            date = date.plusDays(1);
        }
    	return days;
    	
    }
    
    
    
}
