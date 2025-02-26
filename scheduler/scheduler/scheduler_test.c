/*
 FileName: task_test.c
 Author: Ido Zarchi 
 Date: 02/05/24
 reviewer: 
*/

#include <stdio.h> /* printf */
#include <stdlib.h>
#include "scheduler.h" /* ScedulerCreate */

static void TestScedulerCreateDestroy();
static void TestScedulerAddRemove();
static void TestScedulerRunStop();
int FuncTask(void *param);
void FuncCleanTask(void *param);
void FuncCleanTaskMalloc(void *param);
int FuncTaskStop(void *param);

int main()
{
	TestScedulerCreateDestroy();
	TestScedulerAddRemove();
	TestScedulerRunStop();
	
	return (0);
}

static void TestScedulerCreateDestroy()
{
	scheduler_t *sch = SchedulerCreate();
	SchedulerDestroy(sch);
	
	printf("Test Number 1 | Create & Destroy: Sucsses!\n");
}

static void TestScedulerAddRemove()
{
	int num = 88;
	timespec_t interval = {2, 0};
	ilrd_uid_t uid = {0};
	scheduler_t *sch = SchedulerCreate();
	timespec_t t = {0};
	
	clock_gettime(0, &t);

	t.tv_sec += 3;
	uid = SchedulerAdd(sch, FuncTask, &num, t, interval, FuncCleanTask);

	if(1 == SchedulerSize(sch))
	{
		printf("Test Number 2 | Add: Sucsses!\n");
	}
	else
	{
		printf("Test Number 2 | Add: Fail!\n");
		printf("Expected %d But Recived %ld\n", 1, SchedulerSize(sch));
	}
	
	t.tv_sec += 3;
	uid = SchedulerAdd(sch, FuncTask, &num, t, interval, FuncCleanTask);
	
	if(2 == SchedulerSize(sch))
	{
		printf("Test Number 3 | Add: Sucsses!\n");
	}
	else
	{
		printf("Test Number 3 | Add: Fail!\n");
		printf("Expected %d But Recived %ld\n", 1, SchedulerSize(sch));
	}
	
	SchedulerRemove(sch, uid);
	
	if(1 == SchedulerSize(sch))
	{
		printf("Test Number 4 | Remove: Sucsses!\n");
	}
	else
	{
		printf("Test Number 4 | Remove: Fail!\n");
		printf("Expected %d But Recived %ld\n", 1, SchedulerSize(sch));
	}

	SchedulerDestroy(sch);
}

static void TestScedulerRunStop()
{
	int counter_run = 0;
	timespec_t interval = {2, 0};
	int *m_num = malloc(sizeof(int));
	scheduler_t *sch = SchedulerCreate();
	timespec_t t = {0, 10};

	clock_gettime(0, &t);
	
	t.tv_sec += 2;
	SchedulerAdd(sch, FuncTask, &counter_run, t, interval, FuncCleanTask);

	t.tv_sec += 1;
	SchedulerAdd(sch, FuncTask, &counter_run, t, interval, FuncCleanTask);

	t.tv_sec += 1;
	SchedulerAdd(sch, FuncTask, &counter_run, t, interval, FuncCleanTask);

	t.tv_sec += 1;
	SchedulerAdd(sch, FuncTask, &counter_run, t, interval, FuncCleanTask);

	t.tv_sec += 1;
	SchedulerAdd(sch, FuncTask, &counter_run, t, interval, FuncCleanTask);

	t.tv_sec += 1;
	SchedulerAdd(sch, FuncTask, m_num, t, interval, FuncCleanTaskMalloc);
	
	SchedulerRun(sch);

	if(5 == counter_run)
	{
		printf("Test Number 5 | Run: Sucsses!\n");
	}
	else
	{
		printf("Test Number 5 | Run: Fail!\n");
		printf("Expected %d But Recived %d\n", 5, counter_run);
	}
	
	t.tv_sec += 1;
	SchedulerAdd(sch, FuncTask, &counter_run, t, interval, FuncCleanTask);

	t.tv_sec += 1;
	SchedulerAdd(sch, FuncTask, &counter_run, t, interval, FuncCleanTask);

	t.tv_sec += 1;
	SchedulerAdd(sch, FuncTaskStop, sch, t, interval, FuncCleanTask);

	t.tv_sec += 1;
	SchedulerAdd(sch, FuncTask, &counter_run, t, interval, FuncCleanTask);
	
	counter_run = 0;
	SchedulerRun(sch);

	if(2 == counter_run)
	{
		printf("Test Number 6 | Run&Stop: Sucsses!\n");
	}
	else
	{
		printf("Test Number 6 | Run&Stop: Fail!\n");
		printf("Expected %d But Recived %d\n", 2, counter_run);
	}
	
	
	SchedulerDestroy(sch);	
}


int FuncTask(void *param)
{
	(*(int *)param)++;
	printf("The Run Number Is: %d\n", *(int *)param);
	
	return (1);
}

int FuncTaskStop(void *param)
{
	SchedulerStop((scheduler_t *)param);	
	
	return (1);
}

void FuncCleanTask(void *param)
{
	(void)param;
	printf("This Is Clean Function Of %d...\n", *(int *)param);
}

void FuncCleanTaskMalloc(void *param)
{
	free(param);
	printf("This Is Clean Function Of Malloc...\n");
}

/*static void TestScedulerAddRemove()
{
	int num = 88;
	int interval = 1;
	ilrd_uid_t uid = {0};
	scheduler_t *sch = SchedulerCreate();
	
	uid = SchedulerAdd(sch, FuncTask, &num, time(NULL) + 3, interval, FuncCleanTask);

	if(1 == SchedulerSize(sch))
	{
		printf("Test Number 2 | Add: Sucsses!\n");
	}
	else
	{
		printf("Test Number 2 | Add: Fail!\n");
		printf("Expected %d But Recived %ld\n", 1, SchedulerSize(sch));
	}
	
	SchedulerRemove(sch, uid);
	
	if(1 == SchedulerSize(sch))
	{
		printf("Test Number 4 | Remove: Sucsses!\n");
	}
	else
	{
		printf("Test Number 4 | Remove: Fail!\n");
		printf("Expected %d But Recived %ld\n", 1, SchedulerSize(sch));
	}

	SchedulerDestroy(sch);
}
*/















