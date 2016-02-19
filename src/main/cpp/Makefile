all: RootTreeReader.cpp
	mkdir -p ../../../src/main/resources
	g++ RootTreeReader.cpp -o ../../../src/main/resources/libRootTreeReader.so \
		-fPIC -shared \
		-Wl,--no-as-needed $(shell root-config --cflags --ldflags --libs) -lTreePlayer
