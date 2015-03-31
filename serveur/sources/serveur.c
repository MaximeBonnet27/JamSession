#include "serveur.h"

int port;
int max_user;
int timeout;

int init_config(int count, char ** args){

	if(count % 2 != 0){
		return -1;
	}
	
	// Valeurs par défaut.
	max_user = DEFAULT_MAX_USER;
	timeout = DEFAULT_TIMEOUT;
	port = DEFAULT_PORT;

	int i;
	for(i = 0; i < count; i += 2){
		if(strcmp(args[i],"-max") == 0){
			max_user = atoi(args[i+1]);	
		}
		else if(strcmp(args[i],"-timeout") == 0){
			timeout = atoi(args[i+1]);
		}
		else if(strcmp(args[i],"-port") == 0){
			port = atoi(args[i+1]);
		}
		else{
			fprintf(stderr,"Argument inconnu : %s\n", args[i]);
		}
	}
	return 0;
}

int main(int argc, char ** argv){

	// Initialisation
	if(init_config(argc - 1, argv + 1) == -1){
		fprintf(stderr,"Argument manquant!\n");
		return EXIT_FAILURE;
	}
	
	return EXIT_SUCCESS;
}
