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
			break;
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
	// Arret des communications sur les deux sockets
	shutdown(serveur.clients[index]->socket, SHUT_RDWR);
	shutdown(serveur.clients[index]->socket_audio, SHUT_RDWR);
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
	logf("Socket = %d\n", socket);
	c->socket=socket;
	c->is_admin = 0;
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
 * Renvoie l'indice du client dans le tableau des utilisateurs 
 * a partir de sa socket
 */

int get_indice_from_socket(int socket){
	int i;
	for(i = 0; i < serveur.max_user; i++){
		if(serveur.clients[i] != NULL && serveur.clients[i]->socket == socket){
			break;
		}
	}
	return i;
}

/**
 * Creation de la socket audio du serveur
 */

int creer_socket_audio(){
	// Le numero du port sous forme de chaine de caratere
	// Chaque client a son port, qui correspond a 
	// PORT_AUDIO_INIT + son indice dans le tableau des utilisateurs
	char port_to_string[6]; 
	sprintf(port_to_string,"%d",PORT_AUDIO_INIT);

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
	if((serveur.socket_audio = socket(resultat->ai_family, resultat->ai_socktype, resultat->ai_protocol)) == -1){
		perror("Création socket audio");
		return -1;
	}
	// Réutilisation du port
	if (setsockopt(serveur.socket_audio, SOL_SOCKET, SO_REUSEADDR, &yes, sizeof(int)) == -1) {
		perror("setsockopt audio");
		return -1;
	}
	// Bind
	if(bind(serveur.socket_audio, resultat->ai_addr, resultat->ai_addrlen) == -1){
		close(serveur.socket_audio);
		perror("Bind socket audio");
		return -1;
	}

	freeaddrinfo(resultat);

	// Listen
	if(listen(serveur.socket_audio,LISTEN_QUEUE_SIZE) == -1){
		perror("Listen audio");
		return -1;
	}
	return 1;
}


void set_options(char * style, char * tempo){
	pthread_mutex_lock(&serveur.mutex);

	serveur.style = strdup(style);
	serveur.tempo = strdup(tempo);

	pthread_mutex_unlock(&serveur.mutex);
}

void commencer_jam(){
	pthread_mutex_lock(&serveur.mutex);
	if(serveur.configure){
		serveur.playing = 1;
		log("La jam commence");
	}
	pthread_mutex_unlock(&serveur.mutex);
}

void stopper_jam(){
	pthread_mutex_lock(&serveur.mutex);
	serveur.playing = 0;
	serveur.configure = 0;
	log("La jam se stoppe");
	pthread_mutex_unlock(&serveur.mutex);
}

int read_line(int fd, char ** buffer, int buffer_size){
	log("before memset");
	memset(buffer, 0, sizeof(*buffer));
	log("apres memset");
	int i = 0;
	char c;
	int retour = -1;
	while(1){
		retour = read(fd, &c, 1);
		if(retour < 0){
			perror("Read");
			log("retour < 0");
		       	return retour;
		}
		else{
			if(c == '\n' || c == '\0'){
				return i;
			}
			logf("Caractere lu : (%c)\n", c);
			(*buffer)[i++] = c;	
		}
	}
}


int compte_existe(char * nom, char * mdp){
	pthread_mutex_lock(&serveur.mutex_db);
	char * buffer_ligne;
	buffer_ligne = malloc(128 * sizeof(char));
	log("Avant boucle");
	// On se remet au debut du fichier
	lseek(serveur.fd_comptes, 0, SEEK_SET);
	while(read_line(serveur.fd_comptes, &buffer_ligne, 128) > 0){
		logf("(%s)\n", buffer_ligne);
		log("Boucle");
		strtok(buffer_ligne, " ");
		if(strcmp(buffer_ligne, nom) == 0){
			return 1;
		}	
	}
	log("Apres boucle");
	// On se remet au debut du fichier
	lseek(serveur.fd_comptes, 0, SEEK_SET);
	pthread_mutex_unlock(&serveur.mutex_db);
	return 0;
}

void enregistrer_nouveau_compte(char * nom, char * mdp){
	pthread_mutex_lock(&serveur.mutex_db);
	// On va a la fin du fichier
	
	lseek(serveur.fd_comptes, 0, SEEK_END);
	
	char ligne_a_ecrire[128];
	sprintf(ligne_a_ecrire,"%s %s\n", nom, mdp);
	log("Avant write");
	if(write(serveur.fd_comptes, ligne_a_ecrire, strlen(ligne_a_ecrire) + 1) == -1){
		perror("Write");
	}
	log("Apres write");
	// Revenir au debut
	lseek(serveur.fd_comptes, 0, SEEK_SET);
	pthread_mutex_unlock(&serveur.mutex_db);
}
