/*
 FileName: wdt_client.c
 Author: Ido Zarchi
 Date: 14/07/24
 reviewer: Yarin Hagever
*/

#define MAX_LONG_PID (21)
#define FIRST_CHAR_VALUE (10)
#define IS_BASE_LEGAL(b) ((MIN_BASE <= b && MAX_BASE >= b))
#define MAX_BASE (36)
#define MIN_BASE (2)
#define USR_PATH ("./usr.out")

#define _POSIX_C_SOURCE 200112L

#include <stdlib.h>    /*malloc*/
#include <signal.h>    /*sig_atomic_t*/
#include <unistd.h>    /*fork*/
#include <fcntl.h>     /*sem_open*/
#include <semaphore.h> /*sem_t*/
#include <stdio.h>     /*printf*/
#include <pthread.h>   /*pthread_t*/
#include <time.h>      /*timespec_t*/
#include <assert.h>    /*assert*/
#include <sys/wait.h>  /*wait*/
#include <string.h>    /*strlen*/

#include "wdt.h"       /*headers*/
#include "scheduler.h" /*SchedulerCreate*/

enum
{
    FALSE,
    TRUE
};

typedef struct task_params
{
    timespec_t interval;
    size_t tolerance;
    scheduler_t *sched;
    const char *argv;
    const char *path_to_wd_out;

} task_params_t;

sig_atomic_t fault_tolerance_counter = 0;
sig_atomic_t is_stop = 0;

/*Global Semaphores*/
sem_t *sem_usr;
sem_t *sem_wd;
sem_t *sem_block_start;

/*Global names and values of env*/
char usr_value[MAX_LONG_PID] = "0";
char usr_name[] = "pid_usr";

static int WDCreate(task_params_t *params);
static int WDCommunication(void *param);

static int TaskSendSignal(void *params);
static int TaskHandshake(void *param);
static int TaskCheckFault(void *params);
static void TaskSendSignalClean(void *param);
static char *IntToString(int num, char *dest, size_t base);

void sigusr1_handler(int sig);
void sigusr2_handler(int sig);

int main(int argc, char *argv[])
{
    task_params_t *params = NULL; /*here will be the casting from argv*/

    (void)argc;
    (void)argv;

    params = malloc(sizeof(task_params_t));
    if(NULL == params)
    {
        return (-1);
    }

    sem_usr = sem_open("/sem_usr", O_CREAT, 0666, 0);
    sem_wd = sem_open("/sem_wd", O_CREAT, 0666, 0);

    params->interval.tv_sec = 10;
    params->interval.tv_nsec = 0;
    params->tolerance = 5;
    params->argv = *argv;
    params->path_to_wd_out = "";

    WDCreate(params);
    WDCommunication(params);

    return (0);
}

static int WDCreate(task_params_t *params)
{
    scheduler_t *wd_sched = NULL;
    timespec_t start_time = {0, 0};
    timespec_t task_interval = {0, 0};
    ilrd_uid_t task_uid = {0};

    wd_sched = SchedulerCreate();
    if (NULL == wd_sched)
    {
        return (-1); /*put currect error in here*/
    }

    params->sched = wd_sched;

    task_interval.tv_sec = params->interval.tv_sec / params->tolerance;
    task_interval.tv_nsec = params->interval.tv_nsec / params->tolerance;

    if(-1 == clock_gettime(0, &start_time))
    {
        return (WATCHDOG_CREATION_FAILURE);
    }

    task_uid = SchedulerAdd(wd_sched, TaskSendSignal, params, start_time, task_interval, TaskSendSignalClean);
    if (UIDIsEqual(UIDGetBadUid(), task_uid))
    {
        SchedulerDestroy(wd_sched);

        return (-1); /*put currect error in here*/
    }

    if(-1 == clock_gettime(0, &start_time))
    {
        return (WATCHDOG_CREATION_FAILURE);
    }

    task_uid = SchedulerAdd(wd_sched, TaskCheckFault, params, start_time, params->interval, TaskSendSignalClean);
    if (UIDIsEqual(UIDGetBadUid(), task_uid))
    {
        SchedulerDestroy(wd_sched);

        return (-1); /*put currect error in here*/
    }

    return (0);
}

