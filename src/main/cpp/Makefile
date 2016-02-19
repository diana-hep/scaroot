all: root2avro.cpp
	mkdir -p ../../../src/main/resources
	g++ ttreeReader.cpp -o ../../../src/main/resources/ttreeReader.so \
		-fPIC -shared
		-Wl,--no-as-needed $(shell root-config --cflags --ldflags --libs) -lTreePlayer
