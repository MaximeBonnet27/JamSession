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

t_audio_buffer* create_audio_buffer(char* tick, char* buffer);
void destroy_audio_buffer(t_audio_buffer* buffer);
int add_queue(t_buffer_queue* queue, t_audio_buffer* buffer);
t_audio_buffer* pop(t_buffer_queue* queue);
void init_queue(t_buffer_queue* queue);
void convertStringToAudio(char* str, t_audio_buffer* buffer);
void convertAudioToString(t_audio_buffer* buffer, char** res);
#endif
