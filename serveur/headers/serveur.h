#ifndef SERVEUR_H_GUARD
#define SERVEUR_H_GUARD



#include "commandes.h"

#define DEFAULT_PORT 2015
#define DEFAULT_MAX_USER 4
#define DEFAULT_TIMEOUT 1000 // millisecondes.


// Initialisation du serveur
int init_serveur(int count, char ** args);
// Handler de commandes
void handle(char* commande,int socket);
// Boucle d'accept du serveur
void * loop(void * args);
// Fonction d'accueil des clients
void * new_connection(void * args);
#endif
