package com.taskmanager.entity;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "tasks")
public class Task {
	
	/**
	 * 対象のフィールドとテーブルの主キーを対応づける
	 * レコードを登録する際、対象のフィールドと対応付けられた列に自動的に値が登録される
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="tasks_id")
	private Long taskId;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "report")
	private String report;
	
	@Column(name = "started")
	private boolean started;
	
	@Column(name = "completed")
	private boolean completed;
	
	@Column(name = "approved")
	private boolean approved;
	
	@Column(nullable = false)
	private boolean rejected = false;
	
	@Column(name = "deadline")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate deadline;
	
	@Column(name = "started_at")
	private LocalDate startedAt;
	
	@Transient //DBには保存しない
	private long daysSinceStarted;
	
	@Column(name = "created_at")
	private LocalDate createdAt;
	
	@Column(name = "entered_doing_at")
	private LocalDate enteredDoingAt;
	
	@Column(name = "previous_status")
	private String previousStatus;
	
	
	

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean getCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean getApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean getRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	public LocalDate getDeadline() {
		return deadline;
	}

	public void setDeadline(LocalDate deadline) {
		this.deadline = deadline;
	}

	public LocalDate getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(LocalDate startedAt) {
		this.startedAt = startedAt;
	}

	public long getDaysSinceStarted() {
		return daysSinceStarted;
	}

	public void setDaysSinceStarted(long daysSinceStarted) {
		this.daysSinceStarted = daysSinceStarted;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDate getEnteredDoingAt() {
		return enteredDoingAt;
	}

	public void setEnteredDoingAt(LocalDate enteredDoingAt) {
		this.enteredDoingAt = enteredDoingAt;
	}

	public String getPreviousStatus() {
		return previousStatus;
	}

	public void setPreviousStatus(String previousStatus) {
		this.previousStatus = previousStatus;
	}
	
	
	
	
	

	
	

}
