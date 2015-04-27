#include "audio.h"
#include "serveur_type.h"
#include <string.h>

int add_queue(t_buffer_queue * queue, t_audio_buffer* buffer){
	int i=0;
	queue->tab[buffer->tick] = buffer;
	return 0;
	/*
	while(i < QUEUE_MAX_SIZE){
		if(queue->tab[i]==NULL){
			queue->tab[i]=buffer;
			printf("insert at %d\n",i);
			return 0;
		}

		if(queue->next_tick_to_send > queue->tab[i]->tick){
			destroy_audio_buffer(queue->tab[i]);
			queue->tab[i]=buffer;
			printf("insert at %d\n",i);
			return 0;
		}
		else
			i++;
	}
	return -1;
	*/
}

void destroy_audio_buffer(t_audio_buffer* buffer){
	free(buffer->buffer);
	free(buffer);
}

t_audio_buffer* pop(t_buffer_queue* queue){
	int found=0;
	int i=0;
	t_audio_buffer* res=NULL;

	while(!found && i<QUEUE_MAX_SIZE){
		printf("cherche a lindice %d\n",i);
		if(queue->tab[i]->tick == queue->next_tick_to_send){
			found = 1;
			queue->next_tick_to_send = (queue->next_tick_to_send + 1) % 4;
		}
		else
			i++;
	}

	if(found){
		//res->tick = queue->tab[i]->tick;
		
		/*for(j = 0; j < AUDIO_BUFFER_SIZE; j++){
			res->buffer[i] = queue->tab[i]->buffer[i];	
			printf("%d/",queue->tab[i]->buffer[i]);
		}*/
		printf("find at %d\n",i);
		res=queue->tab[i];
		printf("recuperer\n");
		queue->tab[i]=NULL;
		if(res==NULL)
			printf("res==null\n");
		
	}
	
	return res;
}

void init_queue(t_buffer_queue * queue){
	int i;
	for (i = 0; i < QUEUE_MAX_SIZE; ++i)
	{
		queue->tab[i]=NULL;
		//queue->tab[i].tick=-1;
	}
	//queue->next_tick_to_send = 0;

}

t_audio_buffer* create_audio_buffer(char* tick, char* buffer){
	t_audio_buffer* res=malloc(sizeof(t_audio_buffer));
	res->buffer=malloc(sizeof(int)*AUDIO_BUFFER_MAX_SIZE);
	res->tick=atoi(tick);

	//printf(">create_audio_buffer call convertStringToAudio\n");
	convertStringToAudio(buffer, res);
	//printf("%d<create_audio_buffer call convertStringToAudio\n",res->size);

	return res;
}

void convertStringToAudio(char* str, t_audio_buffer* buffer){
	int i;
	//printf("in\n");
	str[strlen(str)-1]='\0'; //enleve le '/' a la fin
	//if(str[strlen(str)-1]==',')
	//	str[strlen(str)-1]='\0';
	//printf("[%lu]\n", strlen(str));
	int val;
	char* val_str;

	for(i = 0; i < AUDIO_BUFFER_MAX_SIZE && str!=NULL && strcmp("",str)!=0; ++i){
		/*if(strcmp("", str) == 0){
			printf("Pas assez grand !\n");
			return;
		}*/
		val_str=strtok_r(str, ",",&str);
		val=atoi(val_str);
		//printf("%s",val_str);
		//printf("[str(%s) val(%d)]",val_str,val );
		//printf("size(%d),i(%d),val(%d)\n",size,i,val);
		//(*res)[i] = atoi(strtok(str, ","));
		
		buffer->buffer[i]=val;
		//printf("%d", (*res)[i]);

	}
	buffer->size=i;
	//if(strlen(str)>1){
	//if(strcmp("", str) != 0){
	//	printf("Trop grand ! [%lu->%s]\n",strlen(str),str);
	//}

	/*printf("affiche res\n");
	for(int i=0;i<AUDIO_BUFFER_SIZE;i++){
		printf("%d",res[0][i]);
	}*/
}

void convertAudioToString(t_audio_buffer* buffer, char ** res){
	char val[10];
	//char res[AUDIO_BUFFER_MAX_SIZE];
	strcpy(*res,"");
	int i;
	for (i = 0; i < buffer->size; ++i){
		sprintf(val,"%d,",buffer->buffer[i]);
		//printf("%s\n", val);
		strcat(*res,val);
	}
	//printf("%s\n", *res);

/*
	int *data=buffer->buffer;
	int data_length= buffer->size;

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
*/
}

t_audio_buffer getMix(int tick, t_client * client){
	t_audio_buffer mix;
	mix.tick = tick;
	mix.buffer = malloc(sizeof(int) * AUDIO_BUFFER_MAX_SIZE);
	int i;
	for(i = 0; i < AUDIO_BUFFER_MAX_SIZE; ++i){
		mix.buffer[i] = 0;
	}
	int signal_A;
	int signal_B;
	for(i = 0; i < serveur.max_user; ++i){
		if(serveur.clients[i] == NULL) continue;
		if(serveur.clients[i] != client){
			int j;
			for(j = 0; j < AUDIO_BUFFER_MAX_SIZE; ++j){
				signal_A = mix.buffer[j];
				signal_B = serveur.clients[i]->queue.tab[tick]->buffer[j];
				mix.buffer[j] = signal_A + signal_B - signal_A * signal_B;
				if(mix.buffer[j] > 128) mix.buffer[j] = 128;

			}		
		}

	}

	return mix;
}
