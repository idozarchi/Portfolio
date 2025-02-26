/*
 FileName: scheduler.c
 Author: Ido Zarchi 
 Date: 02/05/24
 reviewer: 
*/

#include <stdlib.h> /*  Malloc */
#include <unistd.h> /* Sleep */
#include <assert.h> /* Assert */

#include "scheduler.h" /* Headers */
#include "task.h"  /*task_t*/
#include "heap_pq.h"    /*pq_t*/

#define MAX2(a, b) ((a) > (b) ? (a) : (b))
#define REPEAT_TASK (0)
#define MOVE_TO_NEXT_TASK (1)

typedef struct  scheduler_task
{
	task_t *task;
	timespec_t start_time;
	timespec_t interval;
}scheduler_task_t;

struct  scheduler
{
	heap_pq_t *pq;
	int is_stop;
};

static int CmpTaskScheduler(const void *sch_task1, const void *sch_task2);
static int IsMatchUid(const void *task1, const void *uid_task);
static scheduler_task_t *SchedulerTaskCreate(scheduler_t *handler, scheduler_func_t task, 
       void *param, timespec_t start_time, timespec_t interval, scheduler_clean_t clean);
static ilrd_uid_t SchedualerTaskGetUid(scheduler_task_t *sch_task);
static void SchedulerTaskDestroy(scheduler_task_t *sch_task);
static void UpdateTime(scheduler_task_t *sch_task);
static void MaxOfNanoSec(timespec_t *t);

scheduler_t *SchedulerCreate(void)
{
	scheduler_t *sch = malloc(sizeof(scheduler_t));
	if(NULL == sch)
	{
		return (NULL);
	}
	
	sch->pq = HeapPQCreate(CmpTaskScheduler);
	if(NULL == sch->pq)
	{
		free(sch);
		return (NULL);
	}
	
	sch->is_stop = 1;
	
	return (sch);
}

void SchedulerDestroy(scheduler_t *handler)
{
	assert(handler);
	
	SchedulerClear(handler);
	
	HeapPQDestroy(handler->pq);
	free(handler);
}

ilrd_uid_t SchedulerAdd(scheduler_t *handler, scheduler_func_t task, void *param,
 timespec_t start_time, timespec_t interval, scheduler_clean_t clean)
 {
 	scheduler_task_t *sch_task = SchedulerTaskCreate(handler, task, param,
 	start_time, interval, clean);
 	
 	assert(handler);
 	assert(task);
 	assert(clean);
 	
 	if(NULL == sch_task)
	{
		return (TaskGetUid(TaskGetBadTask()));
	}
 	
 	if(0 != HeapPQEnqueue(handler->pq, sch_task))
 	{
 		SchedulerTaskDestroy(sch_task);
 		return (TaskGetUid(TaskGetBadTask()));
 	}
 	
 	return (SchedualerTaskGetUid(sch_task));
 }

int SchedulerRemove(scheduler_t *handler, ilrd_uid_t uid)
{
	scheduler_task_t *sch_task = 
	(scheduler_task_t *)HeapPQErase(handler->pq, IsMatchUid, &uid);
	
	if(NULL == sch_task)
	{
		return (-1);
	}
	
	assert(handler);
	
	SchedulerTaskDestroy(sch_task);
	
	return (0);
}

size_t SchedulerSize(const scheduler_t *handler)
{
	assert(handler);
	
	return (HeapPQSize(handler->pq));
}

int SchedulerIsEmpty(const scheduler_t *handler)
{
	assert(handler);
	
	return (HeapPQIsEmpty(handler->pq));
}

void SchedulerClear(scheduler_t *handler)
{
	assert(handler);
	
	while(!SchedulerIsEmpty(handler))
	{
		SchedulerTaskDestroy(HeapPQDequeue(handler->pq));
	}
}

void SchedulerStop(scheduler_t *handler)
{
	assert(handler);
	
	handler->is_stop = 1;
}

