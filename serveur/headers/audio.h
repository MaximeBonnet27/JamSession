#ifndef AUDIO_H_GUARD
#define AUDIO_H_GUARD

#include <stdlib.h>
#include <stdio.h>
#include <string.h>

#define AUDIO_BUFFER_MAX_SIZE 44100
#define QUEUE_MAX_SIZE 10

typedef struct
{
	int* buffer;
	int size;
	int tick;
} t_audio_buffer;

typedef struct
{
	t_audio_buffer* tab[QUEUE_MAX_SIZE];
	int next_tick_to_send;
} t_buffer_queue;

/**
 * creer un buffer audio 
 */
t_audio_buffer* create_audio_buffer(char* tick, char* buffer);

/**
 * libere la mémoire du buffer audio
 */
void destroy_audio_buffer(t_audio_buffer* buffer);

/**
 * ajout a sa place dans la 'queue' le buffer audio
 */
int add_queue(t_buffer_queue* queue, t_audio_buffer* buffer);

/**
 * renvoie le buffer du tick attendu et passe au prochain tick
 */
t_audio_buffer* pop(t_buffer_queue* queue);

/**
 * initialise la 'queue' avec des cases NULL
 */
void init_queue(t_buffer_queue* queue);

/**
 * converti un string en buffer audio (int)
 */
void convertStringToAudio(char* str, t_audio_buffer* buffer);

/**
 * converti un buffer audio (int) en string
 */
void convertAudioToString(t_audio_buffer* buffer, char** res);

#endif
