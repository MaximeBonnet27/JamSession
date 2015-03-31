#include "commandes.h"

t_commande string_to_commande(char * commande){
	if(strcmp(commande,"CONNECT") == 0){
		return CONNECT;
	}else if(strcmp(commande,"WELCOME") == 0){
		return WELCOME;
	}else if(strcmp(commande,"AUDIO_PORT") == 0){
		return AUDIO_PORT;
	}else if(strcmp(commande,"AUDIO_OK") == 0){
		return AUDIO_OK;
	}else if(strcmp(commande,"CONNECTED") == 0){
		return CONNECTED;
	}else if(strcmp(commande,"EXIT") == 0){
		return EXIT;
	}else if(strcmp(commande,"EXITED") == 0){
		return EXITED;
	}else if(strcmp(commande,"EMPTY_SESSION") == 0){
		return EMPTY_SESSION;
	}else if(strcmp(commande,"CURRENT_SESSION") == 0){
		return CURRENT_SESSION;
	}else if(strcmp(commande,"SET_OPTIONS") == 0){
		return SET_OPTIONS;
	}else if(strcmp(commande,"ACK_OPTS") == 0){
		return ACK_OPTS;
	}else if(strcmp(commande,"FULL_SESSION") == 0){
		return FULL_SESSION;
	}else if(strcmp(commande,"AUDIO_CHUNK") == 0){
		return AUDIO_CHUNK;
	}else if(strcmp(commande,"AUDIO_OK") == 0){
		return AUDIO_OK;
	}else if(strcmp(commande,"AUDIO_KO") == 0){
		return AUDIO_KO;
	}else if(strcmp(commande,"AUDIO_MIX") == 0){
		return AUDIO_MIX;
	}else if(strcmp(commande,"AUDIO_ACK") == 0){
		return AUDIO_ACK;
	}else {
		return UNKNOWN;
	}	       

}
char * commande_to_string(t_commande commande){
	
	switch(commande){
		case CONNECT : return "CONNECT";
		case WELCOME : return "WELCOME";
		case AUDIO_PORT : return "AUDIO_PORT";
		case AUDIO_OK : return "AUDIO_OK";
		case CONNECTED : return "CONNECTED";
		case EXIT : return "EXIT";
		case EXITED : return "EXITED";
		case EMPTY_SESSION : return "EMPTY_SESSION";
		case CURRENT_SESSION : return "CURRENT_SESSION";
		case SET_OPTIONS : return "SET_OPTIONS";
		case ACK_OPTS : return "ACK_OPTS";
		case FULL_SESSION : return "FULL_SESSION";
		case AUDIO_CHUNK : return "AUDIO_CHUNK";
		case AUDIO_OK : return "AUDIO_OK";
		case AUDIO_KO : return "AUDIO_KO";
		case AUDIO_MIX : return "AUDIO_MIX";
		case AUDIO_ACK : return "AUDIO_ACK";
		default : return "UNKNOWN";
	}


}