int SchedulerRun(scheduler_t *handler)
{
	scheduler_task_t *sch_task = NULL;
	timespec_t sleep_time = {0};
	timespec_t current_time = {0};
	int status = 0;
	int status_sleep = -1;
	
	assert(handler);
	
	handler->is_stop = 0;
	while((!handler->is_stop) && !SchedulerIsEmpty(handler))
	{
		sch_task = (scheduler_task_t *)(HeapPQDequeue(handler->pq));
		
		/*sleep_time = MAX2(sch_task->start_time.tv_sec - time(NULL), 0);*/
		clock_gettime(0, &current_time);
		sleep_time.tv_sec = MAX2(0, sch_task->start_time.tv_sec - current_time.tv_sec);
		sleep_time.tv_nsec = MAX2(0, sch_task->start_time.tv_nsec - current_time.tv_nsec);
		/*MaxOfNanoSec(&sleep_time);*/

		while(0 != nanosleep(&sleep_time, &sleep_time))
		{		
			;
		}
		
		status = TaskExec(sch_task->task);
		
		if(MOVE_TO_NEXT_TASK == status)
		{
			SchedulerTaskDestroy(sch_task);
		}
		
		else if(REPEAT_TASK == status)
		{
			UpdateTime(sch_task);
			
			if(0 != (status = HeapPQEnqueue(handler->pq, sch_task)))
			{
				SchedulerTaskDestroy(sch_task);
				SchedulerStop(handler);
			}
		}
		else
		{
			SchedulerTaskDestroy(sch_task);
			SchedulerStop(handler);
		}
	}
	SchedulerStop(handler);
	
	return (status);
}

static scheduler_task_t *SchedulerTaskCreate(scheduler_t *handler, scheduler_func_t task, 
       void *param, timespec_t start_time, timespec_t interval, scheduler_clean_t clean)
{
	scheduler_task_t *sch_task = malloc(sizeof(scheduler_task_t));
	if(NULL == sch_task)
	{
		return (NULL);
	}
	
	(void)handler;

	sch_task->start_time = start_time;
	sch_task->interval = interval;
	
	sch_task->task = TaskCreate(task, param, (clean_func_t)clean);
	
	
	return (sch_task);
}

static void SchedulerTaskDestroy(scheduler_task_t *sch_task)
{
	TaskDestroy(sch_task->task);
	free(sch_task);
}

static void UpdateTime(scheduler_task_t *sch_task)
{
	timespec_t t = {0};
	clock_gettime(0, &t);

	sch_task->start_time.tv_sec = t.tv_sec + sch_task->interval.tv_sec;
	
	if(1000000000L < t.tv_nsec + sch_task->interval.tv_nsec)
	{
		++sch_task->start_time.tv_sec;
		sch_task->start_time.tv_nsec = t.tv_nsec + sch_task->interval.tv_nsec - 1000000000L;
	}
	/*sch_task->start_time.tv_nsec = t.tv_nsec + sch_task->interval.tv_nsec;*/
}

static ilrd_uid_t SchedualerTaskGetUid(scheduler_task_t *sch_task)
{
	return (TaskGetUid(sch_task->task));
}

static int CmpTaskScheduler(const void *sch_task1, const void *sch_task2)
{
	scheduler_task_t *sch_task_one = (scheduler_task_t *)sch_task1;
	scheduler_task_t *sch_task_two = (scheduler_task_t *)sch_task2;
	
	return ((0 > sch_task_one->start_time.tv_sec - (sch_task_two->start_time.tv_sec) &&
	 0 > sch_task_one->start_time.tv_nsec - (sch_task_two->start_time.tv_nsec)) ? -1 : 1);
}

static int IsMatchUid(const void *task1, const void *uid_task)
{
	scheduler_task_t *sch_task = (scheduler_task_t *)task1;
	ilrd_uid_t uid = *(ilrd_uid_t *)uid_task;
	
	return (TaskMatchUid(sch_task->task, uid));
}

static void MaxOfNanoSec(timespec_t *t)
{
	timespec_t current_time = {0};
	clock_gettime(0, &current_time);

	t->tv_sec -= current_time.tv_sec;
	t->tv_nsec -= current_time.tv_nsec;

	if(t->tv_sec < 0 || t->tv_nsec < 0)
	{
		t->tv_sec = 0;
		t->tv_nsec = 0;
	}
}
