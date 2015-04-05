CC = gcc
OBJ = obj
BIN = bin
SRC = sources
INC = headers
CFLAGS = -Wall
LIBS = -lpthread
TARGETS = $(BIN)/serveur 


all: dirs $(TARGETS)


debug : CFLAGS += -DDEBUG=1
debug : clean all
$(BIN)/serveur : $(OBJ)/serveur.o $(OBJ)/commandes.o $(OBJ)/serveur_type.o
	$(CC) -o $@ $^ $(LIBS) 


$(OBJ)/%.o: $(SRC)/%.c
	$(CC) $(CFLAGS) -I $(INC) -o $@ -c $<

run: $(BIN)/serveur
	@bin/serveur

dirs:
	@mkdir -p $(OBJ) $(BIN)

clean:
	rm -rf $(BIN)/* $(OBJ)/*

cleanall:
	rm -rf $(INC)/*~ $(SRC)/*~ 
