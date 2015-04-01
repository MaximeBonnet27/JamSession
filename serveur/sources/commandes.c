#include "commandes.h"
void init_commandes(){
	printf("initialisation des commandes\n");

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
	fprintf(stderr, "commande inconnue\n");
}

void handler_CONNECT(char * args,int socket){
	printf("nouvelle connexion de: %s\n",strtok(args,"/"));

	if(add_client(args,socket) == -1){
		fprintf(stderr,"Serveur complet");
		return;
	}

	handler_WELCOME(args,socket);
}

void handler_WELCOME(char * args,int socket){
	int i;
	printf("afficher liste client nb=%d\n",serveur.nb_user);
	for(i=0;i<serveur.nb_user;i++){
		printf("client %d: %s\n",i,serveur.clients[i]->name);
	}
	printf("fin\n");
}

void handler_AUDIO_PORT(char * args,int socket){}
void handler_AUDIO_OK(char * args,int socket){}
void handler_CONNECTED(char * args,int socket){}
void handler_EXIT(char * args,int socket){}
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

