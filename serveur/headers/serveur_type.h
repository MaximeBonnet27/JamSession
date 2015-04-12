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
	// Nom du client
	char* name;
	// Socket de controle
	int socket;
	// Socket audio
	int socket_audio;
	/*autre chose?*/
} t_client;

typedef struct {
	// Port principal du serveur
	int port;
	// Port principal, ecrit en chaine de caractere
	char * port_string;
	// Nombre maximal d'utilisateurs connectes en meme temps
	int max_user;
	// Nombre d'utilisateurs connectes actuellement
	int nb_user;
	int timeout;
	// Socket du serveur
	int socket;
	// Socket audio du serveur
	int socket_audio;
	// Options de la jam
	char * style;
	char * tempo;
	// Tableau des utilisateurs	
	t_client** clients;


	pthread_mutex_t mutex;
} t_serveur;

// Instance unique du serveur
t_serveur serveur;

int add_client(char* name, int socket);
void supprimer_client(char * name);
t_client* creer_client(char* name, int socket);
int get_indice_client(char * name);
int creer_socket_audio();
void set_options(char * style, char * tempo);
#endif
