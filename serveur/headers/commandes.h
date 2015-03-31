#ifndef COMMANDS_H_GUARD
#define COMMANDS_H_GUARD

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define NBCOMMANDES 18

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
	AUDIO_ACK

} t_commande;

typedef struct {
	t_commande type;
	void (*handler)(char **);
} s_commande; 

s_commande tab_commandes[NBCOMMANDES];

/* INIT */

void init_commandes();

/* PARSE */

s_commande string_to_commande(char * commande);
char * commande_to_string(s_commande commande);

/* HANDLERS */
void handler_UNKNOWN(char ** args);
void handler_CONNECT(char ** args);
void handler_WELCOME(char ** args);
void handler_AUDIO_PORT(char ** args);
void handler_AUDIO_OK(char ** args);
void handler_CONNECTED(char ** args);
void handler_EXIT(char ** args);
void handler_EXITED(char ** args);
void handler_EMPTY_SESSION(char ** args);
void handler_CURRENT_SESSION(char ** args);
void handler_SET_OPTIONS(char ** args);
void handler_ACK_OPTS(char ** args);
void handler_FULL_SESSION(char ** args);
void handler_AUDIO_CHUNK(char ** args);
void handler_AUDIO_KO(char ** args);
void handler_AUDIO_MIX(char ** args);
void handler_AUDIO_ACK(char ** args);
void handler_UNKNOWN(char ** args);

#endif

