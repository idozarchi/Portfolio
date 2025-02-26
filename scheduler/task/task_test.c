/*
 FileName: task_test.c
 Author: Ido Zarchi 
 Date: 02/05/24
 reviewer: 
*/

#include <stdio.h> /* printf */
#include "task.h" /* TaskCreate */
#include "uid.h" /* UIDCreate */

static int TestTaskFunc(void *param);
static void TestTaskClean(void *param);
static void TestTask();

int main()
{
	TestTask();
	
	return (0);
}

static void TestTask()
{
	int num = 88;
	
	task_t *task = TaskCreate(TestTaskFunc, &num, TestTaskClean);
	TaskDestroy(task);
	
	printf("Test Number 1 | Creat & Destroy: Sucsses\n");
	
	task = TaskCreate(TestTaskFunc, &num, TestTaskClean);
	
	printf("Test Number 2 | Uid counter of task is: %ld\n", TaskGetUid(task).counter);
	
	printf("Test Number 3 | TaskExex:\n");
	TaskExec(task);
	
	printf("Test Number 4 | TaskClean:\n");
	TaskClean(task);
	
	TaskDestroy(task);
}

static int TestTaskFunc(void *param)
{
	printf("the number is: %d\n", *(int *)param);
	
	return (0);
}

static void TestTaskClean(void *param)
{
	(void)param;
	printf("This is the clean function\n");
}






























