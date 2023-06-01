NAME:=stress-strain
LIB_NAME:=lib$(NAME).so
RES_PATH:=../src/main/resources
FLAGS:=-Wall -ffree-form -ffree-line-length-none -cpp -fno-underscoring -std=legacy -finit-local-zero -fcheck=all -g

ifdef DEBUG
	FLAGS+=-g
endif

ifdef PRECISE
	FLAGS+=-fdefault-integer-8 -fdefault-real-8 
endif

MV_LIB:=mv -f $(LIB_NAME)
ifeq ($(OS),Windows_NT)
	MV_LIB+= $(RES_PATH)/$(patsubst lib%,%,$(addsuffix .dll,$(basename $(LIB_NAME))))
	FLAGS+=-static-libgfortran
else
	MV_LIB+= $(RES_PATH)/$(LIB_NAME)
endif

.PHONY: all test

all: lib

lib: $(LIB_NAME)
	$(MV_LIB)

$(LIB_NAME):
	@echo "Building native library..."
	gfortran -fPIC -shared $(FLAGS) arraylist.f90 $(NAME).for -o $(LIB_NAME)

test:
	@echo "Running test:"; cat test.in
	cat test.in | ./stress-strain

clean:
	rm -f $(LIB_NAME) $(addprefix $(NAME),.dat .a .bias) zo dzo