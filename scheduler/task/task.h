#ifndef __ILRD_TASK_H__
#define __ILRD_TASK_H__

#include <time.h> /*time_t*/
#include "uid.h"  /*uid_t*/

typedef int(*task_func_t)(void *param);
typedef void(*clean_func_t)(void *param);

typedef struct task task_t;

extern task_t bad_task;

task_t *TaskCreate(task_func_t task, void * param, clean_func_t clean);/*O(1)*/

void TaskDestroy(task_t *task);/*O(1)*/

ilrd_uid_t TaskGetUid(task_t *task);/*O(1)*/

task_t *TaskGetBadTask(); /*O(1) Only Mine*/

int TaskExec(task_t *task);/*task func complexity*/

void TaskClean(task_t *task);/* clean func complexity */

int TaskMatchUid(task_t *task, ilrd_uid_t uid);

#endif /* __ILRD_TASK_H__ */

