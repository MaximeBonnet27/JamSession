#include "commandes.h"

/**
 * Initialisation du tableau des commandes.
 * On associe à chaque commande son type et sa fonction de handler.
 *
 */

void init_commandes(){
	log("Initialisation des commandes");

	tab_commandes[CONNECT].type             = CONNECT;
	tab_commandes[CONNECT].handler          = handler_CONNECT;

	tab_commandes[WELCOME].type             = WELCOME;
	tab_commandes[WELCOME].handler          = handler_WELCOME;

	tab_commandes[AUDIO_PORT].type          = AUDIO_PORT;
	tab_commandes[AUDIO_PORT].handler       = handler_AUDIO_PORT;

	tab_commandes[AUDIO_OK].type            = AUDIO_OK;
	tab_commandes[AUDIO_OK].handler         = handler_AUDIO_OK;

	tab_commandes[CONNECTED].type           = CONNECTED;
	tab_commandes[CONNECTED].handler        = handler_CONNECTED;

	tab_commandes[EXIT].type                = EXIT;
	tab_commandes[EXIT].handler             = handler_EXIT;

	tab_commandes[EXITED].type              = EXITED;
	tab_commandes[EXITED].handler           = handler_EXITED;

	tab_commandes[EMPTY_SESSION].type       = EMPTY_SESSION;
	tab_commandes[EMPTY_SESSION].handler    = handler_EMPTY_SESSION;

	tab_commandes[CURRENT_SESSION].type     = CURRENT_SESSION;
	tab_commandes[CURRENT_SESSION].handler  = handler_CURRENT_SESSION;

	tab_commandes[SET_OPTIONS].type         = SET_OPTIONS;
	tab_commandes[SET_OPTIONS].handler      = handler_SET_OPTIONS;

	tab_commandes[ACK_OPTS].type            = ACK_OPTS;
	tab_commandes[ACK_OPTS].handler         = handler_ACK_OPTS;

	tab_commandes[FULL_SESSION].type        = FULL_SESSION;
	tab_commandes[FULL_SESSION].handler     = handler_FULL_SESSION;

	tab_commandes[AUDIO_CHUNK].type         = AUDIO_CHUNK;
	tab_commandes[AUDIO_CHUNK].handler      = handler_AUDIO_CHUNK;

	tab_commandes[AUDIO_KO].type            = AUDIO_KO;
	tab_commandes[AUDIO_KO].handler         = handler_AUDIO_KO;

	tab_commandes[AUDIO_MIX].type           = AUDIO_MIX;
	tab_commandes[AUDIO_MIX].handler        = handler_AUDIO_MIX;

	tab_commandes[AUDIO_ACK].type           = AUDIO_ACK;
	tab_commandes[AUDIO_ACK].handler        = handler_AUDIO_ACK;

	tab_commandes[UNKNOWN].type             = UNKNOWN;
	tab_commandes[UNKNOWN].handler          = handler_UNKNOWN;
	/*
	 * Fonction en + par rapport à ce qui est demandé, 
	 * le client peut demander au serveur d'afficher
	 * les clients connectés.
	 */
	tab_commandes[LS].type = LS;
	tab_commandes[LS].handler = handler_LS;
}

/**
 * Renvoie la t_commande associée à une commande sous forme de chaine de caractère
 */

