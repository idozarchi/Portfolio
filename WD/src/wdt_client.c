/*
 FileName: wdt_client.c
 Author: Ido Zarchi
 Date: 07/07/24
 reviewer:
*/

#define MAX_LONG_PID (21)
#define FIRST_CHAR_VALUE (10)
#define IS_BASE_LEGAL(b) ((MIN_BASE <= b && MAX_BASE >= b))
#define MAX_BASE (36)
#define MIN_BASE (2)
#define WD_PATH ("./wd.out")
#define ARGV_MAX_LEN (300)

#define _POSIX_C_SOURCE 200112L

#include <stdio.h>     /*printf*/
#include <stdlib.h>    /*malloc*/
#include <unistd.h>    /*fork*/
#include <assert.h>    /*assert*/
#include <string.h>    /*strlen*/
#include <fcntl.h>     /*sem_open*/
#include <semaphore.h> /*sem_t*/
#include <pthread.h>   /*pthread_t*/
#include <signal.h>    /*sig_atomic_t*/
#include <sys/wait.h>  /*wait*/
#include <time.h>      /*timespec_t*/

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
    const char **argv;
    const char *path_to_wd_out;
} task_params_t;

sig_atomic_t fault_tolerance_counter = 0;
sig_atomic_t is_stop = 0;

/*Global Semaphores*/
sem_t *sem_usr;
sem_t *sem_wd;
sem_t *sem_block_start;

pthread_t user_thread = 0;

/*Global names and values of env*/
char usr_value[MAX_LONG_PID] = "0";
char usr_name[] = "pid_usr";
char wd_value[MAX_LONG_PID] = "0";
char wd_name[] = "pid_wd";

static int WDUsrCommunicationCreate(task_params_t *params);
static void *UsrCommunication(void *param);
static void Revive(void *param);

static int TaskSendSignal(void *params);
static int TaskHandshake(void *param);
static int TaskCheckFault(void *params);
static void TaskSendSignalClean(void *param);
static char *IntToString(int num, char *dest, size_t base);

void sigusr1_handler(int sig);

int MakeMeImmortal(timespec_t interval, size_t tolerance, const char *argv[],
                   const char *path_to_wd_out)
{
    task_params_t *params = NULL;

    if (NULL == getenv(wd_name))
    {
        if(SUCCESS != sem_unlink("/sem_usr"))
        {
            return (WATCHDOG_CREATION_FAILURE);
        }
        if(SUCCESS != sem_unlink("/sem_wd"))
        {
            return (WATCHDOG_CREATION_FAILURE);
        }
        if(SUCCESS != sem_unlink("/sem_block_start"))
        {
            return (WATCHDOG_CREATION_FAILURE);
        }
    }

    sem_block_start = sem_open("/sem_block_start", O_CREAT, 0666, 0);
    sem_usr = sem_open("/sem_usr", O_CREAT, 0666, 0);
    sem_wd = sem_open("/sem_wd", O_CREAT, 0666, 0);

    params = malloc(sizeof(task_params_t));
    if (NULL == params)
    {
        return (-1);
    }
    params->interval = interval;
    params->tolerance = tolerance;
    params->argv = argv;
    params->path_to_wd_out = path_to_wd_out;

    WDUsrCommunicationCreate(params);

    while (0 != sem_wait(sem_block_start))
        ;

    return (0);
}

int DoNotResuscitate(void)
{
    is_stop = 1;
    pthread_join(user_thread, NULL);

    return 0;
}

