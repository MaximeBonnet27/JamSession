#include "audio.h"
#include "serveur_type.h"
#include <string.h>

/**
 * ajout a sa place dans la 'queue' le buffer audio
 */
int add_queue(t_buffer_queue * queue, t_audio_buffer* buffer){
	if(queue->tab[buffer->tick]!=NULL){
		destroy_audio_buffer(queue->tab[buffer->tick]);
	}
	queue->tab[buffer->tick] = buffer;
	return 0;
}

/**
 * libere la mémoire du buffer audio
 */
void destroy_audio_buffer(t_audio_buffer* buffer){
	free(buffer->buffer);
	free(buffer);
}

/**
 * renvoie le buffer du tick attendu et passe au prochain tick
 */
t_audio_buffer* pop(t_buffer_queue* queue){
	t_audio_buffer * res;
	res=queue->tab[queue->next_tick_to_send];
	queue->next_tick_to_send = (queue->next_tick_to_send + 1) % 4;
	return res;
}

/**
 * initialise la 'queue' avec des cases NULL
 */
void init_queue(t_buffer_queue * queue){
	int i;
	for (i = 0; i < QUEUE_MAX_SIZE; ++i)
	{
		queue->tab[i]=NULL;
	}
}

/**
 * creer un buffer audio 
 */
t_audio_buffer* create_audio_buffer(char* tick, char* buffer){
	t_audio_buffer* res=malloc(sizeof(t_audio_buffer));
	res->buffer=malloc(sizeof(int)*AUDIO_BUFFER_MAX_SIZE);
	res->tick=atoi(tick);
	convertStringToAudio(buffer, res);
	return res;
}

/**
 * converti un string en buffer audio (int)
 */
void convertStringToAudio(char* str, t_audio_buffer* buffer){
	int i;

	str[strlen(str)-1]='\0'; //enleve le '/' a la fin
	int val;
	char* val_str;

	for(i = 0; i < AUDIO_BUFFER_MAX_SIZE && str!=NULL && strcmp("",str)!=0; ++i){
		val_str=strtok_r(str, ",",&str);
		val=atoi(val_str);
		buffer->buffer[i]=val;
	}
	buffer->size=i;
}

/**
 * converti un buffer audio (int) en string
 */
void convertAudioToString(t_audio_buffer* buffer, char ** res){
	char val[10];
	
	strcpy(*res,"");
	int i;
	for (i = 0; i < buffer->size; ++i){
		sprintf(val,"%d,",buffer->buffer[i]);
		strcat(*res,val);
	}
}


