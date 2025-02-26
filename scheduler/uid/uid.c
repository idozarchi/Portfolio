/*
 FileName: uid
 Author: Ido Zarchi 
 Date: 30/04/24
 reviewer: Shalom
*/

#include <unistd.h>
#include <pthread.h>   /*pthread_mutex*/
#include "uid.h"

#define SUCSSES (1)
#define FAIL (0)

pthread_mutex_t counter_lock = PTHREAD_MUTEX_INITIALIZER;
volatile size_t count = 0;
ilrd_uid_t bad_uid = {-1, 0, 0};

ilrd_uid_t UIDCreate(void)
{
	ilrd_uid_t uid = {0};
	uid.pid = getpid();
	uid.time_stamp = time(NULL);
	
	if(-1 == uid.time_stamp)
	{
		return (bad_uid);
	}
	
	pthread_mutex_lock(&counter_lock);
	uid.counter = count;
	count += 1;
	pthread_mutex_unlock(&counter_lock);
	
	return (uid);
}

ilrd_uid_t UIDGetBadUid(void)
{	
	return (bad_uid);
}

int UIDIsEqual(ilrd_uid_t uid_1, ilrd_uid_t uid_2)
{
	if(uid_1.time_stamp != uid_2.time_stamp)
	{
		return (FAIL);
	}
	
	if(uid_1.pid != uid_2.pid)
	{
		return (FAIL);
	}
	
	if(uid_1.counter != uid_2.counter)
	{
		return (FAIL);
	}
	
	return (SUCSSES);
}

