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
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/time.h>

#include "audio.h"

#ifndef DEBUG
#define DEBUG 0
#endif

#define PORT_AUDIO_INIT 2020
#define LISTEN_QUEUE_SIZE 5


#define log(X) if(DEBUG) fprintf(stderr,"%s\n", X);
#define logf(X,Y) if(DEBUG) fprintf(stderr,X,Y);
#define log2f(X,Y,Z) if(DEBUG) fprintf(stderr,X,Y,Z);
#define log3f(A,B,C,D) if(DEBUG) fprintf(stderr,A,B,C,D);

// Hack pour Mac OS
#ifndef MSG_NOSIGNAL
#define MSG_NOSIGNAL SO_NOSIGPIPE
#endif
typedef struct 
{
	// Nom du client
	char* name;
	// Socket de controle
	int socket;
	// Socket audio
	int socket_audio;
	// Booleen, indique si le client est l'admin
	int is_admin;
	// Liste des buffers
	t_buffer_queue queue;
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
	// Indique si la jam est active ou non.
	int playing;
	// Booleen, indique si le serveur a ete correctement
	// configure (on ne voudrait pas lancer la jam si non)
	int configure;
	// Tableau des utilisateurs	
	t_client** clients;
	// Descripteur du fichier des comptes
	FILE * file_comptes;
	// Mutex d'acces au serveur
	pthread_mutex_t mutex;
	// Mutex d'acces au fichier db
	pthread_mutex_t mutex_db;
	int current_tick;
} t_serveur;

// Instance unique du serveur
t_serveur serveur;

int add_client(char* name, int socket, int login);
void supprimer_client(char * name);
t_client* creer_client(char* name, int socket);
int get_indice_client(char * name);
int get_indice_from_socket(int socket);
int get_indice_from_socket_audio(int socket_audio);
int creer_socket_audio();
void set_options(char * style, char * tempo);
void commencer_jam();
void stopper_jam();
int compte_existe(char * nom, char * mdp);
void enregistrer_nouveau_compte(char * nom, char * mdp);
int check_authentification(char * nom, char * mdp);
char * nom_valide(char * nom);
t_audio_buffer getMix(int tick, t_client * client);
#endif