t_commande string_to_commande(char * commande){
	if(strcmp(commande,"CONNECT") == 0){
		return tab_commandes[CONNECT];
	}else if(strcmp(commande,"WELCOME") == 0){
		return tab_commandes[WELCOME];
	}else if(strcmp(commande,"AUDIO_PORT") == 0){
		return tab_commandes[AUDIO_PORT];
	}else if(strcmp(commande,"AUDIO_OK") == 0){
		return tab_commandes[AUDIO_OK];
	}else if(strcmp(commande,"CONNECTED") == 0){
		return tab_commandes[CONNECTED];
	}else if(strcmp(commande,"EXIT") == 0){
		return tab_commandes[EXIT];
	}else if(strcmp(commande,"EXITED") == 0){
		return tab_commandes[EXITED];
	}else if(strcmp(commande,"EMPTY_SESSION") == 0){
		return tab_commandes[EMPTY_SESSION];
	}else if(strcmp(commande,"CURRENT_SESSION") == 0){
		return tab_commandes[CURRENT_SESSION];
	}else if(strcmp(commande,"SET_OPTIONS") == 0){
		return tab_commandes[SET_OPTIONS];
	}else if(strcmp(commande,"ACK_OPTS") == 0){
		return tab_commandes[ACK_OPTS];
	}else if(strcmp(commande,"FULL_SESSION") == 0){
		return tab_commandes[FULL_SESSION];
	}else if(strcmp(commande,"AUDIO_CHUNK") == 0){
		return tab_commandes[AUDIO_CHUNK];
	}else if(strcmp(commande,"AUDIO_MIX") == 0){
		return tab_commandes[AUDIO_MIX];
	}else if(strcmp(commande,"AUDIO_ACK") == 0){
		return tab_commandes[AUDIO_ACK];
	}
	else if(strcmp(commande,"LS") == 0){
		return tab_commandes[LS];
	}else {
		return tab_commandes[UNKNOWN];
	}	       

}

/**
 * Renvoie le nom sous forme de chaine de caractère associé
 * à la t_commande passée en paramètre.
 */

char * commande_to_string(t_commande commande){

	switch(commande.type){
		case CONNECT:           return "CONNECT";
		case WELCOME:           return "WELCOME";
		case AUDIO_PORT:        return "AUDIO_PORT";
		case AUDIO_OK:          return "AUDIO_OK";
		case CONNECTED:         return "CONNECTED";
		case EXIT:              return "EXIT";
		case EXITED:            return "EXITED";
		case EMPTY_SESSION:     return "EMPTY_SESSION";
		case CURRENT_SESSION:   return "CURRENT_SESSION";
		case SET_OPTIONS:       return "SET_OPTIONS";
		case ACK_OPTS:          return "ACK_OPTS";
		case FULL_SESSION:      return "FULL_SESSION";
		case AUDIO_CHUNK:       return "AUDIO_CHUNK";
		case AUDIO_KO:          return "AUDIO_KO";
		case AUDIO_MIX:         return "AUDIO_MIX";
		case AUDIO_ACK:         return "AUDIO_ACK";
		default:                return "UNKNOWN";
	}

}

/**
 * Reception d'une commande inconnue.
 */
void handler_UNKNOWN(char * args, int socket){
	fprintf(stderr, "Commande inconnue\n");
}
/*
 * Receptionn de CONNECT
 * Args : user/(...)
 * socket : ctrl
 */ 
void handler_CONNECT(char * args,int socket){
	// On récupère juste la première partie qui correspond au nom de l'utilisateur
	strtok(args,"/");	
	// Ajout de cet user.
	if(add_client(args,socket) == -1){
		fprintf(stderr,"Serveur complet");
		// Si le serveur est complet, on le signale au client
		handler_FULL_SESSION(NULL, socket);
		return;
	}
	// Suite de la procédure de connexion.
	handler_WELCOME(args,socket);
	handler_AUDIO_PORT(args,socket);
	handler_CONNECTED(args, socket);
	// Mise en place des parametres
	if(serveur.nb_user == 1){
		handler_EMPTY_SESSION(NULL, socket);
	}else{
		handler_CURRENT_SESSION(NULL, socket);
	}
}


/*
 * Envoi de WELCOME
 * Args : user
 * socket : ctrl
 */
void handler_WELCOME(char * args,int socket){
	// Ecriture de la commande
	char welcome_cmd[COMMAND_MAX_SIZE];
	sprintf(welcome_cmd,"WELCOME/%s/\n",args);
	// Envoi au client
	if(send(socket, welcome_cmd, strlen(welcome_cmd) + 1, 0) == -1){
		perror("Send welcome");
	}
	logf("<- %s", welcome_cmd); 
	// La suite de la procédure de connection est lancée par CONNECT,
	// on n'a plus rien a faire.
	// (Prochaine fonction appelee : AUDIO_PORT)
}
/*
 * Envoi de AUDIO_PORT
 * Args : user
 * socket : ctrl
 */
