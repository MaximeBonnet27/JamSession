#ifndef SERVEUR_H_GUARD
#define SERVEUR_H_GUARD



#include "commandes.h"

#define DEFAULT_PORT 2015
#define DEFAULT_MAX_USER 4
#define DEFAULT_TIMEOUT 1000 // millisecondes.


// Initialisation du serveur
int init_serveur(int count, char ** args);
// Boucle d'accept du serveur
void * loop(void * args);

#endif
