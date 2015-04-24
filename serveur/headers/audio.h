#ifndef AUDIO_H_GUARD
#define AUDIO_H_GUARD

#include <stdlib.h>
#include <stdio.h>
#include <string.h>


#define AUDIO_BUFFER_SIZE 44100
#define QUEUE_SIZE 10
typedef struct
{
	int buffer[AUDIO_BUFFER_SIZE];
	int tick;
} t_audio_buffer;

typedef struct
{
	t_audio_buffer tab[QUEUE_SIZE];
	int next_tick_to_send;
} t_buffer_queue;

void create_audio_buffer(char * tick, char * buffer, t_audio_buffer * res);
int add_queue(t_buffer_queue * queue, t_audio_buffer buffer);
int pop(t_buffer_queue* queue, t_audio_buffer * res);
void init_queue(t_buffer_queue * queue);
void convertStringToAudio(char* str, int res[][AUDIO_BUFFER_SIZE]);
void convertAudioToString(int audio_code[], char ** res);
#endif
