#ifndef _ILRD_SCHEDULER_H_
#define _ILRD_SCHEDULER_H_

#include <time.h> /*time_t*/
#include <pthread.h>
#include "uid.h"   /*uid_t*/

typedef struct timespec timespec_t;

typedef struct scheduler scheduler_t; 

typedef int(*scheduler_func_t)(void *param); 
typedef void(*scheduler_clean_t)(void *param);

scheduler_t *SchedulerCreate(void); /* O(1) */

void SchedulerDestroy(scheduler_t *handler); /* O(N) */

ilrd_uid_t SchedulerAdd(scheduler_t *handler, scheduler_func_t task, void *param,
 timespec_t start_time, timespec_t interval, scheduler_clean_t clean); /* O(N) */
 
int SchedulerRemove(scheduler_t *handler, ilrd_uid_t uid); /* O(N) */
 
int SchedulerRun(scheduler_t *handler);/* O(N) */

void SchedulerStop(scheduler_t *handler);  /* O(1) */

size_t SchedulerSize(const scheduler_t *handler);/* O(N) */

int SchedulerIsEmpty(const scheduler_t *handler); /* O(1) */

void SchedulerClear(scheduler_t *handler); /* O(N) */

#endif /* _ILRD_SCHEDULER_H_ */
