#include "serveur.h"


int init_serveur(int count, char ** args){
	printf("initialisation du serveur\n");
	if(count % 2 != 0){
		return -1;
	}

	// Valeurs par défaut.
	serveur.max_user = DEFAULT_MAX_USER;
	serveur.nb_user=0;
	serveur.timeout = DEFAULT_TIMEOUT;
	serveur.port = DEFAULT_PORT;

	int i;
	for(i = 0; i < count; i += 2){
		if(strcmp(args[i],"-max") == 0){
			serveur.max_user = atoi(args[i+1]);	
		}
		else if(strcmp(args[i],"-timeout") == 0){
			serveur.timeout = atoi(args[i+1]);
		}
		else if(strcmp(args[i],"-port") == 0){
			serveur.port = atoi(args[i+1]);
		}
		else{
			fprintf(stderr,"Argument inconnu : %s\n", args[i]);
		}
	}
	serveur.clients=malloc(sizeof(t_client)*serveur.max_user);
	
	for(i=0;i<serveur.max_user;i++)
		serveur.clients[i]=NULL;
	serveur.mutex=(pthread_mutex_t)PTHREAD_MUTEX_INITIALIZER;

	serveur.port_string = (char *) malloc(sizeof(char) * 6);
	sprintf(serveur.port_string,"%d",serveur.port);

	struct addrinfo hints, *resultat;

	int yes = 1;

	memset(&hints, 0, sizeof(hints));
	hints.ai_family = AF_INET; // IPv4
	hints.ai_socktype = SOCK_STREAM; // TCP
	hints.ai_flags = AI_PASSIVE; // en local

	// Recuperer les infos 
	if (getaddrinfo(NULL, serveur.port_string, &hints, &resultat) != 0) {
		return -1;
	}
	// Création socket
	if((serveur.socket = socket(resultat->ai_family, resultat->ai_socktype, resultat->ai_protocol)) == -1){
		perror("Création socket");
		return -1;
	}
	// Réutilisation du port
	if (setsockopt(serveur.socket, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1) {
		perror("setsockopt");
		return -1;
	}
	// Bind
	if(bind(serveur.socket, resultat->ai_addr, resultat->ai_addrlen) == -1){
		close(serveur.socket);
		perror("Bind socket");
		return -1;
	}

	freeaddrinfo(resultat);

	// Listen
	if(listen(serveur.socket,LISTEN_QUEUE_SIZE) == -1){
		perror("Listen");
		return -1;
	}

	return 0;
}

void handle(char* message,int socket){
	char* args;
	char* commande_name=strtok_r(message,"/",&args);

	t_commande commande=string_to_commande(commande_name);
	commande.handler(args,socket);
}

void * loop(void * args){
	printf("lancement du serveur\n");

	struct sockaddr_storage addr_user;
	socklen_t size_usr = sizeof(addr_user);
	int socket_accept;

	while(1){
		socket_accept = accept(serveur.socket, (struct sockaddr *) &addr_user, &size_usr);
		if(socket_accept == -1){
			perror("Accept nouvelle connexion");
			continue;
		}
		pthread_t new_connection_thread;
		if((pthread_create(&new_connection_thread, NULL, new_connection, &socket_accept)) != 0){
			perror("Création du thread d'accueil");
			exit(-1);
			return NULL;
		}
	}

}

void * new_connection(void * args){

	int *ptr_socket = (int *) args;
	int socket = *ptr_socket;
	char commande[COMMAND_MAX_SIZE];
	// Tout d'abord, on attend le CONNECT.
	if(recv(socket, commande, sizeof(commande), 0) == -1){
		perror("Recv CONNECT");
		exit(-1);
		return NULL;
	}
	handle(commande,socket);
	pthread_exit((void *)0);
}

int main(int argc, char ** argv){

	// Initialisation
	if(init_serveur(argc - 1, argv + 1) == -1){
		fprintf(stderr,"Argument manquant!\n");
		return EXIT_FAILURE;
	}
	log("HELLOOO");
	init_commandes();
	// Création du thread gérant la boucle principale.
	pthread_t main_thread;
	if((pthread_create(&main_thread, NULL, loop, NULL)) != 0){
		perror("Création du thread principal ");
		return -1;
	}
	int * status;
	if(pthread_join(main_thread, (void **) &status) != 0){
		perror("Join");
		return -1;
	}
	return EXIT_SUCCESS;
}
