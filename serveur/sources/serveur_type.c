#include "serveur_type.h"

int add_client(char* name, int socket){
	printf("ajout client\n");
	pthread_mutex_lock(&(serveur.mutex));
	printf("cest partie!\n");

	if(serveur.nb_user >= serveur.max_user){
		printf("bug\n");
		return -1;
	}

	int i;
	int placer=1;

	for(i=0;i<serveur.max_user && placer;i++){
		if(serveur.clients[i]==NULL){
			serveur.clients[i]=creer_client(name,socket);
			serveur.nb_user++;
			placer=0;
		}
	}

	pthread_mutex_unlock(&(serveur.mutex));
	return 0;
}

t_client* creer_client(char* name, int socket){
	t_client* c=malloc(sizeof(t_client));
	c->name=strdup(name);
	c->socket=socket;
	return c;
}