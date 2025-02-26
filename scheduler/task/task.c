/*
 FileName: task.c
 Author: Ido Zarchi 
 Date: 02/05/24
 reviewer: 
*/

#include <stdlib.h> /*  Malloc */
#include "task.h" /* Headers */

struct task
{
	ilrd_uid_t uid;
	task_func_t func;
	clean_func_t clean;
	void *param;
};

task_t bad_task = {{0}, 0, 0, 0};

task_t *TaskCreate(task_func_t task, void * param, clean_func_t clean)
{
	ilrd_uid_t uid = {0};
	
	task_t *res_task = malloc(sizeof(task_t));
	if(NULL == res_task)
	{
		return (NULL);
	}
	
	res_task->func = task;
	res_task->clean = clean;
	res_task->param = param;
	
	uid = UIDCreate();
	if(UIDIsEqual(uid, UIDGetBadUid()))
	{
		free(res_task);
		return (TaskGetBadTask());
	}
	
	res_task->uid = uid;
	
	return (res_task);
}

void TaskDestroy(task_t *task)
{
	TaskClean(task);
	
	free(task);
	task = NULL;
}

ilrd_uid_t TaskGetUid(task_t *task)
{
	return (task->uid);
}

task_t *TaskGetBadTask()
{
	bad_task.uid = UIDGetBadUid();
	
	return (&bad_task);
}

int TaskExec(task_t *task)
{
	return (task->func)(task->param);
}

void TaskClean(task_t *task)
{
	(task->clean)(task->param);
}

int TaskMatchUid(task_t *task, ilrd_uid_t uid)
{
	return (UIDIsEqual(TaskGetUid(task), uid));
}


























