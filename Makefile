# MAKEFILE PRINCIPAL

run_serveur : 
	cd serveur && make run

run_client :
	cd client && ant run

debug_serveur :
	cd serveur && make debug && make run
clean :
	@cd serveur && make clean
	@cd client && ant clean
	@rm *~
	@echo "Repertoire nettoyé!\n"
