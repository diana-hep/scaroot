all: RootTreeReader.cpp
	mkdir -p ../../../target/native/lib/linux_x64
	g++ RootTreeReader.cpp -o ../../../target/native/lib/linux_x64/libRootTreeReader.so \
		-fPIC -shared
# -Wl,--no-as-needed $(shell root-config --cflags --ldflags --libs) -lTreePlayer