static int WDUsrCommunicationCreate(task_params_t *params)
{
    scheduler_t *usr_sched = NULL;
    timespec_t start_time = {0, 0};
    timespec_t task_interval = {0, 0};
    ilrd_uid_t task_uid = {0};

    usr_sched = SchedulerCreate();
    if (NULL == usr_sched)
    {
        return (-1);
    }

    params->sched = usr_sched;

    task_interval.tv_sec = params->interval.tv_sec / params->tolerance;
    task_interval.tv_nsec = params->interval.tv_nsec / params->tolerance;

    if(-1 == clock_gettime(0, &start_time))
    {
        return (WATCHDOG_CREATION_FAILURE);
    }
    

    task_uid = SchedulerAdd(usr_sched, TaskSendSignal, params, start_time, task_interval, TaskSendSignalClean);
    if (UIDIsEqual(UIDGetBadUid(), task_uid))
    {
        SchedulerDestroy(usr_sched);

        return (WATCHDOG_CREATION_FAILURE);
    }

    clock_gettime(0, &start_time);

    task_uid = SchedulerAdd(usr_sched, TaskCheckFault, params, start_time, params->interval, TaskSendSignalClean);
    if (UIDIsEqual(UIDGetBadUid(), task_uid))
    {
        SchedulerDestroy(usr_sched);

        return (WATCHDOG_CREATION_FAILURE);
    }

    if (0 != pthread_create(&user_thread, 0, UsrCommunication, params))
    {
        SchedulerDestroy(usr_sched);

        return (WATCHDOG_CREATION_FAILURE);
    }

    return (0);
}

static void *UsrCommunication(void *param)
{
    int status = 0;
    scheduler_t *sched = ((task_params_t *)param)->sched;
    struct sigaction sigusr1;
    pid_t wd_pid = 0;

    (void)status;

    IntToString(getpid(), usr_value, 10);

    if (setenv(usr_name, usr_value, 1))
    {
        perror("Error in \"putenv\"\n");
        SchedulerDestroy(sched);

        return (NULL);
    }

    /*printf("env of usr pid %d\n", atoi(getenv(usr_name)));*/

    if (NULL == getenv(wd_name))
    {
        IntToString(wd_pid, wd_value, 10);

        if (setenv(wd_name, wd_value, 1))
        {
            printf("Error in \"putenv\"\n");

            SchedulerDestroy(sched);

            return (NULL);
        }

        Revive(param);
    }

    sigusr1.sa_handler = sigusr1_handler;
    sigaction(SIGUSR1, &sigusr1, NULL);

    TaskHandshake(param);

    status = SchedulerRun(sched);

    SchedulerDestroy(sched);

    if(SUCCESS != sem_unlink("/sem_usr"))
    {
        return (NULL);
    }
    if(SUCCESS != sem_unlink("/sem_wd"))
    {
        return (NULL);
    }
    if(SUCCESS != sem_unlink("/sem_block_start"))
    {
        return (NULL);
    }
    
    free(param);

    return (NULL);
}

static void Revive(void *param)
{
    scheduler_t *sched = ((task_params_t *)param)->sched;
    pid_t wd_pid = 0;

    waitpid(atoi(getenv(wd_name)), NULL, 0);

    wd_pid = fork();

    if (0 == wd_pid)
    {
        execlp(WD_PATH, "wd.out", NULL);
    }
    else
    {
        IntToString(wd_pid, wd_value, 10);
        printf("pid_usr after revive is %d\n", wd_pid);
        if (setenv(wd_name, wd_value, 1))
        {
            printf("Error in \"putenv\"\n");

            SchedulerDestroy(sched);

            return;
        }
    }
}

static int TaskHandshake(void *param)
{
    sem_post(sem_usr);

    while (0 != sem_wait(sem_wd))
        ;

    printf("handshake usr\n");

    (void)param;

    return (1);
}

static int TaskSendSignal(void *params)
{
    pid_t pid = 0;
    char pid_wd[] = "pid_wd";

    pid = atoi(getenv(pid_wd));

    if (TRUE == is_stop)
    {
        kill(pid, SIGUSR2);
        SchedulerStop(((task_params_t *)params)->sched);
    }
    else
    {
        ++fault_tolerance_counter;
        printf("counter of usr is %d\n", fault_tolerance_counter);
        kill(pid, SIGUSR1);
    }

    return (0);
}

static int TaskCheckFault(void *params)
{
    sig_atomic_t max_faults = (sig_atomic_t)((task_params_t *)params)->tolerance;
    timespec_t start_time = {0, 0};

    if(-1 == clock_gettime(0, &start_time))
    {
        return (WATCHDOG_CREATION_FAILURE);
    }

    if (max_faults < fault_tolerance_counter)
    {
        printf("reviving now\n");
        Revive(params);

        TaskHandshake(params);
        fault_tolerance_counter = 0;
    }
    else /* need to be removed*/
    {
        printf("usr got signals\n");
    }

    return (SUCCESS);
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
