#ifndef SERVEUR_H_GUARD
#define SERVEUR_H_GUARD

#include <stdio.h>
#include <stdlib.h>
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

#include "commandes.h"

#define DEFAULT_PORT 2015
#define DEFAULT_MAX_USER 4
#define DEFAULT_TIMEOUT 1000 // millisecondes.

#define LISTEN_QUEUE_SIZE 5
#define COMMAND_MAX_SIZE 128

// Initialisation du serveur
int init_serveur(int count, char ** args);
// Handler de commandes
void handle(char* commande,int socket);
// Boucle d'accept du serveur
void * loop(void * args);
// Fonction d'accueil des clients
void * new_connection(void * args);
#endif
