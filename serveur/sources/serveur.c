#include "serveur.h"

/*
 * Fonction annexe pour adresse en string
 */

void *get_in_addr(struct sockaddr *sa)
{
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*)sa)->sin_addr);
	}
	return &(((struct sockaddr_in6*)sa)->sin6_addr);
}


/**
 * Initialisation de la structure du serveur
 */
int init_serveur(int count, char ** args){
	log("Initialisation du serveur");
	// Il faut un nombre pair d'arguments 
	// (On a des paires (nomArgument + valeur))
	if(count % 2 != 0){
		return -1;
	}

	// Valeurs par défaut.
	serveur.max_user = DEFAULT_MAX_USER;
	serveur.nb_user=0;
	serveur.timeout = DEFAULT_TIMEOUT;
	serveur.port = DEFAULT_PORT;
	// On recupere les valeurs passes en arguments, si specifiees.
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

	// Au depart, la jam n'est pas active
	serveur.playing = 0;
	serveur.configure = 0;
	// Allocation du tableau des clients
	serveur.clients=malloc(sizeof(t_client)*serveur.max_user);

	for(i=0;i<serveur.max_user;i++){
		serveur.clients[i]=NULL;
	}
	// Initialisation du mutex central du serveur.
	serveur.mutex=(pthread_mutex_t)PTHREAD_MUTEX_INITIALIZER;

	// On aura besoin du port du serveur sous forme de chaine de caracteres
	// pour certaines fonctions sur les sockets.
	serveur.port_string = (char *) malloc(sizeof(char) * 6);
	sprintf(serveur.port_string,"%d",serveur.port);

	// Initialisation de la socket
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
	
	// Listen
	if(listen(serveur.socket,LISTEN_QUEUE_SIZE) == -1){
		perror("Listen");
		return -1;
	}
	creer_socket_audio();

	// Création du fichier des comptes

	// Creation du mutex
	serveur.mutex_db=(pthread_mutex_t)PTHREAD_MUTEX_INITIALIZER;

	// Recuperer l'adresse sous forme de String
	// et creer le nom du fichier
	char nom_fichier[128];
	char s[INET6_ADDRSTRLEN];
	inet_ntop(resultat->ai_family, get_in_addr((struct sockaddr *)resultat->ai_addr),s, sizeof s);
	// On libere la structure resultat, plus utile
	freeaddrinfo(resultat);

	//sprintf(nom_fichier,".%s_%s.db",s,serveur.port_string);
	sprintf(nom_fichier,".localhost_%s.db",serveur.port_string);

	serveur.file_comptes = fopen(nom_fichier, "a+");
	if(serveur.file_comptes == NULL){
		perror("Open file_comptes");
		return -1;
	}
	return 0;
}
/**
 * Traitement d'une commande venant du client
 */
void handle(char* message,int socket){
	char* args;
	logf("CLIENT -> %s\n", message);
	// On recupere les arguments de la commande
	char* commande_name=strtok_r(message,"/",&args);
	// On recupere la t_commande correspondant a la commande recue
	t_commande commande=string_to_commande(commande_name);
	// Et on appelle la fonction correspondant a cette commande
	// avec les arguments
	commande.handler(args,socket);
}
/**
 * Boucle principale du serveur, attente de nouvelles connexions.
 *
 */
void * loop(void * args){
	log("Serveur lancé !");

	struct sockaddr_storage addr_user;
	socklen_t size_usr = sizeof(addr_user);
	int socket_accept;

	while(1){
		// On attend une nouvelle connexion sur la socket principale
		socket_accept = accept(serveur.socket, (struct sockaddr *) &addr_user, &size_usr);
		if(socket_accept == -1){
			perror("Accept nouvelle connexion");
			continue;
		}
		// On lance un nouveau thread qui va s'occuper de traiter le nouveau client
		pthread_t handle_commandes_thread;
		if((pthread_create(&handle_commandes_thread, NULL, thread_handle_commandes, &socket_accept)) != 0){
			perror("Création du thread d'accueil");
			exit(-1);
			return NULL;
		}
	}

}
/*
 * Thread de traitement d'un client, boucle en attente de commandes. 
 */
void * thread_handle_commandes(void * args){
	// Recupere la socket passee en argument.
	int *ptr_socket = (int *) args;
	int socket = *ptr_socket;
	char commande[COMMAND_MAX_SIZE];
	while(1){
		// Reset de la commande
		memset(commande, 0, COMMAND_MAX_SIZE);
		// On recoit la commande.
		if(recv(socket, commande, sizeof(commande), 0) <= 0){
			log("Socket client fermée");
			check_client_deconnectes();
			pthread_exit((void *)0);
		}

		// Traitement de la commande recue
		handle(commande,socket);
	}
	// Normalement, ne devrait pas arriver.
	pthread_exit((void *)0);
}

/* Fonction de terminaison du programme */

void serveur_shutdown(int signum){

	int i;
	log("Suppression des clients");
	for(i = 0; i < serveur.max_user; i++){
		if(serveur.clients[i] != NULL){
			supprimer_client(serveur.clients[i]->name);
		}
		free(serveur.clients[i]);
	}
	free(serveur.clients);
	log("Fermeture du fichier db");
	fclose(serveur.file_comptes);
	log("Fin d'execution");
}

int main(int argc, char ** argv){

	// Initialisation
	if(init_serveur(argc - 1, argv + 1) == -1){
		fprintf(stderr,"Argument manquant!\n");
		return EXIT_FAILURE;
	}
	init_commandes();

	// Gestion des signaux pour quitter proprement le programme
	
	struct sigaction action;
	action.sa_handler = serveur_shutdown;
	sigemptyset(&action.sa_mask);
	//sigaction(SIGINT, &action, NULL);
	// Création du thread gérant la boucle principale.
	pthread_t main_thread;
	if((pthread_create(&main_thread, NULL, loop, NULL)) != 0){
		perror("Création du thread principal ");
		return -1;
	}
	// Normalement, le main_thread est une boucle infinie, 
	// donc le code ci-dessous n'est jamais atteint.
	int * status;
	if(pthread_join(main_thread, (void **) &status) != 0){
		perror("Join");
		return -1;
	}
	return EXIT_SUCCESS;
}
