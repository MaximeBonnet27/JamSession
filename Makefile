# MAKEFILE PRINCIPAL

run_serveur : 
	cd serveur && make run

run_client :
	cd client && ant run

clean :
	cd serveur && make clean
	cd client && ant clean
