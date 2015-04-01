#ifndef SERVEUR_TYPE_H_GUARD
#define SERVEUR_TYPE_H_GUARD

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <pthread.h>
#ifndef DEBUG
#define DEBUG 0
#endif
#define log(X) if(DEBUG) fprintf(stderr,"%s\n", X);

typedef struct 
{
	char* name;
	int socket;
	/*autre chose?*/
} t_client;

typedef struct {

	int port;
	char * port_string;
	int max_user;
	int nb_user;
	int timeout;
	int socket;
		
	// + liste de clients
	t_client** clients;


	pthread_mutex_t mutex;
} t_serveur;

t_serveur serveur;

int add_client(char* name, int socket);
t_client* creer_client(char* name, int socket);
#endif
