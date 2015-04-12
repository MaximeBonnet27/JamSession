#ifndef COMMANDS_H_GUARD
#define COMMANDS_H_GUARD

#include "serveur_type.h"

#define NBCOMMANDES 18
#define COMMAND_MAX_SIZE 128
typedef enum {

	UNKNOWN,
	CONNECT,
	WELCOME,
	AUDIO_PORT,
	AUDIO_OK,
	CONNECTED,
	EXIT,
	EXITED,
	EMPTY_SESSION,
	CURRENT_SESSION,
	SET_OPTIONS,
	ACK_OPTS,
	FULL_SESSION,
	AUDIO_CHUNK,
	AUDIO_KO,
	AUDIO_MIX,
	AUDIO_ACK,
	LS

} e_commande;

typedef struct {
	e_commande type;
	void (*handler)(char *,int);
} t_commande; 

t_commande tab_commandes[NBCOMMANDES];

/* INIT */

void init_commandes();

/* PARSE */

t_commande string_to_commande(char * commande);
char * commande_to_string(t_commande commande);

/* HANDLERS */
void handler_UNKNOWN(char * args ,int socket);
void handler_CONNECT(char * args ,int socket);
void handler_WELCOME(char * args ,int socket);
void handler_AUDIO_PORT(char * args ,int socket);
void handler_AUDIO_OK(char * args ,int socket);
void handler_CONNECTED(char * args ,int socket);
void handler_EXIT(char * args ,int socket);
void handler_EXITED(char * args ,int socket);
void handler_EMPTY_SESSION(char * args ,int socket);
void handler_CURRENT_SESSION(char * args ,int socket);
void handler_SET_OPTIONS(char * args ,int socket);
void handler_ACK_OPTS(char * args ,int socket);
void handler_FULL_SESSION(char * args ,int socket);
void handler_AUDIO_CHUNK(char * args ,int socket);
void handler_AUDIO_KO(char * args ,int socket);
void handler_AUDIO_MIX(char * args ,int socket);
void handler_AUDIO_ACK(char * args ,int socket);
void handler_UNKNOWN(char * args ,int socket);
void handler_LS(char * args, int socket);

/* Fonctions annexes */
void check_client_deconnectes();
#endif

