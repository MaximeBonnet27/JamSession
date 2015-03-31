#ifndef COMMANDS_H_GUARD
#define COMMANDS_H_GUARD

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
	AUDIO_OK,
	AUDIO_KO,
	AUDIO_MIX,
	AUDIO_ACK

} t_commande;

t_commande string_to_commande(char * commande);
char * commande_to_string(t_commande commande);

#endif

