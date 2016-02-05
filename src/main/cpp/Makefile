all: scaroot.cpp
	g++ -fPIC -shared -Wl,--no-as-needed $(shell root-config --cflags --ldflags --libs) -o ../../../src/main/resources/native/scaroot.so scaroot.cpp