void handler_AUDIO_PORT(char * args,int socket){
	// Création d'une nouvelle socket pour le canal audio
	int new_socket_audio = creer_socket_audio(args);
	// Ecriture de la commande
	char audio_port_cmd[COMMAND_MAX_SIZE];
	sprintf(audio_port_cmd,"AUDIO_PORT/%d/\n",PORT_AUDIO_INIT + get_indice_client(args));
	// Envoi au client
	if(send(socket, audio_port_cmd, strlen(audio_port_cmd) + 1, 0) == -1){
		perror("Send audio port");
	}
	logf("<- %s",audio_port_cmd); 
	// Pour s'assurer que la connexion a bien marche.
	handler_AUDIO_OK(args, new_socket_audio);	

}
/*
 * Envoi de AUDIO_OK
 * Args : user
 * socket : audio
 */
void handler_AUDIO_OK(char * args,int socket){
	// Ecriture de la commande
	char audio_ok_cmd[COMMAND_MAX_SIZE];
	sprintf(audio_ok_cmd,"AUDIO_OK/\n");

	// Attente d'une nouvelle connexion sur la socket passee
	// en argument, qui est la socket creee precedemment
	// dans le handler AUDIO_PORT
	struct sockaddr_storage addr_user;
	socklen_t size_usr = sizeof(addr_user);

	int socket_accept = accept(socket, (struct sockaddr *) &addr_user, &size_usr);
	if(socket_accept == -1){
		perror("Accept nouvelle connexion audio");
		return;
	}
	// La connexion s'est bien passee, on peut effectivement associer
	// la socket audio au client.
	pthread_mutex_lock(&serveur.mutex);
	serveur.clients[get_indice_client(args)]->socket_audio = socket_accept;	
	pthread_mutex_unlock(&serveur.mutex);
	// Envoi de la confirmation
	if(send(serveur.clients[get_indice_client(args)]->socket, audio_ok_cmd, strlen(audio_ok_cmd) + 1, 0) == -1){
		perror("Send audio ok");
	}
	logf("<- %s", audio_ok_cmd); 
	// Le canal audio est donc bien etabli
	// Le handler de CONNECTED est ensuite appele par la handler de CONNECT

}
/*
 * Envoi de CONNECTED
 * Args : user
 * socket : ctrl
 */
void handler_CONNECTED(char * args,int socket){
	// Ecriture de la commande
	char connected_cmd[COMMAND_MAX_SIZE];
	sprintf(connected_cmd,"CONNECTED/%s/\n",args);
	int i;
	// BROADCAST
	for(i = 0; i < serveur.max_user; i++){
		// Si le client est connecte
		if(serveur.clients[i] != NULL){
			if(send(serveur.clients[i]->socket, connected_cmd, strlen(connected_cmd) + 1, 0) == -1){
				perror("Send connected_cmd");
			}
		}
	}
	// Procedure de connexion d'un nouvel utilisateur finie.
}
/*
 * Reception de EXIT
 * Args : user/(...)
 * socket : ctrl
 */
void handler_EXIT(char * args,int socket){
	// On recupere seulement la premiere partie de la commande
	// qui correspond au nom d'utilisateur, et on le supprime dans la
	// structure du serveur.
	supprimer_client(strtok(args,"/"));

	// Signal a tout le monde de la deconnexion
	handler_EXITED(NULL, socket);

}
/**
 * Envoi de EXITED
 * Args : inutile
 * socket : ctrl
 */
