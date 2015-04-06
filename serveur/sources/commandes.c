#include "commandes.h"
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

	tab_commandes[LS].type = LS;
	tab_commandes[LS].handler = handler_LS;
}

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

void handler_UNKNOWN(char * args, int socket){
	fprintf(stderr, "Commande inconnue\n");
}
/*
 * Receptionn de CONNECT
 * Args : user/(...)
 * socket : ctrl
 */ 
void handler_CONNECT(char * args,int socket){
	strtok(args,"/");	
	logf("Connect : NAME = (%s)\n", args);
	if(add_client(args,socket) == -1){
		fprintf(stderr,"Serveur complet");
		return;
	}

	handler_WELCOME(args,socket);
	handler_AUDIO_PORT(args,socket);
	handler_CONNECTED(args, socket);
}
/*
 * Envoi de WELCOME
 * Args : user
 * socket : ctrl
 */
void handler_WELCOME(char * args,int socket){
	char welcome_cmd[COMMAND_MAX_SIZE];
	sprintf(welcome_cmd,"WELCOME/%s/\n",args);
	if(send(socket, welcome_cmd, strlen(welcome_cmd) + 1, 0) == -1){
		perror("Send welcome");
	}
	logf("<- %s", welcome_cmd); 
}
/*
 * Envoi de AUDIO_PORT
 * Args : user
 * socket : ctrl
 */
void handler_AUDIO_PORT(char * args,int socket){

	int new_socket_audio = creer_socket_audio(args);
	char audio_port_cmd[COMMAND_MAX_SIZE];
	sprintf(audio_port_cmd,"AUDIO_PORT/%d/\n",PORT_AUDIO_INIT + get_indice_client(args));

	if(send(socket, audio_port_cmd, strlen(audio_port_cmd) + 1, 0) == -1){
		perror("Send audio port");
	}
	logf("<- %s",audio_port_cmd); 

	handler_AUDIO_OK(args, new_socket_audio);	

}
/*
 * Envoi de AUDIO_OK
 * Args : user
 * socket : audio
 */
void handler_AUDIO_OK(char * args,int socket){

	char audio_ok_cmd[COMMAND_MAX_SIZE];
	sprintf(audio_ok_cmd,"AUDIO_OK/\n");


	struct sockaddr_storage addr_user;
	socklen_t size_usr = sizeof(addr_user);

	int socket_accept = accept(socket, (struct sockaddr *) &addr_user, &size_usr);
	if(socket_accept == -1){
		perror("Accept nouvelle connexion audio");
		return;
	}
	pthread_mutex_lock(&serveur.mutex);
	serveur.clients[get_indice_client(args)]->socket_audio = socket_accept;	
	pthread_mutex_unlock(&serveur.mutex);
	if(send(serveur.clients[get_indice_client(args)]->socket, audio_ok_cmd, strlen(audio_ok_cmd) + 1, 0) == -1){
		perror("Send audio ok");
	}
	logf("<- %s", audio_ok_cmd); 



}
/*
 * Envoi de CONNECTED
 * Args : user
 * socket : ctrl
 */
void handler_CONNECTED(char * args,int socket){

	char connected_cmd[COMMAND_MAX_SIZE];
	sprintf(connected_cmd,"CONNECTED/%s/\n",args);
	int i;
	// BROADCAST
	for(i = 0; i < serveur.max_user; i++){
		if(serveur.clients[i] != NULL){
			if(send(serveur.clients[i]->socket, connected_cmd, strlen(connected_cmd) + 1, 0) == -1){
				perror("Send connected_cmd");
			}
		}
	}

}
/*
 * Reception de EXIT
 * Args : user/(...)
 * socket : ctrl
 */
void handler_EXIT(char * args,int socket){
	supprimer_client(strtok(args,"/"));
	char exited_cmd[COMMAND_MAX_SIZE];
	sprintf(exited_cmd,"EXITED/%s/\n",args);
	// BROADCAST
	int i;
	for(i = 0; i < serveur.max_user; i++){
		if(serveur.clients[i] != NULL){
			if(send(serveur.clients[i]->socket, exited_cmd, strlen(exited_cmd) + 1, 0) == -1){
				perror("Send exited_cmd");
			}
		}
	}
}
void handler_EXITED(char * args,int socket){}
void handler_EMPTY_SESSION(char * args,int socket){}
void handler_CURRENT_SESSION(char * args,int socket){}
void handler_SET_OPTIONS(char * args,int socket){}
void handler_ACK_OPTS(char * args,int socket){}
void handler_FULL_SESSION(char * args,int socket){}
void handler_AUDIO_CHUNK(char * args,int socket){}
void handler_AUDIO_KO(char * args,int socket){}
void handler_AUDIO_MIX(char * args,int socket){}
void handler_AUDIO_ACK(char * args,int socket){}

/*
 * Reception de LS
 * Arguments inutiles.
 */
void handler_LS(char * args, int socket){
	log("Etat du serveur actuel : [");
	log2f("Utilisateurs : %d / %d\n", serveur.nb_user, serveur.max_user);
	int i;
	for(i = 0; i <  serveur.max_user; i++){
		if(serveur.clients[i] != NULL){
			logf("Client : %s\n", serveur.clients[i]->name);
		}
	}
	log("]");
}