static int WDCommunication(void *param)
{
    int status = 0;
    scheduler_t *sched = ((task_params_t *)param)->sched;
    struct sigaction sigusr1;
    struct sigaction sigusr2;
    char wd_value[MAX_LONG_PID] = "0";
    char wd_name[] = "pid_wd";

    sem_block_start = sem_open("/sem_block_start", O_CREAT, 0666, 0);

    (void)status;

    IntToString(getpid(), wd_value, 10);

    if (setenv(wd_name, wd_value, 1))
    {
        perror("Error in \"putenv\"\n");

        SchedulerDestroy(sched);

        return (0); /*put currect error in here*/
    }

    sigusr1.sa_handler = sigusr1_handler;
    sigaction(SIGUSR1, &sigusr1, NULL);

    sigusr2.sa_handler = sigusr2_handler;
    sigaction(SIGUSR2, &sigusr2, NULL);

    TaskHandshake(param);

    status = SchedulerRun(sched);

    SchedulerDestroy(sched);
    free(param);

    return (0);
}

static void Revive(void *param)
{
    scheduler_t *sched = ((task_params_t *)param)->sched;
    pid_t usr_pid = 0;

    waitpid(atoi(getenv(usr_name)), NULL, 0);

    usr_pid = fork();

    if (0 == usr_pid)
    {
        execlp(USR_PATH, "usr.out");
    }
    else
    {
        IntToString(usr_pid, usr_value, 10);
        printf("pid_usr after revive is %d\n", usr_pid);

        if (setenv(usr_name, usr_value, 1))
        {
            printf("Error in \"putenv\"\n");

            SchedulerDestroy(sched);

            return; /*put currect error in here*/
        }
    }
}

static int TaskHandshake(void *param)
{
    sem_post(sem_wd);
    while (0 != sem_wait(sem_usr))
        ;

    sem_post(sem_block_start);

    printf("handshake wd\n");

    (void)param;

    return (1);
}

static int TaskSendSignal(void *params)
{
    pid_t pid = 0;
    char pid_usr[] = "pid_usr";

    pid = atoi(getenv(pid_usr));

    if (TRUE == is_stop)
    {
        printf("scheduler of wd is stopping\n");
        SchedulerStop(((task_params_t *)params)->sched);
    }
    else
    {
        ++fault_tolerance_counter;
        printf("counter of wd is %d\n", fault_tolerance_counter);
        kill(pid, SIGUSR1);
    }

    return (0);
}

static int TaskCheckFault(void *params)
{
    sig_atomic_t max_faults = (sig_atomic_t)((task_params_t *)params)->tolerance;

    if (max_faults < fault_tolerance_counter)
    {
        printf("reviving now\n");
        Revive(params);

        TaskHandshake(params);
        fault_tolerance_counter = 0;
    }
    else
    {
        printf("wd got signals\n");
    }

    return (0);
}

static void TaskSendSignalClean(void *param)
{
    (void)param;
}


void sigusr1_handler(int sig)
{
    (void)sig;
    fault_tolerance_counter = 0;
}

void sigusr2_handler(int sig)
{
    (void)sig;
    is_stop = 1;
}

static size_t CountCharsInBase(int num, size_t base)
{
    size_t count = 0;

    assert(1 < base && 37 > base);

    while (0 != num)
    {
        ++count;
        num /= base;
    }

    return (count);
}

static char *IntToString(int num, char *dest, size_t base)
{
    char *write = dest;

    assert(NULL != dest);

    if (!IS_BASE_LEGAL(base))
    {
        return (NULL);
    }
    if (0 > num)
    {
        *write = '-';
        num = -num;
        ++write;
    }
    write += CountCharsInBase(num, base);
    *write = '\0';
    do
    {
        --write;
        if (FIRST_CHAR_VALUE > (num % base))
        {
            *write = (num % base) + '0';
        }
        else
        {
            *write = (num % base) - FIRST_CHAR_VALUE + 'a';
        }
        num /= base;
    } while (0 != num);

    return (dest);
}
