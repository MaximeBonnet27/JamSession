#include "serveur_type.h"
/**
 * Fonction d'ajout d'un nouveau client dans la structure 
 * du serveur
 */
int add_client(char* name, int socket){
	pthread_mutex_lock(&(serveur.mutex));
	// On ne peut pas se connecter si le serveur est plein
	if(serveur.nb_user >= serveur.max_user){
		fprintf(stderr,"bug\n");
		return -1;
	}
	// Si le serveur n'est pas plein, on recherche une place pour
	// le nouvel utilisateur.
	int i;
	int place=1;
	
	for(i=0;i<serveur.max_user && place;i++){
		if(serveur.clients[i]==NULL){
			// Creation du client a la place trouvee.
			serveur.clients[i]=creer_client(name,socket);
			serveur.nb_user++;
			place=0;
		}
	}

	pthread_mutex_unlock(&(serveur.mutex));
	return 0;
}
/**
 * Fonctionde suppression d'un client lors de sa deconnexion.
 */
void supprimer_client(char *name){
	pthread_mutex_lock(&serveur.mutex);
	// On recupere l'indice ou il etait stocke dans le serveur
	int index = get_indice_client(name);
	if(index == serveur.max_user){
		log("Impossible de supprimer ce client");
		return;
	}
	// Fermeture des deux sockets du client.
	close(serveur.clients[index]->socket);
	close(serveur.clients[index]->socket_audio);
	// Desallocation de son nom
	free(serveur.clients[index]->name);
	// Desallocation de sa structure
	free(serveur.clients[index]);
	serveur.clients[index] = NULL;	

	serveur.nb_user--;
	pthread_mutex_unlock(&serveur.mutex);
}

/**
 * Creation d'une structure client correspondant au nom et a la socket
 * passes en parametres.
 */
t_client* creer_client(char* name, int socket){
	t_client* c=malloc(sizeof(t_client));
	c->name=strdup(name);
	c->socket=socket;
	return c;
}
/**
 * Renvoie l'indice du client dans le tableau des utilisateurs
 * a partir de son nom
 */
int get_indice_client(char * name){
	int i;
	for(i = 0; i < serveur.max_user; i++){
		if(serveur.clients[i] != NULL && strcmp(serveur.clients[i]->name, name) == 0){
			break;
		}
	}
	return i;
}

/**
 * Creation de la socket audio associee a l'utilisateur
 * dont le nom est passe en parametre
 */

int creer_socket_audio(char * name){
	// La nouvelle socket a creer
	int socket_audio;

	// Le numero du port sous forme de chaine de caratere
	// Chaque client a son port, qui correspond a 
	// PORT_AUDIO_INIT + son indice dans le tableau des utilisateurs
	char port_to_string[6]; 
	sprintf(port_to_string,"%d",PORT_AUDIO_INIT + get_indice_client(name));

	// Initialisation de la socket
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
		perror("Création socket audio");
		return -1;
	}
	// Réutilisation du port
	if (setsockopt(socket_audio, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1) {
		perror("setsockopt audio");
		return -1;
	}
	// Bind
	if(bind(socket_audio, resultat->ai_addr, resultat->ai_addrlen) == -1){
		close(socket_audio);
		perror("Bind socket audio");
		return -1;
	}

	freeaddrinfo(resultat);

	// Listen
	if(listen(socket_audio,LISTEN_QUEUE_SIZE) == -1){
		perror("Listen audio");
		return -1;
	}

	return socket_audio;

}
/**
 * Fonction de nettoyage des clients deconnectes,
 * il se peut qu'un client se soit deconnecte sans avoir envoyer de
 * commande EXIT, on le verifie ici
 */
void check_client_deconnectes(){
	// Broadcast d'un PING
	int i;
	char ping_msg[] = "PING\n";
	for(i = 0; i < serveur.max_user; i++){
		if(serveur.clients[i] != NULL){
			// On supprime ceux dont la socket est fermée
			if(send(serveur.clients[i]->socket, ping_msg, strlen(ping_msg) + 1, MSG_NOSIGNAL) == -1){
				log("Client mal deconnecté");
				supprimer_client(serveur.clients[i]->name);
			}
		}
	}

}

void set_options(char * style, char * tempo){
	pthread_mutex_lock(&serveur.mutex);

	serveur.style = strdup(style);
	serveur.tempo = strdup(tempo);

	pthread_mutex_unlock(&serveur.mutex);

}
