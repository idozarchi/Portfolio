/*
Filename: wdt.h
Author: Ido Zarchi 
Reviewer: 
Date: 05/07/2024
*/

#ifndef _ILRD_WDT_H_
#define _ILRD_WDT_H_

#include <pthread.h> /*struct timespec*/
#include <stddef.h> /*size_t*/
#include <time.h> /*struct timespec*/

/******************************************************************************
	SIGUSR1 and SIGUSR2 is occupied - NOT SHURE ABOUT SIGUSR2.
	Use MakeMeImmortal at start, and DoNotResuscitate at the end.
******************************************************************************/


typedef enum status
{
    SUCCESS,
    WATCHDOG_CREATION_FAILURE /*fork, pthread_create, env_var_create?, gettime*/
}status_t;

/* --------------------------------------------------------------------------
* Description: attach a watchdog to a program.
*              call this function right before the code clock to be watched.
*              this fuction created a child watchdog process and a new thread.
*              the watchdog and the thread comunicate through SIGUSR1.
*              they notify each other that each of them is alive.
*              whenever one of them terminates it is rebooted by the other.
* Params: 	argv - list of program argument (argv[0] = "./<user_program>.out").
* 		    interval - max accepted time that the watched process is terminated.
*    		tolerance - max number of times a signal can be missed.
*           path_to_wd_out - string of path to watchdog.out file.
* Return value: status - 0 for SUCCESS, 1 for creation failure.
* Time Complexity: .
* Undefined Behavior: argv not appropriate, interval <= 0, interval/tolerance < x(sec).
--------------------------------------------------------------------------*/
int MakeMeImmortal(struct timespec interval, size_t tolerance, const char *argv[],
												 const char *path_to_wd_out);/*WDStart*/

/*
DoNotResuscitate:

*/
int DoNotResuscitate(void);/*WDStop*/

#endif /* _ILRD_WDT_H_ */




/******************************************************************************
 * 	To Delete!
******************************************************************************/

/*
Files in the project: 
				user_program.c
				wdt_client.c
				wdt.c
				wdt.exe
				wdt.h
				wdt.so
*/


/*
Requirements:
			IPC: signals
			fault tolerance
			mutual protection
			If user_program dies, the WDT does not close itself!
*/
