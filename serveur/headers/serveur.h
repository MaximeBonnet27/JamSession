#ifndef SERVEUR_H_GUARD
#define SERVEUR_H_GUARD

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "commandes.h"

#define DEFAULT_PORT 2015
#define DEFAULT_MAX_USER 4
#define DEFAULT_TIMEOUT 1000 // millisecondes.

int init_config(int count, char ** args);

void handle(char* commande);

#endif
