# MAKEFILE PRINCIPAL

run_serveur : 
	cd serveur && make run

run_client :
	cd client && ant run -Darg0=Maxime
run_client_2 : 
	cd client && ant run -Darg0=Plop

debug_serveur :
	cd serveur && make debug && make run
clean :
	@cd serveur && make clean
	@cd client && ant clean
	@rm -f *~
	@echo "Repertoire nettoyé!\n"
