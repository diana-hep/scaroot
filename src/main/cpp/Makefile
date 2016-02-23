all: RootTreeReader.cpp
	mkdir -p ../../../target/native/linux-x86-64
	g++ RootTreeReader.cpp -o ../../../target/native/linux-x86-64/libRootTreeReader.so \
		-fPIC -shared \
		-Wl,--no-as-needed $(shell root-config --cflags --ldflags --libs) -lTreePlayer
