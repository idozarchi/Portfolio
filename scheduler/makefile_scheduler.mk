# Paths to the source directories
PATH_SCHEDULER := ./scheduler/
PATH_UID := ./uid/
PATH_TASK := ./task/
PATH_SRC := ../../ds/src/
PATH_INCLUDE := ../../ds/include/
PATH_UTILS_H := ../../utils/include/

# Filenames (without extensions)
FILENAME = scheduler
FILENAME1 = uid
FILENAME2 = task
FILENAME3 = heap
FILENAME4 = heap_pq
FILENAME5 = dvector

# Compiler and flags
CC = gcc
CFLAGS = -I $(PATH_SCHEDULER) -I $(PATH_UID) -I $(PATH_TASK) -I $(PATH_INCLUDE) -I $(PATH_UTILS_H) -ansi -pedantic-errors -Wall -Wextra -fPIC 
SHAREDFLAG = -shared
RM = rm -f

# Library name
LIBNAME = libscheduler

# Source and object files
SRCS := $(PATH_SCHEDULER)$(FILENAME).c $(PATH_SCHEDULER)$(FILENAME)_test.c \
$(PATH_UID)$(FILENAME1).c $(PATH_TASK)$(FILENAME2).c \
$(PATH_SRC)$(FILENAME3).c $(PATH_SRC)$(FILENAME4).c $(PATH_SRC)$(FILENAME5).c
OBJS := $(SRCS:.c=.o)

# Silent make output
.SILENT:

# Phony targets
.PHONY: test clean debug release all

# Default target
all: test

# Shared library target
$(LIBNAME): $(OBJS)
	@echo "Creating Shared Object"
	$(CC) $(SHAREDFLAG) -o $(LIBNAME).so $^

# Object file compilation
%.o: %.c
	@echo "Compiling: $<"
	$(CC) -c $(CFLAGS) $< -o $@

# Test executable
test: $(OBJS) $(LIBNAME)
	@echo "Linking test executable"
	$(CC) $(OBJS) -o scheduler.out

# Debug build
debug: CFLAGS += -g
debug: clean test

# Release build
release: CFLAGS += -O3 -DNDEBUG	
release: clean test

# Clean target
clean:
	@echo "Removing files"
	@$(RM) $(OBJS) $(LIBNAME).so scheduler.out

