#ifndef SERVEUR_TYPE_H_GUARD
#define SERVEUR_TYPE_H_GUARD

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <pthread.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>

#ifndef DEBUG
#define DEBUG 0
#endif

#define PORT_AUDIO_INIT 2020
#define LISTEN_QUEUE_SIZE 5


#define log(X) if(DEBUG) fprintf(stderr,"%s\n", X);
#define logf(X,Y) if(DEBUG) fprintf(stderr,X,Y);
#define log2f(X,Y,Z) if(DEBUG) fprintf(stderr,X,Y,Z);
#define log3f(A,B,C,D) if(DEBUG) fprintf(stderr,A,B,C,D);
typedef struct 
{
	char* name;
	int socket;
	int socket_audio;
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
void supprimer_client(char * name);
t_client* creer_client(char* name, int socket);
int get_indice_client(char * name);
int creer_socket_audio(char * name);
void check_client_deconnectes();
#endif
