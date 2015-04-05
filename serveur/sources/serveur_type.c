#include "serveur_type.h"

int add_client(char* name, int socket){
	pthread_mutex_lock(&(serveur.mutex));

	if(serveur.nb_user >= serveur.max_user){
		fprintf(stderr,"bug\n");
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

void supprimer_client(char *name){
	log("Suppression du client ...");
	pthread_mutex_lock(&serveur.mutex);
	log("Suppression, mutex pris");
	int index = get_indice_client(name);
	log("Suppression, indice OK");
	if(index == serveur.max_user){
		log("Impossible de supprimer ce client");
		return;
	}
	close(serveur.clients[index]->socket);
	close(serveur.clients[index]->socket_audio);
	free(serveur.clients[index]->name);
	free(serveur.clients[index]);
	serveur.clients[index] = NULL;	
	pthread_mutex_unlock(&serveur.mutex);
	log("Suprression, mutex laché");
	log("Fin Suppression du client");
}
t_client* creer_client(char* name, int socket){
	t_client* c=malloc(sizeof(t_client));
	c->name=strdup(name);
	c->socket=socket;
	return c;
}

int get_indice_client(char * name){
	int i;
	logf("NAME -> (%s)\n", name);
	for(i = 0; i < serveur.max_user; i++){
		if(serveur.clients[i] != NULL && strcmp(serveur.clients[i]->name, name) == 0){
			break;
		}
	}
	logf("INDICE Client : %d\n", i);
	return i;
}
int creer_socket_audio(char * name){

	int socket_audio;

	char port_to_string[6]; 
	sprintf(port_to_string,"%d",PORT_AUDIO_INIT + get_indice_client(name));
	struct addrinfo hints, *resultat;

	int yes = 1;

	memset(&hints, 0, sizeof(hints));
	hints.ai_family = AF_INET; // IPv4
	hints.ai_socktype = SOCK_STREAM; // TCP
	hints.ai_flags = AI_PASSIVE; // en local

	// Recuperer les infos 
	if (getaddrinfo(NULL, port_to_string, &hints, &resultat) != 0) {
		return -1;
	}
	// Création socket
	if((socket_audio = socket(resultat->ai_family, resultat->ai_socktype, resultat->ai_protocol)) == -1){
		perror("Création socket");
		return -1;
	}
	// Réutilisation du port
	if (setsockopt(socket_audio, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1) {
		perror("setsockopt");
		return -1;
	}
	// Bind
	if(bind(socket_audio, resultat->ai_addr, resultat->ai_addrlen) == -1){
		close(serveur.socket);
		perror("Bind socket");
		return -1;
	}

	freeaddrinfo(resultat);

	// Listen
	if(listen(socket_audio,LISTEN_QUEUE_SIZE) == -1){
		perror("Listen");
		return -1;
	}

	return socket_audio;

}
