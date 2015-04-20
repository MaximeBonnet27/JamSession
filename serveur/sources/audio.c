#include "audio.h"

int add_queue(t_buffer_queue * queue, t_audio_buffer buffer){
	int good=0;
	int i=0;

	while(!good && i < QUEUE_SIZE){
		if(queue->next_tick_to_send > queue->tab[i].tick){
			queue->tab[i]=buffer;
			good=1;
			return 0;
		}
		else
			i++;
	}
	return -1;
}

int pop(t_buffer_queue* queue, t_audio_buffer * res){
	int found=0;
	int i=0;
	while(!found && i<QUEUE_SIZE){
		printf("%d\n", i);
		if(queue->tab[i].tick == queue->next_tick_to_send){
			printf("dans if \n");
			found = 1;
			queue->next_tick_to_send++;
		}
		else
			i++;
	}
	if(found){
		printf("Avant \n");
		res->tick = queue->tab[i].tick;
		int j;
		for(j = 0; j < AUDIO_BUFFER_SIZE; j++){
		res->buffer[i] = queue->tab[i].buffer[i];	
		}
		
		printf("Apres \n");
		return 1;
	}
	
	return 0;
}

void init_queue(t_buffer_queue * queue){
	int i;
	for (i = 0; i < QUEUE_SIZE; ++i)
	{
		queue->tab[i].tick=-1;
	}
	queue->next_tick_to_send = 0;

}

void create_audio_buffer(char * tick, char * buffer, t_audio_buffer * res){
	res->tick=atoi(tick);
	printf("conver\n");
	convertStringToAudio(buffer, &(res->buffer));
}

void convertStringToAudio(char* str, int res[][AUDIO_BUFFER_SIZE]){
	int i;
	printf("in\n");
	for(i = 0; i < AUDIO_BUFFER_SIZE; ++i){
		if(strcmp("", str) == 0){
			printf("Pas assez grand !\n");
			return;
		}
		(*res)[i] = atoi(strtok(str, ","));
	}
	if(strcmp("", str) != 0){
		printf("Trop grand !\n");
	}
}

void convertAudioToString(int audio_code[], char ** res){

	int *data=audio_code;
	int data_length= AUDIO_BUFFER_SIZE;

	*res ="";
	int output_length=0;

	for (; data_length; data_length--) {
		int length = snprintf(*res, output_length, "%d", *data++);
		if (length >= output_length) {
			return;
		}
		*res += length;
		output_length -= length;
	}

}