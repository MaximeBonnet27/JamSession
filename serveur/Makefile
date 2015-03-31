CC = gcc
OBJ = obj
BIN = bin
SRC = sources
INC = headers
CFLAGS = -Wall 
LFLAGS = -l pthread -ansi -I $(INC)
LIBS = -lpthread -lrt
TARGETS = $(BIN)/serveur 

all: dirs $(TARGETS)


$(BIN)/serveur : $(OBJ)/serveur.o
	$(CC) $(LFLAGS) -o $@ $^ $(LIBS)


$(OBJ)/%.o: $(SRC)/%.c
	$(CC) $(CFLAGS) -I $(INC) -c $< -o $@ $(LIBS)

dirs:
	@mkdir -p $(OBJ) $(BIN)

clean:
	rm -rf $(BIN)/* $(OBJ)/*

cleanall:
	rm -rf *~ $(OBJ) $(BIN) $(INC)/*~ $(SRC)/*~ 
