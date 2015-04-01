CC = gcc
OBJ = obj
BIN = bin
SRC = sources
INC = headers
CFLAGS = -Wall
LIBS = -lpthread
TARGETS = $(BIN)/serveur 

all: dirs $(TARGETS)


$(BIN)/serveur : $(OBJ)/serveur.o $(OBJ)/commandes.o $(OBJ)/serveur_type.o
	$(CC) -o $@ $(LIBS) $^ 


$(OBJ)/%.o: $(SRC)/%.c
	$(CC) $(CFLAGS) -I $(INC) -o $@ -c $<

dirs:
	@mkdir -p $(OBJ) $(BIN)

clean:
	rm -rf $(BIN)/* $(OBJ)/*

cleanall:
	rm -rf *~ $(OBJ) $(BIN) $(INC)/*~ $(SRC)/*~ 