void handler_EXITED(char * args,int socket){

	// Ecriture de la commande
	char exited_cmd[COMMAND_MAX_SIZE];
	sprintf(exited_cmd,"EXITED/%s/\n",args);
	// BROADCAST
	int i;
	for(i = 0; i < serveur.max_user; i++){
		if(serveur.clients[i] != NULL){
			if(send(serveur.clients[i]->socket, exited_cmd, strlen(exited_cmd) + 1, 0) == -1){
				perror("Send exited_cmd");
			}

			// Rajout, comme le serveur a ete mis a jour avec le exit,
			// on pourrait envoyer current session pour rapeller a tous
			// les clients l'etat actuel du serveur
			handler_CURRENT_SESSION(NULL, serveur.clients[i]->socket);
		}
	}
	
}
/**
 * Envoi de EMPTY_SESSION
 * Args : user
 * socket : ctrl
 */
void handler_EMPTY_SESSION(char * args,int socket){

	// Ecriture de la commande
	char empty_session_cmd[COMMAND_MAX_SIZE];
	sprintf(empty_session_cmd, "EMPTY_SESSION/\n");
	// Envoi de la commande
	if(send(socket, empty_session_cmd, strlen(empty_session_cmd) + 1, 0) == -1){
		perror("Send empty_session");
	}	

}
/**
 * Envoi de CURRENT_SESSION
 * Args : inutile
 * socket : ctrl
 */
void handler_CURRENT_SESSION(char * args,int socket){

	// Ecriture de la commande
	char current_session_cmd[COMMAND_MAX_SIZE];
	sprintf(current_session_cmd, "CURRENT_SESSION/%s/%s/%d/\n", 
			serveur.style, serveur.tempo, serveur.nb_user);
	// Envoi de la commande
	if(send(socket, current_session_cmd, strlen(current_session_cmd) + 1, 0) == -1){
		perror("Send current_session");
	}

}
/**
 * Reception de SET_OPTIONS
 * Args : style/tempo/
 * socket : ctrl
 */
void handler_SET_OPTIONS(char * args,int socket){
	// On recupere la configuration passe en parametre
	char * tempo;
	char * style = strtok_r(args,"/",&tempo);
	// On applique les changemnts au serveur
	set_options(style, strtok(tempo,"/"));
	// Envoi de la confirmation
	handler_ACK_OPTS(NULL, socket);
}

/**
 * Envoi de ACK_OPTS
 * Args : inutile
 * socket : ctrl
 */

void handler_ACK_OPTS(char * args,int socket){

	// Ecriture de la commande
	char ack_opts_cmd[COMMAND_MAX_SIZE];
	sprintf(ack_opts_cmd, "ACK_OPTS/\n");
	// Envoi de la commande
	if(send(socket, ack_opts_cmd, strlen(ack_opts_cmd) + 1, 0) == -1){
		perror("Send ack_opts");
	}
}

/**
 * Envoi de FULL_SESSION
 * Args : inutile
 * socket : ctrl
 */
void handler_FULL_SESSION(char * args,int socket){

	// Ecriture de la commande
	char full_session_cmd[COMMAND_MAX_SIZE];
	sprintf(full_session_cmd,"FULL_SESSION/\n");
	// Envoi de la commande
	if(send(socket, full_session_cmd, strlen(full_session_cmd) + 1, 0) == -1){
		perror("Send full_session");
	}
}



void handler_AUDIO_CHUNK(char * args,int socket){}
void handler_AUDIO_KO(char * args,int socket){}
void handler_AUDIO_MIX(char * args,int socket){}
void handler_AUDIO_ACK(char * args,int socket){}

/*
 * Reception de LS
 * Arguments inutiles.
 */
void handler_LS(char * args, int socket){
	// La fonction, au final, ne fait que des logs.
	// On parcourt toute la table des clients, et on affiche
	// le nom de ceux qui sont connectes.
	log("Etat du serveur actuel : \n[");
	log2f("Config : %s a %s BPM\n", serveur.style, serveur.tempo);
	log2f("Utilisateurs : %d / %d\n", serveur.nb_user, serveur.max_user);
	int i;
	for(i = 0; i <  serveur.max_user; i++){
		if(serveur.clients[i] != NULL){
			logf("Client : %s\n", serveur.clients[i]->name);
		}
	}
	log("]");
}
