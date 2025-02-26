/*
 FileName: uid
 Author: Ido Zarchi 
 Date: 30/04/24
 reviewer: 
*/

#include "uid.h"
#include <stdio.h>

static void TestUID();

int main()
{
	TestUID();
	
	return (0);
}

static void TestUID()
{
	ilrd_uid_t uid1 = UIDCreate();
	ilrd_uid_t uid2 = UIDCreate();
	ilrd_uid_t uid3 = UIDCreate();
	
	printf("Time stamp of first uid is: %ld\n", uid1.time_stamp);
	printf("Counter of first uid is: %ld\n", uid1.counter);
	printf("Pid of first uid is: %d\n", uid1.pid);
	
	printf("\nTime stamp of second uid is: %ld\n", uid2.time_stamp);
	printf("Counter of second uid is: %ld\n", uid2.counter);
	printf("Pid of second uid is: %d\n", uid2.pid);
	
	printf("\nTime stamp of third uid is: %ld\n", uid3.time_stamp);
	printf("Counter of third uid is: %ld\n", uid3.counter);
	printf("Pid of third uid is: %d\n", uid3.pid);
	
	printf("\nWill it return 1? %d\n", UIDIsEqual(uid1, uid1));
	printf("Will it return 0? %d\n", UIDIsEqual(uid1, uid2));
}

























