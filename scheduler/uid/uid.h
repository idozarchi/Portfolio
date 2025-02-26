#ifndef _ILRD_UID_H_
#define _ILRD_UID_H_

#include <stddef.h> /* size_t */
#include <time.h> /* time_t */ 
#include <sys/types.h> /* pid */
#include <signal.h> /*sig_atomic_t*/

typedef struct uid
{
	time_t time_stamp;
	pid_t pid;
	size_t counter;
} ilrd_uid_t;

ilrd_uid_t UIDCreate(void);                          /* O(1) */
ilrd_uid_t UIDGetBadUid(void);                       /* O(1) */
int UIDIsEqual(ilrd_uid_t uid_1, ilrd_uid_t uid_2);  /* O(1) */

#endif /* _ILRD_UID_H_ */
